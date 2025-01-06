package com.open.hr.ai.manager;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.AmPrompt;
import com.open.ai.eros.db.mysql.hr.entity.AmResume;
import com.open.ai.eros.db.mysql.hr.service.impl.AmPromptServiceImpl;
import com.open.ai.eros.db.mysql.hr.service.impl.AmResumeServiceImpl;
import com.open.hr.ai.bean.req.AddOrUpdateAmPromptReq;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 逻辑按照php处理的, 暂时未调试
 * @Date 2025/1/6 20:00
 */
@Slf4j
@Component
public class ResumeManager {

    @Resource
    private AmResumeServiceImpl amResumeService;



    public ResultVO resumeDetail(Integer id) {
        try {
            AmResume amResume = amResumeService.getById(id);
            return ResultVO.success(amResume);
        }catch (Exception e){
            log.error("获取简历详情 id={}",id,e);
        }
        return ResultVO.fail("获取简历详情异常");
    }




}
