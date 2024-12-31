package com.open.ai.eros.user.job;


import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.common.constants.CommonConstant;
import com.open.ai.eros.common.service.RedisClient;
import com.open.ai.eros.common.util.ObjectToHashMapConverter;
import com.open.ai.eros.db.mysql.pay.entity.CacheUserRightsVo;
import com.open.ai.eros.db.mysql.pay.entity.UserRights;
import com.open.ai.eros.db.mysql.pay.service.impl.UserRightsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 用户同步权益
 */
@Component
@Slf4j
@EnableScheduling
public class SyncUserRightsJob {


    @Autowired
    private RedisClient redisClient;


    @Autowired
    private UserRightsServiceImpl userRightsService;


    @Scheduled(fixedDelay = 10000)
    public void syncUserNumberRights() {
        int size = 10;
        String key = CommonConstant.USER_RIGHTS_SYNC;
        while (true) {
            List<UserRights> userRightsList = null;
            try {
                Set<String> spop = redisClient.spop(key, size);
                if (CollectionUtils.isEmpty(spop)) {
                    break;
                }
                userRightsList = new ArrayList<>(size);
                for (String rightsId : spop) {
                    String rightsKey = String.format(CommonConstant.USER_RIGHTS_KEY, rightsId);
                    Map<String, String> stringStringMap = redisClient.hgetAll(rightsKey);
                    if (stringStringMap.isEmpty()) {
                        continue;
                    }
                    CacheUserRightsVo cacheUserRightsVo = new CacheUserRightsVo();
                    ObjectToHashMapConverter.setValuesToObject(stringStringMap, cacheUserRightsVo);

                    UserRights userRights = new UserRights();
                    userRights.setId(Long.parseLong(rightsId));
                    userRights.setTotalRightsValue(cacheUserRightsVo.getTotalRightsValue());
                    userRights.setUsedRightsValue(cacheUserRightsVo.getUsedRightsValue());
                    userRights.setUpdateTime(LocalDateTime.now());
                    userRightsList.add(userRights);
                }
                boolean updateBatchResult = userRightsService.updateBatchById(userRightsList);
                log.info("updateUserRights updateBatchResult={}", updateBatchResult);
            } catch (Exception e) {
                log.error("updateUserRights userRightsList={}", JSONObject.toJSONString(userRightsList), e);
            }
        }
    }


}
