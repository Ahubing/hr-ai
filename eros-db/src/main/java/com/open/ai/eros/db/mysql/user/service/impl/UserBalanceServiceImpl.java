package com.open.ai.eros.db.mysql.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.open.ai.eros.common.constants.CommonConstant;
import com.open.ai.eros.common.exception.BizException;
import com.open.ai.eros.db.constants.UserBalanceTypeEnum;
import com.open.ai.eros.db.mysql.user.entity.UserBalance;
import com.open.ai.eros.db.mysql.user.mapper.UserBalanceMapper;
import com.open.ai.eros.db.mysql.user.service.IUserBalanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 用户余额表，存储用户的可提取和不可提取余额 服务实现类
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-06
 */
@Slf4j
@Service
public class UserBalanceServiceImpl extends ServiceImpl<UserBalanceMapper, UserBalance> implements IUserBalanceService {


    /**
     * 获取用户的余额信息
     *
     * @param userId
     * @return
     */
    public List<UserBalance> getUserBalance(Long userId) {
        return this.getBaseMapper().getUserBalance(userId);
    }


    /**
     * 获取用户不可提现余额
     *
     * @param userId
     * @return
     */
    public UserBalance getUserNON_WITHDRAWABLEBalance(Long userId) {
        return this.baseMapper.getUserBalanceByType(userId, UserBalanceTypeEnum.NON_WITHDRAWABLE.name());
    }


    /**
     * 获取用户可提现余额
     *
     * @param userId
     * @return
     */
    public UserBalance getUserWITHDRAWABLEBalance(Long userId) {
        return this.baseMapper.getUserBalanceByType(userId, UserBalanceTypeEnum.WITHDRAWABLE.name());
    }

    /**
     * 获取用户类型账号
     *
     * @param userId
     * @param type
     * @return
     */
    public UserBalance getUserTypeBalance(Long userId, String type) {
        return this.baseMapper.getUserBalanceByType(userId, type);
    }

    /**
     * 更新用户不可提现的余额
     *
     * @param userId
     * @param balance
     * @return
     */
    public int updateNON_WITHDRAWABLEUserBalance(Long userId, Long balance) {
        log.info("updateNON_WITHDRAWABLEUserBalance userId={},opBalance={}", userId, balance);
        return this.baseMapper.updateUserBalance(userId, balance, UserBalanceTypeEnum.NON_WITHDRAWABLE.name());
    }

    /**
     * 更新用户可提现余额
     *
     * @param userId
     * @param balance
     * @return
     */
    public int updateWITHDRAWABLEUserBalance(Long userId, Long balance) {
        log.info("updateWITHDRAWABLEUserBalance userId={},opBalance={}", userId, balance);
        return this.baseMapper.updateUserBalance(userId, balance, UserBalanceTypeEnum.WITHDRAWABLE.name());
    }


    /**
     * 初始化用户默认余额
     *
     * @param userId
     * @return
     */
    @Transactional
    public boolean addUserInitUserBalances(Long userId) {

        /**
         * 用户注册的初始余额
         */
        Long userInitBalance = CommonConstant.userInitBalance;
        List<UserBalance> userBalances = Arrays.asList(
                buildUserBalances(userId, userInitBalance, UserBalanceTypeEnum.NON_WITHDRAWABLE.name()),
                buildUserBalances(userId, 0L, UserBalanceTypeEnum.WITHDRAWABLE.name())
        );
        boolean saveBatch = this.saveBatch(userBalances);
        if (!saveBatch) {
            throw new BizException("初始化用户余额失败");
        }
        return true;
    }


    private UserBalance buildUserBalances(Long userId, Long userInitBalance, String type) {

        UserBalance userBalances = new UserBalance();
        userBalances.setBalance(userInitBalance);
        userBalances.setType(type);
        userBalances.setUserId(userId);
        userBalances.setCreatedAt(LocalDateTime.now());
        return userBalances;
    }



    /**
     * 新增用户不可提现的余额
     *
     * @param userId
     * @param balance
     * @return
     */
    public int addNON_WITHDRAWABLEUserBalance(Long userId, Long balance) {
        log.info("addNON_WITHDRAWABLEUserBalance userId={},opBalance={}", userId, balance);
        return this.baseMapper.addUserBalance(userId, balance, UserBalanceTypeEnum.NON_WITHDRAWABLE.name());
    }

    /**
     * 新增用户可提现余额
     *
     * @param userId
     * @param balance
     * @return
     */
    public int addWITHDRAWABLEUserBalance(Long userId, Long balance) {
        log.info("addWITHDRAWABLEUserBalance userId={},opBalance={}", userId, balance);
        return this.baseMapper.addUserBalance(userId, balance, UserBalanceTypeEnum.WITHDRAWABLE.name());
    }




}
