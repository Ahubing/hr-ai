package com.open.ai.eros.ai.model.processor.billing;


import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.constants.RightsTypeEnum;
import com.open.ai.eros.db.mysql.pay.service.impl.UserRightsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 用户的权益 判断 是否可以访问
 * 兜底链条
 */
@Component
public class UserRightsCheckService {


    @Autowired
    private UserRightsServiceImpl userRightsService;


    /**
     * 检测该用户拥有的权益是否可以访问
     *
     * @param userId
     * @return
     */
    public ResultVO<Void> rightsCheck(Long userId, String model) {
        // 是否可以访问 次数权益
        boolean canChat = userRightsService.checkUserRightsCanChat(userId, RightsTypeEnum.TIME_NUMBER.getType(), model);
        if(canChat){
            return ResultVO.success();
        }
        // 是否可以访问 余额权益
        canChat = userRightsService.checkUserRightsCanChat(userId, RightsTypeEnum.TIME_BALANCE.getType(), model);
        return canChat?ResultVO.success():ResultVO.fail("没有相应权益支持该操作！");
    }
}
