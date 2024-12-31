package com.open.ai.eros.social.manager;


import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.constants.MessageStatusEnum;
import com.open.ai.eros.db.mysql.social.entity.PushMessage;
import com.open.ai.eros.db.mysql.social.service.impl.PushMessageServiceImpl;
import com.open.ai.eros.social.bean.vo.LastMessagePushMessageVo;
import com.open.ai.eros.social.bean.req.PushMessageAddReq;
import com.open.ai.eros.social.bean.req.PushMessageUpdateReq;
import com.open.ai.eros.social.convert.PushMessageConvert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class PushMessageManager {


    @Autowired
    private PushMessageServiceImpl pushMessageService;


    /**
     * 标识已读
     * @param id
     * @param userId
     * @return
     */
    public ResultVO readMessage(Long id,Long userId){
        PushMessage pushMessage = new PushMessage();
        pushMessage.setId(id);
        pushMessage.setUserId(userId);
        pushMessage.setStatus(MessageStatusEnum.read.name());
        boolean updatedResult = pushMessageService.updateById(pushMessage);
        log.info("readMessage id={},userId={} updatedResult={}",id,userId,updatedResult);
        return updatedResult?ResultVO.success():ResultVO.fail("标记已读失败！");
    }



    /**
     * 获取用户最新一条未读消息
     *
     * @param userId
     * @return
     */
    public ResultVO<LastMessagePushMessageVo> getLastMessage(Long userId,String source) {
        PushMessage userLastMessage = pushMessageService.getUserLastMessage(userId,source);
        return ResultVO.success(PushMessageConvert.I.convertLastMessagePushMessageVo(userLastMessage));
    }

    /**
     * 新增消息
     *
     * @param account
     * @param req
     * @return
     */
    public ResultVO addMessage(String account, PushMessageAddReq req) {
        PushMessage pushMessage = PushMessageConvert.I.convertPushMessage(req);
        pushMessage.setCreateAccount(account);
        pushMessage.setCreateTime(LocalDateTime.now());
        pushMessage.setStatus(MessageStatusEnum.unRead.name());
        boolean saveResult = pushMessageService.save(pushMessage);
        log.info("addMessage account={},req={},saveResult={} ", account, JSONObject.toJSONString(req), saveResult);
        return saveResult ? ResultVO.success() : ResultVO.fail("新增消息失败");
    }


    /**
     * 修改消息
     *
     * @param account
     * @param req
     * @return
     */
    public ResultVO updateMessage(String account, PushMessageUpdateReq req) {
        PushMessage pushMessage = PushMessageConvert.I.convertPushMessage(req);
        pushMessage.setStatus(MessageStatusEnum.unRead.name());
        boolean upateResult = pushMessageService.updateById(pushMessage);
        log.info("updateMessage account={},req={},saveResult={} ", account, JSONObject.toJSONString(req), upateResult);
        return upateResult ? ResultVO.success() : ResultVO.fail("修改消息失败");
    }

    /**
     * 删除消息
     *
     * @param id
     * @return
     */
    public ResultVO deleteMessage(Long id) {
        boolean removeResult = pushMessageService.removeById(id);
        log.info("deleteMessage id={},saveResult={} ", id, removeResult);
        return removeResult ? ResultVO.success() : ResultVO.fail("删除消息失败");
    }


}
