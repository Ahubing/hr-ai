package com.open.ai.eros.db.mysql.pay.service.impl;

import com.open.ai.eros.db.mysql.pay.entity.GoodsSnapshot;
import com.open.ai.eros.db.mysql.pay.mapper.GoodsSnapshotMapper;
import com.open.ai.eros.db.mysql.pay.service.IGoodsSnapshotService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品快照表 服务实现类
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-24
 */
@Service
public class GoodsSnapshotServiceImpl extends ServiceImpl<GoodsSnapshotMapper, GoodsSnapshot> implements IGoodsSnapshotService {



    public GoodsSnapshot getNewGoodsSnapshot(Long goodId){

        return this.getBaseMapper().getNewGoodsSnapshot(goodId);

    }



}
