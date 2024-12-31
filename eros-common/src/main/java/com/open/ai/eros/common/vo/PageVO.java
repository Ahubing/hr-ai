package com.open.ai.eros.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;


@ApiModel("分页组件")
public class PageVO<T> {

    /**
     * 总数
     */
    @ApiModelProperty("总数")
    private long total;

    /**
     * 分页数据
     */
    @ApiModelProperty("分页数据")
    private List<T> data;

    public static <T> PageVO<T> allListNoPage(List<T> data) {
        return new PageVO<T>(0, data);
    }

    public static <T> PageVO<T> build(long total, List<T> data) {
        return new PageVO<T>(total, data);
    }

    public PageVO() {
    }

    public PageVO(long total, List<T> data) {
        this.total = total;
        this.data = data;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }


    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
