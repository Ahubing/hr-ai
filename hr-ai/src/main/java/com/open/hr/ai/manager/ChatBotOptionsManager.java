package com.open.hr.ai.manager;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.AmChatbotOptions;
import com.open.ai.eros.db.mysql.hr.entity.AmChatbotOptionsItems;
import com.open.ai.eros.db.mysql.hr.entity.AmChatbotPositionOption;
import com.open.ai.eros.db.mysql.hr.service.impl.AmChatbotOptionAiRoleServiceImpl;
import com.open.ai.eros.db.mysql.hr.service.impl.AmChatbotOptionsItemsServiceImpl;
import com.open.ai.eros.db.mysql.hr.service.impl.AmChatbotOptionsServiceImpl;
import com.open.ai.eros.db.mysql.hr.service.impl.AmChatbotPositionOptionServiceImpl;
import com.open.hr.ai.bean.req.AddOrUpdateAmChatbotOptions;
import com.open.hr.ai.bean.req.AddOrUpdateAmChatbotOptionsItems;
import com.open.hr.ai.bean.vo.AmChatbotOptionsItemsVo;
import com.open.hr.ai.bean.vo.AmChatbotOptionsVo;
import com.open.hr.ai.convert.AmChatBotOptionConvert;
import com.open.hr.ai.convert.AmChatBotPositionItemsConvert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 逻辑按照php处理的, 暂时未调试
 *
 * @Date 2025/1/4 23:28
 */
@Component
@Slf4j
public class ChatBotOptionsManager {


    @Resource
    private AmChatbotOptionsServiceImpl amChatbotOptionsService;

    @Resource
    private AmChatbotOptionsItemsServiceImpl amChatbotOptionsItemsService;

    @Resource
    private AmChatbotOptionAiRoleServiceImpl amChatbotOptionAiRoleService;

    @Resource
    private AmChatbotPositionOptionServiceImpl amChatbotPositionOptionService;

