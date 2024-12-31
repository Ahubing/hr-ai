package com.open.ai.eros.user.manager;

import com.open.ai.eros.common.constants.CommonConstant;
import com.open.ai.eros.common.service.RedisClient;
import com.open.ai.eros.common.util.BalanceFormatUtil;
import com.open.ai.eros.common.util.DateUtils;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.constants.UserRightsStatusEnum;
import com.open.ai.eros.db.mysql.pay.entity.RightsSnapshot;
import com.open.ai.eros.db.mysql.pay.entity.UserRights;
import com.open.ai.eros.db.mysql.pay.service.impl.RightsSnapshotServiceImpl;
import com.open.ai.eros.db.mysql.pay.service.impl.UserRightsServiceImpl;
import com.open.ai.eros.user.bean.vo.UserRightsVo;
import com.open.ai.eros.user.convert.RightsUserConvert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @类名：UserRightsManager
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/20 22:57
 */

@Slf4j
@Component
public class UserRightsManager {


    @Autowired
    private RightsSnapshotServiceImpl rightsSnapshotService;


    @Autowired
    private UserRightsServiceImpl userRightsService;


    @Autowired
    private RedisClient redisClient;



    /**
     * 获取 我的权益
     *
     * @param userId
     * @return
     */
    public ResultVO<List<UserRightsVo>> getMyRights(Long userId,Integer status) {
        List<UserRights> userRightsList = userRightsService.getUserRights(userId,status);
        if (CollectionUtils.isEmpty(userRightsList)) {
            return ResultVO.success();
        }

        List<Long> rightsSnapshotIds = userRightsList.stream().map(UserRights::getRightsSnapshotId).collect(Collectors.toList());
        List<RightsSnapshot> cacheRightsSnapshots = rightsSnapshotService.getCacheRightsSnapshot(rightsSnapshotIds);
        Map<Long, Optional<RightsSnapshot>> rightsMap = cacheRightsSnapshots.stream().collect(Collectors.toMap(RightsSnapshot::getId, Optional::of, (k1, k2) -> k1));


        List<UserRightsVo> rightsVos = userRightsList.stream().map(e -> {
            UserRightsVo userRightsVo = RightsUserConvert.I.convertUserRightsVo(e);

            Optional<RightsSnapshot> rightsSnapshot = rightsMap.get(e.getRightsSnapshotId());
            if(rightsSnapshot.isPresent()){
                userRightsVo.setName(rightsSnapshot.get().getName());
            }else{
                userRightsVo.setName("unknown");
            }
            userRightsVo.setStatusDesc(UserRightsStatusEnum.getDesc(e.getStatus()));
            userRightsVo.setExpireTime(DateUtils.getExpireTimeDesc(System.currentTimeMillis(), DateUtils.convertLocalDateTimeToTimestamp(e.getEffectiveEndTime())));
            if(e.getType().contains("BALANCE")){
                userRightsVo.setTotalRightsValue(BalanceFormatUtil.getUserBalance(e.getTotalRightsValue()));
                userRightsVo.setUsedRightsValue(BalanceFormatUtil.getUserBalance(e.getUsedRightsValue()));
            }
            return userRightsVo;
        }).collect(Collectors.toList());

        return ResultVO.success(rightsVos);
    }


    /**
     * 删除用户每个权益
     *
     * @param userId
     * @param rightsId
     * @return
     */
    public ResultVO deleteRights(Long userId,Long rightsId){
        userRightsService.invalidUserRights(rightsId,userId);
        return ResultVO.success();
    }



    /**
     * 刷新权益
     *
     * @param userId
     * @return
     */
    public ResultVO refreshRights(Long userId){
        List<UserRights> userRightsList = userRightsService.getUserRights(userId,UserRightsStatusEnum.ACTIVE.getStatus());
        if (CollectionUtils.isEmpty(userRightsList)) {
            return ResultVO.success();
        }
        for (UserRights userRights : userRightsList) {
            // 权益类型  放入用户权益集合中
            String type = userRights.getType();
            String key = String.format(CommonConstant.USER_RIGHTS_SET, userId, type);
            Boolean sismember = redisClient.sismember(key, String.valueOf(userRights.getId()));
            if(sismember==null || !sismember){
                userRightsService.addUserRights(userRights.getId(),userId);
                continue;
            }
            String userRightsKey = String.format(CommonConstant.USER_RIGHTS_KEY, userRights.getId());
            Boolean exists = redisClient.exists(userRightsKey);
            if(exists==null || !exists){
                userRightsService.addUserRights(userRights.getId(),userId);
            }
        }
        return ResultVO.success();
    }








    public ResultVO addUserRights(Long userId,Long rightsId){

        boolean addUserRightsResult = userRightsService.addUserRights(rightsId, userId);
        return addUserRightsResult?ResultVO.success():ResultVO.fail("新增用户权益失败！");

    }



}
