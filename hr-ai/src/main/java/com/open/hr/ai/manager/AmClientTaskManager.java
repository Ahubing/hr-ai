package com.open.hr.ai.manager;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.db.mysql.hr.entity.AmClientTasks;
import com.open.ai.eros.db.mysql.hr.entity.AmPosition;
import com.open.ai.eros.db.mysql.hr.entity.AmZpLocalAccouts;
import com.open.ai.eros.db.mysql.hr.service.impl.AmClientTasksServiceImpl;
import com.open.hr.ai.constant.AmClientTaskStatusEnums;
import com.open.hr.ai.constant.ClientTaskTypeEnums;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author
 * @Date 2025/1/12 18:22
 */
@Slf4j
@Component
public class AmClientTaskManager {


    @Resource
    private AmClientTasksServiceImpl amClientTasksService;

    /**
     * 批量关闭打开岗位
     */
    public Boolean batchCloseOrOpenPosition(String bossId, AmPosition amPosition, Integer status) {
        try {
                AmClientTasks amClientTasks = new AmClientTasks();
                amClientTasks.setBossId(bossId);
                amClientTasks.setCreateTime(LocalDateTime.now());
                amClientTasks.setTaskType(ClientTaskTypeEnums.SWITCH_JOB_STATE.getType());
                amClientTasks.setOrderNumber(ClientTaskTypeEnums.SWITCH_JOB_STATE.getOrder());
                amClientTasks.setStatus(AmClientTaskStatusEnums.NOT_START.getStatus());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("encrypt_id", amPosition.getEncryptId());
                amClientTasks.setData(jsonObject.toJSONString());
                boolean result = amClientTasksService.save(amClientTasks);
                log.info("batchCloseOrOpenPosition amClientTasks={} result={}", amClientTasks, result);
        } catch (Exception e) {
            log.error("batchCloseOrOpenPosition error", e);
            return false;
        }
        return true;
    }

}