    public ResultVO<List<AmChatbotOptionsVo>> chatbotOptionsList(Long adminId, Integer type, String keyword) {
        try {
            LambdaQueryWrapper<AmChatbotOptions> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AmChatbotOptions::getAdminId, adminId);
            if (Objects.nonNull(type)){
                queryWrapper.eq(AmChatbotOptions::getType, type);
            }
            if (StringUtils.isNotBlank(keyword)) {
                queryWrapper.like(AmChatbotOptions::getName, keyword);
            }
            queryWrapper.orderByAsc(AmChatbotOptions::getId);
            List<AmChatbotOptionsVo> amChatbotOptionsList = amChatbotOptionsService.list(queryWrapper).stream().map(AmChatBotOptionConvert.I::convertOptionVo).collect(Collectors.toList());
            for (AmChatbotOptionsVo amChatbotOptionsVo : amChatbotOptionsList) {
                amChatbotOptionsVo.setRelativePositionNums(StringUtils.isNotBlank(amChatbotOptionsVo.getPositionIds()) ? amChatbotOptionsVo.getPositionIds().split(",").length : 0);
                LambdaQueryWrapper<AmChatbotOptionsItems> itemsQueryWrapper = new LambdaQueryWrapper<>();
                itemsQueryWrapper.eq(AmChatbotOptionsItems::getOptionId, amChatbotOptionsVo.getId());
                List<AmChatbotOptionsItems> amChatbotOptionsItems = amChatbotOptionsItemsService.list(itemsQueryWrapper);
                List<AmChatbotOptionsItemsVo> amChatbotOptionsItemsVos = amChatbotOptionsItems.stream().map(AmChatBotPositionItemsConvert.I::convertPositionOptionVoItems).collect(Collectors.toList());
                for (AmChatbotOptionsItemsVo amChatbotOptionsItem : amChatbotOptionsItemsVos) {
                    amChatbotOptionsItem.setRepeatContent(StringUtils.isNotBlank(amChatbotOptionsItem.getContent()) ? amChatbotOptionsItem.getContent().split("\\|").length : new ArrayList<>());
                }
                amChatbotOptionsVo.setItems(amChatbotOptionsItemsVos);
            }
            return ResultVO.success(amChatbotOptionsList);
        } catch (Exception e) {
            log.error("获取方案列表失败 adminId={},type={},keyWord={}", adminId, type, keyword, e);
        }
        return ResultVO.fail("系统异常,获取方案列表失败");
    }


    public ResultVO<AmChatbotOptionsVo> chatbotOptionsDetail(Integer id) {
        try {
            AmChatbotOptions amChatbotOptions = amChatbotOptionsService.getById(id);
            if (Objects.isNull(amChatbotOptions)) {
                return ResultVO.fail("方案不存在");
            }
            AmChatbotOptionsVo amChatbotOptionsVo = AmChatBotOptionConvert.I.convertOptionVo(amChatbotOptions);
            amChatbotOptionsVo.setRelativePositionNums(StringUtils.isNotBlank(amChatbotOptionsVo.getPositionIds()) ? amChatbotOptionsVo.getPositionIds().split(",").length : 0);
            LambdaQueryWrapper<AmChatbotOptionsItems> itemsQueryWrapper = new LambdaQueryWrapper<>();
            itemsQueryWrapper.eq(AmChatbotOptionsItems::getOptionId, amChatbotOptionsVo.getId());
            List<AmChatbotOptionsItems> amChatbotOptionsItems = amChatbotOptionsItemsService.list(itemsQueryWrapper);
            List<AmChatbotOptionsItemsVo> amChatbotOptionsItemsVos = amChatbotOptionsItems.stream().map(AmChatBotPositionItemsConvert.I::convertPositionOptionVoItems).collect(Collectors.toList());
            for (AmChatbotOptionsItemsVo amChatbotOptionsItem : amChatbotOptionsItemsVos) {
                amChatbotOptionsItem.setRepeatContent(StringUtils.isNotBlank(amChatbotOptionsItem.getContent()) ? amChatbotOptionsItem.getContent().split("\\|").length : new ArrayList<>());
            }
            amChatbotOptionsVo.setItems(amChatbotOptionsItemsVos);
            return ResultVO.success(amChatbotOptionsVo);
        } catch (Exception e) {
            log.error("获取方案列表详情 id={}", id, e);
        }
        return ResultVO.fail("系统异常,获取方案详情失败");
    }


    public ResultVO addOrUpdateChatbotOptions(AddOrUpdateAmChatbotOptions req, Long adminId) {
        try {
            if (Objects.nonNull(req.getId())) {
                AmChatbotOptions amChatbotOptions = amChatbotOptionsService.getById(req.getId());
                if (Objects.isNull(amChatbotOptions)) {
                    return ResultVO.fail("方案不存在");
                }
                amChatbotOptions.setName(req.getName());
                amChatbotOptions.setPositionIds(req.getPositionIds());
                amChatbotOptions.setType(req.getType());
                amChatbotOptions.setManAlias(req.getManAlias());
                amChatbotOptions.setWomanAlias(req.getWomanAlias());
                amChatbotOptions.setRechatDuration(req.getRechatDuration());
                amChatbotOptions.setAdminId(adminId);
                amChatbotOptions.setUpdateTime(LocalDateTime.now());
                boolean result = amChatbotOptionsService.updateById(amChatbotOptions);
                log.info("编辑方案 result={}", result);
                AmChatbotOptionsVo amChatbotOptionsVo = AmChatBotOptionConvert.I.convertOptionVo(amChatbotOptions);
                return ResultVO.success(amChatbotOptionsVo);
            } else {
                AmChatbotOptions amChatbotOptions = new AmChatbotOptions();
                amChatbotOptions.setName(req.getName());
                amChatbotOptions.setPositionIds(req.getPositionIds());
                amChatbotOptions.setType(req.getType());
                amChatbotOptions.setManAlias(req.getManAlias());
                amChatbotOptions.setWomanAlias(req.getWomanAlias());
                amChatbotOptions.setRechatDuration(req.getRechatDuration());
                amChatbotOptions.setAdminId(adminId);
                amChatbotOptions.setCreateTime(LocalDateTime.now());
                amChatbotOptions.setUpdateTime(LocalDateTime.now());
                boolean result = amChatbotOptionsService.save(amChatbotOptions);
                log.info("新增方案 result={}", result);
                AmChatbotOptionsVo amChatbotOptionsVo = AmChatBotOptionConvert.I.convertOptionVo(amChatbotOptions);
                return ResultVO.success(amChatbotOptionsVo);
            }
        } catch (Exception e) {
            log.error("编辑或新增方案 req={}", JSONObject.toJSONString(req), e);
        }
        return ResultVO.fail("系统异常,编辑或新增方案失败");
    }

    public ResultVO editItems(AddOrUpdateAmChatbotOptionsItems req) {
        try {
            if (Objects.nonNull(req.getId())) {
                AmChatbotOptionsItems amChatbotOptionsItems = amChatbotOptionsItemsService.getById(req.getId());
                if (Objects.isNull(amChatbotOptionsItems)) {
                    return ResultVO.fail("方案执行话术项目不存在");
                }
                amChatbotOptionsItems.setOptionId(req.getOptionId());
                amChatbotOptionsItems.setContent(req.getContent());
                amChatbotOptionsItems.setDayNum(req.getDayNum());
                amChatbotOptionsItems.setExecTime(req.getExecTime());
                amChatbotOptionsItems.setReplyType(req.getReplyType());
                amChatbotOptionsItems.setAiRole(req.getAiRole());
                amChatbotOptionsItems.setRepeatContent(req.getRepeatContent());
                amChatbotOptionsItems.setUpdateTime(LocalDateTime.now());
                amChatbotOptionsItemsService.updateById(amChatbotOptionsItems);
                return ResultVO.success(amChatbotOptionsItems);
            } else {
                AmChatbotOptionsItems amChatbotOptionsItems = new AmChatbotOptionsItems();
                amChatbotOptionsItems.setOptionId(req.getOptionId());
                amChatbotOptionsItems.setContent(req.getContent());
                amChatbotOptionsItems.setDayNum(req.getDayNum());
                amChatbotOptionsItems.setExecTime(req.getExecTime());
                amChatbotOptionsItems.setReplyType(req.getReplyType());
                amChatbotOptionsItems.setAiRole(req.getAiRole());
                amChatbotOptionsItems.setRepeatContent(req.getRepeatContent());
                amChatbotOptionsItems.setCreateTime(LocalDateTime.now());
                amChatbotOptionsItemsService.save(amChatbotOptionsItems);
                return ResultVO.success(amChatbotOptionsItems);
            }
        } catch (Exception e) {
            log.error("新增/编辑方案执行话术项目异常 req={}", JSONObject.toJSONString(req), e);
        }
        return ResultVO.fail("系统异常,新增/编辑方案执行话术项目失败");
    }

    public ResultVO getAiRoles() {
        try {

            return ResultVO.success(amChatbotOptionAiRoleService.list());
        } catch (Exception e) {
            log.error("获取AI角色列表", e);
        }
        return ResultVO.fail("系统异常,获取AI角色列表失败");
    }

    public ResultVO deleteOptions(Integer id) {
        try {

            LambdaQueryWrapper<AmChatbotPositionOption> itemsQueryWrapper = new LambdaQueryWrapper<>();
            itemsQueryWrapper.eq(AmChatbotPositionOption::getRechatOptionId, id);
            int count = amChatbotPositionOptionService.count();
            if (count > 0) {
                return ResultVO.fail("该复聊方案已被引用,无法删除");
            }
            boolean result = amChatbotOptionsService.removeById(id);
            return result ? ResultVO.success("删除成功") : ResultVO.fail("删除失败");
        } catch (Exception e) {
            log.error("删除失败 id={}", id, e);
        }
        return ResultVO.fail("系统异常,删除失败");
    }

    public ResultVO deleteOptionsItem(Integer id) {
        try {
            boolean result = amChatbotOptionsItemsService.removeById(id);
            return result ? ResultVO.success("删除成功") : ResultVO.fail("删除失败");
        } catch (Exception e) {
            log.error("删除失败 id={}", id, e);
        }
        return ResultVO.fail("系统异常,删除失败");
    }
}
