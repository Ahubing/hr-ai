package com.open.hr.ai.controller;


import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.AmZpLocalAccouts;
import com.open.hr.ai.manager.AmZpLocalAccoutsManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 招聘本地账户 前端控制器
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Api(tags = "招聘本地账户前端控制器")
@RestController
@RequestMapping("/amZpLocalAccouts")
public class AmZpLocalAccoutsController {


    @Autowired
    private AmZpLocalAccoutsManager amZpLocalAccoutsManager;


    @ApiOperation("1获取本地登录账号列表")
    @GetMapping("/chatbot/get_local_accounts")
    public ResultVO<AmZpLocalAccouts> list(){
        return null;
    }


}

