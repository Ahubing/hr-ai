package com.open.ai.eros.user.manager;

import com.open.ai.eros.common.constants.CommonConstant;
import com.open.ai.eros.common.exception.BizException;
import com.open.ai.eros.common.service.RedisClient;
import com.open.ai.eros.common.util.BalanceFormatUtil;
import com.open.ai.eros.common.util.ObjectToHashMapConverter;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.constants.UserBalanceTypeEnum;
import com.open.ai.eros.db.mysql.user.entity.User;
import com.open.ai.eros.db.mysql.user.entity.UserBalance;
import com.open.ai.eros.db.mysql.user.service.impl.UserBalanceServiceImpl;
import com.open.ai.eros.db.mysql.user.service.impl.UserServiceImpl;
import com.open.ai.eros.user.bean.req.UpdateUserBalanceReq;
import com.open.ai.eros.user.bean.vo.UserBalanceVo;
import com.open.ai.eros.user.bean.vo.UserCacheBalanceVo;
import com.open.ai.eros.user.constants.UserBalanceRecordEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @类名：UserBalanceManager
 * @项目名：web-eros-ai
 * @description：
 * @创建人：Administrator
 * @创建时间：2024/8/6 0:20
 */

@Component
@Slf4j
public class UserBalanceManager {


    @Autowired
    private UserBalanceServiceImpl userBalancesService;

    @Autowired
    private RedisClient redisClient;


    @Autowired
    private UserBalanceRecordManager userBalanceRecordManager;

    @Autowired
    private UserServiceImpl userService;


    @Value("${invitationCode.balance}")
    private Long invitationCodeBalance;

    /**
     * 初始化用户基础余额
     *
     * @param userId
     * @return
     */
    @Transactional
    public boolean initUserBalance(Long userId){
        boolean result = userBalancesService.addUserInitUserBalances(userId);
        log.info(" initUserBalance result={} userId={} ",result,userId);
        return result;
    }


    /**
     * 获取用户余额信息
     *
     * @param userId
     * @return
     */
    public UserBalanceVo getUserBalance(Long userId){

        UserBalanceVo userBalanceVo = new UserBalanceVo();
        UserCacheBalanceVo userCacheBalanceVo = new UserCacheBalanceVo();
        String key = String.format(CommonConstant.USER_BALANCE_KEY,userId);
        Map<String, String> stringStringMap = redisClient.hgetAll(key);
        if(!org.springframework.util.CollectionUtils.isEmpty(stringStringMap)){
            ObjectToHashMapConverter.setValuesToObject(stringStringMap,userCacheBalanceVo);
            userBalanceVo.setNoWithDrawable(BalanceFormatUtil.getUserBalance(userCacheBalanceVo.getNoWithDrawable()));
            userBalanceVo.setWithDrawable(BalanceFormatUtil.getUserBalance(userCacheBalanceVo.getWithDrawable()));
            return userBalanceVo;
        }

        List<UserBalance> userBalancesList = userBalancesService.getUserBalance(userId);
        if(CollectionUtils.isEmpty(userBalancesList)){
            throw new BizException("该用户不存在余额信息");
        }
        for (UserBalance userBalances : userBalancesList) {
            String type = userBalances.getType();
            if(UserBalanceTypeEnum.NON_WITHDRAWABLE.name().equals(type)){
                userCacheBalanceVo.setNoWithDrawable(userBalances.getBalance());
                userBalanceVo.setNoWithDrawable(BalanceFormatUtil.getUserBalance(userBalances.getBalance()));
            }else if(UserBalanceTypeEnum.WITHDRAWABLE.name().equals(type)){
                userCacheBalanceVo.setWithDrawable(userBalances.getBalance());
                userBalanceVo.setWithDrawable(BalanceFormatUtil.getUserBalance(userBalances.getBalance()));
            }
        }
        stringStringMap = ObjectToHashMapConverter.convertObjectToHashMap(userCacheBalanceVo);
        // 将用户信息以哈希表的形式存储到Redis中
        redisClient.hset(key, stringStringMap);
        return userBalanceVo;
    }


