package com.open.ai.eros.creator.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.ai.eros.common.util.DateUtils;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.creator.bean.req.GetMaskStatDayReq;
import com.open.ai.eros.creator.bean.vo.*;
import com.open.ai.eros.creator.convert.MaskStatConvert;
import com.open.ai.eros.db.mysql.ai.entity.MaskStatDay;
import com.open.ai.eros.db.mysql.ai.entity.MaskStatList;
import com.open.ai.eros.db.mysql.ai.entity.UserAiConsumeRecordStatVo;
import com.open.ai.eros.db.mysql.ai.entity.UserAiMasksRecordStatVo;
import com.open.ai.eros.db.mysql.ai.service.impl.MaskStatDayServiceImpl;
import com.open.ai.eros.db.mysql.ai.service.impl.UserAiConsumeRecordServiceImpl;
import com.open.ai.eros.db.mysql.creator.service.impl.MaskServiceImpl;
import com.open.ai.eros.db.mysql.user.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MaskStatManager {

    @Resource
    private MaskStatDayServiceImpl maskStatDayService;


    @Autowired
    private UserAiConsumeRecordServiceImpl userAiConsumeRecordService;


    @Autowired
    private MaskServiceImpl maskService;

    @Autowired
    private UserServiceImpl userService;


    @Autowired
    private MaskManager maskManager;


    /**
     * 获取每日统计信息
     *
     * @return 统计记录
     */
    public ResultVO<PageVO<MaskStatDayVo>> getDailyStats(GetMaskStatDayReq req) {
        LambdaQueryWrapper<MaskStatDay> queryWrapper = new LambdaQueryWrapper<>();

        if (req.getMaskId() != null) {
            queryWrapper.eq(MaskStatDay::getMaskId, req.getMaskId());
        }
        if (req.getUserId() != null) {
            queryWrapper.eq(MaskStatDay::getUserId, req.getUserId());
        }
        // 范围查询
        queryWrapper.between(MaskStatDay::getCreateTime, new Date(req.getStartTime()), new Date(req.getEndTime()));

        Page<MaskStatDay> page = new Page<>(req.getPage(), req.getPageSize());

        // 分页查询
        Page<MaskStatDay> dayPage = maskStatDayService.page(page, queryWrapper);
        List<MaskStatDayVo> maskStatDayVos = dayPage.getRecords().stream().map(MaskStatConvert.I::convertMaskStatDayVo).collect(Collectors.toList());
        return ResultVO.success(PageVO.build(dayPage.getTotal(), maskStatDayVos));
    }


    /**
     * 获取面具今日的统计数据
     *
     * @return
     */
    public ResultVO<MaskStatDayVo> getMaskToday(Long userId) {
        List<Long> maskIds = null;
        if (userId != null) {
            maskIds = maskService.getMaskIds(userId);
            if (CollectionUtils.isEmpty(maskIds)) {
                return ResultVO.success();
            }
        }
        Date endTime = DateUtils.endOfDay(new Date());
        Date startTime = DateUtils.startOfDay(new Date());
        UserAiConsumeRecordStatVo userAiConsumeRecordStatVo = userAiConsumeRecordService.statTodayConsumeRecord(maskIds, startTime, endTime);
        return ResultVO.success(MaskStatConvert.I.convertMaskStatDayVo(userAiConsumeRecordStatVo));
    }

    /**
     * 获取面具近七天的统计数据
     *
     * @return
     */
    public ResultVO<List<MaskStatDayVo>> getMaskWeek(Long userId) {
        List<Long> maskIds = null;
        if (userId != null) {
            maskIds = maskService.getMaskIds(userId);
            if (CollectionUtils.isEmpty(maskIds)) {
                return ResultVO.success();
            }
        }

        // 修改: 获取结束时间为今天，开始时间为前6天
        Date endTime = DateUtils.endOfDay(new Date());
        Date startTime = DateUtils.getDayBefore(DateUtils.startOfDay(new Date()), 6);

        List<UserAiConsumeRecordStatVo> userAiConsumeRecordStatVos = userAiConsumeRecordService.statWeekConsumeRecord(maskIds, startTime, endTime);
        List<MaskStatDayVo> collect = userAiConsumeRecordStatVos.stream().map(MaskStatConvert.I::convertMaskStatDayVo).collect(Collectors.toList());
        return ResultVO.success(collect);
    }


    /**
     * 获取名下面具信息
     *
     * @return
     */
    public ResultVO<List<MaskStatListVo>> getPeopleMasksStatList(Long userId, Integer timeWindow) {
        if (userId == null) {
            return ResultVO.success();
        }
        Date startTime = DateUtils.startOfDay(new Date());
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
                    int diff = -(dayOfWeek - calendar.getFirstDayOfWeek()) % 7;
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
            List<MaskStatList> lastMaskStatList = maskStatDayService.getLastMaskStatList(userId, startTime);
            List<MaskStatListVo> result = lastMaskStatList.stream()
                    .sorted(Comparator.comparingLong(MaskStatList::getCost).reversed())
                    .map(MaskStatConvert.I::convertMasksStatListVo).collect(Collectors.toList());
            for (MaskStatListVo orderStatDayVo : result) {
                BMaskVo cacheBMaskById = maskManager.getCacheBMaskById(orderStatDayVo.getMaskId());
                if (Objects.nonNull(cacheBMaskById)) {
                    orderStatDayVo.setMaskName(cacheBMaskById.getName());
                }
            }
            return ResultVO.success(result);
        } else {
            List<UserAiConsumeRecordStatVo> userAiConsumeRecordStatVos = userAiConsumeRecordService.todayStatConsumeRecordByUserId(startTime, userId);
            List<MaskStatDayVo> maskStatDays = userAiConsumeRecordStatVos.stream()
                    .sorted(Comparator.comparingLong(UserAiConsumeRecordStatVo::getCost).reversed())
                    .map(MaskStatConvert.I::convertMaskStatDayVo)
                    .collect(Collectors.toList());
            for (MaskStatDayVo maskStatDay : maskStatDays) {
                BMaskVo cacheBMaskById = maskManager.getCacheBMaskById(maskStatDay.getMaskId());
                if (Objects.nonNull(cacheBMaskById)) {
                    maskStatDay.setMaskName(cacheBMaskById.getName());
                }
            }
        }
        return ResultVO.success();
    }

    /**
     * 获取名下面具统计信息
     *
     * @return
     */
    public ResultVO<MasksInfoVo> getPeopleMasksInfo(Long userId, Integer timeWindow) {
        List<Long> maskIds = null;
        if (userId != null) {
            maskIds = maskService.getMaskIds(userId);
            if (CollectionUtils.isEmpty(maskIds)) {
                return ResultVO.success();
            }
        }
        MasksInfoVo masksInfoVo = new MasksInfoVo();
        masksInfoVo.setMaskCount(maskIds.size());
        Date startTime = DateUtils.startOfDay(new Date());
        if (Objects.nonNull(timeWindow)) {
            //  如果查询时间窗口 0 当天 , 1 本周, 2 这个月
            Calendar calendar = Calendar.getInstance();
            switch (timeWindow) {
                case 1: // 本周
                    calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                    startTime = DateUtils.startOfDay(calendar.getTime()); // 设置为本周的第一天
                    break;
                case 2: // 这个月
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    startTime = DateUtils.startOfDay(calendar.getTime()); // 设置为这个月的第一天
                    break;
                default:
                    break;
            }
        }
        UserAiMasksRecordStatVo userAiMasksRecordStatVo = userAiConsumeRecordService.statTodayConsumeRecord(maskIds, timeWindow == -1 ? null : startTime);
        MasksInfoVo infoVo = MaskStatConvert.I.convertMasksInfoVo(userAiMasksRecordStatVo);
        infoVo.setMaskCount(maskIds.size());
        return ResultVO.success(infoVo);
    }


    public ResultVO<MaskStatCountVo> getMaskStatData() {
        MaskStatCountVo maskStatCountVo = new MaskStatCountVo();
        Date startTime = DateUtils.startOfDay(new Date());
        try {
            //现有面具总数
            Long maskSum = maskService.getMaskSum();
            //历史访问次数
            Long historyRecordCount = maskStatDayService.getLastMaskStatRecordCount();
            //今天访问次数
            Long todayStatCount = userAiConsumeRecordService.statMasksRecordToday(startTime);

            // 获取全部人数
            Long allUserSum = userService.getAllUserSum();

            // 获取创作者数
            Long allCreatorSum = userService.getAllCreatorSum();

            // 获取普通用户数
            Long allCommonUserSum = userService.getAllCommonUserSum();

            //获取今日注册数
            Long todayRegister = userService.getTodayRegister();

            maskStatCountVo.setMaskSum(maskSum);
            maskStatCountVo.setTodayRecordCount(todayStatCount);
            maskStatCountVo.setHistoryRecordCount(historyRecordCount);
            maskStatCountVo.setAllUserSum(allUserSum);
            maskStatCountVo.setAllCommonSum(allCommonUserSum);
            maskStatCountVo.setAllCreatorSum(allCreatorSum);
            maskStatCountVo.setTodayRegister(todayRegister);
        } catch (Exception e) {
            log.error("getMaskStatData error ", e);
        }
        return ResultVO.success(maskStatCountVo);
    }

}
