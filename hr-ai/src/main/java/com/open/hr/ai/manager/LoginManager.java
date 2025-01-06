package com.open.hr.ai.manager;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.open.ai.eros.common.constants.CommonConstant;
import com.open.ai.eros.common.service.RedisClient;
import com.open.ai.eros.common.util.ObjectToHashMapConverter;
import com.open.ai.eros.common.vo.CacheUserInfoVo;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.AmAdmin;
import com.open.ai.eros.db.mysql.hr.service.impl.AmAdminServiceImpl;
import com.open.ai.eros.db.mysql.user.entity.User;
import com.open.ai.eros.db.privacy.utils.AESUtil;
import com.open.ai.eros.user.manager.UserManager;
import com.open.hr.ai.bean.req.HrAddUserReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class LoginManager {

    @Resource
    private AmAdminServiceImpl amAdminService;

    @Resource
    private UserManager userManager;


    @Autowired
    private RedisClient redisClient;


    public ResultVO login(String userName, String password) {
        try {
            // 1、查询是否存在该用户
            QueryWrapper<AmAdmin> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", userName);
            AmAdmin amAdmin = amAdminService.getOne(queryWrapper);
            // 判断用户是否存在
            if (amAdmin == null) {
                return ResultVO.fail("用户不存在");
            }
            if (amAdmin.getStatus() == 0) {
                return ResultVO.fail("用户未启用");
            }
            if (amAdmin.getStatus() == 1) {
                return ResultVO.fail("用户被禁用");
            }
            // 3、验证密码是否正确
            if (!amAdmin.getPassword().equals(password)) {
                return ResultVO.fail("用户名或密码错误");
            }
            // 4、登录成功
            CacheUserInfoVo cacheUserInfoVo = userManager.buildCacheUserInfoVo(amAdmin.convertToUser());
            Map<String, String> toHashMap = ObjectToHashMapConverter.convertObjectToHashMap(cacheUserInfoVo);

            String userToken = AESUtil.encryptBase64(AESUtil.USER_TOKEN_KEY, cacheUserInfoVo.getUserName());
            cacheUserInfoVo.setToken(userToken);

            String key = String.format(CommonConstant.USER_LOGIN_TOKEN_KEY, userToken);
            redisClient.hset(key, toHashMap);
            redisClient.expire(key, CommonConstant.TOKEN_TIME_OUT);

            HashMap<String, String> map = new HashMap<>();
            map.put(CommonConstant.USER_LOGIN_TOKEN, userToken);
            return ResultVO.success(map);

        } catch (Exception e) {
            return ResultVO.fail("登录失败，请稍后重试");
        }
    }


    public ResultVO register(HrAddUserReq req) {
        // 1、查询是否存在该用户
        QueryWrapper<AmAdmin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", req.getUsername());
        AmAdmin amAdmin = amAdminService.getOne(queryWrapper);
        if (amAdmin != null) {
            return ResultVO.fail("当前账号已存在");
        }
        int addUserResult = amAdminService.addUser(req.getEmail(), req.getPassword(), req.getUsername(), req.getCompany(),req.getMobile());
        return addUserResult > 0 ? ResultVO.success() : ResultVO.fail("注册失败！请联系管理员");

    }
}
