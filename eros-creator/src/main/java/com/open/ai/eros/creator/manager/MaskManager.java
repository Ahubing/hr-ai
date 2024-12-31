package com.open.ai.eros.creator.manager;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.api.client.util.Lists;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.creator.bean.RankTypeEnum;
import com.open.ai.eros.creator.bean.req.*;
import com.open.ai.eros.creator.bean.vo.*;
import com.open.ai.eros.creator.convert.MaskConvert;
import com.open.ai.eros.db.constants.MaskStatusEnum;
import com.open.ai.eros.db.mysql.creator.entity.Mask;
import com.open.ai.eros.db.mysql.creator.service.impl.MaskServiceImpl;
import com.open.ai.eros.db.mysql.user.entity.User;
import com.open.ai.eros.db.mysql.user.entity.UserFollowMask;
import com.open.ai.eros.db.mysql.user.service.impl.UserFollowMaskServiceImpl;
import com.open.ai.eros.db.mysql.user.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @类名：MaskManager
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/13 23:27
 */

@Slf4j
@Component
public class MaskManager {


    @Autowired
    private UserServiceImpl userService;


    @Autowired
    private UserFollowMaskServiceImpl userFollowMaskService;

    @Autowired
    private RankManager rankManager;


    @Autowired
    private MaskServiceImpl maskService;

    private final LoadingCache<Long, Optional<Mask>> MASK_CACHE = CacheBuilder.newBuilder().expireAfterWrite(3, TimeUnit.MINUTES).
            initialCapacity(20).maximumSize(1000).build(new CacheLoader<Long, Optional<Mask>>() {

                @Override
                public Optional<Mask> load(Long aLong) throws Exception {
                    Map<Long, Optional<Mask>> loadMaskMap = loadMaskMap(com.google.common.collect.Lists.newArrayList(aLong));
                    if (!loadMaskMap.get(aLong).isPresent()) {
                        return Optional.empty();
                    }
                    return loadMaskMap.get(aLong);
                }

                @Override
                public Map<Long, Optional<Mask>> loadAll(Iterable<? extends Long> keys) throws Exception {
                    return loadMaskMap(Lists.newArrayList(keys));
                }
            });


    private Map<Long, Optional<Mask>> loadMaskMap(List<Long> ids) {
        List<Mask> masks = maskService.listByIds(ids);
        Map<Long, Optional<Mask>> maskMap = masks.stream().collect(Collectors.toMap(Mask::getId, Optional::of, (k1, k2) -> k1));
        for (Long id : ids) {
            maskMap.computeIfAbsent(id, k -> Optional.empty());
        }
        return maskMap;
    }


    /**
     * 批量获取发布的的面具消息
     *
     * @param ids
     * @return
     */
    public List<Mask> batchGetCacheMask(List<Long> ids) {
        List<Mask> masks = new ArrayList<>();
        try {
            if (CollectionUtils.isEmpty(ids)) {
                return masks;
            }
            ImmutableMap<Long, Optional<Mask>> immutableMap = MASK_CACHE.getAll(ids);
            for (Optional<Mask> mask : immutableMap.values()) {
                if (mask.isPresent() && mask.get().getStatus() == MaskStatusEnum.OK.getStatus()) {
                    masks.add(mask.get());
                }
            }
        } catch (Exception e) {
            log.error("batchGetMask error ids={} ", JSONObject.toJSONString(ids), e);
            return batchGetMask(ids);
        }
        return masks;
    }

    public List<Mask> batchGetMask(List<Long> ids) {
        List<Mask> newMasks = new ArrayList<>();
        List<Mask> masks = maskService.listByIds(ids);
        for (Mask mask : masks) {
            if (MaskStatusEnum.OK.getStatus() == mask.getStatus()) {
                newMasks.add(mask);
            }
        }
        return newMasks;
    }


    public ResultVO<List<SimpleMaskVo>> getUserSimpleMask() {

        List<Mask> simpleMask = maskService.getSimpleMask();
        List<SimpleMaskVo> SimpleMaskVos = new ArrayList<>();
        for (Mask mask : simpleMask) {
            SimpleMaskVo maskVo = new SimpleMaskVo();
            maskVo.setAvatar(mask.getAvatar());
            maskVo.setId(mask.getId());
            maskVo.setName(mask.getName());
            SimpleMaskVos.add(maskVo);
        }
        return ResultVO.success(SimpleMaskVos);
    }


    /**
     * 创作者查看面具详情
     *
     * @param userId
     * @param maskId
     * @param role
     * @return
     */
    public ResultVO<BMaskVo> getBMaskById(Long userId, Long maskId, Integer status, String role) {
        Mask mask = maskService.getById(maskId);
        if (mask == null || !mask.getStatus().equals(status)) {
            return ResultVO.fail("面具不存在！");
        }
        if (!mask.getUserId().equals(userId) || !role.equals(RoleEnum.SYSTEM.getRole())) {
            return ResultVO.fail("没有查看该面具的权限");
        }
        BMaskVo bMaskVo = MaskConvert.I.convertBMaskVo(mask);
        return ResultVO.success(bMaskVo);
    }


