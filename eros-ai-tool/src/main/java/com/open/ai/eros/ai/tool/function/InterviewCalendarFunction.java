package com.open.ai.eros.ai.tool.function;

import cn.hutool.core.collection.CollectionUtil;
import com.open.ai.eros.ai.tool.tmp.ICTmpManager;
import com.open.ai.eros.ai.tool.tmp.tmpbean.IcRecordAddReq;
import com.open.ai.eros.ai.tool.tmp.tmpbean.IcSpareTimeReq;
import com.open.ai.eros.ai.tool.tmp.tmpbean.IcSpareTimeVo;
import com.open.ai.eros.common.vo.ResultVO;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Slf4j
public class InterviewCalendarFunction {

    @Resource
    private ICTmpManager icTmpManager;

    @Tool(name = "get_spare_time", value = {"查询可用面试时间。返回时间段内所有可面试时间"})
    public String get_spare_time(@P("开始时间（IOS时间）") String startTime,
                                 @P("结束时间（IOS时间）") String endTime,
                                 @P("当前角色的面具ID") String maskId) {
        log.info("get_spare_time function params startTime:{} endTime:{} maskId:{}", startTime, endTime, maskId);
        LocalDateTime sTime = LocalDateTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime eTime = LocalDateTime.parse(endTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        ResultVO<IcSpareTimeVo> resultVO = icTmpManager.getSpareTime(new IcSpareTimeReq(Long.parseLong(maskId), sTime, eTime));

        return resultVO.getCode() == 200 ? buildSpareTimeResponse(resultVO.getData()) : "该时间段不可用";
    }

    @Tool(name = "appoint_interview", value = {"通过分析求职者的上下文,获取到求职者期望的面试开始时间以及当前的面具id mask_id,招聘者的id admin_id,面试开始时间,格式为yyyy:MM:ddThh:mm:ss,这个职位的id positionId,以及当前boss账号的id accountId，求职者在平台的uid"})
    public String appoint_interview(@P("当前角色的面具id maskId") String maskId,
                                    @P("当前管理员/hr的id adminId") String adminId,
                                    @P("当前求职者uid employeeUid") String employeeUid,
                                    @P("求职者期望的面试开始时间") String startTime,
                                    @P("当前招聘的职位id positionId") String positionId,
                                    @P("当前角色所登录的平台账号的id accountId") String accountId) {
        log.info("appoint_interview function params maskId:{} adminId:{} employeeUid:{} startTime:{} positionId:{} accountId:{}", maskId, adminId, employeeUid, startTime, positionId, accountId);
        LocalDateTime sTime = LocalDateTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        ResultVO<String> resultVO = icTmpManager.appointInterview(new IcRecordAddReq(Long.parseLong(maskId),Long.parseLong(adminId),employeeUid,sTime,Long.parseLong(positionId),accountId));

        return (resultVO.getCode() == 200 && resultVO.getData() != null) ? "这边帮你约好了面试，我们面试都会有一个id，你的面试的id是" + resultVO.getData() + "后续你可以通过这个id，我这边可以帮你取消或者修改面试时间\n" : "刚刚看了一下后台系统，这个时间安排了别的面试，换个时间吧\n";
    }

    @Tool(name = "cancel_interview", value = {"通过分析求职者的上下文,判断求职者想要取消面试,并获取到上下文中求职者面试的id"})
    public String cancel_interview(@P("求职者想要取消面试的id") String id) {
        log.info("cancel_interview function params id:{}", id);
        ResultVO<Boolean> resultVO = icTmpManager.cancelInterview(id,2);

        return (resultVO.getCode() == 200 && resultVO.getData()) ? "OK，已经取消了\n" : "后台面试取消失败了，你看下你之前是不是取消过面试或者提供的面试id有问题\n";
    }

    @Tool(name = "modify_time", value = {"通过分析求职者的上下文,判断求职者想要修改面试时间,并获取到上下文中求职者面试的id和想要修改到的面试时间格式为yyyy-MM-ddThh:mm:ss"})
    public String modify_time(@P("求职者想要修改面试的id") String id,
                              @P("求职者想要修改面试的时间") String newTime) {
        log.info("modify_time function params id:{} newTime:{}", id, newTime);
        LocalDateTime sTime = LocalDateTime.parse(newTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        ResultVO<Boolean> resultVO = icTmpManager.modifyTime(id,sTime);

        return (resultVO.getCode() == 200 && resultVO.getData()) ? "OK，修改好了\n" : "后台面修改面试时间失败了，" + (resultVO.getCode() == 501 ? "系统显示这个时间我还有别的面试\n" : "你看下你之前是不是取消过面试或者提供的面试id有问题\n");
    }

    private String buildSpareTimeResponse(IcSpareTimeVo data) {
        List<IcSpareTimeVo.SpareDateVo> dateVos = data.getSpareDateVos();
        if (CollectionUtil.isNotEmpty(dateVos)) {
            StringBuilder sb = new StringBuilder();
            sb.append("我大概看了一下，我在这几个时间段是空闲的:\n");
            for (IcSpareTimeVo.SpareDateVo dateVo : dateVos) {
                LocalDate date = dateVo.getLocalDate();
                int monthValue = date.getMonthValue();
                int dayOfMonth = date.getDayOfMonth();
                sb.append(monthValue).append("月").append(dayOfMonth).append("日").append(":\n");
                for (IcSpareTimeVo.SparePeriodVo sparePeriodVo : dateVo.getSparePeriodVos()) {
                    LocalDateTime startTime = sparePeriodVo.getStartTime();
                    LocalDateTime endTime = sparePeriodVo.getEndTime();
                    int startHour = startTime.getHour();
                    int startMinute = startTime.getMinute();
                    int endHour = endTime.getHour();
                    int endMinute = endTime.getMinute();
                    sb.append(startHour).append("点").append(startMinute).append("分到").append(endHour).append("点").append(endMinute).append("分").append("\\");
                }
            }
            return sb.toString();
        }
        return "抱歉，这个时间我有别的面试了，换个时间吧";
    }
}
