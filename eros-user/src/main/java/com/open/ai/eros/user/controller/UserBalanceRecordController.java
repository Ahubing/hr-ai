package com.open.ai.eros.user.controller;

import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.user.bean.req.UserBalanceRecordQueryReq;
import com.open.ai.eros.user.bean.vo.UserBalanceRecordResultVo;
import com.open.ai.eros.user.config.UserBaseController;
import com.open.ai.eros.user.constants.UserBalanceRecordEnum;
import com.open.ai.eros.user.manager.UserBalanceRecordManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * 用户余额记录---控制类
 *
 * @author Eros-AI
 * @since 2024-08-13
 */
@Slf4j
@Api(tags = "用户余额的记录")
@RestController
public class UserBalanceRecordController extends UserBaseController {

    @Resource
    private UserBalanceRecordManager userBalanceRecordManager;

    /**
     * 分页查询
     * @param req 查询请求参数类
     * @return 查询结果
     */
    @VerifyUserToken
    @ApiOperation(value = "获取我的收益")
    @PostMapping({"/userBalanceRecord/page","/userBalanceRecord/income"})
    public ResultVO<UserBalanceRecordResultVo> getPage(@RequestBody @Valid UserBalanceRecordQueryReq req) {
        try {
            List<String> recordEnums = new ArrayList<>();
            //String role = getRole();
            //// 所有用户共有
            //recordEnums.add(UserBalanceRecordEnum.INVITATION_NEW_USER_BALANCE);
            //recordEnums.add(UserBalanceRecordEnum.SYSTEM_UPDATE_BALANCE);
            //if(role.equals(RoleEnum.CREATOR.getRole())){
            //    recordEnums.addAll(Arrays.asList(
            //            UserBalanceRecordEnum.MASK_CHAT_BALANCE
            //    ));
            //}
            if(StringUtils.isNoneEmpty(req.getType())){
                recordEnums.add(req.getType());
            }else{
                recordEnums = Arrays.stream(UserBalanceRecordEnum.values()).map(UserBalanceRecordEnum::getType).collect(Collectors.toList());
            }
            return userBalanceRecordManager.getPage(getUserId(),recordEnums, req);
        } catch (Exception e) {
            log.error("分页查询用户余额记录失败", e);
            return ResultVO.fail("查询失败");
        }
    }

}

