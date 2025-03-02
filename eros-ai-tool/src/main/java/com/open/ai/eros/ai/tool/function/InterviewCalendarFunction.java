package com.open.ai.eros.ai.tool.function;

import cn.hutool.core.collection.CollectionUtil;
import com.open.ai.eros.ai.tool.tmp.ICTmpManager;
import com.open.ai.eros.ai.tool.tmp.tmpbean.IcRecordAddReq;
import com.open.ai.eros.ai.tool.tmp.tmpbean.IcSpareTimeReq;
import com.open.ai.eros.ai.tool.tmp.tmpbean.IcSpareTimeVo;
import com.open.ai.eros.common.vo.ResultVO;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class InterviewCalendarFunction {

    @Resource
    private ICTmpManager icTmpManager;

    @Tool(name = "get_spare_time", value = {"通过分析求职者的上下文,推測获取到求职者期望面试的起始时间和截止时间,比如如果用戶希望明天下午面試,格式为yyyy-MM-ddThh:mm:ss。同时获取到当前角色的面具id。返回求职者期望时间段内的所有可用时间段"})
    public String get_spare_time(@P("推测求职者期望的面试开始时间,格式为yyyy-MM-ddThh:mm:ss") LocalDateTime startTime,
                                 @P("求职者期望的面试截止时间,格式为yyyy-MM-ddThh:mm:ss") LocalDateTime endTime,
                                 @P("当前角色的面具id maskId") Long maskId) {
        ResultVO<IcSpareTimeVo> resultVO = icTmpManager.getSpareTime(new IcSpareTimeReq(maskId, startTime, endTime));

        return resultVO.getCode() == 200 ? buildSpareTimeResponse(resultVO.getData()) : "这个时间我有别的面试了，换个时间吧\n";
    }

    @Tool(name = "appoint_interview", value = {"通过分析求职者的上下文,获取到求职者期望的面试开始时间以及当前的面具id mask_id,招聘者的id admin_id,面试开始时间,格式为yyyy:MM:ddThh:mm:ss,这个职位的id positionId,以及当前boss账号的id accountId，求职者在平台的uid"})
    public String appoint_interview(@P("当前角色的面具id maskId") Long maskId,
                                    @P("当前管理员/hr的id adminId") Long adminId,
                                    @P("当前求职者uid employeeUid") String employeeUid,
                                    @P("求职者期望的面试开始时间,格式为yyyy-MM-ddThh:mm:ss") LocalDateTime startTime,
                                    @P("当前招聘的职位id positionId") Long positionId,
                                    @P("当前角色所登录的平台账号的id accountId") String accountId) {
        ResultVO<String> resultVO = icTmpManager.appointInterview(new IcRecordAddReq(maskId,adminId,employeeUid,startTime,positionId,accountId));

        return (resultVO.getCode() == 200 && resultVO.getData() != null) ? "这边帮你约好了面试，我们面试都会有一个id，你的面试的id是" + resultVO.getData() + "后续你可以通过这个id，我这边可以帮你取消或者修改面试时间\n" : "刚刚看了一下后台系统，这个时间安排了别的面试，换个时间吧\n";
    }

    @Tool(name = "cancel_interview", value = {"通过分析求职者的上下文,判断求职者想要取消面试,并获取到上下文中求职者面试的id"})
    public String cancel_interview(@P("求职者想要取消面试的id") String id) {
        ResultVO<Boolean> resultVO = icTmpManager.cancelInterview(id,2);

        return (resultVO.getCode() == 200 && resultVO.getData()) ? "OK，已经取消了\n" : "后台面试取消失败了，你看下你之前是不是取消过面试或者提供的面试id有问题\n";
    }

    @Tool(name = "modify_time", value = {"通过分析求职者的上下文,判断求职者想要修改面试时间,并获取到上下文中求职者面试的id和想要修改到的面试时间格式为yyyy-MM-ddThh:mm:ss"})
    public String modify_time(@P("求职者想要修改面试的id") String id,
                              @P("求职者想要修改面试的时间,格式为yyyy-MM-ddThh:mm:ss") LocalDateTime newTime) {
        ResultVO<Boolean> resultVO = icTmpManager.modifyTime(id,newTime);

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
