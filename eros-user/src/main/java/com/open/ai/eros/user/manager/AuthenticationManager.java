package com.open.ai.eros.user.manager;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.open.ai.eros.common.constants.CommonConstant;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.exception.BizException;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.service.RedisClient;
import com.open.ai.eros.common.util.ObjectToHashMapConverter;
import com.open.ai.eros.common.vo.CacheUserInfoVo;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.user.entity.User;
import com.open.ai.eros.db.mysql.user.service.impl.UserServiceImpl;
import com.open.ai.eros.db.privacy.utils.AESUtil;
import com.open.ai.eros.user.bean.req.AddUserInfoReq;
import com.open.ai.eros.user.constants.UserStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


/**
 * 身份验证业务类：登录、注册、第三方账号登录
 */
@Slf4j
@Component
public class AuthenticationManager {

    @Resource
    private UserServiceImpl usersService;

    @Resource
    private UserManager userManager;

    @Autowired
    private RedisClient redisClient;


    @Autowired
    private UserBalanceManager userBalanceManager;


    /**
     * 用户注册
     *
     * @param req
     * @return
     */
    @Transactional
    public ResultVO register(AddUserInfoReq req) {
        String email = req.getEmail();
        User user = usersService.getUserByAccount(email);
        if (user != null) {
            return ResultVO.fail("该邮箱已经被注册了！");
        }
        int addUserResult = usersService.addUser(req.getEmail(), req.getPassword(), req.getUserName(), req.getInvitedCode());
        log.info("register addUserResult={},req={}", addUserResult, JSONObject.toJSONString(addUserResult));
        user = usersService.getUserByAccount(email);
        if (user.getId() != null) {
            boolean inited = userBalanceManager.initUserBalance(user.getId());
            if (!inited) {
                throw new BizException("新增初始化余额错误");
            }
        }

        // 邀请人的注册码
        String invitedCode = req.getInvitedCode();
        if(StringUtils.isNoneEmpty(invitedCode)){
            // 新增邀请积分
            userBalanceManager.syncAddInvitationUserBalance(invitedCode,"邀请新用户:"+req.getUserName());
        }
        return addUserResult > 0 ? ResultVO.success() : ResultVO.fail("注册失败！请联系管理员");
    }


    /**
     * 用户登录
     *
     * @param email    邮箱
     * @param password 密码
     */
    public ResultVO login(String email, String password) {
        try {
            // 1、查询是否存在该用户
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("email", email);
            User users = usersService.getOne(queryWrapper);

            // 判断用户是否存在
            if (users == null) {
                log.error("邮箱不存在，email:{}", email);
                return ResultVO.fail("邮箱不存在");
            }

            // 2、判断用户是否被禁用
            if (UserStatusEnum.DISABLED.name().equals(users.getStatus())) {
                log.error("邮箱被禁用，email:{}", email);
                return ResultVO.fail("邮箱已被禁用");
            }

            // 3、验证密码是否正确
            if (!users.getPassword().equals(password)) {
                log.error("密码错误，username:{}", email);
                return ResultVO.fail("用户名或密码错误");
            }

            // 4、登录成功
            log.info("登录成功，username:{}", email);
            CacheUserInfoVo cacheUserInfoVo = userManager.buildCacheUserInfoVo(users);
            Map<String, String> toHashMap = ObjectToHashMapConverter.convertObjectToHashMap(cacheUserInfoVo);

            String userToken = AESUtil.encryptBase64(AESUtil.USER_TOKEN_KEY,cacheUserInfoVo.getEmail());
            cacheUserInfoVo.setToken(userToken);

            String key = String.format(CommonConstant.USER_LOGIN_TOKEN_KEY, userToken);
            redisClient.hset(key, toHashMap);
            redisClient.expire(key, CommonConstant.TOKEN_TIME_OUT);

            HashMap<String, String> map = new HashMap<>();
            map.put(CommonConstant.USER_LOGIN_TOKEN, userToken);
            return ResultVO.success(map);
        } catch (Exception e) {
            log.error("登录过程中发生错误", e);
            return ResultVO.fail("登录失败，请稍后重试");
        }
    }

