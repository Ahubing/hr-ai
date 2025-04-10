package com.open.ai.eros.pay.goods.controller;


import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.constants.RightsTypeEnum;
import com.open.ai.eros.db.mysql.pay.entity.RightsSimpleVo;
import com.open.ai.eros.pay.config.PayBaseController;
import com.open.ai.eros.pay.goods.bean.req.RightsAddReq;
import com.open.ai.eros.pay.goods.bean.req.RightsUpdateReq;
import com.open.ai.eros.pay.goods.bean.vo.BRightsRuleVo;
import com.open.ai.eros.pay.goods.bean.vo.BRightsTypeVo;
import com.open.ai.eros.pay.goods.bean.vo.RightsVo;
import com.open.ai.eros.pay.goods.manager.RightsManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 权益 前端控制器
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-20
 */
@Api(tags = "权益控制类")
@RestController
public class RightsController extends PayBaseController {


    @Autowired
    private RightsManager rightsManager;


    /**
     * 新增权益
     *
     * @param req
     * @return
     */
    @ApiOperation("新增权益")
    @VerifyUserToken(role = {RoleEnum.SYSTEM})
    @PostMapping("/rights/add")
    public ResultVO addRights(@RequestBody  @Valid RightsAddReq req) {

        if (!RightsTypeEnum.exist(req.getType())) {
            return ResultVO.fail("未知权益类型！");
        }

        return rightsManager.addRights(req);
    }


    /**
     * 修改权益
     *
     * @param req
     * @return
     */
    @ApiOperation("修改权益")
    @VerifyUserToken(role = {RoleEnum.SYSTEM})
    @PostMapping("/rights/update")
    public ResultVO updateRights(@RequestBody @Valid RightsUpdateReq req) {

        if (!RightsTypeEnum.exist(req.getType())) {
            return ResultVO.fail("未知权益类型！");
        }
        return rightsManager.updateRights(req);
    }


    /**
     * 权益列表
     *
     * @return
     */
    @ApiOperation("权益列表")
    @VerifyUserToken(role = {RoleEnum.SYSTEM})
    @GetMapping("/rights/list")
    public ResultVO<PageVO<RightsVo>> list(@RequestParam(value = "status", defaultValue = "1") Integer status,@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return rightsManager.list(status,pageNum, pageSize);
    }


    /**
     * 权益列表
     *
     * @return
     */
    @ApiOperation("简单权益列表")
    @VerifyUserToken(role = {RoleEnum.SYSTEM})
    @PostMapping("/rights/simple/list")
    public ResultVO<List<RightsSimpleVo>> simpleList() {
        return rightsManager.getRightsSimpleVo();
    }


    /**
     * 给b端的权益类型枚举
     *
     * @return
     */
    @ApiOperation("权益类型枚举")
    @VerifyUserToken(role = {RoleEnum.SYSTEM})
    @GetMapping("/rights/type/list")
    public ResultVO<List<BRightsTypeVo>> getRightsTypeList() {
        return rightsManager.getRightsTypeVos();
    }

    /**
     * 给b端的权益规则枚举
     *
     * @return
     */
    @ApiOperation("权益规则枚举")
    @VerifyUserToken(role = {RoleEnum.SYSTEM})
    @GetMapping("/rights/rule/list")
    public ResultVO<List<BRightsRuleVo>> getRightsRuleList() {
        return rightsManager.getRightsRuleVos();
    }

}

