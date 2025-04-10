package com.open.ai.eros.pay.goods.job;

import com.open.ai.eros.common.constants.CommonConstant;
import com.open.ai.eros.common.service.RedisClient;
import com.open.ai.eros.common.util.DateUtils;
import com.open.ai.eros.db.constants.RightsRuleEnum;
import com.open.ai.eros.db.mysql.pay.entity.RightsSnapshot;
import com.open.ai.eros.db.mysql.pay.entity.UserRights;
import com.open.ai.eros.db.mysql.pay.service.impl.RightsSnapshotServiceImpl;
import com.open.ai.eros.db.mysql.pay.service.impl.UserRightsServiceImpl;
import com.open.ai.eros.pay.goods.bean.vo.RightsRuleVo;
import com.open.ai.eros.pay.goods.bean.vo.RightsVo;
import com.open.ai.eros.pay.goods.convert.RightsConvert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
@EnableScheduling
public class UserRightsRuleUpdateJob {

    @Autowired
    private UserRightsServiceImpl userRightsService;

    @Autowired
    private RightsSnapshotServiceImpl rightsSnapshotService;

    @Autowired
    private RedisClient redisClient;


    /**
     * 根据用户的权益中的规则进行更新  用户所使用的权益
     */
    //@Scheduled(cron = "0 0 1 * * ?")
    @Scheduled(fixedDelay = 1000 * 60 * 60 * 2)
    public void updateUserRightsByRule() {

        int pageNum = 1;
        int pageSize = 10;
        //获取前一天的开始时间和结束时间
        Date beforeOneDayDate = DateUtils.plusDays(new Date(), -1);
        Date endTime = DateUtils.endOfDay(beforeOneDayDate);
        Date startTime = DateUtils.startOfDay(beforeOneDayDate);

        while (true) {
            List<UserRights> activeUserRights = userRightsService.getActiveUserRights(startTime,endTime,pageNum, pageSize);
            if (CollectionUtils.isEmpty(activeUserRights)) {
                break;
            }
            List<Long> rightsIds = activeUserRights.stream().map(UserRights::getRightsSnapshotId).collect(Collectors.toList());
            List<RightsSnapshot> cacheRightsSnapshots = rightsSnapshotService.getCacheRightsSnapshot(rightsIds);

            Map<Long, RightsSnapshot> snapshotMap = cacheRightsSnapshots.stream().collect(Collectors.toMap(RightsSnapshot::getId, Function.identity()));

            for (UserRights activeUserRight : activeUserRights) {
                Long rightsSnapshotId = activeUserRight.getRightsSnapshotId();
                RightsSnapshot rightsSnapshot = snapshotMap.get(rightsSnapshotId);
                if (rightsSnapshot == null) {
                    continue;
                }
                RightsVo rightsVo = RightsConvert.I.convertRights(rightsSnapshot);
                RightsRuleVo rule = rightsVo.getRule();
                if (rule == null || CollectionUtils.isEmpty(rule.getRule())) {
                    continue;
                }
                Long everyUpdateNumber = rule.getEveryUpdateNumber();
                if (everyUpdateNumber == null || everyUpdateNumber < 0) {
                    continue;
                }
                List<String> ruleList = rule.getRule();
                for (String updateRule : ruleList) {
                    if (RightsRuleEnum.EVERY_DAY_ADD_TOTAL.getRule().equals(updateRule)) {
                        // 每天新增总量
                        userRightsService.addUserRightsTotalRightsValue(activeUserRight.getId(), everyUpdateNumber);
                        redisClient.sadd(CommonConstant.USER_RIGHTS_SYNC, String.valueOf(activeUserRight.getId()));
                    } else if (RightsRuleEnum.EVERY_DAY_INIT_USED.getRule().equals(updateRule)) {
                        // 每天更新已用量
                        userRightsService.initUserRightsUsedRightsValue(activeUserRight.getId(), everyUpdateNumber);
                        redisClient.sadd(CommonConstant.USER_RIGHTS_SYNC, String.valueOf(activeUserRight.getId()));
                    }
                }
            }

            pageNum++;
            if (activeUserRights.size() < pageSize) {
                break;
            }
        }

    }


}
