package com.open.hr.ai.bean.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.open.ai.eros.db.mysql.hr.entity.AmPositionPost;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 部门
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AmPositionSectionVo {


    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 归属的账号
     */
    private Long adminId;

    /**
     * 部门名称
     */
    private String name;

    private List<AmPositionPost> amPositionPosts;

}
