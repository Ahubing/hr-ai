package com.open.ai.eros.db.mysql.hr.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.db.mysql.hr.entity.IcRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.open.ai.eros.db.mysql.hr.req.IcRecordPageReq;
import com.open.ai.eros.db.mysql.hr.vo.IcRecordVo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 面试日历-预约记录 Mapper 接口
 * </p>
 *
 * @author Eros-AI
 * @since 2025-02-24
 */
public interface IcRecordMapper extends BaseMapper<IcRecord> {

    IPage<IcRecordVo> pageIcRecord(@Param("page")Page<IcRecordVo> page, @Param("req") IcRecordPageReq req);
}
