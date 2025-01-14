package com.open.hr.ai.controller;

import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.AmSquareRoles;
import com.open.hr.ai.bean.req.AddOrUpdateSquareReq;
import com.open.hr.ai.bean.req.ClientBossNewMessageReq;
import com.open.hr.ai.bean.req.ClientFinishTaskReq;
import com.open.hr.ai.bean.req.ClientQrCodeReq;
import com.open.hr.ai.bean.vo.AmSquareListVo;
import com.open.hr.ai.config.HrAIBaseController;
import com.open.hr.ai.manager.ClientManager;
import com.open.hr.ai.manager.SquareAiManager;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Objects;

/**
 * @Date 2025/1/6 23:33
 */
@RestController
@Slf4j
public class SquareAiController extends HrAIBaseController {

     @Resource
     private SquareAiManager squareAiManager;

     @ApiOperation("模型训练广场获取数据")
     @VerifyUserToken
     @GetMapping("/square/list")
     public ResultVO<AmSquareListVo> getSquareList(@RequestParam(value = "name", required = false) String name) {
         return squareAiManager.getSquareList(name,getUserId());
     }

    @ApiOperation("获取角色详情")
    @VerifyUserToken
    @GetMapping("/square/detail")
    public ResultVO<AmSquareRoles> getSquareDetail(@RequestParam(value = "id", required = false) Integer id) {
        return squareAiManager.getSquareDetail(id);
    }

    @ApiOperation("删除角色详情")
    @VerifyUserToken
    @GetMapping("/square/delete")
    public ResultVO deleteSquare( @RequestParam(value = "id", required = false) Integer id) {
        return squareAiManager.deleteSquareRolesById(id);
    }


    @ApiOperation("新增/编辑角色")
    @VerifyUserToken
    @PostMapping("/square/edit")
    public ResultVO addOrUpdateSquare(@RequestBody @Valid AddOrUpdateSquareReq req) {
        if (Objects.isNull(req)) {
            return ResultVO.fail("参数不能为空");
        }
        return squareAiManager.addOrUpdateSquare(req,getUserId());
    }





}
