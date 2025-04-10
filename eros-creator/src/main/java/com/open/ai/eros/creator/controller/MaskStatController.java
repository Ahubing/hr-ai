package com.open.ai.eros.creator.controller;

import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.creator.bean.req.GetMaskStatDayReq;
import com.open.ai.eros.creator.bean.req.MasksInfoSearchVo;
import com.open.ai.eros.creator.bean.vo.MaskStatCountVo;
import com.open.ai.eros.creator.bean.vo.MaskStatDayVo;
import com.open.ai.eros.creator.bean.vo.MaskStatListVo;
import com.open.ai.eros.creator.bean.vo.MasksInfoVo;
import com.open.ai.eros.creator.config.CreatorBaseController;
import com.open.ai.eros.creator.manager.MaskStatManager;
import com.open.ai.eros.db.mysql.ai.entity.MaskStatList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@Slf4j
@Api(tags = "面具统计消耗记录")
@RestController
public class MaskStatController extends CreatorBaseController {


    @Resource
    private MaskStatManager maskStatDayManager;


    /**
     * 获取面具按日统计消耗记录
     *
     * @return 统计记录
     */
    @ApiOperation(value = "获取面具消耗记录")
    @VerifyUserToken(role = {RoleEnum.SYSTEM, RoleEnum.CREATOR})
    @GetMapping("/mask/stat/day")
    public ResultVO<PageVO<MaskStatDayVo>> getMaskStatDay(@Valid GetMaskStatDayReq req) {
        try {
            if (req.getPage() == null) {
                req.setPage(1);
            }
            if (req.getPageSize() == null) {
                req.setPageSize(10);
            }
            if (RoleEnum.CREATOR.getRole().equals(getRole())) {
                req.setUserId(getUserId());
            }
            return maskStatDayManager.getDailyStats(req);
        } catch (Exception e) {
            log.error("获取面具每日统计消耗记录失败", e);
            return ResultVO.fail("获取失败: " + e.getMessage());
        }
    }


    @ApiOperation(value = "获取面具今日统计记录")
    @VerifyUserToken(role = {RoleEnum.SYSTEM, RoleEnum.CREATOR})
    @GetMapping("/mask/stat/today")
    public ResultVO<MaskStatDayVo> getMaskToday(@RequestParam(value = "userId",required = false) Long userId) {
        if(!getRole().equals(RoleEnum.SYSTEM.getRole())){
            userId = getUserId();
        }
        return maskStatDayManager.getMaskToday(userId);
    }


    @ApiOperation(value = "获取面具一周统计记录")
    @VerifyUserToken(role = {RoleEnum.SYSTEM, RoleEnum.CREATOR})
    @GetMapping("/mask/stat/week")
    public ResultVO<List<MaskStatDayVo>> getMaskWeek() {
        return maskStatDayManager.getMaskWeek(getUserId());
    }


    @ApiOperation(value = "获取创作者面具相关信息")
    @VerifyUserToken(role = {RoleEnum.SYSTEM, RoleEnum.CREATOR})
    @PostMapping("/mask/info/stat")
    public ResultVO<MasksInfoVo> getMasksInfo(@RequestBody @Valid MasksInfoSearchVo masksInfoSearchVo) {
        Long userId = masksInfoSearchVo.getUserId();
        if(!getRole().equals(RoleEnum.SYSTEM.getRole()) || Objects.isNull(userId)){
            userId = getUserId();
        }
        return maskStatDayManager.getPeopleMasksInfo(userId, masksInfoSearchVo.getTimeWindow());
    }

    @ApiOperation(value = "获取创作者面具相关信息")
    @VerifyUserToken(role = {RoleEnum.SYSTEM, RoleEnum.CREATOR})
    @PostMapping("/mask/stat/list")
    public ResultVO<List<MaskStatListVo>> getMasksInfoList(@RequestBody @Valid MasksInfoSearchVo masksInfoSearchVo) {
        Long userId = masksInfoSearchVo.getUserId();
        if(!getRole().equals(RoleEnum.SYSTEM.getRole()) || Objects.isNull(userId)){
            userId = getUserId();
        }
        return maskStatDayManager.getPeopleMasksStatList(userId, masksInfoSearchVo.getTimeWindow());
    }


    @ApiOperation(value = "管理员获取面具使用信息")
    @VerifyUserToken(role = {RoleEnum.SYSTEM})
    @GetMapping("/mask/stat/count")
    public ResultVO<MaskStatCountVo> getMaskCounts() {
        return maskStatDayManager.getMaskStatData();
    }


}
