package com.open.hr.ai.bean.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 招聘平台

 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AmZpPlatformsVo{


    private Integer id;

    /**
     * 招聘平台名称
     */
    private String name;


}
