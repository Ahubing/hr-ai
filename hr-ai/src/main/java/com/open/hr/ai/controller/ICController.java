package com.open.hr.ai.controller;

import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.hr.ai.bean.req.IcRecordAddReq;
import com.open.hr.ai.bean.req.IcRecordPageReq;
import com.open.hr.ai.bean.req.IcSpareTimeReq;
import com.open.hr.ai.bean.vo.IcGroupDaysVo;
import com.open.hr.ai.bean.vo.IcRecordVo;
import com.open.hr.ai.bean.vo.IcSpareTimeVo;
import com.open.hr.ai.config.HrAIBaseController;
import com.open.hr.ai.manager.ICManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Api(tags = "ic 面试日历")
@Slf4j
@RestController("/ic")
public class ICController extends HrAIBaseController {

    @Resource
    private ICManager icManager;

    @ApiOperation("获取所有空闲时间")
    @VerifyUserToken
    @PostMapping("/getSpareTime")
    public ResultVO<IcSpareTimeVo> getSpareTime(@RequestBody @Valid IcSpareTimeReq spareTimeReq) {
        return ResultVO.success(icManager.getSpareTime(spareTimeReq));
    }

    @ApiOperation("预约面试")
    @VerifyUserToken
    @PostMapping("/appointInterview")
    public ResultVO<String> appointInterview(@RequestBody @Valid IcRecordAddReq req) {
        req.setAdminId(getUserId());
        return ResultVO.success(icManager.appointInterview(req));
    }

    @ApiOperation("取消面试预约")
    @VerifyUserToken
    @GetMapping("/cancelInterview")
    public ResultVO<Boolean> cancelInterview(@RequestParam(value = "icUuid") @ApiParam("面试uuid") String icUuid,
                                             @RequestParam(value = "cancelWho") @ApiParam("谁取消了，1-招聘方，2-受聘方") Integer cancelWho) {
        return ResultVO.success(icManager.cancelInterview(icUuid,cancelWho));
    }

    @ApiOperation("修改面试时间")
    @VerifyUserToken
    @GetMapping("/modifyTime")
    public ResultVO<Boolean> modifyTime(@RequestParam(value = "icUuid") @ApiParam("面试uuid") String icUuid,
                                        @RequestParam(value = "newTime") @ApiParam("新面试时间") LocalDateTime newTime) {
        return ResultVO.success(icManager.modifyTime(icUuid,newTime));
    }

    @ApiOperation("获取最近n天面试日历（群面）")
    @VerifyUserToken
    @GetMapping("/getGroupDaysIC")
    public ResultVO<List<IcGroupDaysVo>> getGroupDaysIC(@RequestParam(value = "dayNum") @ApiParam("天数") Integer dayNum) {
        return ResultVO.success(icManager.getGroupDaysIC(getUserId(),dayNum));
    }

    @ApiOperation("分页查询所有面试")
    @VerifyUserToken
    @PostMapping("/pageIcRecord")
    public ResultVO<PageVO<IcRecordVo>> pageIcRecord(@RequestBody @Valid IcRecordPageReq req) {
        req.setAdminId(getUserId());
        return ResultVO.success(icManager.pageIcRecord(req));
    }
}
