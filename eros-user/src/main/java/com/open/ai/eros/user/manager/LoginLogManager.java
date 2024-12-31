package com.open.ai.eros.user.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.user.entity.LoginLog;
import com.open.ai.eros.db.mysql.user.mapper.LoginLogMapper;
import com.open.ai.eros.db.mysql.user.service.impl.LoginLogServiceImpl;
import com.open.ai.eros.user.bean.req.LoginLogQueryReq;
import com.open.ai.eros.user.bean.vo.LoginLogVo;
import com.open.ai.eros.user.convert.LoginLogConvert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

import static com.open.ai.eros.common.util.SessionUser.getRole;

@Slf4j
@Component
public class LoginLogManager {

    @Autowired
    private HttpServletRequest request;

    @Resource
    private LoginLogServiceImpl loginLogServiceImpl;

    @Resource
    private LoginLogMapper loginLogMapper;


    /**
     * 保存或更新登录日志
     * @param userId
     */
    @Transactional
    public Integer saveOrUpdateLoginLog(Long userId) {
        LoginLog loginLog = new LoginLog();

        // 1、用户ID
        loginLog.setUserId(userId);
        // 2、登录时间
        loginLog.setLoginTime( LocalDateTime.now());
        // 3、登录ip
        loginLog.setLoginIp(getClientIpAddress());

        // 3、保存登录日志
        int insert = loginLogMapper.insert(loginLog);

        return insert;
    }

    /**
     * 分页查询
     * @param req
     */
    public ResultVO<IPage<LoginLogVo>> pageLoginLog(LoginLogQueryReq req) {
        // 分页构造器
        Page<LoginLog> page = new Page<>(req.getPageNum(), req.getPageSize());

        // 条件构造器
        LambdaQueryWrapper<LoginLog> queryWrapper = new LambdaQueryWrapper<>();

        // 获取当前用户的角色
        String role = getRole();

        // 如果不是管理员角色，添加用户ID的查询条件
        if (!RoleEnum.SYSTEM.getRole().equals(role)) {
            queryWrapper.eq(LoginLog::getUserId, req.getUserId());
        }

        // 执行分页查询方法
        IPage<LoginLog> loginLogPage = loginLogMapper.selectPage(page, queryWrapper);

        // 转换为vo类
        IPage<LoginLogVo> convert = loginLogPage.convert(LoginLogConvert.I::convertLoginLogVO);

        return ResultVO.success(convert);
    }


    /**
     * 批量删除登录ID信息
     * @param ids 删除的多ID
     * @return
     */
    public ResultVO deleteLoginLogByIds(List<Long> ids) {
        // 执行批量删除
        boolean success = loginLogServiceImpl.removeByIds(ids);

        return ResultVO.success(success);
    }

    /**
     * 辅助方法---获取用户的ip地址
     */
    private String getClientIpAddress() {
        if (request == null) {
            return "unknown";
        }

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
