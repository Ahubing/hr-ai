package com.open.hr.ai.controller;

import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.AmChatbotGreetMessages;
import com.open.hr.ai.bean.vo.AmChatMessageVo;
import com.open.hr.ai.config.HrAIBaseController;
import com.open.hr.ai.manager.AmMessageManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Date 2025/1/4 23:19
 */

@Api(tags = "Message管理类")
@Slf4j
@RestController
public class MessageController extends HrAIBaseController {


    @Resource
    private AmMessageManager amMessageManager;


    @ApiOperation("获取消息聊天列表")
    @VerifyUserToken
    @GetMapping("conversation/list")
    public ResultVO<List<AmChatMessageVo>> promptList(@RequestParam(value = "recruiterId", required = true) String recruiterId, @RequestParam(value = "userId", required = true)String userId) {
        return amMessageManager.queryChatMessage(recruiterId,userId);
    }
}
