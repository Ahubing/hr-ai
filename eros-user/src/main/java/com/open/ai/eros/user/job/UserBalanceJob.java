package com.open.ai.eros.user.job;

import com.open.ai.eros.common.constants.CommonConstant;
import com.open.ai.eros.common.service.RedisClient;
import com.open.ai.eros.common.util.ObjectToHashMapConverter;
import com.open.ai.eros.db.mysql.user.service.impl.UserBalanceServiceImpl;
import com.open.ai.eros.user.bean.vo.UserCacheBalanceVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @类名：UserBalanceJob
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/18 17:07
 */

@Component
@Slf4j
@EnableScheduling
public class UserBalanceJob {


    @Autowired
    private UserBalanceServiceImpl userBalanceService;

    @Autowired
    private RedisClient redisClient;



    @Scheduled(fixedDelay = 1000)
    public void syncUserBalance() {
        List<Integer> list = Arrays.asList(CommonConstant.nonWithdrawableBalanceType, CommonConstant.drawableBalanceType);
        for (Integer type : list) {
            sync(type);
        }
    }

    public void sync(Integer result){
        String key = String.format(CommonConstant.USER_BALANCE_SYNC,result);

        while (true){
            Set<String> spop = redisClient.spop(key, 100);
            if(CollectionUtils.isEmpty(spop)){
                break;
            }
            log.info("开始同步余额 key={}",key);
            for (String userId : spop) {
                try {
                    String userKey = String.format(CommonConstant.USER_BALANCE_KEY, userId);
                    Map<String, String> stringStringMap = redisClient.hgetAll(userKey);
                    if(stringStringMap.isEmpty()){
                        continue;
                    }
                    UserCacheBalanceVo userCacheBalanceVo = new UserCacheBalanceVo();
                    ObjectToHashMapConverter.setValuesToObject(stringStringMap,userCacheBalanceVo);
                    if(result.equals(CommonConstant.nonWithdrawableBalanceType)){
                        userBalanceService.updateNON_WITHDRAWABLEUserBalance(Long.parseLong(userId),userCacheBalanceVo.getNoWithDrawable());
                    }
                    if(result.equals(CommonConstant.drawableBalanceType)){
                        userBalanceService.updateWITHDRAWABLEUserBalance(Long.parseLong(userId),userCacheBalanceVo.getWithDrawable());
                    }
                }catch (Exception e){
                    log.error("sync 同步用户账号余额失败 user={}",userId,e);
                }

            }
        }
    }

}
