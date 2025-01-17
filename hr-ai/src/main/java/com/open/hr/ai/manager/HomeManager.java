package com.open.hr.ai.manager;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.AmPosition;
import com.open.ai.eros.db.mysql.hr.entity.AmPrompt;
import com.open.ai.eros.db.mysql.hr.entity.AmResume;
import com.open.ai.eros.db.mysql.hr.service.impl.AmPositionServiceImpl;
import com.open.ai.eros.db.mysql.hr.service.impl.AmResumeServiceImpl;
import com.open.hr.ai.bean.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * 逻辑按照php处理的
 *
 * @Date 2025/1/6 20:00
 */
@Slf4j
@Component
public class HomeManager {

    @Resource
    private AmPositionServiceImpl amPositionService;

    @Resource
    private AmResumeServiceImpl amResumeService;


    public ResultVO<AmHomeDataVo> getHomeDetail(Long adminId) {

        try {
            LambdaQueryWrapper<AmPosition> queryWrapper = new LambdaQueryWrapper<>();
            AmHomeDataVo amHomeDataVo = new AmHomeDataVo();
            queryWrapper.eq(AmPosition::getAdminId, adminId);
            int totalResume = amPositionService.count(queryWrapper);
            queryWrapper.eq(AmPosition::getIsOpen, 1);
            int openPositionsCount = amPositionService.count(queryWrapper);
            int count = amResumeService.count(new LambdaQueryWrapper<AmResume>().eq(AmResume::getAdminId, adminId));
            int pending_interview = 0;
            List<Integer> last_20_day_resumes = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20);
            AmRobotVo amRobotVo = new AmRobotVo();
            amRobotVo.setToday_num(0);
            amRobotVo.setAssistant_status(1);
            amRobotVo.setAccount_status(1);
            AmRobotNewsVo amRobotNewsVo = new AmRobotNewsVo();
            amRobotNewsVo.setContent("AI助手完成1次候选人筛选");
            amRobotNewsVo.setType(0);
            amRobotNewsVo.setType_txt("AI助手");
            JSONObject jsonObject = new JSONObject();
            JSONObject rechat = new JSONObject();
            JSONObject communication = new JSONObject();
            JSONObject ResumeTypes = new JSONObject();
            JSONObject review = new JSONObject();

            AmRechatVo amRechatVo = new AmRechatVo();


            amRechatVo.setNums(0);
            amRechatVo.setActives(0);
            rechat.put("week", amRechatVo);
            rechat.put("month", amRechatVo);
            jsonObject.put("rechat", rechat);

            AmCommunicationVo amCommunicationVo = new AmCommunicationVo();
            amCommunicationVo.setChat(0);
            amCommunicationVo.setGreet(0);
            amCommunicationVo.setReply(0);

            communication.put("week", amCommunicationVo);
            communication.put("month", amCommunicationVo);
            jsonObject.put("communication", communication);

            ResumeTypes.put("total", 0);
            ResumeTypes.put("exchange_phone", 0);
            ResumeTypes.put("exchange_wechat", 0);
            ResumeTypes.put("take", 0);
            jsonObject.put("get_resume_types", ResumeTypes);

            review.put("invite", 0);
            review.put("accept", 0);
            review.put("visits", 0);
            jsonObject.put("review", review);

            jsonObject.put("offer", 0);
            jsonObject.put("entry", 0);

            amHomeDataVo.setTotal_resume(totalResume);
            amHomeDataVo.setOpen_positions(openPositionsCount);
            amHomeDataVo.setNew_resume(count);
            amHomeDataVo.setPending_interview(pending_interview);
            amHomeDataVo.setAi_robot(amRobotVo);
            amHomeDataVo.setNews(amRobotNewsVo);
            amHomeDataVo.setData_funnels(jsonObject);
            amHomeDataVo.setLast_20_day_resumes(last_20_day_resumes);
            return ResultVO.success(amHomeDataVo);
        } catch (Exception e) {
            log.error("获取招聘首页异常", e);
        }
        return ResultVO.fail("获取招聘首页异常");
    }

}
