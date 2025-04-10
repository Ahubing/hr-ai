package com.open.ai.eros.ai.manager;

import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.open.ai.eros.ai.bean.req.ModelConfigAddReq;
import com.open.ai.eros.ai.bean.req.ModelConfigSearchReq;
import com.open.ai.eros.ai.bean.req.ModelConfigUpdateReq;
import com.open.ai.eros.ai.convert.ModelConfigConvert;
import com.open.ai.eros.ai.lang.chain.provider.ModelConfigService;
import com.open.ai.eros.common.exception.BizException;
import com.open.ai.eros.common.service.RedisClient;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.ai.entity.ModelConfig;
import com.open.ai.eros.db.mysql.ai.entity.ModelConfigSearchVo;
import com.open.ai.eros.db.mysql.ai.entity.ModelConfigVo;
import com.open.ai.eros.db.mysql.ai.service.impl.ModelConfigServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @类名：ModelConfigManager
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/7 22:27
 */
@Slf4j
@Component
public class ModelConfigManager implements ModelConfigService {


    private final LoadingCache<Long, Optional<ModelConfig>> modelConfig_CACHE = CacheBuilder.newBuilder().expireAfterWrite(3, TimeUnit.MINUTES).
            initialCapacity(20).maximumSize(1000).build(new CacheLoader<Long, Optional<ModelConfig>>() {

                @Override
                public Optional<ModelConfig> load(Long aLong) throws Exception {
                    ModelConfig modelConfig = modelConfigService.getById(aLong);
                    if (modelConfig == null) {
                        return Optional.empty();
                    }
                    return Optional.of(modelConfig);
                }
            });

    /**
     * 模型的渠道
     */
    private static String templateModelList = "template:model:list:%s";

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private ModelConfigServiceImpl modelConfigService;


    //@PostConstruct
    //public void init() {
    //    List<ModelConfig> modelConfigs = ModelConfigUtil.getModelConfig();
    //    if (CollectionUtils.isEmpty(modelConfigs)) {
    //        return;
    //    }
    //    LambdaQueryWrapper<ModelConfig> lambdaQueryWrapper = new LambdaQueryWrapper<>();
    //    boolean removed = modelConfigService.remove(lambdaQueryWrapper);
    //    log.info("modelConfig init  removed={}", removed);
    //    boolean savedBatch = modelConfigService.saveBatch(modelConfigs);
    //    log.info("modelConfig init  savedBatch={}", savedBatch);
    //}


    public ModelConfigVo getModelConfig(String templateModel) {
        String key = String.format(templateModelList, templateModel);
        Long lsize = redisClient.lsize(key);
        if (lsize == null || lsize <= 0) {
            log.error("getModelConfig size is 0  templateMode={}",templateModel);
            return null;
        }
        Random random = new Random();
        int nextInt = random.nextInt(Integer.parseInt(String.valueOf(lsize)));
        String modelConfigId = redisClient.lindex(key, nextInt);
        if (StringUtils.isEmpty(modelConfigId)) {
            log.error("getModelConfig lsize ={}  templateMode={}  modelConfigId is null ",lsize,templateModel);
            return null;
        }
        ModelConfig modelConfig = null;
        try {
            Optional<ModelConfig> modelConfigOptional = modelConfig_CACHE.get(Long.parseLong(modelConfigId));
            if (modelConfigOptional.isPresent()) {
                modelConfig = modelConfigOptional.get();
            }
        } catch (Exception e) {
            log.error("getModelConfig error templateModel={}", templateModel, e);
            modelConfig = modelConfigService.getCacheById(Long.parseLong(modelConfigId));
        }
        return ModelConfigConvert.I.convertModelConfigVo(modelConfig);
    }

    /**
     * 根据模块获取渠道信息
     *
     * @return
     */
    public ModelConfigVo getModelConfig(String template, String model) {
        return getModelConfig(template + ":" + model);
    }


