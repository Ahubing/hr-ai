package com.open.ai.eros.user.manager;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.ai.eros.common.util.BalanceFormatUtil;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.user.entity.UserBalanceRecord;
import com.open.ai.eros.db.mysql.user.service.impl.UserBalanceRecordServiceImpl;
import com.open.ai.eros.user.bean.req.UserBalanceRecordQueryReq;
import com.open.ai.eros.user.bean.vo.UserBalanceRecordResultVo;
import com.open.ai.eros.user.bean.vo.UserBalanceRecordVo;
import com.open.ai.eros.user.constants.UserBalanceRecordEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户余额记录---业务类
 */
@Slf4j
@Component
public class UserBalanceRecordManager {

    @Resource
    private UserBalanceRecordServiceImpl userBalanceRecordServiceImpl;

    /**
     * 分页查询
     * @param userId 登录用户的ID
     * @param req 查询请求参数类
     * @return 查询结果
     */
    public ResultVO<UserBalanceRecordResultVo> getPage(Long userId, List<String> recordEnums, UserBalanceRecordQueryReq req) {

        UserBalanceRecordResultVo resultVo = new UserBalanceRecordResultVo();

        // 构建查询条件
        LambdaQueryWrapper<UserBalanceRecord> queryWrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            queryWrapper.eq(UserBalanceRecord::getUserId, userId);
        }
        if(CollectionUtils.isNotEmpty(recordEnums)){
            queryWrapper.in(UserBalanceRecord::getType,recordEnums);
        }
        queryWrapper.orderByDesc(UserBalanceRecord::getCreateTime);

        // 执行分页查询
        Page<UserBalanceRecord> page = new Page<>(req.getPageNum(), req.getPageSize());
        IPage<UserBalanceRecord> recordPage = userBalanceRecordServiceImpl.page(page, queryWrapper);

        // 组装VO数据
        List<UserBalanceRecordVo> recordVos = recordPage.getRecords().stream().map(record -> new UserBalanceRecordVo()
                .setId(record.getId())
                .setBalance(BalanceFormatUtil.getUserExactBalance(Long.parseLong(record.getBalance()))) // 类型转换
                .setType(UserBalanceRecordEnum.getDesc(record.getType()))
                .setCreateTime(record.getCreateTime())).collect(Collectors.toList());

        resultVo.setRecordVos(recordVos);
        if(recordVos.size() < req.getPageSize()){
            resultVo.setLastPage(true);
        }
        return ResultVO.success(resultVo);
    }


    /**
     * 新增用户余额更换记录
     *
     * @param userId
     * @param balance
     * @param type
     * @param desc
     * @param balanceId
     * @return
     */
    @Transactional
    public ResultVO addUserBalanceRecord(Long userId,Long balance,String type,Integer userBalanceType,String desc,Long balanceId){

        UserBalanceRecord record = new UserBalanceRecord();
        record.setBalance(String.valueOf(balance));
        record.setType(type);
        record.setUserBalanceType(userBalanceType);
        record.setDescription(desc);
        record.setUserBalanceId(balanceId);
        record.setCreateTime(LocalDateTime.now());
        record.setUserId(userId);
        boolean save = userBalanceRecordServiceImpl.save(record);
        if(!save){
            log.error("addUserBalanceRecord error record={}", JSONObject.toJSONString(record));
        }
        return save? ResultVO.success():ResultVO.fail("新增余额信息记录失败！");
    }



}
