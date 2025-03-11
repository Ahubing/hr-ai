package com.open.hr.ai.manager;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.ai.eros.ai.bean.vo.IcConfigVo;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.constants.MaskStatusEnum;
import com.open.ai.eros.db.mysql.hr.entity.AmChatbotPositionOption;
import com.open.ai.eros.db.mysql.hr.entity.AmNewMask;
import com.open.ai.eros.db.mysql.hr.entity.IcConfig;
import com.open.ai.eros.db.mysql.hr.service.impl.AmChatbotPositionOptionServiceImpl;
import com.open.ai.eros.db.mysql.hr.service.impl.AmNewMaskServiceImpl;
import com.open.ai.eros.db.mysql.hr.service.impl.IcConfigServiceImpl;
import com.open.hr.ai.bean.req.AmNewMaskAddReq;
import com.open.hr.ai.bean.req.AmNewMaskUpdateReq;
import com.open.hr.ai.bean.req.IcConfigUpdateReq;
import com.open.hr.ai.bean.vo.AmMaskSearchReq;
import com.open.hr.ai.bean.vo.AmMaskTypeVo;
import com.open.hr.ai.bean.vo.AmNewMaskVo;
import com.open.hr.ai.constant.AmMaskTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
public class AmNewMaskManager {

    @Autowired
    private AmNewMaskServiceImpl amNewMaskService;


    @Autowired
    private AmChatbotPositionOptionServiceImpl amChatbotPositionOptionService;

    @Autowired
    private IcConfigServiceImpl icConfigService;

    /**
     * 删除面具
     *
     * @param adminId
     * @param maskId
     * @return
     */
    public ResultVO deleteAmNewMask(Long adminId, Long maskId) {

        AmNewMask amNewMask = amNewMaskService.getById(maskId);
        if (amNewMask == null) {
            return ResultVO.fail("删除不存在！");
        }
        if (!amNewMask.getAdminId().equals(adminId) ) {
            return ResultVO.fail("没有该面具操作的权限");
        }

        //判断是否该面具绑定了chatbot
        LambdaQueryWrapper<AmChatbotPositionOption> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AmChatbotPositionOption::getAmMaskId, maskId);
        int count = amChatbotPositionOptionService.count(queryWrapper);
        if (count > 0) {
            return ResultVO.fail("该面具已经绑定了职位，不能删除");
        }