    /**
     * 新增模型渠道
     *
     * @param account
     * @param req
     * @return
     */
    @Transactional
    public ResultVO addModelConfig(String account, ModelConfigAddReq req) {
        ModelConfig modelConfig = ModelConfigConvert.I.convertModelConfig(req);
        modelConfig.setCreateAccount(account);
        modelConfig.setCreateTime(LocalDateTime.now());
        boolean saveResult = modelConfigService.save(modelConfig);
        log.info("addModelConfig  req={}, saveResult={}", JSONObject.toJSONString(req), saveResult);

        if (saveResult) {
            Long id = modelConfig.getId();
            String templateModel = modelConfig.getTemplateModel();
            Integer weight = modelConfig.getWeight();
            addModelConfigToTemplate(id, weight, templateModel);
        }
        return saveResult ? ResultVO.success() : ResultVO.fail("新增失败！");
    }


    /**
     * 将渠道放进调用渠道中
     *
     * @param id
     */
    public void addModelConfigToTemplate(Long id, Integer weight, String templateModels) {
        String[] templateModelArr = templateModels.split(",");
        for (String templateModel : templateModelArr) {
            String key = String.format(templateModelList, templateModel);
            String[] ids = new String[weight];
            Arrays.fill(ids, String.valueOf(id));
            Long lpush = redisClient.lpush(key, ids);
            log.info("addModelConfigToTemplate to cache id={},lpush={}，templateModel={}", id, lpush, templateModel);
            if (lpush == null || lpush <= 0) {
                throw new BizException("新增失败！");
            }
        }
    }

    public void removeModelConfigToTemplate(Long id, String templateModels) {
        String[] templateModelArr = templateModels.split(",");
        for (String templateModel : templateModelArr) {
            String key = String.format(templateModelList, templateModel);
            Long lrem = redisClient.lrem(key, 0, String.valueOf(id));
            log.info("removeModelConfigToTemplate to cache id={},lrem={}", id, lrem);
        }
    }


    /**
     * 修改模型渠道
     *
     * @param account
     * @param req
     * @return
     */
    public ResultVO updateModelConfig(String account, ModelConfigUpdateReq req) {

        ModelConfig config = modelConfigService.getById(req.getId());
        if (config == null) {
            return ResultVO.fail("该渠道不存在");
        }

        ModelConfig modelConfig = ModelConfigConvert.I.convertModelConfig(req);
        modelConfig.setUpdateAccount(account);
        modelConfig.setUpdateTime(LocalDateTime.now());
        boolean updateResult = modelConfigService.updateById(modelConfig);
        log.info("updateModelConfig  req={}, updateResult={}", JSONObject.toJSONString(req), updateResult);
        if (updateResult) {
            Long id = modelConfig.getId();
            Integer weight = modelConfig.getWeight();
            removeModelConfigToTemplate(id, config.getTemplateModel());
            addModelConfigToTemplate(id, weight, modelConfig.getTemplateModel());
        }
        return updateResult ? ResultVO.success() : ResultVO.fail("修改失败！");
    }


    /**
     * 搜索
     *
     * @param req
     * @return
     */
    public PageVO<ModelConfigVo> searchModelConfig(ModelConfigSearchReq req) {

        ModelConfigSearchVo searchVo = ModelConfigSearchVo.builder()
                .id(req.getId())
                .token(req.getToken())
                .pageIndex((req.getPage() - 1) * req.getPageSize())
                .pageSize(req.getPageSize())
                .status(req.getStatus())
                .template(req.getTemplate())
                .modelConfigName(req.getModelConfigName())
                .build();
        PageVO<ModelConfigVo> pageVO = new PageVO<>();

        int count = modelConfigService.getBaseMapper().searchModelConfigCount(searchVo);
        pageVO.setTotal(count);
        if (count > 0) {
            // 上面加密 会自动转化为 加密之后的字段，所以这里需要 再将原值设置进去
            searchVo.setToken(req.getToken());
            List<ModelConfig> modelConfigs = modelConfigService.getBaseMapper().searchModelConfig(searchVo);
            pageVO.setData(
                    modelConfigs.stream()
                            .map(ModelConfigConvert.I::convertModelConfigVo)
                            .collect(Collectors.toList())
            );
        }
        return pageVO;
    }


    /**
     * 删除渠道
     *
     * @param id
     * @return
     */
    @Transactional
    public ResultVO deleteModelConfig(Long id) {
        ModelConfig config = modelConfigService.getById(id);
        if (config == null) {
            return ResultVO.fail("该渠道不存在");
        }
        boolean result = modelConfigService.removeById(id);
        log.info("deleteModelConfig id={},result={}", id, result);
        return result ? ResultVO.success() : ResultVO.fail("删除渠道失败！");
    }


}
