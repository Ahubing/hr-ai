package com.open.hr.ai.controller;

import com.open.ai.eros.ai.constatns.ModelTemplateEnum;
import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.constants.ReviewStatusEnums;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.hr.ai.bean.req.AmMaskAddReq;
import com.open.hr.ai.bean.req.AmMaskUpdateReq;
import com.open.hr.ai.bean.req.AmNewMaskAddReq;
import com.open.hr.ai.bean.req.AmNewMaskUpdateReq;
import com.open.hr.ai.bean.vo.*;
import com.open.hr.ai.config.HrAIBaseController;
import com.open.hr.ai.manager.AmMaskManager;
import com.open.hr.ai.manager.AmNewMaskManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @Date 2025/1/19 15:51
 */

@Slf4j
@Api(tags = "新的AI角色管理类")
@RestController
public class AmNewMaskController extends HrAIBaseController {


    @Resource
    private AmNewMaskManager amNewMaskManager;

    @ApiOperation("新增面具")
    @VerifyUserToken
    @PostMapping("/amNewMask/add")
    public ResultVO addAmNewMask(@RequestBody @Valid AmNewMaskAddReq req) {
        return amNewMaskManager.addAmNewMask(getUserId(), req);
    }




    @ApiOperation("更新面具")
    @VerifyUserToken
    @PostMapping("/amNewMask/update")
    public ResultVO updateAmMask(@RequestBody @Valid AmNewMaskUpdateReq req) {
        return amNewMaskManager.updateAmNewMask(getUserId(), req);
    }

    @ApiOperation("删除面具")
    @VerifyUserToken
    @GetMapping("/amNewMask/delete")
    public ResultVO deleteAmMask(@RequestParam(value = "id", required = true) Long id) {
        return amNewMaskManager.deleteAmNewMask(getUserId(), id);
    }

    @ApiOperation("根据面具id查面具详情")
    @VerifyUserToken
    @GetMapping("/amNewMask/getById")
    public ResultVO getAmMaskDetail(@RequestParam(value = "id", required = true) Long id) {
        return amNewMaskManager.searchAmMaskById( id,getUserId());
    }




    @ApiOperation("查询面具")
    @VerifyUserToken
    @PostMapping("/amNewMask/search")
    public ResultVO<PageVO<AmNewMaskVo>> searchMaskVoList(@RequestBody @Valid AmMaskSearchReq req) {
        return amNewMaskManager.searchAmNewMask(req,getUserId());
    }

    @ApiOperation("查询面具类型")
    @VerifyUserToken
    @GetMapping("/amNewMask/type")
    public ResultVO<List<AmMaskTypeVo>> getAmMaskType() {
        return amNewMaskManager.getAmMaskType();
    }


    @ApiOperation("获取支持的模型模版列表")
    @VerifyUserToken
    @GetMapping("/amNewMask/model/list")
    public ResultVO<List<AmModelVo>> model(@RequestParam(value = "template", required = false) String template) {
        List<AmModelVo> modelVos = new ArrayList<>();
        for (ModelTemplateEnum value : ModelTemplateEnum.values()) {
            if (StringUtils.isNoneEmpty(template) && !value.getTemplate().equals(template)) {
                continue;
            }
            String modelTemplate = value.getTemplate();
            String desc = value.getDesc();
            for (String model : value.getModels()) {
                AmModelVo modelVo = new AmModelVo();
                modelVo.setName(String.format("%s:%s", desc, model));
                modelVo.setValue(String.format("%s:%s", modelTemplate, model));
                modelVos.add(modelVo);
            }
        }
        return ResultVO.success(modelVos);
    }



    @ApiOperation("获取流程状态")
    @VerifyUserToken
    @GetMapping("/amNewMask/review/status")
    public ResultVO<List<ReviewStatusVo>> reviewStatus() {
        List<ReviewStatusVo> reviewStatusVos = new ArrayList<>();
        for (ReviewStatusEnums statusEnums : ReviewStatusEnums.values()) {
            if (statusEnums.getStatus() == ReviewStatusEnums.ABANDON.getStatus()
            || statusEnums.getStatus() == ReviewStatusEnums.OFFER_ISSUED.getStatus()
            || statusEnums.getStatus() == ReviewStatusEnums.ONBOARD.getStatus()
            ) {
                // 不展示给前端
                continue;
            }
            ReviewStatusVo reviewStatusVo = new ReviewStatusVo();
            reviewStatusVo.setCode(statusEnums.getStatus());
            reviewStatusVo.setDesc(statusEnums.getDesc());
            reviewStatusVo.setKey(statusEnums.getKey());
            reviewStatusVos.add(reviewStatusVo);
        }
        return ResultVO.success(reviewStatusVos);
    }
}
