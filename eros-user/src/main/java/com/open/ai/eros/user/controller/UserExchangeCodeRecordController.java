package com.open.ai.eros.user.controller;

import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.user.bean.vo.CExchangeCodeResultVo;
import com.open.ai.eros.user.bean.vo.CExchangeCodeVo;
import com.open.ai.eros.user.config.UserBaseController;
import com.open.ai.eros.user.manager.UserExchangeCodeRecordManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @类名：UserExchangeCodeRecordController
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/28 23:29
 */

@Api(tags = "用户使用兑换码控制类")
@RestController
public class UserExchangeCodeRecordController extends UserBaseController {


    @Autowired
    private UserExchangeCodeRecordManager userExchangeCodeRecordManager;



    @ApiOperation("查询自己的兑换码记录")
    @VerifyUserToken
    @GetMapping("/use/exchange/code/record")
    public ResultVO<List<CExchangeCodeVo>> getMyUseExchangeCodeRecord(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                                        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        return userExchangeCodeRecordManager.getMyExchangeRecord(getUserId(),pageNum,pageSize);
    }



    @ApiOperation("查询兑换码的兑换记录")
    @VerifyUserToken
    @GetMapping("/use/exchange/code/user/record")
    public ResultVO<CExchangeCodeResultVo>  getUserUseExchangeCodeRecord(@RequestParam(value = "id",required = true) Long id,
                                                                                    @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                                                 @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        return userExchangeCodeRecordManager.getMyExchangeRecord(id,getUserId(),pageNum,pageSize,getRole());
    }




}
