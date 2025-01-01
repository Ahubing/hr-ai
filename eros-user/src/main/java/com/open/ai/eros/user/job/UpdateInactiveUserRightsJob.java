//package com.open.ai.eros.user.job;
//
//import com.open.ai.eros.db.mysql.pay.entity.UserRights;
//import com.open.ai.eros.db.mysql.pay.service.impl.UserRightsServiceImpl;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.collections.CollectionUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
///**
// *
// * 更新已经过期的用户权益
// *
// * @类名：UpdateInactiveUserRightsJob
// * @项目名：web-eros-ai
// * @description：
// * @创建人：陈臣
// * @创建时间：2024/8/23 22:32
// */
//
//@Component
//@Slf4j
//@EnableScheduling
//public class UpdateInactiveUserRightsJob {
//
//
//    @Autowired
//    private UserRightsServiceImpl userRightsService;
//
//
//    @Scheduled(fixedDelay = 1000)
//    public void updateInactiveUserRights(){
//        while (true){
//            List<UserRights> userRightsList = userRightsService.getInactive();
//            if(CollectionUtils.isEmpty(userRightsList)){
//                break;
//            }
//            for (UserRights userRights : userRightsList) {
//                userRightsService.invalidUserRights(userRights.getId(),userRights.getUserId());
//            }
//        }
//    }
//
//
//}
