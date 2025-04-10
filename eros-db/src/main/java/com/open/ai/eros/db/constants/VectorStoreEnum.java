package com.open.ai.eros.db.constants;

public enum VectorStoreEnum {
    MILVUS("milvus"),
    PG_VECTOR("pg_vector")
    ;


    private String vector;

    VectorStoreEnum(String vector) {
        this.vector = vector;
    }


    public String getVector() {
        return vector;
    }
}
