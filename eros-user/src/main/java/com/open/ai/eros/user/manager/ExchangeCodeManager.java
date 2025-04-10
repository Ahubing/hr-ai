package com.open.ai.eros.user.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.ai.eros.common.constants.CommonConstant;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.exception.BizException;
import com.open.ai.eros.common.service.RedisClient;
import com.open.ai.eros.common.util.DistributedLockUtils;
import com.open.ai.eros.common.util.ObjectToHashMapConverter;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.constants.CommonStatusEnum;
import com.open.ai.eros.db.constants.ExchangeCodeTypeEnum;
import com.open.ai.eros.db.mysql.pay.service.impl.UserRightsServiceImpl;
import com.open.ai.eros.db.mysql.user.entity.ExchangeCode;
import com.open.ai.eros.db.mysql.user.entity.UserExchangeCodeRecord;
import com.open.ai.eros.db.mysql.user.service.impl.ExchangeCodeServiceImpl;
import com.open.ai.eros.db.mysql.user.service.impl.UserBalanceServiceImpl;
import com.open.ai.eros.db.mysql.user.service.impl.UserExchangeCodeRecordServiceImpl;
import com.open.ai.eros.user.bean.req.AddExchangeCodeReq;
import com.open.ai.eros.user.bean.req.ExchangeCodeQueryReq;
import com.open.ai.eros.user.bean.req.UpdateExchangeCodeReq;
import com.open.ai.eros.user.bean.vo.ExchangeCodeResultVo;
import com.open.ai.eros.user.bean.vo.ExchangeCodeTypeVo;
import com.open.ai.eros.user.bean.vo.ExchangeCodeVo;
import com.open.ai.eros.user.bean.vo.UserCacheBalanceVo;
import com.open.ai.eros.user.constants.UserBalanceRecordEnum;
import com.open.ai.eros.user.convert.ExchangeCodeConvert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

/**
 * @类名：ExchangeCodeManager
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/27 22:45
 */
@Slf4j
@Component
public class ExchangeCodeManager {


    @Autowired
    private UserExchangeCodeRecordServiceImpl userExchangeCodeRecordService;

    @Autowired
    private ExchangeCodeServiceImpl exchangeCodeService;


    @Autowired
    private UserBalanceManager userBalanceManager;

    @Autowired
    private UserBalanceServiceImpl userBalanceService;


    @Autowired
    private UserRightsServiceImpl userRightsService;

    @Autowired
    private RedisClient redisClient;


    public ResultVO<ExchangeCodeResultVo> getExchangeCode(Long userId, Integer pageNum, Integer pageSize, Integer status){

        ExchangeCodeResultVo resultVo = new ExchangeCodeResultVo();

        List<ExchangeCode> exchangeCodes = exchangeCodeService.getExchangeCode(userId, pageNum, pageSize, status);
        if(exchangeCodes.size()<pageSize){
            resultVo.setLastPage(true);
        }

        List<ExchangeCodeVo> exchangeCodeVos = exchangeCodes.stream().map(ExchangeCodeConvert.I::convertExchangeCodeVo).collect(Collectors.toList());
        resultVo.setExchangeCodeVos(exchangeCodeVos);

        return ResultVO.success(resultVo);
    }



    public ResultVO<PageVO<ExchangeCodeVo>> searchExchangeCode(ExchangeCodeQueryReq req){

        Page<ExchangeCode> page = new Page<>(req.getPageNum(),req.getPageSize());
        LambdaQueryWrapper<ExchangeCode> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        if(StringUtils.isNoneEmpty(req.getType())){
            lambdaQueryWrapper.eq(ExchangeCode::getType,req.getType());
        }
        if(StringUtils.isNoneEmpty(req.getCode())){
            lambdaQueryWrapper.eq(ExchangeCode::getCode,req.getCode());
        }
        if(req.getUserId()!=null){
            lambdaQueryWrapper.eq(ExchangeCode::getUserId,req.getUserId());
        }
        if(req.getStatus()!=null){
            lambdaQueryWrapper.eq(ExchangeCode::getStatus,req.getStatus());
        }
        lambdaQueryWrapper.orderByDesc(ExchangeCode::getCreateTime);
        Page<ExchangeCode> exchangeCodePage = exchangeCodeService.page(page, lambdaQueryWrapper);
        List<ExchangeCodeVo> exchangeCodeVos = exchangeCodePage.getRecords().stream().map(ExchangeCodeConvert.I::convertExchangeCodeVo).collect(Collectors.toList());
        return ResultVO.success(PageVO.build(exchangeCodePage.getTotal(),exchangeCodeVos));
    }



