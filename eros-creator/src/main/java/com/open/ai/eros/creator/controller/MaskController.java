package com.open.ai.eros.creator.controller;

import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.creator.bean.req.*;
import com.open.ai.eros.creator.bean.vo.*;
import com.open.ai.eros.creator.config.CreatorBaseController;
import com.open.ai.eros.creator.manager.MaskManager;
import com.open.ai.eros.db.constants.MaskEnum;
import com.open.ai.eros.db.constants.MaskStatusEnum;
import com.open.ai.eros.db.constants.MaskTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @类名：MaskController
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/13 23:24
 */

@Api(tags = "面具管理类")
@RestController
public class MaskController extends CreatorBaseController {


    @Autowired
    private MaskManager maskManager;


    @ApiOperation("面具tab")
    @GetMapping("/mask/tab")
    public ResultVO<List<MaskTypeVo>> getMaskTab() {
        List<MaskTypeVo> maskTypeVos = Arrays.stream(MaskTabEnum.values()).map(e -> {
            MaskTypeVo maskTypeVo = new MaskTypeVo();
            maskTypeVo.setType(e.getType());
            maskTypeVo.setDesc(e.getDesc());
            return maskTypeVo;
        }).collect(Collectors.toList());

        for (MaskTypeEnum value : MaskTypeEnum.values()) {
            MaskTypeVo maskTypeVo = new MaskTypeVo();
            maskTypeVo.setType(value.getType());
            maskTypeVo.setDesc(value.getDesc());
            maskTypeVos.add(maskTypeVo);
        }
        return ResultVO.success(maskTypeVos);
    }


    @ApiOperation("面具类别")
    @GetMapping("/mask/type")
    public ResultVO<List<MaskTypeVo>> getMaskType() {
        List<MaskTypeVo> maskTypeVos = Arrays.stream(MaskTypeEnum.values()).map(e -> {
            MaskTypeVo maskTypeVo = new MaskTypeVo();
            maskTypeVo.setType(e.getType());
            maskTypeVo.setDesc(e.getDesc());
            return maskTypeVo;
        }).collect(Collectors.toList());
        return ResultVO.success(maskTypeVos);
    }


    @ApiOperation("面具类型")
    @GetMapping("/mask/maskType")
    public ResultVO<List<MaskTypeVo>> getMask() {
        List<MaskTypeVo> maskTypeVos = Arrays.stream(MaskEnum.values()).map(e -> {
            MaskTypeVo maskTypeVo = new MaskTypeVo();
            maskTypeVo.setType(e.getType());
            maskTypeVo.setDesc(e.getDesc());
            return maskTypeVo;
        }).collect(Collectors.toList());
        return ResultVO.success(maskTypeVos);
    }

    @ApiOperation("删除面具")
    @GetMapping("/mask/delete")
    @VerifyUserToken(role = {RoleEnum.SYSTEM, RoleEnum.CREATOR})
    public ResultVO deleteMask(@RequestParam(value = "id", required = true) Long id) {
        return maskManager.deleteMask(getUserId(), id, getRole());
    }


    @ApiOperation("新增面具")
    @PostMapping("/mask/add")
    @VerifyUserToken(role = {RoleEnum.SYSTEM, RoleEnum.CREATOR})
    public ResultVO addMask(@RequestBody @Valid MaskAddReq req) {
        return maskManager.addMask(getUserId(), req);
    }


    @ApiOperation("更新面具")
    @PostMapping("/mask/update")
    @VerifyUserToken(role = {RoleEnum.SYSTEM, RoleEnum.CREATOR})
    public ResultVO updateMask(@RequestBody @Valid MaskUpdateReq req) {
        return maskManager.updateMask(getUserId(), req, getRole());
    }


    @ApiOperation("c端界面-搜索面具")
    @GetMapping("/mask/c/search")
    public ResultVO<MaskSearchResultVo> c_searchMask(@Valid MaskSearchReq req) {
        //String type = req.getType();
        //if(StringUtils.isEmpty(type)){
        //    req.setType(MaskEnum.MASK.getType());
        //}
        return maskManager.c_searchMask(req);
    }


    @VerifyUserToken
    @ApiOperation("分享面具")
    @PostMapping("/mask/share")
    public ResultVO shareMask(@RequestBody @Valid ShareMaskReq req) {
        return maskManager.shareMask(getUserId(), req);
    }


    @ApiOperation("b端界面-搜索面具")
    @PostMapping("/mask/b/search")
    @VerifyUserToken(role = {RoleEnum.SYSTEM, RoleEnum.CREATOR})
    public ResultVO<PageVO<BMaskVo>> b_searchMask(@RequestBody @Valid MaskAdminSearchReq req) {
        if (!RoleEnum.SYSTEM.getRole().equals(getRole())) {
            req.setUserId(getUserId());
        }
        return maskManager.b_searchMask(req);
    }


    @ApiOperation("b端-获取面具详细数据")
    @GetMapping("/mask/b/get")
    @VerifyUserToken(role = {RoleEnum.SYSTEM, RoleEnum.CREATOR})
    public ResultVO<BMaskVo> getBMask(@RequestParam(value = "id", required = true) Long id) {
        return maskManager.getBMaskById(getUserId(), id, MaskStatusEnum.OK.getStatus(), getRole());
    }


    @ApiOperation("c端-获取面具下面")
    @GetMapping("/mask/c/simple/list")
    @VerifyUserToken
    public ResultVO<List<SimpleMaskVo>> getUserSimpleMask() {
        return maskManager.getUserSimpleMask();
    }


    @ApiOperation("c端-获取面具详细数据")
    @GetMapping("/mask/c/get")
    @VerifyUserToken(required = false)
    public ResultVO<CMaskVo> getCMask(@RequestParam(value = "id", required = true) Long id) {
        return maskManager.getCMaskById(getUserId(), id, MaskStatusEnum.OK.getStatus());
    }


}
