package com.open.ai.eros.user.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.util.BalanceFormatUtil;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.constants.ExchangeCodeTypeEnum;
import com.open.ai.eros.db.mysql.pay.entity.RightsSnapshot;
import com.open.ai.eros.db.mysql.pay.service.impl.RightsSnapshotServiceImpl;
import com.open.ai.eros.db.mysql.user.entity.ExchangeCode;
import com.open.ai.eros.db.mysql.user.entity.User;
import com.open.ai.eros.db.mysql.user.entity.UserExchangeCodeRecord;
import com.open.ai.eros.db.mysql.user.service.impl.ExchangeCodeServiceImpl;
import com.open.ai.eros.db.mysql.user.service.impl.UserExchangeCodeRecordServiceImpl;
import com.open.ai.eros.db.mysql.user.service.impl.UserServiceImpl;
import com.open.ai.eros.user.bean.vo.CExchangeCodeResultVo;
import com.open.ai.eros.user.bean.vo.CExchangeCodeVo;
import com.open.ai.eros.user.bean.vo.UserExchangeCodeRecordVo;
import com.open.ai.eros.user.convert.ExchangeCodeConvert;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @类名：UserExchangeCodeRecordManager
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/28 23:31
 */
@Component
public class UserExchangeCodeRecordManager {


    @Autowired
    private UserExchangeCodeRecordServiceImpl userExchangeCodeRecordService;


    @Autowired
    private ExchangeCodeServiceImpl exchangeCodeService;

    @Autowired
    private RightsSnapshotServiceImpl rightsSnapshotService;


    @Autowired
    private UserServiceImpl userService;



    public ResultVO<CExchangeCodeResultVo> getMyExchangeRecord(Long id, Long userId, Integer pageNum, Integer pageSize, String role){

        List<UserExchangeCodeRecordVo> userExchangeCodeRecords = new ArrayList<>();

        CExchangeCodeResultVo resultVo = new CExchangeCodeResultVo();
        resultVo.setLastPage(true);
        resultVo.setUserExchangeCodeRecords(userExchangeCodeRecords);

        ExchangeCode exchangeCode = exchangeCodeService.getById(id);
        if(exchangeCode==null){
            return ResultVO.success(resultVo);
        }
        if(!exchangeCode.getUserId().equals(userId) || !role.equals(RoleEnum.SYSTEM.getRole())){
            return ResultVO.success(resultVo);
        }
        LambdaQueryWrapper<UserExchangeCodeRecord> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserExchangeCodeRecord::getExchangeCodeId,id);
        lambdaQueryWrapper.orderByDesc(UserExchangeCodeRecord::getCreateTime);
        Page<UserExchangeCodeRecord> page = new Page<>(pageNum,pageSize);
        Page<UserExchangeCodeRecord> codeRecordPage = userExchangeCodeRecordService.page(page, lambdaQueryWrapper);
        List<UserExchangeCodeRecord> records = codeRecordPage.getRecords();
        boolean lastPage = records.size() < pageSize;
        resultVo.setLastPage(lastPage);
        if(records.isEmpty()){
            return ResultVO.success(resultVo);
        }
        List<Long> userIds = records.stream().map(UserExchangeCodeRecord::getUserId).collect(Collectors.toList());

        List<User> users = userService.listByIds(userIds);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(User::getId, e -> e, (k1, k2) -> k1));

        for (UserExchangeCodeRecord record : records) {
            User user = userMap.get(record.getUserId());
            if(user==null){
                continue;
            }
            UserExchangeCodeRecordVo recordVo = new UserExchangeCodeRecordVo();
            recordVo.setAvatar(user.getAvatar());
            recordVo.setUserName(user.getUserName());
            recordVo.setCreateTime(record.getCreateTime());
            recordVo.setId(user.getId());
            userExchangeCodeRecords.add(recordVo);
        }
        return ResultVO.success(resultVo);
    }





    public ResultVO<List<CExchangeCodeVo>> getMyExchangeRecord(Long userId, Integer pageNum, Integer pageSize){

        LambdaQueryWrapper<UserExchangeCodeRecord> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserExchangeCodeRecord::getUserId,userId);
        lambdaQueryWrapper.orderByDesc(UserExchangeCodeRecord::getCreateTime);

        Page<UserExchangeCodeRecord> page = new Page<>(pageNum,pageSize);
        Page<UserExchangeCodeRecord> codeRecordPage = userExchangeCodeRecordService.page(page, lambdaQueryWrapper);

        List<Long> exchangeCodeRecordIds = codeRecordPage.getRecords().stream().map(UserExchangeCodeRecord::getExchangeCodeId).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(exchangeCodeRecordIds)){
            return ResultVO.success();
        }

        List<ExchangeCode> exchangeCodes = exchangeCodeService.listByIds(exchangeCodeRecordIds);

        Map<Long, ExchangeCode> exchangeCodeMap = exchangeCodes.stream().collect(Collectors.toMap(ExchangeCode::getId, e -> e, (k1, k2) -> k1));
        List<UserExchangeCodeRecord> records = codeRecordPage.getRecords();

        List<CExchangeCodeVo> cExchangeCodeVos = new ArrayList<>();
        for (UserExchangeCodeRecord record : records) {
            ExchangeCode exchangeCode = exchangeCodeMap.get(record.getExchangeCodeId());
            if (exchangeCode != null) {
                CExchangeCodeVo cExchangeCodeVo = ExchangeCodeConvert.I.convertCExchangeCodeVo(exchangeCode);
                String bizValue = getBizValue(exchangeCode);
                cExchangeCodeVo.setBizValue(bizValue);
                cExchangeCodeVos.add(cExchangeCodeVo);
            }
        }

        return ResultVO.success(cExchangeCodeVos);
    }


    public String getBizValue(ExchangeCode exchangeCode){
        String type = exchangeCode.getType();
        if(type.equals(ExchangeCodeTypeEnum.BALANCE.getType())){
            return BalanceFormatUtil.getUserBalance(Long.parseLong(exchangeCode.getBizValue()));
        }else if(type.equals(ExchangeCodeTypeEnum.RIGHTS.getType())){
            RightsSnapshot lastRightsSnapshot = rightsSnapshotService.getLastRightsSnapshot(Long.parseLong(exchangeCode.getBizValue()));
            if(lastRightsSnapshot!=null){
                return lastRightsSnapshot.getName();
            }
        }
        return exchangeCode.getBizValue();
    }

}
