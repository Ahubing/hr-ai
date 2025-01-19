package com.open.hr.ai.controller;

import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.hr.ai.bean.req.AmMaskAddReq;
import com.open.hr.ai.bean.req.AmMaskUpdateReq;
import com.open.hr.ai.bean.vo.AmMaskSearchReq;
import com.open.hr.ai.bean.vo.AmMaskTypeVo;
import com.open.hr.ai.bean.vo.AmMaskVo;
import com.open.hr.ai.config.HrAIBaseController;
import com.open.hr.ai.manager.AmMaskManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @Date 2025/1/19 15:51
 */

@Slf4j
@Api(tags = "AI角色管理类")
@RestController
public class AmMaskController extends HrAIBaseController {


    @Resource
    private AmMaskManager amMaskManager;

    @ApiOperation("新增面具")
    @VerifyUserToken
    @PostMapping("/amMask/add")
    public ResultVO addAmMask(@RequestBody @Valid AmMaskAddReq req) {
        return amMaskManager.addAmMask(getUserId(), req);
    }



    @ApiOperation("更新面具")
    @VerifyUserToken
    @PostMapping("/amMask/update")
    public ResultVO updateAmMask(@RequestBody @Valid AmMaskUpdateReq req) {
        return amMaskManager.updateAmMask(getUserId(), req);
    }

    @ApiOperation("删除面具")
    @VerifyUserToken
    @GetMapping("/amMask/delete")
    public ResultVO deleteAmMask(@RequestParam(value = "id", required = true) Long id) {
        return amMaskManager.deleteAmMask(getUserId(), id);
    }

    @ApiOperation("根据面具id查面具详情")
    @VerifyUserToken
    @GetMapping("/amMask/getById")
    public ResultVO getAmMaskDetail(@RequestParam(value = "id", required = true) Long id) {
        return amMaskManager.searchAmMaskById( id,getUserId());
    }




    @ApiOperation("查询面具")
    @VerifyUserToken
    @PostMapping("/amMask/search")
    public ResultVO<PageVO<AmMaskVo>> searchMaskVoList(@RequestBody @Valid AmMaskSearchReq req) {
        return amMaskManager.searchAmMask(req,getUserId());
    }

    @ApiOperation("查询面具类型")
    @VerifyUserToken
    @GetMapping("/amMask/type")
    public ResultVO<List<AmMaskTypeVo>> getAmMaskType() {
        return amMaskManager.getAmMaskType();
    }


}
