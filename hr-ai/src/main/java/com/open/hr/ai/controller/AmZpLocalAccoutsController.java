package com.open.hr.ai.controller;


import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.AmZpLocalAccouts;
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
@RestController
@RequestMapping("/amZpLocalAccouts")
public class AmZpLocalAccoutsController {


    @GetMapping("/chatbot/get_local_accounts")
    public ResultVO<AmZpLocalAccouts> list(){
        return null;
    }


}

