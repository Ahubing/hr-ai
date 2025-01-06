package com.open.ai.eros.db.mysql.hr.service.impl;

import com.open.ai.eros.db.mysql.hr.entity.AmAdmin;
import com.open.ai.eros.db.mysql.hr.mapper.AmAdminMapper;
import com.open.ai.eros.db.mysql.hr.service.IAmAdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Service
public class AmAdminServiceImpl extends ServiceImpl<AmAdminMapper, AmAdmin> implements IAmAdminService {

    @Autowired
    private HttpServletRequest request;


    /**
     * 根据账号获取用户信息
     *
     * @param account
     * @return
     */
    public AmAdmin getUserByAccount(String account) {
        return this.baseMapper.getUserByAccount(account);
    }

    public int addUser(String email, String password, String username, String company, String mobile) {

        AmAdmin users = new AmAdmin();
        users.setEmail(email);
        users.setPassword(password);
        users.setUsername(username);
        users.setCompany(company);
        users.setCreateTime((int) (System.currentTimeMillis() / 1000));
        users.setLastLoginTime(LocalDateTime.now());
        users.setMobile(mobile);
        users.setSalt(generateSalt());// 自动生成的
        users.setStatus(0);
        users.setSpecialPermission(null);
        users.setLastLoginClientIp(getClientIpAddress());//获取id
        // 新增加的是 普通用户
        return this.getBaseMapper().insert(users);
    }

    private String generateSalt() {
        Random RANDOM = new Random();
        StringBuilder soleResult = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            //判断产生的随机数是0还是1，是0进入if语句用于输出数字，是1进入else用于输出字符
            int mark = Math.random() >= 0.5 ? 1 : 0;
            if (0 == mark) {
                soleResult.append(RANDOM.nextInt(10));
            } else {
                soleResult.append((char) ('A' + RANDOM.nextInt(26)));
            }
        }
        return soleResult.toString();
    }

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
