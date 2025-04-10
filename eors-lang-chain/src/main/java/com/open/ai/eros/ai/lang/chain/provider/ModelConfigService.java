package com.open.ai.eros.ai.lang.chain.provider;

import com.open.ai.eros.db.mysql.ai.entity.ModelConfigVo;

public interface ModelConfigService {

    ModelConfigVo getModelConfig(String templateModel);

}
