package com.open.ai.eros.db.mysql.pay.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.open.ai.eros.common.constants.CommonConstant;
import com.open.ai.eros.common.exception.BizException;
import com.open.ai.eros.common.service.RedisClient;
import com.open.ai.eros.common.util.DateUtils;
import com.open.ai.eros.common.util.ObjectToHashMapConverter;
import com.open.ai.eros.db.constants.RightsCanAddEnum;
import com.open.ai.eros.db.constants.RightsTypeEnum;
import com.open.ai.eros.db.constants.UserRightsStatusEnum;
import com.open.ai.eros.db.mysql.pay.entity.CacheUserRightsVo;
import com.open.ai.eros.db.mysql.pay.entity.RightsSnapshot;
import com.open.ai.eros.db.mysql.pay.entity.UserRights;
import com.open.ai.eros.db.mysql.pay.mapper.UserRightsMapper;
import com.open.ai.eros.db.mysql.pay.service.IUserRightsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-20
 */
@Slf4j
@Service
public class UserRightsServiceImpl extends ServiceImpl<UserRightsMapper, UserRights> implements IUserRightsService {

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private RightsSnapshotServiceImpl rightsSnapshotService;


    /**
     * 获取前多少位 失效的用户权益
     * @return
     */
    public List<UserRights> getInactive(){
        return this.getBaseMapper().getInactive(new Date());
    }





    // TIME_NUMBER TIME_BALANCE

    /**
     * 检测用户当前权益是否可以进行聊天
     * 成功： 1
     * 失败：0
     */
    private String checkUserRightsCanChatLua =
            " local key = KEYS[1] " +
                    " local set_members = redis.call('SMEMBERS', key) " +
                    " if #set_members == 0 then " +
                    "    return 0 " +
                    " end" +
                    " local currentTime = tonumber(ARGV[1])" +
                    " local model = ARGV[2] " +
                    " for _, member in ipairs(set_members) do " +
                    "    local rightsKey = 'user:rights:' ..  member" +
                    "    local fields = redis.call('HMGET', rightsKey, 'effectiveEndTime', 'usedRightsValue', 'totalRightsValue','canUseModel' )" +
                    "    local effectiveEndTime = tonumber(fields[1]) " +
                    "    local usedRightsValue = tonumber(fields[2]) " +
                    "    local totalRightsValue = tonumber(fields[3]) " +
                    "    local canUseModel = fields[4] " +
                    "    if not effectiveEndTime or  currentTime > effectiveEndTime   then " +
                    "    elseif not usedRightsValue or not totalRightsValue then " +
                    "    elseif not canUseModel or not string.find(model, canUseModel) then " +
                    "    else " +
                    "        if totalRightsValue > usedRightsValue then " +
                    "            return 1 " +
                    "        end " +
                    "    end " +
                    "end " +
                    "return 0";


//    "local key = KEYS[1]   " +
//            "local set_members = redis.call('SMEMBERS', key) " +
//            "if next(set_members) == nil then " +
//            "return 0 " +
//            "end " +
//            "for _, member in ipairs(set_members) do " +
//            "  local flag = 1  "+
//            "  local rightsKey = 'user:rights:' ..  member   " +
//            "  local effectiveEndTime =  redis.call('HGET', rightsKey , 'effectiveEndTime')  " +
//            "  if effectiveEndTime == nil or effectiveEndTime > ARGV[1] then " +
//            "    flag = 0 " +
//            "  end " +
//            "  local usedRightsValue =  redis.call('HGET', rightsKey , 'usedRightsValue')  " +
//            "  if usedRightsValue == nil  then " +
//            "    flag = 0 " +
//            "  end " +
//            "  local totalRightsValue =  redis.call('HGET', rightsKey , 'totalRightsValue')  " +
//            "  if totalRightsValue == nil  then  " +
//            "    flag = 0 " +
//            "  end " +
//            "  if flag == 1  then  " +
//            "    if totalRightsValue > usedRightsValue   then  " +
//            "      return 1 " +
//            "    end " +
//            "  end " +
//            "end " +
//            "return 0 ";


