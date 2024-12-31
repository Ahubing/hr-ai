package com.open.ai.eros.ai.model.processor.billing;


import com.open.ai.eros.user.manager.UserBalanceManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 用户余额扣费
 */

@Slf4j
@Component
public class UserBalanceBillingService {


    @Autowired
    private UserBalanceManager userBalanceManager;


    /**
     * 余额计费
     *
     * @param userId
     * @param cost
     * @return
     */
    public boolean billing(Long userId, Long cost) {
        //计费
        Integer result = userBalanceManager.costUserBalance(userId, cost);

        if(result>0){
            //同步用户余额
            userBalanceManager.syncUserBalance(userId,result);
            return true;
        }
        return false;
    }
}
