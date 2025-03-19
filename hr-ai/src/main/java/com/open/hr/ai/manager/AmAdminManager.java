package com.open.hr.ai.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.ai.eros.common.constants.AmAdminRoleEnum;
import com.open.ai.eros.common.constants.AmAdminStatusEnums;
import com.open.ai.eros.common.constants.CommonConstant;
import com.open.ai.eros.common.service.RedisClient;
import com.open.ai.eros.common.util.ObjectToHashMapConverter;
import com.open.ai.eros.common.vo.CacheUserInfoVo;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.AmAdmin;
import com.open.ai.eros.db.mysql.hr.service.impl.AmAdminServiceImpl;
import com.open.ai.eros.db.privacy.utils.AESUtil;
import com.open.ai.eros.db.privacy.utils.CryptoUtil;
import com.open.ai.eros.user.manager.UserManager;
import com.open.hr.ai.bean.req.*;
import com.open.hr.ai.bean.vo.AmAdminVo;
import com.open.hr.ai.convert.AmAdminConvert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AmAdminManager {

    @Resource
    private AmAdminServiceImpl amAdminService;


    @Resource
    private UserManager userManager;


    @Resource
    private AmNewMaskManager  amNewMaskManager;

    @Resource
    private ChatBotOptionsManager chatBotOptionsManager;


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
        AmAdmin user = amAdminService.addUser(req.getEmail(), req.getPassword(), req.getUsername(), req.getCompany(), req.getMobile());
        if (Objects.nonNull(user)){
            // 添加默认的面具和复聊数据
            amNewMaskManager.createDefaultMask(user.getId());
            chatBotOptionsManager.createDefaultRechat(user.getId());
        }

        return Objects.nonNull(user)  ? ResultVO.success() : ResultVO.fail("注册失败！请联系管理员");
    }

    public ResultVO<PageVO<AmAdminVo>> searchAdmin(SearchAmAdminReq req, Long adminId) {
        LambdaQueryWrapper<AmAdmin> queryWrapper = new LambdaQueryWrapper<>();
        AmAdmin admin = amAdminService.getById(adminId);
        if (Objects.isNull(admin)) {
            return ResultVO.fail("账号不存在,不能查询用户!");
        }
        // 如果不是管理员,则只允许查询自己创建的用户
        if (!AmAdminRoleEnum.SYSTEM.getType().equals(admin.getRole())) {
            queryWrapper.eq(AmAdmin::getCreatorId, adminId);
        }
        Integer pageNum = req.getPage();
        Integer pageSize = req.getSize();
        if (StringUtils.isNotBlank(req.getUsername())) {
            queryWrapper.like(AmAdmin::getUsername, req.getUsername());
        }
        if (StringUtils.isNotBlank(req.getEmail())) {
            queryWrapper.like(AmAdmin::getEmail, req.getEmail());
        }
        if (StringUtils.isNotBlank(req.getMobile())) {
            queryWrapper.like(AmAdmin::getMobile, req.getMobile());
        }
        if (req.getStatus() != null) {
            queryWrapper.eq(AmAdmin::getStatus, req.getStatus());
        } else {
            queryWrapper.eq(AmAdmin::getStatus, AmAdminStatusEnums.OPEN.getStatus());
        }

        Page<AmAdmin> page = new Page<>(pageNum, pageSize);
        Page<AmAdmin> amMaskPage = amAdminService.page(page, queryWrapper);
        List<AmAdminVo> amMaskVos = amMaskPage.getRecords().stream().map(AmAdminConvert.I::convertAmAdminVo).collect(Collectors.toList());
        return ResultVO.success(PageVO.build(amMaskPage.getTotal(), amMaskVos));
    }


    public ResultVO createUser(HrAddUserReq req, Long adminId) {
        try {
            AmAdmin admin = amAdminService.getById(adminId);
            if (Objects.isNull(admin)) {
                return ResultVO.fail("账号不存在,不能创建用户!");
            }

            if (admin.getRole().equals(AmAdminRoleEnum.COMMON.getType()) || admin.getRole().equals(AmAdminRoleEnum.VIP.getType())) {
                return ResultVO.fail("没有权限创建用户");
            }

            // 1、查询是否存在该用户
            QueryWrapper<AmAdmin> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", req.getUsername());
            AmAdmin amAdmin = amAdminService.getOne(queryWrapper);
            if (amAdmin != null) {
                return ResultVO.fail("当前账号已存在");
            }
            if (Objects.nonNull(req.getRole())){
                Boolean isExit = AmAdminRoleEnum.getByType(req.getRole());
                if (!isExit) {
                    return ResultVO.fail("角色不存在");
                }
                if (req.getRole().equals(AmAdminRoleEnum.VIP.getType()) && Objects.isNull(req.getExpireTime())) {
                    return ResultVO.fail("会员到期时间不能为空");
                }

                if (req.getRole().equals(AmAdminRoleEnum.SYSTEM.getType())) {
                    return ResultVO.fail("不能更新用户角色为系统管理员");
                }
            }

            String encodePassWord = Base64.getEncoder().encodeToString(CryptoUtil.encryptMD5(req.getPassword().getBytes("UTF-8")));
            AmAdmin user = amAdminService.createUser(adminId, req.getEmail(), encodePassWord, req.getUsername(), req.getCompany(), req.getMobile(), req.getRole(), req.getExpireTime());
            if (Objects.nonNull(user)){
                // 添加默认的面具和复聊数据
                amNewMaskManager.createDefaultMask(user.getId());
                chatBotOptionsManager.createDefaultRechat(user.getId());
            }

            return Objects.nonNull(user)  ? ResultVO.success() : ResultVO.fail("注册失败！请联系管理员");

        } catch (Exception e) {
            log.error("createUser error req={}", req, e);
        }
        return ResultVO.fail("注册失败！请联系管理员");
    }

    /**
     *  删除账号
     */
    public ResultVO deleteUser(Long userId, Long adminId) {
        AmAdmin admin = amAdminService.getById(adminId);
        if (Objects.isNull(admin)) {
            return ResultVO.fail("账号不存在,不能操作用户!");
        }
        AmAdmin user = amAdminService.getById(userId);
        if ((admin.getRole().equals(AmAdminRoleEnum.COMMON.getType()) || !Objects.equals(user.getCreatorId(), adminId)) && !admin.getRole().equals(AmAdminRoleEnum.SYSTEM.getType())) {
            return ResultVO.fail("没有权限删除用户");
        }
        boolean result = amAdminService.removeById(userId);
        return result ? ResultVO.success() : ResultVO.fail("删除失败");
    }



    public ResultVO banAdmin(Long userId, Long adminId) {
        AmAdmin admin = amAdminService.getById(adminId);
        if (Objects.isNull(admin)) {
            return ResultVO.fail("账号不存在,不能操作用户!");
        }
        AmAdmin user = amAdminService.getById(userId);
        if ((admin.getRole().equals(AmAdminRoleEnum.COMMON.getType()) || !Objects.equals(user.getCreatorId(), adminId)) && !admin.getRole().equals(AmAdminRoleEnum.SYSTEM.getType())) {
            return ResultVO.fail("没有权限禁用用户");
        }

        user.setStatus(AmAdminStatusEnums.BAN.getStatus());
        boolean result = amAdminService.updateById(user);
        return result ? ResultVO.success() : ResultVO.fail("禁用失败");
    }

    public ResultVO unbanAdmin(Long userId, Long adminId) {
        AmAdmin admin = amAdminService.getById(adminId);
        if (Objects.isNull(admin)) {
            return ResultVO.fail("账号不存在,不能操作用户!");
        }
        AmAdmin user = amAdminService.getById(userId);
        if ((admin.getRole().equals(AmAdminRoleEnum.COMMON.getType()) || !Objects.equals(user.getCreatorId(), adminId)) && !admin.getRole().equals(AmAdminRoleEnum.SYSTEM.getType())) {
            return ResultVO.fail("没有权限启用用户");
        }

        user.setStatus(AmAdminStatusEnums.OPEN.getStatus());
        boolean result = amAdminService.updateById(user);
        return result ? ResultVO.success() : ResultVO.fail("启用失败");
    }

    public ResultVO updatePassword(UpdateAmAdminPasswordReq req, Long adminId) {
        try {
            AmAdmin admin = amAdminService.getById(adminId);
            if (Objects.isNull(admin)) {
                return ResultVO.fail("账号不存在,不能操作用户!");
            }
            AmAdmin user = amAdminService.getById(req.getId());
            if (!Objects.equals(user.getCreatorId(), adminId) &&
                    (!admin.getRole().equals(AmAdminRoleEnum.SYSTEM.getType()) || !admin.getRole().equals(AmAdminRoleEnum.ADMIN.getType()))) {
                return ResultVO.fail("没有权限更新");
            }

            String password = req.getPassword();
            String encodePassWord = Base64.getEncoder().encodeToString(CryptoUtil.encryptMD5(password.getBytes("UTF-8")));
            user.setPassword(encodePassWord);
            boolean result = amAdminService.updateById(user);
            return result ? ResultVO.success() : ResultVO.fail("更新失败");
        } catch (Exception e) {
            log.error("更新异常 req={},adminId={}", req, adminId, e);
        }
        return ResultVO.fail("更新失败");
    }


    public ResultVO updateRole(UpdateAmAdminRoleReq req, Long adminId) {
        try {
            Boolean isExit = AmAdminRoleEnum.getByType(req.getRole());
            if (!isExit) {
                return ResultVO.fail("角色不存在");
            }
            if (req.getRole().equals(AmAdminRoleEnum.VIP.getType()) && Objects.isNull(req.getExpireTime())) {
                return ResultVO.fail("会员到期时间不能为空");
            }

            if (req.getRole().equals(AmAdminRoleEnum.SYSTEM.getType())) {
                return ResultVO.fail("不能更新用户角色为系统管理员");
            }
            AmAdmin admin = amAdminService.getById(adminId);
            if (Objects.isNull(admin)) {
                return ResultVO.fail("账号不存在,不能操作用户!");
            }

            // 判断用户是否为管理员或者系统管理员
            if (!admin.getRole().equals(AmAdminRoleEnum.ADMIN.getType()) && !admin.getRole().equals(AmAdminRoleEnum.SYSTEM.getType())) {
                return ResultVO.fail("没有权限更新");
            }

            //如果更新角色为管理员,则用户角色必须为系统管理员
            if (req.getRole().equals(AmAdminRoleEnum.ADMIN.getType())) {
                if (!admin.getRole().equals(AmAdminRoleEnum.SYSTEM.getType())) {
                    return ResultVO.fail("没有权限更新");
                }
            }
            AmAdmin user = amAdminService.getById(req.getId());
            if (Objects.isNull(user)) {
                return ResultVO.fail("账号不存在,不能操作用户!");
            }
            user.setRole(req.getRole());
            user.setExpireTime(req.getExpireTime());
            user.setUpdateTime(LocalDateTime.now());
            boolean result = amAdminService.updateById(user);
            return result ? ResultVO.success() : ResultVO.fail("更新失败");
        } catch (Exception e) {
            log.error("更新异常 req={},adminId={}", req, adminId, e);
        }
        return ResultVO.fail("更新失败");
    }


    public ResultVO updateBaseInfo(UpdateAmAdminInfoReq req, Long adminId) {
        try {
            AmAdmin admin = amAdminService.getById(adminId);
            AmAdmin user = amAdminService.getById(req.getId());
            if (Objects.isNull(admin) || Objects.isNull(user)) {
                return ResultVO.fail("账号不存在,不能操作用户!");
            }
            if (!admin.getRole().equals(AmAdminRoleEnum.SYSTEM.getType()) && !admin.getRole().equals(AmAdminRoleEnum.ADMIN.getType())) {
                return ResultVO.fail("没有权限更新");
            }
            if (Objects.nonNull(req.getRole())){
                Boolean isExit = AmAdminRoleEnum.getByType(req.getRole());
                if (!isExit) {
                    return ResultVO.fail("角色不存在");
                }
                if (req.getRole().equals(AmAdminRoleEnum.VIP.getType()) && Objects.isNull(req.getExpireTime())) {
                    return ResultVO.fail("会员到期时间不能为空");
                }

                if (req.getRole().equals(AmAdminRoleEnum.SYSTEM.getType())) {
                    return ResultVO.fail("不能更新用户角色为系统管理员");
                }
            }
            if (StringUtils.isNotBlank(req.getCompany())) {
                user.setCompany(req.getCompany());
            }
            if (StringUtils.isNotBlank(req.getEmail())) {
                user.setEmail(req.getEmail());
            }
            if (StringUtils.isNotBlank(req.getMobile())) {
                user.setMobile(req.getMobile());
            }
            if (StringUtils.isNotBlank(req.getRole())) {
                user.setRole(req.getRole());
            }
            if (Objects.nonNull(req.getExpireTime())) {
                user.setExpireTime(req.getExpireTime());
            }
            boolean result = amAdminService.updateById(user);
            return result ? ResultVO.success() : ResultVO.fail("更新失败");
        } catch (Exception e) {
            log.error("更新异常 req={},adminId={}", req, adminId, e);
        }
        return ResultVO.fail("更新失败");
    }
}