    /**
     * 余额权益扣费
     * keys:  用户权益类型标识
     * value: 时间  所需扣费的cost
     * 成功：当前计费的权益的id 失败：小于等于 0
     */
    private String billingChatUserRightsLua =
            " local key = KEYS[1] " +
                    " local set_members = redis.call('SMEMBERS', key) " +
                    " if #set_members == 0 then " +
                    "    return -1 " +
                    " end" +
                    " local set_members = redis.call('SMEMBERS', key) " +
                    " local currentTime = tonumber(ARGV[1])" +
                    " local cost = tonumber(ARGV[2]) " +
                    " local model = ARGV[3] " +
                    " for _, member in ipairs(set_members) do " +
                    "    local rightsKey = 'user:rights:' ..  member" +
                    "    local fields = redis.call('HMGET', rightsKey, 'effectiveEndTime', 'usedRightsValue', 'totalRightsValue', 'canUseModel' )" +
                    "    local effectiveEndTime = tonumber(fields[1]) " +
                    "    local usedRightsValue = tonumber(fields[2]) " +
                    "    local totalRightsValue = tonumber(fields[3]) " +
                    "    local canUseModel = fields[4] " +
                    "    if not effectiveEndTime or currentTime > effectiveEndTime then " +
                    "    elseif not usedRightsValue or not totalRightsValue or usedRightsValue >= totalRightsValue  then " +
                    "    elseif not canUseModel or not string.find(model, canUseModel) then " +
                    "    else " +
                    "      local newUsedRightsValue = cost + usedRightsValue " +
                    "        if totalRightsValue > newUsedRightsValue then " +
                    "            redis.call('HSET', rightsKey, 'usedRightsValue', newUsedRightsValue) " +
                    "            return member  " +
                    "        end " +
                    "        redis.call('HSET', rightsKey, 'usedRightsValue', totalRightsValue) " +
                    "        return member  " +
                    "    end " +
                    " end " +
                    " return -1 ";

