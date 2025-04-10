package com.open.ai.eros.user.manager;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.ai.eros.common.util.DateUtils;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.user.entity.UserIncomeStatDay;
import com.open.ai.eros.db.mysql.user.entity.UserIncomeStatVo;
import com.open.ai.eros.db.mysql.user.service.impl.UserBalanceRecordServiceImpl;
import com.open.ai.eros.db.mysql.user.service.impl.UserIncomeStatDayServiceImpl;
import com.open.ai.eros.user.bean.req.GetUserIncomeStatDayReq;
import com.open.ai.eros.user.bean.vo.UserIncomeStatDayVo;
import com.open.ai.eros.user.constants.UserBalanceRecordEnum;
import com.open.ai.eros.user.convert.UserIncomeStatConvert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserIncomeStatManager {


    @Autowired
    private UserIncomeStatDayServiceImpl userIncomeStatDayService;


    @Autowired
    private UserBalanceRecordServiceImpl userBalanceRecordService;

    /**
     * 获取今日的收益
     *
     * @param userId
     * @return
     */
    public ResultVO<UserIncomeStatDayVo> getUserIncomeStatToday(Long userId) {
        Date endTime = DateUtils.endOfDay(new Date());
        Date startTime = DateUtils.startOfDay(new Date());
        List<String> types = Arrays.asList(UserBalanceRecordEnum.INVITATION_NEW_USER_BALANCE.getType(), UserBalanceRecordEnum.MASK_CHAT_BALANCE.getType());
        UserIncomeStatVo userIncomeStatVo = userBalanceRecordService.statUserTodayIncome(userId, types, startTime, endTime);
        return ResultVO.success(UserIncomeStatConvert.I.convertUserIncomeStatDayVo(userIncomeStatVo));
    }



    public ResultVO<PageVO<UserIncomeStatDayVo>> getDailyStats(GetUserIncomeStatDayReq req) {

        LambdaQueryWrapper<UserIncomeStatDay> queryWrapper = new LambdaQueryWrapper<>();

        if (req.getUserId() != null) {
            queryWrapper.eq(UserIncomeStatDay::getUserId, req.getUserId());
        }
        // 范围查询
        queryWrapper.between(UserIncomeStatDay::getCreateTime, new Date(req.getStartTime()), new Date(req.getEndTime()));

        Page<UserIncomeStatDay> page = new Page<>(req.getPage(), req.getPageSize());

        Page<UserIncomeStatDay> statDayPage = userIncomeStatDayService.page(page, queryWrapper);

        List<UserIncomeStatDayVo> userIncomeStatDayVos = statDayPage.getRecords().stream().map(UserIncomeStatConvert.I::convertUserIncomeStatDayVo).collect(Collectors.toList());
        return ResultVO.success(PageVO.build(statDayPage.getTotal(), userIncomeStatDayVos));
    }



}