    public ResultVO deleteExchangeCode(Long id,Long userId,String role){

        ExchangeCode exchangeCode = exchangeCodeService.getById(id);
        if(exchangeCode==null || (!exchangeCode.getUserId().equals(userId) && !RoleEnum.SYSTEM.getRole().equals(role) )){
            return ResultVO.fail("该兑换码不存在");
        }

        if(exchangeCode.getStatus().equals(CommonStatusEnum.DELETE.getStatus())){
            return ResultVO.fail("该兑换码已被销毁了");
        }

        ExchangeCode exchangeCodeByCode = new ExchangeCode();
        exchangeCodeByCode.setId(id);
        exchangeCodeByCode.setStatus(CommonStatusEnum.DELETE.getStatus());
        boolean updatedResult = exchangeCodeService.updateById(exchangeCodeByCode);
        if(!updatedResult){
            return ResultVO.fail("销毁兑换码失败");
        }
        if(role.equals(RoleEnum.CREATOR.getRole())){
            Lock lock = DistributedLockUtils.getLock("useExchangeCode:"+exchangeCode.getCode(), 30);
            if(lock.tryLock()){
                exchangeCode = exchangeCodeService.getById(id);
                try {
                    long canUse = exchangeCode.getTotal() - exchangeCode.getUsedNum();
                    if(canUse>0){
                        long balance = canUse * Long.parseLong(exchangeCode.getBizValue());
                        userBalanceManager.createCode(exchangeCode.getUserId(),balance,UserBalanceRecordEnum.EXCHANGE_CODE,"销毁兑换码："+canUse+"*"+exchangeCode.getName());
                    }
                }finally {
                    lock.unlock();
                }
            }
        }
        return ResultVO.success();
    }


    /**
     * 用户使用兑换码
     *
     * @param userId
     * @param code
     * @return
     */
    @Transactional
    public ResultVO useExchangeCode(Long userId,String code){

        Lock lock = DistributedLockUtils.getLock("useExchangeCode:"+code, 30);
        if (lock.tryLock()) {

            try {
                ExchangeCode exchangeCodeByCode = exchangeCodeService.getExchangeCodeByCode(code);
                if(exchangeCodeByCode==null || CommonStatusEnum.DELETE.getStatus() == exchangeCodeByCode.getStatus()){
                    return ResultVO.fail("兑换码不存在！");
                }

                if(exchangeCodeByCode.getUsedNum()>=exchangeCodeByCode.getTotal()){
                    return ResultVO.fail("兑换码已经被兑换完了！");
                }

                UserExchangeCodeRecord exchangeCodeRecord = userExchangeCodeRecordService.getExchangeCodeRecordByUserIdAndCode(userId, exchangeCodeByCode.getId());
                if(exchangeCodeRecord!=null){
                    return ResultVO.fail("你已经使用过该兑换码！");
                }

                int updateUsedNum = exchangeCodeService.updateUsedNum(exchangeCodeByCode.getId());
                if(updateUsedNum<=0){
                    return ResultVO.fail("使用兑换码失败！");
                }
                // 新增兑换记录
                boolean saveResult = userExchangeCodeRecordService.addRecord(exchangeCodeByCode.getId(),userId);
                if(!saveResult){
                    throw new BizException("新增使用兑换码记录失败！");
                }

                // 根据不同权益进行兑换
                String type = exchangeCodeByCode.getType();
                if(type.equals(ExchangeCodeTypeEnum.BALANCE.getType())){
                    // 新增余额
                    userBalanceManager.addUserNonWithDrawAbleBalance(userId, Long.parseLong(exchangeCodeByCode.getBizValue()), UserBalanceRecordEnum.EXCHANGE_CODE);
                } else if (type.equals(ExchangeCodeTypeEnum.RIGHTS.getType())) {
                    // 权益兑换码
                    userRightsService.addUserRights(Long.parseLong(exchangeCodeByCode.getBizValue()),userId);
                }

            }finally {
                lock.unlock();
            }
        }
        return ResultVO.success();
    }


