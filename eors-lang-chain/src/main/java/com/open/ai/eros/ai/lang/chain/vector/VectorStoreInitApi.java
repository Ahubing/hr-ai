package com.open.ai.eros.ai.lang.chain.vector;


import com.open.ai.eros.ai.lang.chain.bean.CollectionVo;
import com.open.ai.eros.db.constants.VectorStoreEnum;

import java.util.List;

public abstract class VectorStoreInitApi {


    public abstract List<CollectionVo> getCollectionName();

    public abstract VectorStoreEnum vectorStoreType();

}
