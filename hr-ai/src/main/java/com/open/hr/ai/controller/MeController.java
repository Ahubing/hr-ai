package com.open.hr.ai.controller;

import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.hr.ai.bean.req.*;
import com.open.hr.ai.bean.vo.MiniUniUserExchangeCodeVo;
import com.open.hr.ai.bean.vo.MiniUniUserVo;
import com.open.hr.ai.config.HrAIBaseController;
import com.open.hr.ai.manager.MeManager;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Objects;

/**
 * @Date 2025/1/6 23:33
 */
@RestController
@Slf4j
public class MeController extends HrAIBaseController {

    @Resource
    private MeManager meManager;

    @ApiOperation("获取用户列表")
    @VerifyUserToken
    @PostMapping("/user/list")
    public ResultVO<PageVO<MiniUniUserVo>> getUserList(@RequestBody @Valid SearchUserReq req) {
        return meManager.getUserList(req, getUserId());
    }

    @ApiOperation("获取角色详情")
    @VerifyUserToken
    @GetMapping("/user/getInfo")
    public ResultVO<MiniUniUserVo> getUserDetail(@RequestParam(value = "id", required = true) Integer id) {
        return meManager.getUserDetail(id);
    }

    @ApiOperation("删除用户")
    @VerifyUserToken
    @GetMapping("/user/delete")
    public ResultVO deleteUser(@RequestParam(value = "id", required = true) Integer id) {
        return meManager.deleteUserById(id);
    }


    @ApiOperation("添加用户")
    @VerifyUserToken
    @PostMapping("/user/addUser")
    public ResultVO addUser(@RequestBody @Valid AddMiniUserReq req) {
        if (Objects.isNull(req)) {
            return ResultVO.fail("参数不能为空");
        }
        return meManager.addMiniUser(req, getUserId());
    }


    @ApiOperation("修改用户")
    @VerifyUserToken
    @PostMapping("/user/modify")
    public ResultVO modifyUser(@RequestBody @Valid UpdateMiniUserReq req) {
        if (Objects.isNull(req)) {
            return ResultVO.fail("参数不能为空");
        }
        return meManager.updateMiniUser(req, getUserId());
    }


    @ApiOperation("添加兑换卷")
    @VerifyUserToken
    @PostMapping("/user/createExchangeCode")
    public ResultVO createExchangeCode(@RequestBody @Valid AddExchangeCodeReq req) {
        if (Objects.isNull(req)) {
            return ResultVO.fail("参数不能为空");
        }
        return meManager.createExchangeCode(req, getUserId());
    }

    @ApiOperation("兑换码列表")
    @VerifyUserToken
    @GetMapping("/user/exchange_list")
    public ResultVO<PageVO<MiniUniUserExchangeCodeVo>> exchangeList(@RequestParam(value = "code", required = false) String code, @RequestParam(value = "page", required = false) Integer page,
                                                                    @RequestParam(value = "size", required = false) Integer size, @RequestParam(value = "status", required = false) Integer status) {
        if (Objects.isNull(page)) {
            page = 1;
        }
        if (Objects.isNull(size)) {
            size = 10;
        }
        return meManager.getExchangeCodeList(code, status, page, size, getUserId());
    }


}