    /**
     * 检测当前用户的权益 是否可以进行聊天
     *
     * @param userId
     * @return
     */
    public boolean checkUserRightsCanChat(Long userId, String type, String model) {

        try {
            String key = String.format(CommonConstant.USER_RIGHTS_SET, userId, type);
            String time = String.valueOf(System.currentTimeMillis());
            Object result = redisClient.eval(checkUserRightsCanChatLua, 1, key, time, model);
            if (result != null && Integer.parseInt(result.toString()) == 1) {
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("checkUserRightsCanChat error userId={} ", userId, e);
            return false;
        }
    }


    /**
     * @param userId
     * @param type
     * @param cost
     * @return
     */
    public boolean userRightsBilling(Long userId, String type, String model, Long cost) {
        try {
            String key = String.format(CommonConstant.USER_RIGHTS_SET, userId, type);
            String time = String.valueOf(System.currentTimeMillis());

            // 返还的是 当前扣费的用户权益id
            Object result = redisClient.eval(billingChatUserRightsLua, 1, key, time, String.valueOf(cost), model);
            log.info("userRightsBilling  userId={},type={},cost={} result={}", userId, type, cost, result);
            if (result != null && Long.parseLong(result.toString()) > 0) {
                // 异步更新
                redisClient.sadd(CommonConstant.USER_RIGHTS_SYNC, result.toString());
                return true;
            }
        } catch (Exception e) {
            log.info("userRightsBilling  error  userId={},type={},cost={} ", userId, type, cost, e);
        }
        return false;
    }



    @Transactional(rollbackFor = Exception.class)
    public boolean addUserRights(Long rightId, Long userId) {
        RightsSnapshot lastRightsSnapshot = rightsSnapshotService.getLastRightsSnapshot(rightId);
        if (lastRightsSnapshot == null) {
            log.error("addUserRights error rightId={} ",rightId);
            throw new BizException("该权益不存在快照！");
        }
        return addUserRights(lastRightsSnapshot,userId);
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean addUserRightsBySnapshotId(Long snapshotRightId, Long userId) {
        RightsSnapshot lastRightsSnapshot = rightsSnapshotService.getCacheRightsSnapshot(snapshotRightId);
        if (lastRightsSnapshot == null) {
            log.error("addUserSnapshotRights error snapshotRightId={} ",snapshotRightId);
            throw new BizException("该权益不存在快照！");
        }
        return addUserRights(lastRightsSnapshot,userId);
    }


    /**
     * 新增用户权益
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean addUserRights(RightsSnapshot lastRightsSnapshot, Long userId) {

        UserRights userRights = new UserRights();
        // 当前权益的有效时间
        Long effectiveTime = lastRightsSnapshot.getEffectiveTime();
        // 生效日期
        Long effectiveStartTime = System.currentTimeMillis();

        Integer canAdd = lastRightsSnapshot.getCanAdd();
        if(canAdd!=null && canAdd.equals(RightsCanAddEnum.NOT_CAN_ADD.getStatus())){
            // 不可叠加
            // 时间延迟叠加操作
            UserRights lastUserRights = this.getLastUserRightsByType(userId, lastRightsSnapshot.getType());
            if(lastUserRights!=null){
                effectiveStartTime = DateUtils.convertLocalDateTimeToTimestamp(lastUserRights.getEffectiveEndTime());
            }
        }

        // 结束日期
        Long effectiveEndTime = effectiveStartTime + DateUtils.hourToTimeStamp(effectiveTime);
        userRights.setEffectiveEndTime(DateUtils.convertTimestampToLocalDateTime(effectiveEndTime));
        userRights.setEffectiveStartTime(DateUtils.convertTimestampToLocalDateTime(effectiveStartTime));
        userRights.setUserId(userId);
        userRights.setCreateTime(LocalDateTime.now());
        userRights.setRightsSnapshotId(lastRightsSnapshot.getId());
        userRights.setStatus(RightsTypeEnum.getInitStatus(lastRightsSnapshot.getType()).getStatus());
        userRights.setType(lastRightsSnapshot.getType());
        userRights.setUsedRightsValue(0L);
        userRights.setTotalRightsValue(lastRightsSnapshot.getRightsValue());
        userRights.setCanUseModel(lastRightsSnapshot.getCanUseModel());
        userRights.setUpdateTime(LocalDateTime.now());

        boolean saveResult = this.save(userRights);
        log.info("addUserRights saveResult  rightsSnapshotId={},userId={} saveResult={}", lastRightsSnapshot.getId(), userId, saveResult);
        if (!saveResult) {
            throw new BizException("该用户权益新增失败！");
        }
        // 权益类型  放入用户权益集合中
        String type = lastRightsSnapshot.getType();
        String key = String.format(CommonConstant.USER_RIGHTS_SET, userId, type);
        redisClient.sadd(key, String.valueOf(userRights.getId()));

        CacheUserRightsVo cacheUserRightsVo = buildCacheUserRightsVo(userRights);

        cacheUserRightsVo.setEffectiveEndTime(effectiveEndTime);
        cacheUserRightsVo.setEffectiveStartTime(effectiveStartTime);

        // 缓存具体的权益数值
        Map<String, String> objectToHashMap = ObjectToHashMapConverter.convertObjectToHashMap(cacheUserRightsVo);
        Long hset = redisClient.hset(String.format(CommonConstant.USER_RIGHTS_KEY, userRights.getId()), objectToHashMap);
        return hset!=null;
    }


    private CacheUserRightsVo buildCacheUserRightsVo(UserRights userRights) {
        CacheUserRightsVo cacheUserRightsVo = new CacheUserRightsVo();

        cacheUserRightsVo.setId(userRights.getId());
        cacheUserRightsVo.setType(userRights.getType());
        cacheUserRightsVo.setCreateTime(userRights.getCreateTime());
        cacheUserRightsVo.setTotalRightsValue(userRights.getTotalRightsValue());
        cacheUserRightsVo.setUsedRightsValue(userRights.getUsedRightsValue());
        cacheUserRightsVo.setCanUseModel(userRights.getCanUseModel());

        return cacheUserRightsVo;
    }


    /**
     * 让用户某一个权益失效
     *
     * @param id     用户权益表id
     * @param userId
     */
    public void invalidUserRights(Long id, Long userId) {

        UserRights rights = this.getById(id);
        if (rights == null) {
            throw new BizException("该用户权益不存在！");
        }
        if (!rights.getUserId().equals(userId)) {
            throw new BizException("用户不存在该权益！");
        }

        UserRights userRights = new UserRights();

        userRights.setId(id);
        userRights.setStatus(UserRightsStatusEnum.INACTIVE.getStatus());

        boolean updateResult = this.updateById(userRights);

        log.info("deleteUserRights id={},userId={} updateResult={}", id, userId, updateResult);
        String key = String.format(CommonConstant.USER_RIGHTS_SET, userId, rights.getType());
        redisClient.srem(key, String.valueOf(rights.getId()));
        redisClient.del(String.format(CommonConstant.USER_RIGHTS_KEY, userRights.getId()));
    }


    /**
     * 更新用户的权益中已使用的值
     */
    public void initUserRightsUsedRightsValue(Long rightsId, Long usedRightsValue) {
        String key = String.format(CommonConstant.USER_RIGHTS_KEY, rightsId);
        redisClient.hset(key, "usedRightsValue", String.valueOf(usedRightsValue));
        log.info("initUserRightsUsedRightsValue rightsId={} usedRightsValue={}",rightsId,usedRightsValue);
    }


    /**
     * 更新用户的权益中总值
     *
     * @param rightsId
     * @param totalRightsValue
     */
    public void addUserRightsTotalRightsValue(Long rightsId, Long totalRightsValue) {
        String key = String.format(CommonConstant.USER_RIGHTS_KEY, rightsId);
        Long hincr = redisClient.hincr(key, "totalRightsValue", totalRightsValue);
        log.info("addUserRightsTotalRightsValue rightsId={} totalRightsValue={} hincr={}", rightsId, totalRightsValue, hincr);
    }


    /**
     * 获取权益的快照
     *
     * @param userId
     * @return
     */
    public List<UserRights> getUserRights(Long userId,Integer status) {
        return this.baseMapper.getUserRights(userId,status);
    }


    /**
     * 分页获取生效中的权益
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public List<UserRights> getActiveUserRights(Date startTime,Date endTime,Integer pageNum, Integer pageSize) {

        return this.baseMapper.getActiveUserRights(startTime,endTime,(pageNum - 1) * pageSize, pageSize);
    }


    /**
     * 根据用户权益最新的type值
     *
     * @param userId
     * @param type
     * @return
     */
    public UserRights getLastUserRightsByType(Long userId, String type) {
        return this.getBaseMapper().getLastUserRightsByType(userId, type);
    }


}
