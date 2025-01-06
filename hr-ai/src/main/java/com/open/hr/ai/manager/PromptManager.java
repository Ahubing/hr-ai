package com.open.hr.ai.manager;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

    public ResultVO getPromptList(Integer type, Long adminId) {
        try {
            QueryWrapper<AmPrompt> queryWrapper = new QueryWrapper<>();
            JSONObject jsonObject = new JSONObject();
            if (Objects.isNull(type)) {
                queryWrapper.eq("admin_id", adminId);
                List<AmPrompt> amPrompt = amPromptService.list(queryWrapper);
                jsonObject.put("prompts", amPrompt);
            } else {
                queryWrapper.eq("admin_id", adminId).eq("type", type);
                List<AmPrompt> amPrompt = amPromptService.list(queryWrapper);
                jsonObject.put("prompts", amPrompt);
            }
            return ResultVO.success(jsonObject);
        } catch (Exception e) {
            log.error("获取AI跟进prompt列表异常 type={},adminId={}", type, adminId, e);
        }
        return ResultVO.fail("获取AI跟进prompt列表异常");
    }

    public ResultVO getPromptDetail(Integer id) {
        try {
            AmPrompt amPrompt = amPromptService.getById(id);
            return ResultVO.success(amPrompt);
        } catch (Exception e) {
            log.error("获取AI跟进prompt列表异常 id={}", id, e);
        }
        return ResultVO.fail("获取AI跟进prompt详情异常");
    }


    public ResultVO addOrUpdatePrompt(AddOrUpdateAmPromptReq req) {
        try {
            AmPrompt amPrompt = new AmPrompt();
            if (Objects.isNull(req.getId())) {
                amPrompt.setAdminId(req.getAdminId());
                amPrompt.setName(req.getName());
                amPrompt.setModel(req.getModel());
                amPrompt.setType(req.getType());
                amPrompt.setTypeA(req.getTypeA());
                amPrompt.setPrompt(req.getPrompt());
                amPrompt.setPrompt2(req.getPrompt2());
                amPrompt.setResumeId(req.getResumeId());
                amPrompt.setUrl(req.getUrl());
                amPrompt.setIsRead(req.getIsRead());
                amPrompt.setTags(req.getTags());
                amPrompt.setCreateTime(LocalDateTime.now());
            } else {
                amPrompt = amPromptService.getById(req.getId());
                amPrompt.setName(req.getName());
                amPrompt.setModel(req.getModel());
                amPrompt.setType(req.getType());
                amPrompt.setAdminId(req.getAdminId());
                if (StringUtils.isNotBlank(req.getPrompt())) {
                    amPrompt.setPrompt(req.getPrompt());
                }
                if (StringUtils.isNotBlank(req.getPrompt2())) {
                    amPrompt.setPrompt2(req.getPrompt2());
                }
                amPromptService.updateById(amPrompt);
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
