package com.open.ai.eros.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.user.bean.req.LoginLogQueryReq;
import com.open.ai.eros.user.bean.vo.LoginLogVo;
import com.open.ai.eros.user.config.UserBaseController;
import com.open.ai.eros.user.manager.LoginLogManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 登录日志---控制类
 *
 * @author Eros-AI
 * @since 2024-08-13
 */
@Slf4j
@Api(tags = "登录日志")
@RestController
public class LoginLogController extends UserBaseController {

    @Resource
    private LoginLogManager loginLogManager;


    /**
     * 保存或更新登录日志
     * @return
     */
    @VerifyUserToken
    @ApiOperation(value = "保存或更新登录日志", notes = "每次登陆都会更新一次信息")
    @PostMapping(value = "/loginLog/saveOrUpdateLoginLog")
    public ResultVO saveOrUpdateLoginLog() {
        // 1、先判断是否登录成功
        if (getUserId() == null) {
            return ResultVO.fail("请先登录");
        }

       // 2、保存或更新
        try {
            if (loginLogManager.saveOrUpdateLoginLog(getUserId()) == 1) {
                return ResultVO.success("保存登录日志成功");
            }
        } catch (Exception e) {
            log.error("保存登录日志失败", e);
        }
        return ResultVO.fail("保存登录日志失败");
    }

    /**
     * 分页查询
     * @param req 查询请求参数类
     * @return
     */
    @VerifyUserToken
    @ApiOperation(value = "分页查询", notes = "每次登陆都会更新一次信息")
    @PostMapping(value = "/loginLog/pageLoginLog")
    public ResultVO<IPage<LoginLogVo>> pageLoginLog(@RequestBody(required = false) @Valid LoginLogQueryReq req) {
        //// 1、获取角色
        //String role = getRole();
        //
        //// 2、根据角色，判断哪些信息展示给那些角色
        //if(!RoleEnum.SYSTEM.getRole().equals(role)){
        //    // 不是管理员，只能查看自己
        //    req.setUserId(getUserId());
        //}
        req.setUserId(getUserId());
        return loginLogManager.pageLoginLog(req);
    }

    /**
     * 批量删除登录ID信息
     * @param ids 删除的多ID
     *
     */
    @VerifyUserToken(role = RoleEnum.SYSTEM)
    @ApiOperation(value = "批量删除登录ID信息")
    @DeleteMapping(value = "/loginLog/deleteLoginLogByIds")
    public ResultVO deleteLoginLogByIds(@RequestParam("ids") List<Long> ids){
        return loginLogManager.deleteLoginLogByIds(ids);
    }

}

