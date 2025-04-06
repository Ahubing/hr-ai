package com.open.hr.ai.util;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.common.constants.RequestInfoTypeEnum;
import com.open.ai.eros.db.mysql.hr.entity.AmClientTasks;
import com.open.ai.eros.db.mysql.hr.entity.AmResume;
import com.open.ai.eros.db.mysql.hr.entity.AmZpLocalAccouts;
import com.open.ai.eros.db.mysql.hr.service.impl.AmClientTasksServiceImpl;
import com.open.hr.ai.constant.AmClientTaskStatusEnums;
import com.open.hr.ai.constant.ClientTaskTypeEnums;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

/**
 * @Date 2025/3/25 23:46
 */
@Slf4j
@Component
public class AmClientTaskUtil {



    @Resource
    private AmClientTasksServiceImpl amClientTasksService;

    public  void buildRequestTask(AmZpLocalAccouts amZpLocalAccouts, Integer uid, AmResume amResume, Boolean needAttachmentResume) {
        AmClientTasks amClientTasks = new AmClientTasks();
        amClientTasks.setBossId(amZpLocalAccouts.getId());
        amClientTasks.setTaskType(ClientTaskTypeEnums.REQUEST_INFO.getType());
        amClientTasks.setOrderNumber(ClientTaskTypeEnums.REQUEST_INFO.getOrder());
        HashMap<String, Object> hashMap = new HashMap<>();
        HashMap<String, Object> searchDataMap = new HashMap<>();
        hashMap.put("user_id", uid);
        if (needAttachmentResume) {
            hashMap.put("info_type", Collections.singletonList(RequestInfoTypeEnum.ATTACHMENT_RESUME.getType()));
            amClientTasks.setDetail(String.format("请求用户%s附件简历信息",amResume.getName()));
        }else {
            hashMap.put("info_type", Collections.singletonList("resume"));
            amClientTasks.setDetail(String.format("请求用户%s在线简历信息",amResume.getName()));
        }
        if (Objects.nonNull(amResume.getEncryptGeekId())) {
            searchDataMap.put("encrypt_geek_id", amResume.getEncryptGeekId());
        }
        if (StringUtils.isNotBlank(amResume.getName())) {
            searchDataMap.put("name", amResume.getName());
        }

        hashMap.put("search_data", searchDataMap);
        amClientTasks.setData(JSONObject.toJSONString(hashMap));
        amClientTasks.setStatus(AmClientTaskStatusEnums.NOT_START.getStatus());
        amClientTasks.setCreateTime(LocalDateTime.now());
        amClientTasks.setUpdateTime(LocalDateTime.now());
        boolean result = amClientTasksService.save(amClientTasks);
        if (result) {
            log.info("添加request_info任务成功,任务id:{}", amClientTasks.getId());
        } else {
            log.error("添加request_info任务失败,bossId:{}", amZpLocalAccouts.getId());
        }
    }
}
