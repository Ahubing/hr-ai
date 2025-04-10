package com.open.ai.eros.db.mysql.text.service.impl;

import com.open.ai.eros.db.mysql.text.entity.Sop;
import com.open.ai.eros.db.mysql.text.mapper.SopMapper;
import com.open.ai.eros.db.mysql.text.service.ISopService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Eros-AI
 * @since 2024-10-20
 */
@Service
public class SopServiceImpl extends ServiceImpl<SopMapper, Sop> implements ISopService {


    public Sop getSopBuSceneCode(String code){
        return this.getBaseMapper().getSopBuSceneCode(code);
    }



}