    /**
     * 余额是否可以进行 ai聊天
     * @param userId
     * @return
     */
    public UserCacheBalanceVo canAIChat(Long userId){
        UserCacheBalanceVo userCacheBalanceVo = new UserCacheBalanceVo();
        String key = String.format(CommonConstant.USER_BALANCE_KEY,userId);
        Map<String, String> stringStringMap = redisClient.hgetAll(key);
        if(org.springframework.util.CollectionUtils.isEmpty(stringStringMap)){
            return null;
        }

        ObjectToHashMapConverter.setValuesToObject(stringStringMap,userCacheBalanceVo);
        return userCacheBalanceVo;
    }

    /**
     * 返回0 计费失败
     * 1：不可提现计费
     * 2：可提现计费
     */
    private String costBalanceScript =
                    "local noWithDrawable = redis.call('HGET', KEYS[1], 'noWithDrawable') " +
                    "local withDrawable = redis.call('HGET', KEYS[1], 'withDrawable') " +
                    "if noWithDrawable == nil  or withDrawable == nil then " +
                    "return 0 " +
                    "end " +
                    "noWithDrawable = tonumber(noWithDrawable) " +
                    "withDrawable = tonumber(withDrawable) " +
                    "local cost = tonumber(ARGV[1]) "+
                    "if noWithDrawable >= cost   then " +
                    "redis.call('HSET', KEYS[1], 'noWithDrawable' , noWithDrawable - cost  ) "+
                    "return 1 " +
                    "end " +
                    "if noWithDrawable > 0   then " +
                    "redis.call('HSET', KEYS[1], 'noWithDrawable' , 0  ) "+
                    "return 1 " +
                    "end " +
                   //" cost = cost - noWithDrawable "+
                    "if withDrawable >= cost   then " +
                    "redis.call('HSET', KEYS[1],  'withDrawable' , withDrawable - cost  ) "+
                    "return 2 " +
                    "end " +
                    "redis.call('HSET', KEYS[1], 'withDrawable' , 0  ) "+
                    "return 2 ";




    /**
     * 消费用户余额
     *
     * @param userId
     * @param cost
     * @return
     */
    public Integer costUserBalance(Long userId,Long cost){
        String key = String.format(CommonConstant.USER_BALANCE_KEY,userId);
        Object eval = redisClient.eval(costBalanceScript, 1, key, String.valueOf(cost));
        if(eval==null){
            return 0;
        }
        return Integer.parseInt(eval.toString());
    }


    /**
     * 同步用户余额 从redis缓存中
     *
     * @param userId
     * @return
     */
    public boolean syncUserBalance(Long userId,Integer result){
        String key = String.format(CommonConstant.USER_BALANCE_SYNC,result);
        Long sadd = redisClient.sadd(key, String.valueOf(userId));
        return sadd!=null && sadd>0;
    }


    /**
     * 新增用户的不可提现余额
     * @param userId
     * @param balance
     * @return
     */
    public void addUserNonWithDrawAbleBalance(Long userId,Long balance,UserBalanceRecordEnum recordEnum){
        UserBalance nonWithdrawableBalance = userBalancesService.getUserNON_WITHDRAWABLEBalance(userId);
        if(nonWithdrawableBalance==null){
            throw new BizException("余额账号不存在");
        }
        // 防止邀请人的用户没有在缓存中
        getUserBalance(userId);
        String key = String.format(CommonConstant.USER_BALANCE_KEY,userId);
        redisClient.hincr(key,"noWithDrawable",balance);
        syncUserBalance(userId,CommonConstant.nonWithdrawableBalanceType);
        userBalanceRecordManager.addUserBalanceRecord(userId,balance, recordEnum.getType(),CommonConstant.nonWithdrawableBalanceType,null,nonWithdrawableBalance.getId());
    }


    /**
     * 新增可提现余额
     *
     * @param userId
     * @param balance
     * @param recordEnum
     */
    public void addUserWithDrawAbleBalance(Long userId,Long balance,UserBalanceRecordEnum recordEnum,String desc){
        UserBalance userWITHDRAWABLEBalance = userBalancesService.getUserWITHDRAWABLEBalance(userId);
        if(userWITHDRAWABLEBalance==null){
            throw new BizException("余额账号不存在");
        }
        // 防止邀请人的用户没有在缓存中
        getUserBalance(userId);
        String key = String.format(CommonConstant.USER_BALANCE_KEY,userId);
        redisClient.hincr(key,"withDrawable",balance);
        syncUserBalance(userId,CommonConstant.drawableBalanceType);
        userBalanceRecordManager.addUserBalanceRecord(userId,balance, recordEnum.getType(),CommonConstant.drawableBalanceType,desc,userWITHDRAWABLEBalance.getId());
    }