        amNewMask.setStatus(MaskStatusEnum.DELETE.getStatus());
        amNewMask.setUpdateTime(LocalDateTime.now());
        boolean updated = amNewMaskService.updateById(amNewMask);
        log.info("deleteAmNewMask updated={}, mask={}", updated, JSONObject.toJSONString(amNewMask));
        return updated ? ResultVO.success() : ResultVO.fail("删除失败");
    }


    /**
     * 新增
     *
     * @param adminId
     * @param req
     * @return
     */
    public ResultVO addAmNewMask(Long adminId, AmNewMaskAddReq req) {
        AmNewMask amNewMask = new AmNewMask();
        amNewMask.setAiRequestParam(JSONObject.toJSONString(req));
        amNewMask.setContentsNumber(req.getContentsNumber());
        amNewMask.setIntro(req.getIntro());
        amNewMask.setName(req.getName());
        amNewMask.setTemplateModel(String.join(",", req.getTemplateModel()));
        amNewMask.setType(req.getType());
        amNewMask.setCreateTime(LocalDateTime.now());
        amNewMask.setAdminId(adminId);
        amNewMask.setStatus(MaskStatusEnum.OK.getStatus());
        amNewMask.setSkipHolidayStatus(req.getSkipHolidayStatus());
        amNewMask.setInterviewType(req.getInterviewType());
        amNewMask.setGreetMessage(req.getGreetMessage());
        boolean save = amNewMaskService.save(amNewMask);
        if (!save) {
            log.info("addNewMask error mask={}", JSONObject.toJSONString(amNewMask));
            return ResultVO.fail("新增失败");
        }
        //保存配置数据
        List<IcConfigUpdateReq> configReqs = req.getIcConfigUpdateReqs();
        List<IcConfig> configs = configReqs.stream().map(icConfigAddReq -> {
            IcConfig icConfig = new IcConfig();
            BeanUtils.copyProperties(icConfigAddReq, icConfig);
            icConfig.setMaskId(amNewMask.getId());
            return icConfig;
        }).collect(Collectors.toList());
        icConfigService.saveBatch(configs);
        return ResultVO.success();
    }


    /**
     * 修改
     *
     * @param adminId
     * @param req
     * @return
     */
    public ResultVO updateAmNewMask(Long adminId, AmNewMaskUpdateReq req) {

        AmNewMask amNewMask = amNewMaskService.getById(req.getId());
        if (amNewMask == null) {
            return ResultVO.fail("面具不存在不存在！");
        }
        if (!amNewMask.getAdminId().equals(adminId)) {
            return ResultVO.fail("没有该面具操作的权限");
        }

        amNewMask.setAiRequestParam(JSONObject.toJSONString(req));
        amNewMask.setContentsNumber(req.getContentsNumber());
        amNewMask.setIntro(req.getIntro());
        amNewMask.setName(req.getName());
        amNewMask.setTemplateModel(String.join(",", req.getTemplateModel()));
        amNewMask.setType(req.getType());
        amNewMask.setStatus(req.getStatus());
        amNewMask.setUpdateTime(LocalDateTime.now());
        amNewMask.setAdminId(adminId);
        amNewMask.setGreetMessage(req.getGreetMessage());
        boolean updated = amNewMaskService.updateById(amNewMask);
        if (!updated) {
            log.info("updateNewMask error mask={}", JSONObject.toJSONString(amNewMask));
            return ResultVO.fail("修改失败");
        }
        processConfigs(amNewMask.getId(), req.getIcConfigUpdateReqs());
        return ResultVO.success();
    }

    private void processConfigs(Long maskId, List<IcConfigUpdateReq> configReqs) {
        //删除全部数据
        icConfigService.remove(new LambdaQueryWrapper<IcConfig>().eq(IcConfig::getMaskId, maskId));

        //全量添加
        if(CollectionUtil.isNotEmpty(configReqs)){
            List<IcConfig> configs = configReqs.stream().map(icConfigUpdateReq -> {
                IcConfig icConfig = new IcConfig();
                BeanUtils.copyProperties(icConfigUpdateReq, icConfig);
                icConfig.setMaskId(maskId);
                return icConfig;
            }).collect(Collectors.toList());
            icConfigService.saveBatch(configs);
        }
    }


    /**
     * 搜索
     *
     * @param req
     * @return
     */
    public ResultVO<PageVO<AmNewMaskVo>> searchAmNewMask(AmMaskSearchReq req, Long adminId) {
        LambdaQueryWrapper<AmNewMask> queryWrapper = new LambdaQueryWrapper<>();
        String keywords = req.getKeywords();
        Integer status = req.getStatus();
        Integer pageNum = req.getPage();
        Integer pageSize = req.getPageSize();
        if (adminId != null) {
            queryWrapper.eq(AmNewMask::getAdminId, adminId);
        }
        if (StringUtils.isNoneEmpty(keywords)) {
            queryWrapper.like(AmNewMask::getName, keywords);
        }
        if (status != null) {
            queryWrapper.eq(AmNewMask::getStatus, status);
        } else {
            queryWrapper.eq(AmNewMask::getStatus, MaskStatusEnum.OK.getStatus());
        }
        Page<AmNewMask> page = new Page<>(pageNum, pageSize);
        Page<AmNewMask> amNewMaskPage = amNewMaskService.page(page, queryWrapper);
        List<AmNewMaskVo> amNewMaskVos = amNewMaskPage.getRecords().stream().map(this::convertAmNewMaskVo).collect(Collectors.toList());
        return ResultVO.success(PageVO.build(amNewMaskPage.getTotal(), amNewMaskVos));
    }



    /**
     * 搜索
     * @return
     */
    public ResultVO<AmNewMaskVo> searchAmMaskById(Long id,Long adminId) {
        LambdaQueryWrapper<AmNewMask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AmNewMask::getAdminId, adminId);
        queryWrapper.eq(AmNewMask::getId, id);
        AmNewMask amNewMask = amNewMaskService.getOne(queryWrapper, false);
        if (Objects.isNull(amNewMask)) {
            return ResultVO.fail("没有该面具");
        }
        return ResultVO.success(convertAmNewMaskVo(amNewMask));
    }

    /**
     * 搜索
     * @return
     */
    public ResultVO<List<AmMaskTypeVo>> getAmMaskType() {
        List<AmMaskTypeVo> maskTypeVos = Arrays.stream(AmMaskTypeEnum.values()).map(e -> {
            AmMaskTypeVo maskTypeVo = new AmMaskTypeVo();
            maskTypeVo.setType(e.getType());
            maskTypeVo.setDesc(e.getDesc());
            return maskTypeVo;
        }).collect(Collectors.toList());
        return ResultVO.success(maskTypeVos);
    }




    public AmNewMaskVo convertAmNewMaskVo(AmNewMask amNewMask){
        AmNewMaskVo amNewMaskVo = new AmNewMaskVo();
        amNewMaskVo.setId(amNewMask.getId());
        amNewMaskVo.setName(amNewMask.getName());
        amNewMaskVo.setType(amNewMask.getType());
        amNewMaskVo.setTemplateModel(Arrays.asList(amNewMask.getTemplateModel().split(",")));
        amNewMaskVo.setIntro(amNewMask.getIntro());
        amNewMaskVo.setAdminId(amNewMask.getAdminId());
        amNewMaskVo.setContentsNumber(amNewMask.getContentsNumber());
        amNewMaskVo.setStatus(amNewMask.getStatus());
        List<IcConfig> configList = icConfigService
                .list(new LambdaQueryWrapper<IcConfig>().eq(IcConfig::getMaskId, amNewMask.getId()));
        if(CollectionUtil.isNotEmpty(configList)){
            List<IcConfigVo> configVos = configList.stream().map(icConfig -> {
                IcConfigVo configVo = new IcConfigVo();
                BeanUtils.copyProperties(icConfig, configVo);
                return configVo;
            }).collect(Collectors.toList());
            amNewMaskVo.setIcConfigVos(configVos);
        }
        String aiRequestParam = amNewMask.getAiRequestParam();
        if (StringUtils.isNotEmpty(aiRequestParam)) {
            AmNewMaskAddReq amNewMaskAddReq = JSONObject.parseObject(aiRequestParam, AmNewMaskAddReq.class);
            amNewMaskVo.setCompanyInfo(amNewMaskAddReq.getCompanyInfo());
            amNewMaskVo.setDifferentiatedAdvantagesSwitch(amNewMaskAddReq.getDifferentiatedAdvantagesSwitch());
            amNewMaskVo.setInterviewAddress(amNewMaskAddReq.getInterviewAddress());
            amNewMaskVo.setOpenInterviewSwitch(amNewMaskAddReq.getOpenInterviewSwitch());
            amNewMaskVo.setStyle(amNewMaskAddReq.getStyle());
            amNewMaskVo.setOtherRecruitmentInfo(amNewMaskAddReq.getOtherRecruitmentInfo());
            amNewMaskVo.setOtherArgue(amNewMaskAddReq.getOtherArgue());
            amNewMaskVo.setCommunicationScript(amNewMaskAddReq.getCommunicationScript());
            amNewMaskVo.setFilterWords(amNewMaskAddReq.getFilterWords());
            amNewMaskVo.setOpenExchangeWeChat(amNewMaskAddReq.getOpenExchangeWeChat());
            amNewMaskVo.setOpenExchangePhone(amNewMaskAddReq.getOpenExchangePhone());
            amNewMaskVo.setExampleDialogues(amNewMaskAddReq.getExampleDialogues());
            amNewMaskVo.setCode(amNewMaskAddReq.getCode());
        }
        return amNewMaskVo;

    }





}
