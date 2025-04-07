package com.open.hr.ai.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.open.ai.eros.common.util.DistributedLockUtils;
import com.open.ai.eros.db.mysql.hr.entity.AmAdmin;
import com.open.ai.eros.db.mysql.hr.service.impl.AmAdminServiceImpl;
import com.open.hr.ai.constant.AmAdminRoleEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.Lock;

@Component
@Slf4j
@EnableScheduling
public class AmAdminVipJob {


    @Resource
    private AmAdminServiceImpl amAdminService;




    @Scheduled(cron = "0/5 * * * * ?")
    public void checkVip() {
        Lock lock = DistributedLockUtils.getLock("checkVIP", 60);
        if (lock.tryLock()) {
            try {
                LambdaQueryWrapper<AmAdmin> queryWrapper = new QueryWrapper<AmAdmin>().lambda();
                queryWrapper.eq(AmAdmin::getRole, AmAdminRoleEnum.VIP.getType());
                List<AmAdmin> amAdmins = amAdminService.list(queryWrapper);
                for (AmAdmin amAdmin : amAdmins) {
                    //取出过期时间,判断是否过期,如果过期则将用户改成普通用户
                    if (amAdmin.getExpireTime().isBefore(LocalDateTime.now())) {
                        amAdmin.setRole(AmAdminRoleEnum.COMMON.getType());
                        amAdmin.setUpdateTime(LocalDateTime.now());
                        boolean result = amAdminService.updateById(amAdmin);
                        log.info("更改用户状态为普通用户 result={},id={}", result,amAdmin.getId());
                    }
                }
            } finally {
                lock.unlock();
            }
        }
    }
}
