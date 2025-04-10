package com.open.ai.eros.ai.model.processor.billing;


import com.open.ai.eros.db.constants.RightsTypeEnum;
import com.open.ai.eros.db.mysql.pay.service.impl.UserRightsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 用户权益扣费
 */

@Slf4j
@Component
public class UserRightsBillingService {


    @Autowired
    private UserRightsServiceImpl userRightsService;


    /**
     * 次数权益计费
     *
     * @param userId
     * @param cost
     * @return
     */
    public boolean userNumberRightsBilling(Long userId, Long cost,String model) {

        return userRightsService.userRightsBilling(userId, RightsTypeEnum.TIME_BALANCE.getType(),model ,cost);
    }


    /**
     * 余额权益计费
     *
     * @param userId
     * @param cost
     * @return
     */
    public boolean userBalanceRightsBilling(Long userId, Long cost,String model) {

        return userRightsService.userRightsBilling(userId, RightsTypeEnum.TIME_NUMBER.getType(), model,1L);
    }


}
