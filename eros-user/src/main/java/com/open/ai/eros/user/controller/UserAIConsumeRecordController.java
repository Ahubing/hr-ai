package com.open.ai.eros.user.controller;

import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.user.bean.req.UserAIConsumeRecordQueryReq;
import com.open.ai.eros.user.bean.vo.UserAIConsumeRecordQueryResultVo;
import com.open.ai.eros.user.config.UserBaseController;
import com.open.ai.eros.user.manager.UserAIConsumeRecordManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @类名：UserAIConsumeRecordController
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/18 13:02
 */

@Api(tags = "ai消费记录控制类")
@Slf4j
@RestController
public class UserAIConsumeRecordController extends UserBaseController {


    @Autowired
    private UserAIConsumeRecordManager userAIConsumeRecordManager;


    @ApiOperation("搜索ai消费记录")
    @VerifyUserToken
    @GetMapping("/ai/consume/record")
    public ResultVO<UserAIConsumeRecordQueryResultVo> searchRecord(@Valid UserAIConsumeRecordQueryReq req){
        return userAIConsumeRecordManager.searchRecord(getUserId(),req);
    }





}
