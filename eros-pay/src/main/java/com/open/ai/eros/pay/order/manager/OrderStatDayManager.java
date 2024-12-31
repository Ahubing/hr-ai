package com.open.ai.eros.pay.order.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.ai.eros.common.constants.BalanceUnitEnum;
import com.open.ai.eros.common.util.DateUtils;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.constants.OrderStatusEnum;
import com.open.ai.eros.db.mysql.pay.entity.Order;
import com.open.ai.eros.db.mysql.pay.entity.OrderStatDay;
import com.open.ai.eros.db.mysql.pay.service.impl.OrderServiceImpl;
import com.open.ai.eros.db.mysql.pay.service.impl.OrderStatDayServiceImpl;
import com.open.ai.eros.pay.order.convert.OrderStatDayConvert;
import com.open.ai.eros.pay.vo.AllOrderStatVo;
import com.open.ai.eros.pay.vo.OrderStatDayVo;
import com.open.ai.eros.pay.vo.OrderStatHistoryVo;
import com.open.ai.eros.pay.vo.OrderStatListSearchVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author linyous
 * @Date 2024/9/12 22:46
 */
@Slf4j
@Component
public class OrderStatDayManager {

    @Autowired
    private OrderStatDayServiceImpl orderStatDayService;
    @Autowired
    private OrderServiceImpl orderService;

    public ResultVO<AllOrderStatVo> getAllOrderIncome(){
        AllOrderStatVo allOrderStatVo = new AllOrderStatVo();
        try {
            OrderStatDayVo orderTodayIncome = getOrderTodayIncome();
            OrderStatHistoryVo historyOrderIncome = getHistoryOrderIncome();
            BigDecimal totalIncome = new BigDecimal(0);
            if (Objects.nonNull(orderTodayIncome.getIncome())){
                allOrderStatVo.setTodayIncome(orderTodayIncome.getIncome().toString());
                allOrderStatVo.setTodayRecordCount(orderTodayIncome.getRecordCount());
                totalIncome= totalIncome.add(orderTodayIncome.getIncome());
            }
            if (Objects.nonNull(historyOrderIncome.getHistoryIncome())){
                allOrderStatVo.setHistoryIncome(historyOrderIncome.getHistoryIncome().toString());
                allOrderStatVo.setHistoryRecordCount(historyOrderIncome.getRecordCount());
                totalIncome = totalIncome.add(historyOrderIncome.getHistoryIncome());
            }
            allOrderStatVo.setTotalIncome(totalIncome.toString());
        }catch (Exception e){
            log.error("getAllOrderIncome error",e);
        }
        return ResultVO.success(allOrderStatVo);
    }



    public OrderStatDayVo getOrderTodayIncome(){
        OrderStatDayVo orderStatDayVo = new OrderStatDayVo();
        List<Order> todayOrder = orderService.getTodayOrder();
        BigDecimal income =new BigDecimal(0);
        for (Order order : todayOrder) {
            if (order.getUnit().equals(BalanceUnitEnum.DOLLAR.getUnit())) {
                // 将订单价格与汇率相乘，并将结果累加到income上
                // 注意：需要使用order.getPrice().multiply(order.getRate())
                income = income.add(order.getPrice().multiply(order.getRate()));
            }else {
                income = income.add(order.getPrice());
            }
        }
        orderStatDayVo.setRecordCount(Long.valueOf(todayOrder.size()));
        orderStatDayVo.setIncome(income);
        orderStatDayVo.setStatsDay(LocalDateTime.now());
        return orderStatDayVo;
    }



    public OrderStatHistoryVo getHistoryOrderIncome(){
        OrderStatHistoryVo orderStatDayVo = new OrderStatHistoryVo();
        OrderStatDay historyOrderStat = orderStatDayService.getHistoryOrderStat();
        orderStatDayVo.setHistoryIncome(historyOrderStat.getCost());
        orderStatDayVo.setRecordCount(historyOrderStat.getRecordCount());
        return orderStatDayVo;
    }

    public  ResultVO<List<OrderStatDayVo>> getWeekOrderStatDay(){
        // 修改: 获取结束时间为今天，开始时间为前6天
        Date endTime = DateUtils.endOfDay(new Date());
        Date startTime = DateUtils.getDayBefore(DateUtils.startOfDay(new Date()), 6);

        List<OrderStatDay> orderStatByTime = orderStatDayService.getOrderStatByTime(startTime, endTime);
        List<OrderStatDayVo> orderStatDayVos = orderStatByTime.stream().map(OrderStatDayConvert.I::convertOrderVo).collect(Collectors.toList());
        return ResultVO.success(orderStatDayVos);
    }

    public  ResultVO<PageVO<OrderStatDayVo>> getWeekOrderStatDay(OrderStatListSearchVo orderStatListSearchVo){
        PageVO<OrderStatDayVo> pageVO = new PageVO<>();
        Page<OrderStatDay> page = new Page<>(orderStatListSearchVo.getPage(),orderStatListSearchVo.getPageSize());
        LambdaQueryWrapper<OrderStatDay> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        Date startTime = DateUtils.startOfDay(new Date());
        Integer timeWindow = orderStatListSearchVo.getTimeWindow();
        if (Objects.nonNull(timeWindow)) {
            //  如果查询时间窗口 0 当天 , 1 本周, 2 这个月
            Calendar calendar = Calendar.getInstance();
            switch (timeWindow) {
                case 1: // 本周
                    // 显式设置第一天是周一，忽略区域设置的差异
                    calendar.setFirstDayOfWeek(Calendar.MONDAY);
                    // 将周日作为一周的最后一天
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                    // 获取当前日期是本周的第几天
                    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                    // 计算与本周一的差异
                    int diff = - (dayOfWeek - calendar.getFirstDayOfWeek()) % 7;
                    // 特殊处理：如果今天是周日（按欧洲标准，周日的值为1），则需要回溯到上周的周一
                    if (dayOfWeek == Calendar.SUNDAY) {
                        diff = -6;
                    }
                    calendar.add(Calendar.DAY_OF_MONTH, diff);
                    // 更新开始时间为本周的第一天的开始时间
                    startTime = DateUtils.startOfDay(calendar.getTime());
                    break;
                case 2: // 这个月
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    startTime = DateUtils.startOfDay(calendar.getTime()); // 设置为这个月的第一天
                    break;
                default:
                    break;
            }
        }
        if (Objects.nonNull(timeWindow) && timeWindow > 0){
            lambdaQueryWrapper.ge(OrderStatDay::getStatsDay, startTime);
        }
        lambdaQueryWrapper.orderByAsc(OrderStatDay::getCreateTime);
        // 修改: 获取结束时间为今天，开始时间为前6天
        Page<OrderStatDay> orderStatDayPage = orderStatDayService.page(page, lambdaQueryWrapper);
            List<OrderStatDayVo> collect = orderStatDayPage.getRecords().stream().map(OrderStatDayConvert.I::convertOrderVo).collect(Collectors.toList());
            pageVO.setData(collect);
            pageVO.setTotal(orderStatDayPage.getTotal());
        return ResultVO.success(pageVO);
    }
}
