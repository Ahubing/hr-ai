package com.open.ai.eros.db.mysql.hr.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.ai.eros.db.mysql.hr.entity.AmPosition;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.open.ai.eros.db.mysql.hr.req.SearchPositionListReq;
import com.open.ai.eros.db.mysql.hr.vo.AmPositionVo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
public interface AmPositionMapper extends BaseMapper<AmPosition> {

    IPage<AmPositionVo> pagePosition(@Param("page") Page<AmPositionVo> page, @Param("req")SearchPositionListReq req);
}
