package com.open.ai.eros.user.controller;

import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.constants.ExchangeCodeTypeEnum;
import com.open.ai.eros.user.bean.req.AddExchangeCodeReq;
import com.open.ai.eros.user.bean.req.ExchangeCodeQueryReq;
import com.open.ai.eros.user.bean.req.UpdateExchangeCodeReq;
import com.open.ai.eros.user.bean.vo.ExchangeCodeResultVo;
import com.open.ai.eros.user.bean.vo.ExchangeCodeTypeVo;
import com.open.ai.eros.user.bean.vo.ExchangeCodeVo;
import com.open.ai.eros.user.config.UserBaseController;
import com.open.ai.eros.user.manager.ExchangeCodeManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @类名：ExchangeCodeController
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/27 22:44
 */

@Api(tags = "兑换码控制类")
@RestController
public class ExchangeCodeController extends UserBaseController {


    @Autowired
    private ExchangeCodeManager exchangeCodeManager;


    @VerifyUserToken
    @ApiOperation("使用兑换码")
    @GetMapping("/use/exchange/code")
    public ResultVO useExchangeCode(@RequestParam(value = "code") String code){
        return exchangeCodeManager.useExchangeCode(getUserId(),code);
    }



    @VerifyUserToken(role = {RoleEnum.SYSTEM,RoleEnum.CREATOR})
    @ApiOperation("新增兑换码")
    @PostMapping("/add/exchange/code")
    public ResultVO addExchangeCode(@RequestBody @Valid AddExchangeCodeReq req){

        boolean exist = ExchangeCodeTypeEnum.exist(req.getType());
        if(!exist){
            return ResultVO.fail("未知的兑换码类型");
        }
        if(RoleEnum.CREATOR.getRole().equals(getRole())){
            String type = ExchangeCodeTypeEnum.BALANCE.getType();
            if(!type.equals(req.getType())){
                return ResultVO.fail("当前权限不支持该兑换码类型");
            }
        }
        return exchangeCodeManager.addExchangeCode(getUserId(),req,getRole());
    }


    @VerifyUserToken(role = RoleEnum.SYSTEM)
    @ApiOperation("修改兑换码")
    @PostMapping("/update/exchange/code")
    public ResultVO updateExchangeCode(@RequestBody @Valid UpdateExchangeCodeReq req){
        boolean exist = ExchangeCodeTypeEnum.exist(req.getType());
        if(!exist){
            return ResultVO.fail("未知的兑换码类型");
        }
        return exchangeCodeManager.updateExchangeCode(req);
    }


    @VerifyUserToken(role = {RoleEnum.SYSTEM,RoleEnum.CREATOR})
    @ApiOperation("删除兑换码")
    @GetMapping("/delete/exchange/code")
    public ResultVO deleteExchangeCode(@RequestParam(value = "id") Long id){
        return exchangeCodeManager.deleteExchangeCode(id,getUserId(),getRole());
    }


    @VerifyUserToken(role = {RoleEnum.SYSTEM,RoleEnum.CREATOR})
    @ApiOperation("b-查询兑换码")
    @PostMapping("/search/exchange/code")
    public ResultVO<PageVO<ExchangeCodeVo>> searchExchangeCode(@RequestBody @Valid ExchangeCodeQueryReq req){
        if(!RoleEnum.SYSTEM.getRole().equals(getRole())){
            req.setUserId(getUserId());
        }
        return exchangeCodeManager.searchExchangeCode(req);
    }

    @VerifyUserToken(role = RoleEnum.SYSTEM)
    @ApiOperation("查询兑换码的类型")
    @GetMapping("/search/exchange/code/type")
    public ResultVO<List<ExchangeCodeTypeVo>> searchExchangeCodeType(){
        return exchangeCodeManager.updateExchangeCodeType();
    }




    @VerifyUserToken(role = {RoleEnum.SYSTEM,RoleEnum.CREATOR})
    @ApiOperation("c-查询兑换码")
    @GetMapping("/exchange/code/list")
    public ResultVO<ExchangeCodeResultVo> searchExchangeCode(@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                                             @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                                                             @RequestParam(value = "status",defaultValue = "1")Integer status){
        return exchangeCodeManager.getExchangeCode(getUserId(),pageNum,pageSize,status);
    }




}
