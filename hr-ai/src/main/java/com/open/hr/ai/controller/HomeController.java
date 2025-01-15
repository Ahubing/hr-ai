package com.open.hr.ai.controller;

import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.AmPrompt;
import com.open.hr.ai.bean.req.AddOrUpdateAmPromptReq;
import com.open.hr.ai.bean.vo.AmHomeDataVo;
import com.open.hr.ai.config.HrAIBaseController;
import com.open.hr.ai.manager.HomeManager;
import com.open.hr.ai.manager.PromptManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @Date 2025/1/4 23:19
 */

@Api(tags = "Home 首页管理类")
@Slf4j
@RestController
public class HomeController extends HrAIBaseController {

    @Resource
    private HomeManager homeManager;

    @ApiOperation("招聘首页")
    @VerifyUserToken
    @GetMapping("home/index")
    public ResultVO<AmHomeDataVo>  getHomeDetail() {
        return homeManager.getHomeDetail(getUserId());
    }


}
