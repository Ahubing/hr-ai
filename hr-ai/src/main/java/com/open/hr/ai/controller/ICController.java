package com.open.hr.ai.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.ai.eros.ai.tool.tmp.IcRecordAddReq;
import com.open.ai.eros.ai.tool.tmp.IcRecordPageReq;
import com.open.ai.eros.ai.tool.tmp.IcSpareTimeReq;
import com.open.ai.eros.ai.tool.tmp.IcGroupDaysVo;
import com.open.ai.eros.ai.tool.tmp.IcRecordVo;
import com.open.ai.eros.ai.tool.tmp.IcSpareTimeVo;
import com.open.ai.eros.ai.tool.tmp.InterviewStatusEnum;
import com.open.ai.eros.ai.tool.tmp.ICManager;
import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.IcRecord;
import com.open.ai.eros.db.mysql.hr.service.impl.IcRecordServiceImpl;
import com.open.hr.ai.config.HrAIBaseController;
import com.open.hr.ai.convert.IcRecordConvert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "ic 面试日历")
@Slf4j
@RestController("/ic")
public class ICController extends HrAIBaseController {

    @Resource
    private ICManager icManager;

    @Resource
    private IcRecordServiceImpl recordService;

    @ApiOperation("获取所有空闲时间")
    @VerifyUserToken
    @PostMapping("/getSpareTime")
    public ResultVO<IcSpareTimeVo> getSpareTime(@RequestBody @Valid IcSpareTimeReq spareTimeReq) {
        return icManager.getSpareTime(spareTimeReq);
    }

    @ApiOperation("预约面试")
    @VerifyUserToken
    @PostMapping("/appointInterview")
    public ResultVO<String> appointInterview(@RequestBody @Valid IcRecordAddReq req) {
        req.setAdminId(getUserId());
        return icManager.appointInterview(req);
    }

    @ApiOperation("取消面试预约")
    @VerifyUserToken
    @GetMapping("/cancelInterview")
    public ResultVO<Boolean> cancelInterview(@RequestParam(value = "icUuid") @ApiParam("面试uuid") String icUuid,
                                             @RequestParam(value = "cancelWho") @ApiParam("谁取消了，1-招聘方，2-受聘方") Integer cancelWho) {
        return icManager.cancelInterview(icUuid,cancelWho);
    }

    @ApiOperation("修改面试时间")
    @VerifyUserToken
    @GetMapping("/modifyTime")
    public ResultVO<Boolean> modifyTime(@RequestParam(value = "icUuid") @ApiParam("面试uuid") String icUuid,
                                        @RequestParam(value = "newTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @ApiParam("新面试时间") LocalDateTime newTime) {
        return icManager.modifyTime(icUuid,newTime);
    }

    @ApiOperation("获取最近n天面试日历（群面）")
    @VerifyUserToken
    @GetMapping("/getGroupDaysIC")
    public ResultVO<List<IcGroupDaysVo>> getGroupDaysIC(@RequestParam(value = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @ApiParam("开始日期") LocalDate startDate,
                                                        @RequestParam(value = "endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @ApiParam("截止日期") LocalDate endDate,
                                                        @RequestParam(value = "deptId",required = false) @ApiParam("部门id") Integer deptId,
                                                        @RequestParam(value = "postId",required = false) @ApiParam("职位id") Integer postId) {
        return icManager.getGroupDaysIC(getUserId(),startDate,endDate,deptId,postId);
    }

    @ApiOperation("分页查询所有面试")
    @VerifyUserToken
    @PostMapping("/pageIcRecord")
    public ResultVO<PageVO<IcRecordVo>> pageIcRecord(@RequestBody @Valid IcRecordPageReq req) {
        req.setAdminId(getUserId());
        LambdaQueryWrapper<IcRecord> queryWrapper = new LambdaQueryWrapper<>();
        Long adminId = req.getAdminId();
        Integer status = req.getInterviewStatus();
        String type = req.getInterviewType();
        Integer pageNum = req.getPage();
        Integer pageSize = req.getPageSize();
        queryWrapper.eq(adminId != null,IcRecord::getAdminId,adminId)
                .eq(status != null,IcRecord::getCancelStatus,status)
                .eq(StringUtils.isNotEmpty(type),IcRecord::getInterviewType,type);
        Page<IcRecord> page = new Page<>(pageNum, pageSize);
        Page<IcRecord> icRecordPage = recordService.page(page, queryWrapper);
        List<IcRecordVo> icRecordVos = icRecordPage.getRecords().stream().map(IcRecordConvert.I::convertIcRecordVo).collect(Collectors.toList());
        if(CollectionUtil.isNotEmpty(icRecordVos)){
            icRecordVos.forEach(item->
                    item.setCancelStatus(item.getStartTime().isAfter(LocalDateTime.now()) ? item.getCancelStatus() : InterviewStatusEnum.DEPRECATED.getStatus()));
        }
        return ResultVO.success(PageVO.build(icRecordPage.getTotal(), icRecordVos));
    }
}
