package com.open.ai.eros.ai.model.processor.after;


import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.ai.bean.vo.AITextChatVo;
import com.open.ai.eros.ai.lang.chain.bean.TokenUsageVo;
import com.open.ai.eros.db.mysql.ai.entity.ModelConfigVo;
import com.open.ai.eros.ai.model.bean.vo.ChatMessageResultVo;
import com.open.ai.eros.ai.model.processor.AIChatAfterProcessor;
import com.open.ai.eros.ai.model.processor.billing.UserBalanceBillingService;
import com.open.ai.eros.ai.model.processor.billing.UserRightsBillingService;
import com.open.ai.eros.common.constants.CommonConstant;
import com.open.ai.eros.common.constants.ModelPriceEnum;
import com.open.ai.eros.common.vo.CacheUserInfoVo;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.constants.CostTypeEnum;
import com.open.ai.eros.db.constants.DividendEnum;
import com.open.ai.eros.db.mysql.ai.entity.UserAiConsumeRecord;
import com.open.ai.eros.db.mysql.ai.service.impl.UserAiConsumeRecordServiceImpl;
import com.open.ai.eros.user.bean.vo.UserCacheBalanceVo;
import com.open.ai.eros.user.manager.UserBalanceManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户计费的
 * 1.新增用户访问记录
 * 2.更新用户余额
 */
@Slf4j
@Order(10)
@Component
public class UserBillingAfterProcessor implements AIChatAfterProcessor {


    @Autowired
    private UserAiConsumeRecordServiceImpl userAiConsumeRecordService;

    @Autowired
    private UserBalanceManager userBalanceManager;

    @Autowired
    private UserBalanceBillingService userBalanceBilling;


    @Autowired
    private UserRightsBillingService userRightsBilling;

    @Override
    public ResultVO<Void> aiChatAfter(AITextChatVo chatReq, ChatMessageResultVo messageResultVo, CacheUserInfoVo userInfoVo) {
        Long userId = userInfoVo.getId();
        try {

            long promptTokenNumber = messageResultVo.getPromptTokenNumber();
            long relyTokenNumber = messageResultVo.getRelyTokenNumber();
            List<TokenUsageVo> tokenUsages = chatReq.getTokenUsages();
            for (TokenUsageVo tokenUsage : tokenUsages) {
                promptTokenNumber+=tokenUsage.getInputTokenCount();
                relyTokenNumber+=tokenUsage.getOutputTokenCount();
            }

            String model = messageResultVo.getModel();
            UserAiConsumeRecord record = new UserAiConsumeRecord();
            record.setUserId(userId);
            record.setCreateTime(LocalDateTime.now());
            record.setMaskId(chatReq.getMaskId());
            record.setPromptToken(promptTokenNumber);
            record.setRelyToken(relyTokenNumber);
            record.setModel(model);
            record.setChatId(chatReq.getChatId());
            record.setCostType(CostTypeEnum.BALANCE.getStatus());

            ModelPriceEnum modelPrice = ModelPriceEnum.getModelPrice(model);

            ModelConfigVo modelConfigVo = messageResultVo.getModelConfigVo();
            double modelConfigRate = 1;
            if(modelConfigVo!=null){
                modelConfigRate = modelConfigVo.getMultiple() > 0 ? modelConfigVo.getMultiple() : 1;
            }

            Long cost = (long) Math.ceil(((modelPrice.getInPrice() * promptTokenNumber + modelPrice.getOutPrice() * relyTokenNumber ) * modelConfigRate));
            record.setCost(getBillingBalance(cost));

            long extraCost = getShareBalance((long) (cost / 10 / modelConfigRate));
            messageResultVo.setCost(getBillingBalance(cost));

            boolean billing;

            billing = userRightsBilling.userNumberRightsBilling(userId, cost, model);
            if(billing){
                //次数
                record.setDividend(DividendEnum.no_dividend.getDividendType());
                record.setCost(0L);
                record.setCostType(CostTypeEnum.NUMBER.getStatus());
                boolean saveResult = userAiConsumeRecordService.save(record);
                if (!saveResult) {
                    log.error("aiChatAfter userNumberRightsBilling error record={}", JSONObject.toJSONString(record));
                }
                return ResultVO.success();
            }

            billing = userRightsBilling.userBalanceRightsBilling(userId, cost, model);
            if (billing) {
                boolean saveResult = userAiConsumeRecordService.save(record);
                if (!saveResult) {
                    log.error("aiChatAfter userBalanceRightsBilling error record={}", JSONObject.toJSONString(record));
                }
                return ResultVO.success();
            }

            // 用户余额积分
            billing = userBalanceBilling.billing(userId, cost);
            if (!billing) {
                return ResultVO.fail("系统繁忙！");
            }

            UserCacheBalanceVo userCacheBalanceVo = chatReq.getUserCacheBalanceVo();

            if (userCacheBalanceVo != null &&
                    (userCacheBalanceVo.getNoWithDrawable() > CommonConstant.userInitBalance)) {
                // 免费赠送额度不返利 + 系统返利的额度消费不返利
                if (chatReq.getMaskId() != null && chatReq.getBMaskVo() != null && !userInfoVo.getId().equals(chatReq.getBMaskVo().getUserId())) {
                    // 用户访问面具  面具本人使用 是没有返利的
                    record.setDividend(DividendEnum.dividend.getDividendType());
                    userBalanceManager.syncAddMaskCreatorUserBalance(chatReq.getBMaskVo().getUserId(), extraCost, userInfoVo.getUserName() + "使用：" + chatReq.getBMaskVo().getName());
                }
            }
            boolean saveResult = userAiConsumeRecordService.save(record);
            if (!saveResult) {
                log.error("aiChatAfter error record={}", JSONObject.toJSONString(record));
            }

        } catch (Exception e) {
            log.error("aiChatAfter error userId={}", userId, e);
        }
        return ResultVO.success();
    }


    private Long getBillingBalance(Long cost){
        if(cost>CommonConstant.MINI_BILLING_BALANCE){
            return cost;
        }
        return CommonConstant.MINI_BILLING_BALANCE;
    }


    private Long getShareBalance(Long extraCost){
        if(extraCost>CommonConstant.SHARE_MINI_BILLING_BALANCE){
            return extraCost;
        }
        return CommonConstant.SHARE_MINI_BILLING_BALANCE;
    }

}