    public Mask getCacheMask(Long id) {
        Mask mask = null;
        try {
            Optional<Mask> mask1 = MASK_CACHE.get(id);
            if (mask1.isPresent()) {
                mask = mask1.get();
            }
        } catch (Exception e) {
            mask = maskService.getById(id);
        }
        return mask;
    }


    /**
     * c端获取详细的面具信息
     *
     * @param userId
     * @param maskId
     * @param status
     * @return
     */
    public ResultVO<CMaskVo> getCMaskById(Long userId, Long maskId, Integer status) {
        Mask mask = getCacheMask(maskId);
        if (mask == null || !mask.getStatus().equals(status)) {
            return ResultVO.success();
        }
        CMaskVo cMaskVo = MaskConvert.I.convertCMaskVo(mask);

        //创作者的id
        Long creatorId = mask.getUserId();
        Optional<User> user = userService.getCacheUser(creatorId);
        if (user.isPresent()) {
            User cahceUser = user.get();
            cMaskVo.setUsername(cahceUser.getUserName());
            cMaskVo.setUserAvatar(cahceUser.getAvatar());
        }


        if (userId != null) {
            UserFollowMask userFollowMask = userFollowMaskService.getUserFollowMask(userId, maskId);
            if (userFollowMask != null) {
                cMaskVo.setFollow(true);
            }
        }
        return ResultVO.success(cMaskVo);
    }


    public BMaskVo getCacheBMaskById(Long maskId) {
        Mask mask = getCacheMask(maskId);
        return MaskConvert.I.convertBMaskVo(mask);
    }

    /**
     * 检测伴侣中是否有违禁词
     *
     * @param maskId
     * @param prompt
     * @return
     */
    public ResultVO banWordInPrompt(Long maskId, String prompt) {
        if (maskId == null) {
            return ResultVO.success();
        }
        BMaskVo mask = getCacheBMaskById(maskId);
        if (mask == null) {
            return ResultVO.success();
        }
        return bandWord(mask.getBannedWords(), prompt);
    }


    /**
     * 禁用词
     *
     * @param bannedWords
     * @param prompt
     * @return
     */
    public ResultVO bandWord(List<String> bannedWords, String prompt) {
        if (CollectionUtils.isEmpty(bannedWords)) {
            return ResultVO.success();
        }
        for (String bannedWord : bannedWords) {
            if (prompt.contains(bannedWord)) {
                return ResultVO.fail("出现违禁词！");
            }
        }
        return ResultVO.success();
    }


    /**
     * 删除面具
     *
     * @param userId
     * @param maskId
     * @return
     */
    public ResultVO deleteMask(Long userId, Long maskId, String role) {

        Mask mask = maskService.getById(maskId);
        if (mask == null) {
            return ResultVO.fail("删除不存在！");
        }
        if (!mask.getUserId().equals(userId) || !role.equals(RoleEnum.SYSTEM.getRole())) {
            return ResultVO.fail("没有该面具操作的权限");
        }
        mask = new Mask();
        mask.setId(maskId);
        mask.setStatus(MaskStatusEnum.DELETE.getStatus());
        mask.setUpdateTime(LocalDateTime.now());
        boolean updated = maskService.updateById(mask);
        log.info("deleteMask updated={}, mask={}", updated, JSONObject.toJSONString(mask));
        return updated ? ResultVO.success() : ResultVO.fail("删除失败");
    }


    /**
     * 新增
     *
     * @param userId
     * @param req
     * @return
     */
    public ResultVO addMask(Long userId, MaskAddReq req) {
        Mask mask = MaskConvert.I.convertMask(req);
        mask.setCreateTime(LocalDateTime.now());
        mask.setUserId(userId);
        boolean save = maskService.save(mask);
        if (!save) {
            log.info("addMask error mask={}", JSONObject.toJSONString(mask));
        }
        return save ? ResultVO.success() : ResultVO.fail("新增失败");
    }


    /**
     * 修改
     *
     * @param userId
     * @param req
     * @return
     */
    public ResultVO updateMask(Long userId, MaskUpdateReq req, String role) {

        Mask mask = maskService.getById(req.getId());
        if (mask == null) {
            return ResultVO.fail("删除不存在！");
        }
        if (!role.equals(RoleEnum.SYSTEM.getRole()) && (!mask.getUserId().equals(userId) || role.equals(RoleEnum.COMMON.getRole()))) {
            return ResultVO.fail("没有该面具操作的权限");
        }

        mask = MaskConvert.I.convertMask(req);
        mask.setStatus(req.getStatus());
        mask.setCreateTime(LocalDateTime.now());
        mask.setUserId(userId);
        boolean updated = maskService.updateById(mask);
        if (!updated) {
            log.info("addMask error mask={}", JSONObject.toJSONString(mask));
        }
        return updated ? ResultVO.success() : ResultVO.fail("修改失败");
    }


