package com.open.ai.eros.db.mysql.creator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.open.ai.eros.db.mysql.creator.entity.Mask;
import com.open.ai.eros.db.mysql.creator.mapper.MaskMapper;
import com.open.ai.eros.db.mysql.creator.service.IMaskService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-13
 */
@Service
public class MaskServiceImpl extends ServiceImpl<MaskMapper, Mask> implements IMaskService {


    public List<Mask> getSimpleMask(){
        return this.getBaseMapper().getSimpleMask();
    }


    public int updateMaskChannelNull(Long channelId,Long userId){
        return this.getBaseMapper().updateMaskChannelNull(channelId,userId);
    }


    /**
     * 获取用户的面具id
     *
     * @param userId
     * @return
     */
    public List<Long> getMaskIds(Long userId){
        return this.getBaseMapper().getMaskId(userId);
    }


    /**
     * 更新面具 收藏数
     * @param maskId
     * @return
     */
    public int updateMaskCollectNum(Long maskId){
        return this.getBaseMapper().updateMaskCollectNum(maskId);
    }

/**
     * 获取系统面具数
     * @return
     */
    public Long getMaskSum(){
        return this.getBaseMapper().getMaskSum();
    }


    /**
     * 更新面具的热度
     * @param maskId
     * @return
     */
    public int updateMaskHeat(Long maskId){
        return this.getBaseMapper().updateMaskHeat(maskId);
    }


}