    /**
     * 新增兑换码
     *
     * @param req
     * @return
     */
    public ResultVO addExchangeCode(Long userId,AddExchangeCodeReq req,String role){

        if(role.equals(RoleEnum.SYSTEM.getRole())){
            return systemAddExchangeCode(userId,req);
        }
        // 以下是创作者新增 兑换码
        Lock lock = DistributedLockUtils.getLock("addExchangeCode:"+userId, 30);
        if(lock.tryLock()){
            try {
                UserCacheBalanceVo userCacheBalanceVo = new UserCacheBalanceVo();
                String key = String.format(CommonConstant.USER_BALANCE_KEY,userId);
                Map<String, String> stringStringMap = redisClient.hgetAll(key);
                if(org.springframework.util.CollectionUtils.isEmpty(stringStringMap)){
                    return ResultVO.fail("账号余额不存在！");
                }
                ObjectToHashMapConverter.setValuesToObject(stringStringMap,userCacheBalanceVo);
                Long withDrawable = userCacheBalanceVo.getWithDrawable();


                BigDecimal num = new BigDecimal(req.getBizValue());
                BigDecimal oneCodeBalance = num.multiply(new BigDecimal(CommonConstant.ONE_DOLLAR));

                long codeBalanceSum = req.getTotal() * oneCodeBalance.longValue();
                if( withDrawable < codeBalanceSum ){
                    return ResultVO.fail("额度不足");
                }
                ExchangeCode exchangeCode = ExchangeCodeConvert.I.convertExchangeCode(req);
                //前端传值为 $
                exchangeCode.setBizValue(String.valueOf(oneCodeBalance.longValue()));
                exchangeCode.setUserId(userId);
                exchangeCode.setStatus(CommonStatusEnum.OK.getStatus());
                exchangeCode.setCreateTime(LocalDateTime.now());
                String code = UUID.randomUUID().toString().replace("-", "");
                exchangeCode.setCode(code);
                boolean saveResult = exchangeCodeService.save(exchangeCode);
                if(!saveResult){
                    return ResultVO.fail("新增兑换码失败");
                }
                userBalanceManager.createCode(userId,-codeBalanceSum,UserBalanceRecordEnum.EXCHANGE_CODE,"生成兑换码："+req.getTotal()+"*"+exchangeCode.getName());
            }finally {
                lock.unlock();
            }
        }
        return ResultVO.success();
    }


    /**
     * 管理员新增兑换码
     *
     * @param req
     * @return
     */
    public ResultVO systemAddExchangeCode(Long userId,AddExchangeCodeReq req){

        ExchangeCode exchangeCode = ExchangeCodeConvert.I.convertExchangeCode(req);
        exchangeCode.setUserId(userId);
        exchangeCode.setStatus(CommonStatusEnum.OK.getStatus());
        exchangeCode.setCreateTime(LocalDateTime.now());
        String code = UUID.randomUUID().toString().replace("-", "");
        exchangeCode.setCode(code);
        boolean saveResult = exchangeCodeService.save(exchangeCode);
        if(!saveResult){
            return ResultVO.fail("新增兑换码失败");
        }
        return ResultVO.success();
    }


    /**
     * 修改兑换码
     *
     * @param req
     * @return
     */
    public ResultVO updateExchangeCode(UpdateExchangeCodeReq req){

        ExchangeCode exchangeCode = ExchangeCodeConvert.I.convertExchangeCode(req);
        boolean updatedResult = exchangeCodeService.updateById(exchangeCode);
        if(!updatedResult){
            return ResultVO.fail("更新兑换码失败");
        }
        return ResultVO.success();
    }
    /**
     * 遍历枚举
     *
     * @param req
     * @return
     */
    public ResultVO updateExchangeCodeType(){
        List<ExchangeCodeTypeVo> exchangeCodeTypeVos = new ArrayList<>();
        for (ExchangeCodeTypeEnum value : ExchangeCodeTypeEnum.values()) {
            ExchangeCodeTypeVo exchangeCodeTypeVo = new ExchangeCodeTypeVo();
            exchangeCodeTypeVo.setType(value.getType());
            exchangeCodeTypeVo.setDesc(value.getDesc());
            exchangeCodeTypeVos.add(exchangeCodeTypeVo);
        }
        return ResultVO.success(exchangeCodeTypeVos);
    }


}
