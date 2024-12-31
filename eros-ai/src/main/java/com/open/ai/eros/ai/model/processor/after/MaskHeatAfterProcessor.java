package com.open.ai.eros.ai.model.processor.after;


import com.open.ai.eros.ai.bean.vo.AITextChatVo;
import com.open.ai.eros.ai.manager.ThreadPoolManager;
import com.open.ai.eros.ai.model.bean.vo.ChatMessageResultVo;
import com.open.ai.eros.ai.model.processor.AIChatAfterProcessor;
import com.open.ai.eros.common.vo.CacheUserInfoVo;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.creator.manager.RankManager;
import com.open.ai.eros.db.mysql.creator.service.impl.MaskServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 更新面具的热度
 */
@Slf4j
@Order(40)
@Component
public class MaskHeatAfterProcessor implements AIChatAfterProcessor {


    @Autowired
    private MaskServiceImpl maskService;

    @Autowired
    private RankManager rankManager;

    @Override
    public ResultVO<Void> aiChatAfter(AITextChatVo chatReq, ChatMessageResultVo messageResultVo, CacheUserInfoVo userInfoVo) {
        try {
            Long maskId = chatReq.getMaskId();
            if(maskId!=null){
                ThreadPoolManager.maskPool.execute(()->{
                    maskService.updateMaskHeat(maskId);
                    rankManager.updateMaskRank(maskId);
                });
            }
        }catch (Exception e){
            log.error("MaskHeatProcessor aiChatAfter error maskId={}",chatReq.getMaskId(),e);
        }
        return ResultVO.success();
    }
}
