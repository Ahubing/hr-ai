package com.open.hr.ai.manager;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.MiniUniUser;
import com.open.ai.eros.db.mysql.hr.entity.MiniUniUserExchangeCode;
import com.open.ai.eros.db.mysql.hr.service.impl.MiniUniUserExchangeCodeServiceImpl;
import com.open.ai.eros.db.mysql.hr.service.impl.MiniUniUserServiceImpl;
import com.open.ai.eros.db.privacy.utils.CryptoUtil;
import com.open.hr.ai.bean.req.*;
import com.open.hr.ai.bean.vo.MiniUniUserExchangeCodeVo;
import com.open.hr.ai.bean.vo.MiniUniUserVo;
import com.open.hr.ai.convert.MiniUniUserConvert;
import com.open.hr.ai.convert.MiniUniUserExChangeCodeConvert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 逻辑按照php处理的
 *
 * @Date 2025/1/6 20:00
 */
@Slf4j
@Component
public class MeManager {

    @Resource
    private MiniUniUserServiceImpl miniUniUserService;

    @Resource
    private MiniUniUserExchangeCodeServiceImpl miniUniUserExchangeCodeService;


    public ResultVO<PageVO<MiniUniUserVo>> getUserList(SearchUserReq req,Long adminId) {

        try {
        Page<MiniUniUser> page = new Page<>(req.getPage(), req.getSize());
        LambdaQueryWrapper<MiniUniUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MiniUniUser::getAdminId, adminId);
        if (StringUtils.isNotBlank(req.getKeyword())) {
            queryWrapper.and(i -> i.like(MiniUniUser::getUsername, req.getKeyword())
                    .or().like(MiniUniUser::getName, req.getKeyword())
                    .or().like(MiniUniUser::getMobile, req.getKeyword())
                    .or().like(MiniUniUser::getWechat, req.getKeyword()));
        }

        Page<MiniUniUser> miniUniUserPage = miniUniUserService.page(page, queryWrapper);
            List<MiniUniUserVo> miniUniUserVos = miniUniUserPage.getRecords().stream().map(MiniUniUserConvert.I::converAmMiniUniUserVo).collect(Collectors.toList());
            return ResultVO.success(PageVO.build(miniUniUserPage.getTotal(),miniUniUserVos));
        } catch (Exception e) {
            log.error("获取用户列表失败 req={}", JSONObject.toJSONString(req), e);
        }
        return ResultVO.fail("获取用户列表异常");
    }

    /**
     * 用户数据详情
     * @param id
     * @return
     */
    public ResultVO<MiniUniUserVo> getUserDetail(Integer id) {
        try {
            MiniUniUser miniUniUser = miniUniUserService.getById(id);
            if (Objects.isNull(miniUniUser)) {
                return ResultVO.fail("用户不存在");
            }

            MiniUniUserVo miniUniUserVo = MiniUniUserConvert.I.converAmMiniUniUserVo(miniUniUser);
            return ResultVO.success(miniUniUserVo);
        } catch (Exception e) {
            log.error("获取用户详情异常 id={}", id, e);
        }
        return ResultVO.fail("获取用户详情异常");
    }


    /**
     * 添加用户
     * @param req
     * @param adminId
     * @return
     */
    public ResultVO addMiniUser(AddMiniUserReq req, Long adminId) {
        try {
            MiniUniUser miniUniUser = new MiniUniUser();
            miniUniUser.setAdminId(adminId);
            miniUniUser.setUsername(req.getUserName());
            miniUniUser.setName(req.getName());
            miniUniUser.setPassword(Base64.getEncoder().encodeToString(CryptoUtil.encryptMD5(req.getPassWord().getBytes("UTF-8"))));
            miniUniUser.setMobile(req.getMobile());
            miniUniUser.setWechat(req.getWechat());
            miniUniUser.setStatus(0);
            miniUniUser.setUpdateTime(LocalDateTime.now());
            miniUniUser.setCreateTime(LocalDateTime.now());
            miniUniUser.setExpiredTime(LocalDateTime.now());
            boolean result = miniUniUserService.save(miniUniUser);
            log.info("新增用户结果 result={}", result);
            return ResultVO.success();
        } catch (Exception e) {
            log.error("新增用户结果 req={}", JSONObject.toJSONString(req), e);
        }
        return ResultVO.fail("新增用户结果异常");
    }



    /**
     * 添加用户
     * @param req
     * @param adminId
     * @return
     */
    public ResultVO updateMiniUser(UpdateMiniUserReq req, Long adminId) {
        try {
            MiniUniUser miniUniUser = miniUniUserService.getById(req.getId());
            if (Objects.isNull(miniUniUser)) {
                return ResultVO.fail("用户不存在");
            }
            if (StringUtils.isNotBlank(req.getUserName())) {
                miniUniUser.setUsername(req.getUserName());
            }
            if (StringUtils.isNotBlank(req.getName())) {
                miniUniUser.setName(req.getName());
            }
            if (StringUtils.isNotBlank(req.getPassWord())) {
                miniUniUser.setPassword(Base64.getEncoder().encodeToString(CryptoUtil.encryptMD5(req.getPassWord().getBytes("UTF-8"))));
            }
            if (StringUtils.isNotBlank(req.getMobile())) {
                miniUniUser.setMobile(req.getMobile());
            }
            if (StringUtils.isNotBlank(req.getWechat())) {
                miniUniUser.setWechat(req.getWechat());
            }
            miniUniUser.setUpdateTime(LocalDateTime.now());
            boolean result = miniUniUserService.updateById(miniUniUser);
            log.info("更新用户结果 result={}", result);
            return ResultVO.success();
        } catch (Exception e) {
            log.error("更新用户结果 req={}", JSONObject.toJSONString(req), e);
        }
        return ResultVO.fail("更新用户结果异常");
    }

    public ResultVO deleteUserById(Integer id) {
        try {
            MiniUniUser miniUniUser = miniUniUserService.getById(id);
            if (Objects.isNull(miniUniUser)) {
                return ResultVO.fail("用户不存在");
            }
            boolean result = miniUniUserService.removeById(id);
            log.info("删除用户结果 id={} result={}", id, result);
            return result ? ResultVO.success() : ResultVO.fail("删除用户失败");
        } catch (Exception e) {
            log.error("删除用户 id={}", id, e);
        }
        return ResultVO.fail("删除用户异常");
    }


    public ResultVO createExchangeCode(AddExchangeCodeReq req,Long adminId) {
        try {
            if (req.getCnt() < 1 ){
                return ResultVO.fail("兑换码数量不能小于0");
            }
            if (req.getMonths() < 1 ){
                return ResultVO.fail("兑换码有效期不能小于0");
            }
            if (req.getEnd_date().isBefore(LocalDateTime.now())){
                return ResultVO.fail("兑换码有效期不能小于当前时间");
            }

            MiniUniUserExchangeCode miniUniUserExchangeCode = new MiniUniUserExchangeCode();
            miniUniUserExchangeCode.setAdminId(adminId);
            miniUniUserExchangeCode.setMonths(req.getMonths());
            miniUniUserExchangeCode.setEndDate(req.getEnd_date());
            miniUniUserExchangeCode.setCreateTime(LocalDateTime.now());
            for (Integer i = 0; i < req.getCnt(); i++) {
                String code = UUID.randomUUID().toString().replace("-", "");
                miniUniUserExchangeCode.setCode(code);
                boolean result = miniUniUserExchangeCodeService.save(miniUniUserExchangeCode);
                log.info("新增兑换码结果 id={} result={}",miniUniUserExchangeCode.getId(), result);
            }
            return ResultVO.success();
        } catch (Exception e) {
            log.error("新增兑换码失败 req={}", JSONObject.toJSONString(req), e);
        }
        return ResultVO.fail("新增兑换码异常");
    }


    public ResultVO<PageVO<MiniUniUserExchangeCodeVo>> getExchangeCodeList(String code,Integer status,Integer pageSize,Integer size,Long adminId) {
        try {
            Page<MiniUniUserExchangeCode> page = new Page<>(pageSize, size);
            LambdaQueryWrapper<MiniUniUserExchangeCode> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(MiniUniUserExchangeCode::getAdminId, adminId);
            if (StringUtils.isNotBlank(code)) {
                queryWrapper.like(MiniUniUserExchangeCode::getCode, code);
            }
            if (Objects.nonNull(status)) {
                queryWrapper.eq(MiniUniUserExchangeCode::getStatus,status);
            }
            Page<MiniUniUserExchangeCode> miniUniUserExchangeCodePage = miniUniUserExchangeCodeService.page(page, queryWrapper);
            List<MiniUniUserExchangeCodeVo> uniUserExchangeCodeVos = miniUniUserExchangeCodePage.getRecords().stream().map(MiniUniUserExChangeCodeConvert.I::convertExchangeCodeVo).collect(Collectors.toList());
            return ResultVO.success(PageVO.build(miniUniUserExchangeCodePage.getTotal(),uniUserExchangeCodeVos));
        } catch (Exception e) {
            log.error("获取兑换码列表失败 ", e);
        }
        return ResultVO.fail("获取兑换码列表异常");
    }

}
