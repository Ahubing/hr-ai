package com.open.ai.eros.user.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.ai.eros.common.service.LoginUserService;
import com.open.ai.eros.common.vo.CacheUserInfoVo;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.user.entity.User;
import com.open.ai.eros.db.mysql.user.service.impl.UserServiceImpl;
import com.open.ai.eros.db.mysql.user.vo.SearchUserReqVo;
import com.open.ai.eros.user.bean.req.SearchUserDetailReq;
import com.open.ai.eros.user.bean.req.SearchUserReq;
import com.open.ai.eros.user.bean.req.UserUpdateReq;
import com.open.ai.eros.user.bean.vo.SearchUserResponseVo;
import com.open.ai.eros.user.bean.vo.SimpleUserVo;
import com.open.ai.eros.user.bean.vo.UserBalanceVo;
import com.open.ai.eros.user.convert.UserConvert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @类名：UserManager
 * @项目名：web-eros-ai
 * @description：
 * @创建人：Administrator
 * @创建时间：2024/8/3 23:06
 */

/**
 * 组装业务信息
 */
@Slf4j
@Component
public class UserManager implements LoginUserService {


    @Autowired
    private UserServiceImpl usersService;

    @Autowired
    private UserBalanceManager userBalanceManager;


    /**
     * 获取所有的用户信息
     * todo 后期加本地缓存
     * @return
     */
    public ResultVO<List<SimpleUserVo>> getAllSimpleUser(){
        List<User> allUserSimpleInfo = usersService.getAllUserSimpleInfo();
        List<SimpleUserVo> simpleUserVos = allUserSimpleInfo.stream().map(UserConvert.I::convertSimpleUserVo).collect(Collectors.toList());
        return ResultVO.success(simpleUserVos);
    }

    /**
     * 根据用户id 获取用户信息
     *
     * @param userIds
     * @return
     */
    public List<User> getUsersByIds(List<Long> userIds) {
        return usersService.listByIds(userIds);
    }

    /**
     * 根据账号获取缓存信息
     *
     * @param account
     * @return
     */
    @Override
    public CacheUserInfoVo getCacheUserInfoVo(String account) {
        User user = usersService.getUserByAccount(account);
        if (user == null) {
            return null;
        }
        return buildCacheUserInfoVo(user);
    }


    /**
     * 构建登录所需要的用户信息
     *
     * @param users
     * @return
     */
    public CacheUserInfoVo buildCacheUserInfoVo(User users) {
        CacheUserInfoVo cacheUserInfoVo = UserConvert.I.convertCacheUserInfoVo(users);
        //// 获取用户余额信息
        //Pair<Long, Long> userBalancePair = userBalanceManager.getUserBalance(users.getId());
        //cacheUserInfoVo.setNoWithDrawable(BalanceFormatUtil.getSystemBalance(userBalancePair.getKey()));
        //cacheUserInfoVo.setWithDrawable(BalanceFormatUtil.getSystemBalance(userBalancePair.getValue()));
        return cacheUserInfoVo;
    }


    /**
     * 获取邀请列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ResultVO<PageVO<SimpleUserVo>> getInvited(String code, Integer pageNum, Integer pageSize) {

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getInvitedCode, code);

        Page<User> pageObj = new Page<>(pageNum, pageSize);
        Page<User> page = usersService.page(pageObj, queryWrapper);
        List<SimpleUserVo> simpleUserVos = page.getRecords().stream().map(UserConvert.I::convertSimpleUserVo).collect(Collectors.toList());
        return ResultVO.success(PageVO.build(page.getTotal(), simpleUserVos));
    }

    public ResultVO<PageVO<SearchUserResponseVo>> searchUser(SearchUserReq req){
        SearchUserReqVo searchUserReqVo = SearchUserReqVo.builder()
                .userName(req.getUserName())
                .email(req.getEmail() != null ? req.getEmail().trim(): null)
                .status(req.getStatus())
                .role(req.getRole())
                .pageIndex((req.getPage() - 1) * req.getPageSize())
                .pageSize(req.getPageSize())
                .build();
        PageVO<User> pageVO = usersService.searchUser(searchUserReqVo);
        List<SearchUserResponseVo> searchUserResponseVos = pageVO.getData().stream().map(UserConvert.I::convertSearchUserResponseVo).collect(Collectors.toList());
        for (SearchUserResponseVo searchUserResponseVo : searchUserResponseVos) {
            try {
                UserBalanceVo userBalance = userBalanceManager.getUserBalance(searchUserResponseVo.getId());
                searchUserResponseVo.setNoWithDrawable(userBalance.getNoWithDrawable() );
                searchUserResponseVo.setWithDrawable(userBalance.getWithDrawable());
            }catch (Exception e){
                searchUserResponseVo.setNoWithDrawable("0");
                searchUserResponseVo.setWithDrawable("0");
                log.error("该用户不存在余额信息 userId={}",searchUserResponseVo.getId());
            }
        }
    return ResultVO.success(PageVO.build(pageVO.getTotal(),searchUserResponseVos));
    }

    public ResultVO<SearchUserResponseVo> searchUserDetail(SearchUserDetailReq req){
        SearchUserReqVo searchUserReqVo = SearchUserReqVo.builder()
                .email(req.getEmail() != null ? req.getEmail().trim(): null)
                .id(req.getId())
                .build();
        User userDetail = usersService.adminSearchUserDetail(searchUserReqVo);
        SearchUserResponseVo userResponseVo = UserConvert.I.convertSearchUserResponseVo(userDetail);
        try {
            LocalDateTime now = LocalDateTime.now();
            userResponseVo.setRegisterDay( ChronoUnit.DAYS.between(userResponseVo.getCreatedAt(), now));
            UserBalanceVo userBalance = userBalanceManager.getUserBalance(userResponseVo.getId());
            userResponseVo.setNoWithDrawable(userBalance.getNoWithDrawable());
            userResponseVo.setWithDrawable(userBalance.getWithDrawable());
        }catch (Exception e){
            log.error("该用户不存在余额信息 userId={}",userResponseVo.getId(),e);
        }
    return ResultVO.success(userResponseVo);
    }
    /**
     * 更新用户信息
     * @param userUpdateReq 更新用户请求参数类
     * @return
     */
    public boolean updateUserInfo(UserUpdateReq userUpdateReq) {
        User user = new User();
        user.setId(userUpdateReq.getId());
        user.setUserName(userUpdateReq.getUserName());
        user.setAvatar(userUpdateReq.getAvatar());
        user.setUpdatedAt(LocalDateTime.now());
        user.setRole(userUpdateReq.getRole());
        return usersService.updateUserInfo(user);
    }


    public ResultVO<SimpleUserVo> getSimpleUser(Long id){
        Optional<User> cacheUser = usersService.getCacheUser(id);
        if(cacheUser.isPresent()){
            SimpleUserVo simpleUserVo = new SimpleUserVo();
            User user = cacheUser.get();
            simpleUserVo.setId(user.getId());
            simpleUserVo.setUserName(user.getUserName());
            simpleUserVo.setAvatar(user.getAvatar());
            return ResultVO.success(simpleUserVo);
        }
        return ResultVO.success();
    }


}
