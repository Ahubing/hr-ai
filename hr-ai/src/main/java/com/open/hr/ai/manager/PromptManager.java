package com.open.hr.ai.manager;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.AmPrompt;
import com.open.ai.eros.db.mysql.hr.service.impl.AmPromptServiceImpl;
import com.open.hr.ai.bean.req.AddOrUpdateAmPromptReq;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 逻辑按照php处理的
 *
 * @Date 2025/1/6 20:00
 */
@Slf4j
@Component
public class PromptManager {

    @Resource
    private AmPromptServiceImpl amPromptService;

    public ResultVO<List<AmPrompt>> getPromptList(Integer type, Long adminId) {
        try {
            LambdaQueryWrapper<AmPrompt> queryWrapper = new LambdaQueryWrapper<>();
            if (Objects.isNull(type)) {
                queryWrapper.eq(AmPrompt::getAdminId, adminId);
                List<AmPrompt> amPrompt = amPromptService.list(queryWrapper);
                return ResultVO.success(amPrompt);
            } else {
                queryWrapper.eq(AmPrompt::getAdminId, adminId).eq(AmPrompt::getType, type);
                List<AmPrompt> amPrompt = amPromptService.list(queryWrapper);
                return ResultVO.success(amPrompt);
            }
        } catch (Exception e) {
            log.error("获取AI跟进prompt列表异常 type={},adminId={}", type, adminId, e);
        }
        return ResultVO.fail("获取AI跟进prompt列表异常");
    }

    public ResultVO<AmPrompt> getPromptDetail(Integer id) {
        try {
            AmPrompt amPrompt = amPromptService.getById(id);
            return ResultVO.success(amPrompt);
        } catch (Exception e) {
            log.error("获取AI跟进prompt列表异常 id={}", id, e);
        }
        return ResultVO.fail("获取AI跟进prompt详情异常");
    }


    public ResultVO addOrUpdatePrompt(AddOrUpdateAmPromptReq req,Long adminId) {
        try {
            AmPrompt amPrompt = new AmPrompt();
            if (Objects.isNull(req.getId())) {
                amPrompt.setAdminId(adminId);
                amPrompt.setName(req.getName());
                amPrompt.setModel(req.getModel());
                if (Objects.nonNull(req.getType())) {
                    amPrompt.setType(req.getType());
                }
                if (Objects.nonNull(req.getTypeA())) {
                    amPrompt.setTypeA(req.getTypeA());
                }
                if (StringUtils.isNotBlank(req.getPrompt())) {
                    amPrompt.setPrompt(req.getPrompt());
                }
                if (StringUtils.isNotBlank(req.getPrompt2())) {
                    amPrompt.setPrompt2(req.getPrompt2());
                }
                if (Objects.nonNull(req.getResumeId())) {
                    amPrompt.setResumeId(req.getResumeId());
                }
                if (StringUtils.isNotBlank(req.getUrl())) {
                    amPrompt.setUrl(req.getUrl());
                }
                if (Objects.nonNull(req.getIsRead())) {
                    amPrompt.setIsRead(req.getIsRead());
                }
                if (StringUtils.isNotBlank(req.getTags())) {
                    amPrompt.setTags(req.getTags());
                }
                amPrompt.setCreateTime(LocalDateTime.now());
                boolean result = amPromptService.save(amPrompt);
                log.info("prompt新增成功 result={}", result);
            } else {
                amPrompt = amPromptService.getById(req.getId());
                amPrompt.setName(req.getName());
                amPrompt.setModel(req.getModel());
                amPrompt.setType(req.getType());
                amPrompt.setAdminId(adminId);
                if (StringUtils.isNotBlank(req.getPrompt())) {
                    amPrompt.setPrompt(req.getPrompt());
                }
                if (StringUtils.isNotBlank(req.getPrompt2())) {
                    amPrompt.setPrompt2(req.getPrompt2());
                }
                if (Objects.nonNull(req.getResumeId())) {
                    amPrompt.setResumeId(req.getResumeId());
                }
                if (StringUtils.isNotBlank(req.getUrl())) {
                    amPrompt.setUrl(req.getUrl());
                }
                if (Objects.nonNull(req.getIsRead())) {
                    amPrompt.setIsRead(req.getIsRead());
                }
                if (StringUtils.isNotBlank(req.getTags())) {
                    amPrompt.setTags(req.getTags());
                }
                boolean result = amPromptService.updateById(amPrompt);
                if (!result) {
                    return ResultVO.fail("prompt修改失败");
                }
                log.info("prompt修改成功 result={}", result);
            }
            return ResultVO.success(amPrompt);
        } catch (Exception e) {
            log.error("prompt新增或修改 req={}", JSONObject.toJSONString(req), e);
        }
        return ResultVO.fail("prompt新增或修改异常");
    }


    public ResultVO deletePromptById(Integer id) {
        try {
            boolean result = amPromptService.removeById(id);
            return result ? ResultVO.success() : ResultVO.fail("删除prompt失败");
        } catch (Exception e) {
            log.error("删除prompt异常 id={}", id, e);
        }
        return ResultVO.fail("删除prompt失败异常");
    }

}