    /**
     * c端 搜索
     *
     * @param req
     * @return
     */
    public ResultVO<MaskSearchResultVo> c_searchMask(MaskSearchReq req) {
        MaskSearchResultVo resultVo = new MaskSearchResultVo();
        LambdaQueryWrapper<Mask> queryWrapper = new LambdaQueryWrapper<>();
        Integer pageNum = req.getPageNum();
        Integer pageSize = req.getPageSize();
        String keywords = req.getKeywords();
        // 需要特殊化处理
        String tab = req.getTab();
        if (MaskTabEnum.NEW.getType().equals(tab)) {
            // 最新
            queryWrapper.orderByDesc(Mask::getCreateTime);
        } else if (MaskTabEnum.HEAT.getType().equals(tab)) {
            // 最热
            queryWrapper.orderByDesc(Mask::getHeat);
        } else if(RankTypeEnum.exist(tab)){
            // 榜单查询
            Optional<List<Long>> maskIds = rankManager.getMaskIdsByRank(tab, pageNum, pageSize);
            if(!maskIds.isPresent()){
                resultVo.setLastPage(true);
                return ResultVO.success(resultVo);
            }
            List<Mask> masks = maskService.listByIds(maskIds.get());
            List<CMaskVo> cMaskVos = new ArrayList<>(masks.stream().map(MaskConvert.I::convertCMaskVo).collect(Collectors.toList()));
            boolean lastPage = cMaskVos.size() < pageSize;
            resultVo.setLastPage(lastPage);
            resultVo.setMaskVos(cMaskVos);
            return ResultVO.success(resultVo);
        }else{
            if (StringUtils.isNoneEmpty(tab)) {
                queryWrapper.eq(Mask::getMaskType, tab);
            }
        }

        if (StringUtils.isNoneEmpty(keywords)) {
            queryWrapper.or().like(Mask::getName, keywords).or().like(Mask::getTags, keywords).or().like(Mask::getIntro, keywords);
        }
        queryWrapper.eq(Mask::getStatus, MaskStatusEnum.OK.getStatus());
        Page<Mask> page = new Page<>(pageNum, pageSize);
        Page<Mask> maskPage = maskService.page(page, queryWrapper);
        List<CMaskVo> cMaskVos = new ArrayList<>(maskPage.getRecords().stream().map(MaskConvert.I::convertCMaskVo).collect(Collectors.toList()));
        boolean lastPage = cMaskVos.size() < pageSize;
        resultVo.setLastPage(lastPage);
        resultVo.setMaskVos(cMaskVos);
        return ResultVO.success(resultVo);
    }


    /**
     * b端 搜索
     *
     * @param req
     * @return
     */
    public ResultVO<PageVO<BMaskVo>> b_searchMask(MaskAdminSearchReq req) {

        LambdaQueryWrapper<Mask> queryWrapper = new LambdaQueryWrapper<>();

        Long userId = req.getUserId();
        String keywords = req.getKeywords();
        Integer type = req.getType();
        Integer status = req.getStatus();
        Integer pageNum = req.getPageNum();
        Integer pageSize = req.getPageSize();
        if (userId != null) {
            queryWrapper.eq(Mask::getUserId, userId);
        }
        if (StringUtils.isNoneEmpty(keywords)) {
            queryWrapper.like(Mask::getName, keywords);
        }
        if (type != null) {
            queryWrapper.eq(Mask::getType, type);
        }
        if (status != null) {
            queryWrapper.eq(Mask::getStatus, status);
        } else {
            queryWrapper.eq(Mask::getStatus, MaskStatusEnum.OK.getStatus());
        }
        Page<Mask> page = new Page<>(pageNum, pageSize);
        Page<Mask> maskPage = maskService.page(page, queryWrapper);
        List<BMaskVo> BMaskVos = maskPage.getRecords().stream().map(MaskConvert.I::convertBMaskVo).collect(Collectors.toList());
        return ResultVO.success(PageVO.build(maskPage.getTotal(), BMaskVos));
    }


    public ResultVO shareMask(Long userId, ShareMaskReq req) {

        Long maskId = req.getMaskId();

        Mask mask = maskService.getById(maskId);
        if (mask == null) {
            return ResultVO.fail("删除不存在！");
        }


        Long startChatId = req.getStartChatId();
        Long endChatId = req.getEndChatId();

        return ResultVO.success();
    }


}
