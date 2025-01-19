package com.open.hr.ai.manager;

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
import com.open.ai.eros.creator.manager.RankManager;
import com.open.ai.eros.db.constants.MaskStatusEnum;
import com.open.ai.eros.db.mysql.creator.entity.Mask;
import com.open.ai.eros.db.mysql.creator.service.impl.MaskServiceImpl;
import com.open.ai.eros.db.mysql.hr.entity.AmMask;
import com.open.ai.eros.db.mysql.hr.service.impl.AmMaskServiceImpl;
import com.open.ai.eros.db.mysql.user.entity.User;
import com.open.ai.eros.db.mysql.user.entity.UserFollowMask;
import com.open.ai.eros.db.mysql.user.service.impl.UserFollowMaskServiceImpl;
import com.open.ai.eros.db.mysql.user.service.impl.UserServiceImpl;
import com.open.hr.ai.bean.req.AmMaskAddReq;
import com.open.hr.ai.bean.req.AmMaskUpdateReq;
import com.open.hr.ai.bean.vo.AmMaskSearchReq;
import com.open.hr.ai.bean.vo.AmMaskTypeVo;
import com.open.hr.ai.bean.vo.AmMaskVo;
import com.open.hr.ai.constant.AmMaskTypeEnum;
import com.open.hr.ai.convert.AmMaskConvert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
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
public class AmMaskManager {




    @Autowired
    private AmMaskServiceImpl amMaskService;



    /**
     * 删除面具
     *
     * @param adminId
     * @param maskId
     * @return
     */
    public ResultVO deleteAmMask(Long adminId, Long maskId) {

        AmMask amMask = amMaskService.getById(maskId);
        if (amMask == null) {
            return ResultVO.fail("删除不存在！");
        }
        if (!amMask.getAdminId().equals(adminId) ) {
            return ResultVO.fail("没有该面具操作的权限");
        }
        amMask = new AmMask();
        amMask.setId(maskId);
        amMask.setStatus(MaskStatusEnum.DELETE.getStatus());
        amMask.setUpdateTime(LocalDateTime.now());
        boolean updated = amMaskService.updateById(amMask);
        log.info("deleteAmMask updated={}, mask={}", updated, JSONObject.toJSONString(amMask));
        return updated ? ResultVO.success() : ResultVO.fail("删除失败");
    }


    /**
     * 新增
     *
     * @param adminId
     * @param req
     * @return
     */
    public ResultVO addAmMask(Long adminId, AmMaskAddReq req) {
        AmMask amMask = AmMaskConvert.I.convertAddAmMaskReq(req);
        amMask.setCreateTime(LocalDateTime.now());
        amMask.setAdminId(adminId);
        amMask.setStatus(MaskStatusEnum.OK.getStatus());
        boolean save = amMaskService.save(amMask);
        if (!save) {
            log.info("addMask error mask={}", JSONObject.toJSONString(amMask));
        }
        return save ? ResultVO.success() : ResultVO.fail("新增失败");
    }


    /**
     * 修改
     *
     * @param adminId
     * @param req
     * @return
     */
    public ResultVO updateAmMask(Long adminId, AmMaskUpdateReq req) {

        AmMask amMask = amMaskService.getById(req.getId());
        if (amMask == null) {
            return ResultVO.fail("删除不存在！");
        }
        if ( !amMask.getAdminId().equals(adminId)) {
            return ResultVO.fail("没有该面具操作的权限");
        }

        amMask = AmMaskConvert.I.convertUpdateAmMaskReq(req);
        amMask.setStatus(req.getStatus());
        amMask.setCreateTime(LocalDateTime.now());
        amMask.setAdminId(adminId);
        boolean updated = amMaskService.updateById(amMask);
        if (!updated) {
            log.info("addMask error mask={}", JSONObject.toJSONString(amMask));
        }
        return updated ? ResultVO.success() : ResultVO.fail("修改失败");
    }


    /**
     * 搜索
     *
     * @param req
     * @return
     */
    public ResultVO<PageVO<AmMaskVo>> searchAmMask(AmMaskSearchReq req,Long adminId) {
        LambdaQueryWrapper<AmMask> queryWrapper = new LambdaQueryWrapper<>();
        String keywords = req.getKeywords();
        Integer status = req.getStatus();
        Integer pageNum = req.getPage();
        Integer pageSize = req.getPageSize();
        if (adminId != null) {
            queryWrapper.eq(AmMask::getAdminId, adminId);
        }
        if (StringUtils.isNoneEmpty(keywords)) {
            queryWrapper.like(AmMask::getName, keywords);
        }
        if (status != null) {
            queryWrapper.eq(AmMask::getStatus, status);
        } else {
            queryWrapper.eq(AmMask::getStatus, MaskStatusEnum.OK.getStatus());
        }
        Page<AmMask> page = new Page<>(pageNum, pageSize);
        Page<AmMask> amMaskPage = amMaskService.page(page, queryWrapper);
        List<AmMaskVo> amMaskVos = amMaskPage.getRecords().stream().map(AmMaskConvert.I::convertAmMaskVo).collect(Collectors.toList());
        return ResultVO.success(PageVO.build(amMaskPage.getTotal(), amMaskVos));
    }



    /**
     * 搜索
     * @return
     */
    public ResultVO<AmMaskVo> searchAmMaskById(Long id,Long adminId) {
        LambdaQueryWrapper<AmMask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AmMask::getAdminId, adminId);
        queryWrapper.eq(AmMask::getId, id);
        AmMask amMaskVo = amMaskService.getOne(queryWrapper, false);
        if (Objects.isNull(amMaskVo)) {
            return ResultVO.fail("没有该面具");
        }
        return ResultVO.success(AmMaskConvert.I.convertAmMaskVo(amMaskVo));
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





}
