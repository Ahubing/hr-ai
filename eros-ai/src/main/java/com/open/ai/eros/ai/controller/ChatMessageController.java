package com.open.ai.eros.ai.controller;

import com.open.ai.eros.ai.bean.req.AddChatMessageReq;
import com.open.ai.eros.ai.bean.req.ConversionChatMessageReq;
import com.open.ai.eros.ai.bean.req.UpdateChatMessageReq;
import com.open.ai.eros.ai.bean.vo.ConversionChatMessageVo;
import com.open.ai.eros.ai.config.AIBaseController;
import com.open.ai.eros.ai.manager.ChatMessageManager;
import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.vo.CacheUserInfoVo;
import com.open.ai.eros.common.vo.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;
import java.util.Set;

/**
 * @类名：ChatMessageController
 * @项目名：web-eros-ai
 * @description：
 * @创建人：Administrator
 * @创建时间：2024/8/4 14:26
 */
@Api(tags = "AI会话消息控制类")
@RestController
@Slf4j
public class ChatMessageController extends AIBaseController {


    @Autowired
    private ChatMessageManager chatMessageManager;


    /**
     * 获取用户的对话的里面的消息
     *
     * @param req
     * @return
     */
    @ApiOperation(value = "会话消息列表")
    @VerifyUserToken
    @GetMapping("/chat/message/list")
    public ResultVO<ConversionChatMessageVo> getConversionChatMessage(@Valid ConversionChatMessageReq req) {
        if (req.getPageNum() == null) {
            req.setPageNum(1);
        }
        if (req.getPageSize() == null) {
            req.setPageSize(10);
        }

        return chatMessageManager.getConversionChatMessage(getUserId(), req);
    }


    /**
     * 根据消息id删除
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "删除消息")
    @VerifyUserToken
    @GetMapping("/chat/message/delete")
    public ResultVO<Set<Long>> deleteChatMessage(@RequestParam(value = "id") Long id) {
        return chatMessageManager.deleteMessage(getUserId(), id);
    }


    /**
     * 根据消息id删除
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "回溯消息")
    @VerifyUserToken
    @GetMapping("/chat/message/back")
    public ResultVO backChatMessage(@RequestParam(value = "id") Long id) {
        return chatMessageManager.backMessage(getUserId(), id);
    }




    /**
     * 根据消息id修改
     *
     * @return
     */
    @ApiOperation(value = "修改消息")
    @VerifyUserToken
    @PostMapping("/chat/message/update")
    public ResultVO updateChatMessage(@RequestBody @Valid UpdateChatMessageReq req) {
        return chatMessageManager.updateMessage(getUserId(), req);
    }


    /**
     * 发消息
     *
     * @return
     */
    @ApiOperation(value = "发消息")
    @VerifyUserToken
    @PostMapping({"/chat/message/send", "/chat/message/save"})
    public ResultVO<Map<String, String>> saveChatMessage(@RequestBody @Valid AddChatMessageReq req, HttpServletResponse response) {
        CacheUserInfoVo cacheUserInfo = getCacheUserInfo();
        return chatMessageManager.addMessage(cacheUserInfo, req, response);
    }


    /**
     * 发消息
     *
     * @return
     */
    @ApiOperation(value = "发消息")
    @VerifyUserToken
    @PostMapping({"/chat/message/send/temp",})
    public void saveTempChatMessage(@RequestBody @Valid AddChatMessageReq req, HttpServletRequest request, HttpServletResponse response) {
        CacheUserInfoVo cacheUserInfo = getCacheUserInfo();
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        chatMessageManager.addMessage(cacheUserInfo, req, response);
    }


}
