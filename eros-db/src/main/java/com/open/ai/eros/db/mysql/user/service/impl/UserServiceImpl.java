package com.open.ai.eros.db.mysql.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.util.DateUtils;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.db.mysql.user.entity.User;
import com.open.ai.eros.db.mysql.user.mapper.UserMapper;
import com.open.ai.eros.db.mysql.user.service.IUserService;
import com.open.ai.eros.db.mysql.user.vo.SearchUserReqVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 用户表，存储用户的基本信息，包括通过谷歌账号登录的用户 服务实现类
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-20
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {



    private final LoadingCache<Long, Optional<User>> USER_CACHE = CacheBuilder.newBuilder().expireAfterWrite(3, TimeUnit.MINUTES).
            initialCapacity(20).maximumSize(1000).build(new CacheLoader<Long, Optional<User>>() {

                @Override
                public Optional<User> load(Long aLong) throws Exception {
                    User user = getById(aLong);
                    if(user==null){
                        return Optional.empty();
                    }
                    return Optional.of(user);
                }
            });



    public User getById(long userId){
        return super.getById(userId);
    }

    public Optional<User> getCacheUser(Long userId){
        try {
            return USER_CACHE.get(userId);
        }catch (Exception e){
            log.error("getCacheUser error userId={}",userId,e);
            User user = getById(userId);
            if(user==null){
                return Optional.empty();
            }
            return Optional.of(user);
        }
    }



    /**
     * 通过邀请码获取用户信息
     * @param invitationCode
     * @return
     */
    public User getUserByInvitationCode(String invitationCode){
        return this.baseMapper.getUserByInvitationCode(invitationCode);
    }

    /**
     * 获取所有用户信息
     *
     * @return
     */
    public List<User> getAllUserSimpleInfo(){
        return this.baseMapper.getAllUser();
    }

    /**
     * 根据账号获取用户信息
     *
     * @param account
     * @return
     */
    public User getUserByAccount(String account){
        return this.baseMapper.getUserByAccount(account);
    }


    /**
     * 新增用户
     *
     * @param email
     * @param password
     * @param username
     * @return
     */
    public int addUser(String email,String password,String username,String invitedCode){
        User users = new User();
        users.setEmail(email);
        users.setPassword(password);
        users.setUserName(username);
        users.setInvitedCode(invitedCode);
        users.setInvitationCode(Base64.getEncoder().encodeToString(email.getBytes()));
        // 新增加的是 普通用户
        users.setRole(RoleEnum.COMMON.getRole());
        users.setCreatedAt(LocalDateTime.now());
        return this.getBaseMapper().insert(users);
    }

    public PageVO<User> searchUser(SearchUserReqVo req){
        PageVO<User> pageVO = new PageVO<>();
        pageVO.setData(new ArrayList<>());
        Integer count = this.baseMapper.searchUserCount(req);
        pageVO.setTotal(count);
        if(count>0){
            pageVO.setData(this.baseMapper.searchUser(req));
        }
        return pageVO;
    }
    public User adminSearchUserDetail(SearchUserReqVo req){
        return this.baseMapper.searchUserDetail(req);
    }
    /**
     * 更新用户密码
     * @param email 用户邮箱
     * @param newPassword 新密码
     * @return 是否更新成功
     */
    public boolean updatePasswordByEmail(String email, String newPassword) {
        // 创建更新条件
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getEmail, email)
                .set(User::getPassword, newPassword);

        // 执行更新操作
        return this.update(updateWrapper);
    }


    /**
     * 更新用户个人信息
     * @param user 用户类
     * @return 是否更新成功
     */
    public boolean updateUserInfo(User user) {
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId, user.getId())
                .set(StringUtils.isNotBlank(user.getUserName()), User::getUserName, user.getUserName())
                .set(StringUtils.isNotBlank(user.getAvatar()), User::getAvatar, user.getAvatar())
                .set(StringUtils.isNotBlank(user.getRole()), User::getRole, user.getRole())
                .set(User::getUpdatedAt, LocalDateTime.now());

        return this.update(updateWrapper);
    }





    public Long getAllUserSum(){
        return this.baseMapper.getAllUserSum();
    }

    public Long getAllCreatorSum(){
        return this.baseMapper.getAllCreatorSum();
    }

    public Long getAllCommonUserSum(){
        return this.baseMapper.getAllCommonUserSum();
    }
    public Long getTodayRegister(){
        Date startTime = DateUtils.startOfDay(new Date());
        return this.baseMapper.getTodayRegister(startTime);
    }



}
