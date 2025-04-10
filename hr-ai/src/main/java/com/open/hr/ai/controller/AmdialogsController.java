package com.open.hr.ai.controller;


import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.hr.ai.manager.AmdialogsManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@Api(tags = "示例对话")
@RestController
public class AmdialogsController {

    @Resource
    private AmdialogsManager amdialogsManager;

    /*@ApiOperation("新增示例")
    @VerifyUserToken
    @GetMapping("/dialogs/add")
    public ResultVO deleteAmMask(@RequestParam(value = "id", required = true) Long id) {

    }*/
}
