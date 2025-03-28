package com.open.ai.eros.common.vo;

import lombok.Data;

@Data
public class SqlSortParam {

    // 字段名
    private String field;

    // 1=ASC, -1=DESC
    private int order;
}
