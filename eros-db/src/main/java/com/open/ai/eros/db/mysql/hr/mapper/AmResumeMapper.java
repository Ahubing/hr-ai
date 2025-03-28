package com.open.ai.eros.db.mysql.hr.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.ai.eros.common.vo.SqlSortParam;
import com.open.ai.eros.db.mysql.hr.entity.AmResume;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.open.ai.eros.db.mysql.hr.vo.AmResumeVo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 简历 Mapper 接口
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
public interface AmResumeMapper extends BaseMapper<AmResume> {

    IPage<AmResumeVo> resumeList(@Param("page") Page<AmResumeVo> page, @Param("adminId") Long adminId,
                                 @Param("type") Integer type, @Param("post_id") Integer post_id,
                                 @Param("name") String name, @Param("startDateTime") LocalDateTime startDateTime,
                                 @Param("endDateTime") LocalDateTime endDateTime, @Param("expectPosition") String expectPosition,
                                 @Param("postName") String postName, @Param("platformId") Integer platformId,
                                 @Param("score") BigDecimal score, @Param("deptId") Integer deptId,
                                 @Param("deptName") String deptName, @Param("positionId") Integer positionId,
                                 @Param("positionName") String positionName,@Param("platform") String platform,
                                 @Param("sortParams") List<SqlSortParam> sortParams);

    int countByType(@Param("adminId") Long adminId, @Param("type") Integer type, @Param("post_id") Integer post_id,
                    @Param("name") String name, @Param("startDateTime") LocalDateTime startDateTime,
                    @Param("endDateTime") LocalDateTime endDateTime, @Param("expectPosition") String expectPosition,
                    @Param("postName") String postName, @Param("platformId") Integer platformId,
                    @Param("score") BigDecimal score, @Param("deptId") Integer deptId,
                    @Param("deptName") String deptName, @Param("positionId") Integer positionId,
                    @Param("positionName") String positionName,@Param("platform") String platform);
}
