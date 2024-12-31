package com.open.ai.eros.db.mysql.pay.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.open.ai.eros.db.mysql.pay.entity.Goods;
import com.open.ai.eros.db.mysql.pay.mapper.GoodsMapper;
import com.open.ai.eros.db.mysql.pay.service.IGoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-24
 */
@Slf4j
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {

    /**
     * 更新商品的已送数量
     *
     * @param id
     * @return
     */
    public int updateGoodsSeedNum(Long id){
        int result = this.getBaseMapper().updateGoodsSeedNum(id);
        log.info("updateGoodsSeedNum id={} result={}",id,result);
        return result;
    }
}
