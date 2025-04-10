package com.open.ai.eros.db.mysql.ai.service.impl;

import com.open.ai.eros.db.mysql.ai.entity.MaskStatDay;
import com.open.ai.eros.db.mysql.ai.entity.MaskStatList;
import com.open.ai.eros.db.mysql.ai.entity.UserAiConsumeRecordStatVo;
import com.open.ai.eros.db.mysql.ai.mapper.MaskStatDayMapper;
import com.open.ai.eros.db.mysql.ai.service.IMaskStatDayService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 面具消耗的日统计表 服务实现类
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-12
 */
@Service
public class MaskStatDayServiceImpl extends ServiceImpl<MaskStatDayMapper, MaskStatDay> implements IMaskStatDayService {


    /**
     * 获取最新的面具信息
     *
     * @return
     */
    public MaskStatDay getLastMaskStatDay(){
        return this.baseMapper.getLastMaskStatDay();
    }

    /**
     * 获取创作者面具消耗列表
     *
     * @return
     */
    public List<MaskStatList> getLastMaskStatList(Long userId, Date startTime){
        return this.baseMapper.statMasksConsumeRecord(userId,startTime);
    }
    /**
     * 获取历史面具使用数
     *
     * @return
     */
    public Long getLastMaskStatRecordCount(){
        return this.baseMapper.getLastMaskStatRecordCount();
    }

    /**
     * 批量保存统计数据
     *
     * @param maskStatDays
     * @return
     */
    public boolean batchSave(List<MaskStatDay> maskStatDays){
        return this.saveBatch(maskStatDays);
    }

}
