package com.open.ai.eros.db.mysql.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.open.ai.eros.db.mysql.user.entity.ExchangeCode;
import com.open.ai.eros.db.mysql.user.mapper.ExchangeCodeMapper;
import com.open.ai.eros.db.mysql.user.service.IExchangeCodeService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 兑换码表 服务实现类
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-27
 */
@Service
public class ExchangeCodeServiceImpl extends ServiceImpl<ExchangeCodeMapper, ExchangeCode> implements IExchangeCodeService {


    public ExchangeCode getExchangeCodeByCode(String code){
        return this.getBaseMapper().getExchangeCodeByCode(code);
    }



    /**
     * 已使用+1
     * @param id
     * @return
     */
    public int updateUsedNum(Long id){
        return this.getBaseMapper().updateUsedNum(id,1);
    }



    public List<ExchangeCode> getExchangeCode(Long userId, Integer pageNum, Integer pageSize, Integer status){
        return this.getBaseMapper().getExchangeCode(userId, (pageNum-1)*pageSize, pageSize, status);
    }


}
