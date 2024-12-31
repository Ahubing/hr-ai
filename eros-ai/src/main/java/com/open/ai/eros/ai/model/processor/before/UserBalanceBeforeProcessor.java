package com.open.ai.eros.ai.model.processor.before;

import com.open.ai.eros.ai.bean.vo.AITextChatVo;
import com.open.ai.eros.ai.model.processor.AIChatBeforeProcessor;
import com.open.ai.eros.ai.model.processor.billing.UserRightsCheckService;
import com.open.ai.eros.ai.util.SendMessageUtil;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.user.bean.vo.UserCacheBalanceVo;
import com.open.ai.eros.user.manager.UserBalanceManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @类名：UserBalanceBeforeProcessor
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/11 22:14
 */

/**
 * 用户的余额拦截
 */
@Order(20)
@Component
@Slf4j
public class UserBalanceBeforeProcessor implements AIChatBeforeProcessor {


    @Autowired
    private UserBalanceManager userBalanceManager;


    @Autowired
    private UserRightsCheckService userRightsCheckService;


    @Override
    public ResultVO<Void> aiChatBefore(AITextChatVo chatReq, Long userId, SendMessageUtil sendMessageUtil) {

        ResultVO<Void> aiChatBefore = userRightsCheckService.rightsCheck(userId,chatReq.getModel());
        if(aiChatBefore.isOk()){
            // 如果权益可以访问 直接放行 不需要检测余额了
            return aiChatBefore;
        }

        UserCacheBalanceVo userCacheBalanceVo = userBalanceManager.canAIChat(userId);

        if(userCacheBalanceVo==null){
            return ResultVO.fail("获取用户余额失败！");
        }
        chatReq.setUserCacheBalanceVo(userCacheBalanceVo);
        Long noWithDrawable = userCacheBalanceVo.getNoWithDrawable();
        if(noWithDrawable!=null && noWithDrawable>0){
            return ResultVO.success();
        }

        Long withDrawable = userCacheBalanceVo.getWithDrawable();
        if(withDrawable!=null && withDrawable>0){
            return ResultVO.success();
        }

        return ResultVO.fail("用户账号余额不足");

    }

}
