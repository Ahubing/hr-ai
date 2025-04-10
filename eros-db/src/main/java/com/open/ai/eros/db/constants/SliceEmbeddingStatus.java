package com.open.ai.eros.db.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * 状态 1: 未向量  2：向量化中 3 已向量化
 */
public enum SliceEmbeddingStatus {

    UN_EMBEDDING(1,"未向量"),
    EMBEDDING(2,"向量中"),
    EMBEDDING_ED(3,"已向量"),
    ;

    static Map<Integer,SliceEmbeddingStatus> sliceEmbeddingStatusMap = new HashMap<>();

    static {
        for (SliceEmbeddingStatus value : values()) {
            sliceEmbeddingStatusMap.put(value.status, value);
        }
    }

    public static SliceEmbeddingStatus getSliceEmbeddingStatus(Integer status){
        return sliceEmbeddingStatusMap.getOrDefault(status,UN_EMBEDDING);
    }


    private Integer status;
    private String desc;

    SliceEmbeddingStatus(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