    /**
     * 管理员修改用户余额
     *
     * @param req
     * @return
     */
    @Transactional
    public ResultVO updateUserBalance(UpdateUserBalanceReq req){

        Long userId = req.getUserId();

        UserBalance userWITHDRAWABLEBalance = userBalancesService.getUserWITHDRAWABLEBalance(userId);
        UserBalance nonWithdrawableBalance = userBalancesService.getUserNON_WITHDRAWABLEBalance(userId);

        boolean flag = true;

        if(!userWITHDRAWABLEBalance.getBalance().equals(req.getWithDrawable())){
            flag = false;
            userBalanceRecordManager.addUserBalanceRecord(req.getUserId(),userWITHDRAWABLEBalance.getBalance()-req.getWithDrawable(), UserBalanceRecordEnum.SYSTEM_UPDATE_BALANCE.getType(),CommonConstant.drawableBalanceType,null,userWITHDRAWABLEBalance.getId());
        }

        if(!nonWithdrawableBalance.getBalance().equals(req.getNoWithDrawable())){
            flag = false;
            userBalanceRecordManager.addUserBalanceRecord(req.getUserId(),nonWithdrawableBalance.getBalance()-req.getNoWithDrawable(), UserBalanceRecordEnum.SYSTEM_UPDATE_BALANCE.getType(),CommonConstant.nonWithdrawableBalanceType,null,nonWithdrawableBalance.getId());
        }

        if(flag){
            return ResultVO.fail("新旧余额已经是相同的了");
        }

        UserCacheBalanceVo userCacheBalanceVo = new UserCacheBalanceVo();
        userCacheBalanceVo.setNoWithDrawable(req.getNoWithDrawable());
        userCacheBalanceVo.setWithDrawable(req.getWithDrawable());

        String key = String.format(CommonConstant.USER_BALANCE_KEY,userId);
        Map<String, String> stringStringMap = ObjectToHashMapConverter.convertObjectToHashMap(userCacheBalanceVo);
        // 将用户信息以哈希表的形式存储到Redis中
        redisClient.hset(key, stringStringMap);
        // 同步缓存余额到mysql
        syncUserBalance(req.getUserId(),CommonConstant.drawableBalanceType);
        syncUserBalance(req.getUserId(),CommonConstant.nonWithdrawableBalanceType);
        return ResultVO.success();
    }


    /**
     * 新增邀请人的积分奖励
     *
     * @param invitationCode
     */
    public void syncAddInvitationUserBalance(String invitationCode, String desc){
        ThreadPoolManager.userBalancePool.execute(()->{
            try {
                User user = userService.getUserByInvitationCode(invitationCode);
                if(user==null){
                    log.error("addInvitationUserBalance getUser is null  error invitationCode={}",invitationCode);
                    return;
                }
                Long userId = user.getId();

                addUserWithDrawAbleBalance(userId,invitationCodeBalance,UserBalanceRecordEnum.INVITATION_NEW_USER_BALANCE,desc);
            }catch (Exception e){
                log.error("addInvitationUserBalance error invitationCode={},desc={}",invitationCode,desc,e);
            }
        });
    }



    /**
     * 新增创作者的积分奖励
     *
     */
    public void syncAddMaskCreatorUserBalance(Long userId, Long addBalance, String desc){
        ThreadPoolManager.userBalancePool.execute(()->{
            try {
                addUserWithDrawAbleBalance(userId,addBalance,UserBalanceRecordEnum.MASK_CHAT_BALANCE,desc);
            }catch (Exception e){
                log.error("addMaskCreatorUserBalance error userId={},desc={}",userId,desc,e);
            }
        });
    }


    /**
     * 新增创作者的积分奖励
     *
     */
    public void createCode(Long userId, Long addBalance,UserBalanceRecordEnum recordEnum, String desc){
        addUserWithDrawAbleBalance(userId,addBalance,recordEnum,desc);
    }



}
