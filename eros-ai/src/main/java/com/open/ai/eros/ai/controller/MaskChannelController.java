package com.open.ai.eros.ai.controller;

import com.open.ai.eros.ai.config.AIBaseController;
import com.open.ai.eros.ai.util.MaskChannelSseEmitterUtils;
import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.exception.SeeConnectException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @类名：SSEController
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/1 16:12
 */
@Api(tags = "面具频道")
@RestController
public class MaskChannelController extends AIBaseController {


    @Autowired
    private MaskChannelSseEmitterUtils maskChannelSseEmitterUtils;

    /**
     * sse 订阅消息
     */
    @ApiOperation("订阅面具频道消息")
    @VerifyUserToken
    @GetMapping(path = "/sub/channel/mask", produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public SseEmitter sub(@RequestParam(value = "maskId") Long maskId) throws IOException {
        SseEmitter sseEmitter = maskChannelSseEmitterUtils.getConnect(maskId, getUserId());
        if (sseEmitter != null) {
            return sseEmitter;
        }
        throw new SeeConnectException("连接失败！");
    }

    /**
     * sse 发布消息
     */
    @ApiOperation("发布消息")
    @VerifyUserToken
    @GetMapping(path = "/channel/push")
    public void push(@RequestParam("maskId") Long maskId, @RequestParam("content") String content) throws IOException {
        maskChannelSseEmitterUtils.batchSendMessage(maskId, content, getUserId());
    }


    @ApiOperation("断开连接")
    @VerifyUserToken
    @GetMapping(path = "/channel/breakConnect")
    public void breakConnect(@RequestParam("maskId") Long maskId, HttpServletRequest request, HttpServletResponse response) {
        request.startAsync();
        maskChannelSseEmitterUtils.removeUser(maskId, getUserId());
    }


}
