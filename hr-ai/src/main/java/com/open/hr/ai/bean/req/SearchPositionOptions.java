package com.open.hr.ai.bean.req;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Date 2025/1/4 21:17
 */
@Data
public class SearchPositionOptions {


    private Integer positionId;

    @NotNull
    private Integer accountId;
}
