package com.open.ai.eros.db.mysql.pay.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.open.ai.eros.db.constants.RightsStatusEnum;
import com.open.ai.eros.db.mysql.pay.entity.Rights;
import com.open.ai.eros.db.mysql.pay.entity.RightsSimpleVo;
import com.open.ai.eros.db.mysql.pay.mapper.RightsMapper;
import com.open.ai.eros.db.mysql.pay.service.IRightsService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 权益 服务实现类
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-20
 */
@Service
public class RightsServiceImpl extends ServiceImpl<RightsMapper, Rights> implements IRightsService {


    /**
     * 获取所有有效的权益
     *
     * @return
     */
    public List<RightsSimpleVo> getRightsSimple(){
        return this.getBaseMapper().getRightsSimple(RightsStatusEnum.OK.getStatus());
    }



}