    /**
     * 后台用户登录
     * @param email 邮箱
     * @param password 密码
     */
    public ResultVO adminLogin(String email, String password) {
        try {
            // 1、查询是否存在该用户
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("email", email);
            User users = usersService.getOne(queryWrapper);

            // 判断用户是否存在
            if (users == null) {
                log.error("邮箱不存在，email:{}", email);
                return ResultVO.fail("邮箱不存在");
            }

            // 2、判断用户是否被禁用
            if (UserStatusEnum.DISABLED.name().equals(users.getStatus())) {
                log.error("邮箱被禁用，email:{}", email);
                return ResultVO.fail("邮箱已被禁用");
            }

            // 3、验证密码是否正确
            if (!users.getPassword().equals(password)) {
                log.error("密码错误，username:{}", email);
                return ResultVO.fail("用户名或密码错误");
            }

            // 判断用户是否有权限
            if (!StringUtils.containsAny(users.getRole(), RoleEnum.SYSTEM.getRole(), RoleEnum.CREATOR.getRole())) {
                log.error("账户没有权限登录，email:{}", email);
                return ResultVO.fail("账户没有权限登录后台");
            }

            // 4、登录成功
            log.info("登录成功，username:{}", email);

            // 构建用户缓存信息对象
            CacheUserInfoVo cacheUserInfoVo = userManager.buildCacheUserInfoVo(users);

            // 将用户缓存信息对象转换为HashMap，便于存储到Redis
            Map<String, String> toHashMap = ObjectToHashMapConverter.convertObjectToHashMap(cacheUserInfoVo);

            // 使用AES加密用户邮箱生成用户令牌
            String userToken = AESUtil.encryptBase64(AESUtil.USER_TOKEN_KEY, cacheUserInfoVo.getEmail());

            // 将生成的令牌设置到用户缓存信息对象中
            cacheUserInfoVo.setToken(userToken);

            // 构造Redis键名，格式为 "USER_LOGIN_TOKEN:{userToken}"
            String key = String.format(CommonConstant.USER_LOGIN_TOKEN_KEY, userToken);

            // 将用户信息以哈希表的形式存储到Redis中
            redisClient.hset(key, toHashMap);

            // 设置Redis中用户信息的过期时间
            redisClient.expire(key, CommonConstant.TOKEN_TIME_OUT);

            HashMap<String, String> map = new HashMap<>();
            map.put(CommonConstant.USER_LOGIN_TOKEN, userToken);
            return ResultVO.success(map);
        } catch (Exception e) {
            log.error("登录过程中发生错误", e);
            return ResultVO.fail("登录失败，请稍后重试");
        }
    }

    /**
     * 用户退出
     *
     * @param token 用户令牌
     */
    public ResultVO logout(String token) {
        try {
            // 1. 验证令牌
            if (token == null || token.isEmpty()) {
                return ResultVO.fail("无效的令牌");
            }

            // 2. 构造Redis键名
            String key = String.format(CommonConstant.USER_LOGIN_TOKEN_KEY, token);

            // 3. 检查令牌是否存在于Redis中
            if (!redisClient.exists(key)) {
                return ResultVO.fail("用户未登录或会话已过期");
            }

            // 4. 从Redis中删除用户信息
            redisClient.del(key);

            // 5. 记录退出日志
            log.info("用户成功退出，token:{}", token);

            return ResultVO.success("退出成功");
        } catch (Exception e) {
            log.error("退出过程中发生错误", e);
            return ResultVO.fail("退出失败，请稍后重试");
        }
    }


    /**
     * 忘记密码
     * @param email 邮箱
     * @param verificationCode 验证码
     * @param EncryptedNewPassword 加密之后的新密码
     * @return
     */
    public ResultVO forgetPassword(String email, String verificationCode, String EncryptedNewPassword) {
        // 1. 验证邮箱是否已注册
        if (!queryUser(email)) {
            return ResultVO.fail("该邮箱未注册");
        }
        // 2. 验证码校验
        String emailKey = String.format(CommonConstant.CODE_KEY, email);
        String code = redisClient.get(emailKey);
        if (code == null) {
            return ResultVO.fail("该验证码已过期，请重新获取验证码");
        } else if (!code.equalsIgnoreCase(verificationCode)) {
            return ResultVO.fail("验证码错误，请输入正确的验证码");
        }
        // 3. 删除已使用的验证码
        Long del = redisClient.del(emailKey);
        if (del <= 0) {
            return ResultVO.fail("服务器繁忙，请稍后再试");
        }
        // 4. 更新密码
        try {
            usersService.updatePasswordByEmail(email, EncryptedNewPassword);
        } catch (Exception e) {
            log.error("更新密码失败", e);
            return ResultVO.fail("密码更新失败，请稍后再试");
        }
        return ResultVO.success("密码重置成功");
    }


    /**
     * 查询用户
     */
    public Boolean queryUser(String email) {
        User user = usersService.getUserByAccount(email);
        return user != null;
    }

}



