package com.open.ai.eros.social.controller;


import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.social.bean.vo.LastMessagePushMessageVo;
import com.open.ai.eros.social.bean.req.PushMessageAddReq;
import com.open.ai.eros.social.bean.req.PushMessageUpdateReq;
import com.open.ai.eros.social.config.SocialBaseController;
import com.open.ai.eros.social.manager.PushMessageManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Api(tags = "消息推送控制类")
@Slf4j
@RestController
public class PushMessageController extends SocialBaseController {


    @Autowired
    private PushMessageManager pushMessageManager;


    /**
     * 获取用户最新的一条消息
     *
     * @return
     */
    @ApiOperation(value = "获取最新消息")
    @VerifyUserToken
    @GetMapping("/push/lastMessage")
    public ResultVO<LastMessagePushMessageVo> getUserBestMessage(@RequestParam("source") String source) {
        Long userId = getUserId();
        return pushMessageManager.getLastMessage(userId,source);
    }


    /**
     * 新增用户最新的消息
     *
     * @return
     */
    @ApiOperation(value = "新增消息")
    @VerifyUserToken(role = {RoleEnum.SYSTEM})
    @PostMapping("/push/add/lastMessage")
    public ResultVO addUserBestMessage(@RequestBody @Valid PushMessageAddReq req) {
        return pushMessageManager.addMessage(getAccount(), req);
    }



    /**
     * 修改用户最新的消息
     *
     * @return
     */
    @ApiOperation(value = "修改消息")
    @VerifyUserToken(role = {RoleEnum.SYSTEM})
    @PostMapping("/push/update/lastMessage")
    public ResultVO updateUserBestMessage(@RequestBody @Valid PushMessageUpdateReq req) {
        return pushMessageManager.updateMessage(getAccount(), req);
    }



    /**
     * 删除消息
     *
     * @return
     */
    @ApiOperation(value = "删除消息")
    @VerifyUserToken(role = {RoleEnum.SYSTEM})
    @GetMapping("/push/delete/message")
    public ResultVO deleteMessage( @RequestParam("id") Long id) {
        return pushMessageManager.deleteMessage(id);
    }





    /**
     * 消息标记已读
     *
     * @return
     */
    @ApiOperation(value = "标记消息已读")
    @VerifyUserToken
    @GetMapping("/push/read/lastMessage")
    public ResultVO<LastMessagePushMessageVo> readMessage(@RequestParam("id") Long id) {
        Long userId = getUserId();
        return pushMessageManager.readMessage(id,userId);
    }


}
