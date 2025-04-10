/*
 * Copyright (c) 2024 LangChat. TyCoding All Rights Reserved.
 *
 * Licensed under the GNU Affero General Public License, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gnu.org/licenses/agpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.open.ai.eros.ai.lang.chain.provider.build;

import cn.hutool.core.lang.Pair;
import com.open.ai.eros.db.mysql.ai.entity.ModelConfigVo;
import dev.langchain4j.model.embedding.DimensionAwareEmbeddingModel;

/**
 * @author GB
 * @since 2024-08-18 09:57
 */
public interface ModelBuildHandler {

    /**
     * 判断是不是当前模型
     */
    boolean whetherCurrentModel(String templateModel);

    /**
     * basic check
     */
    boolean basicCheck(ModelConfigVo model);

    /**
     * embedding config
     */
    Pair<String, DimensionAwareEmbeddingModel> buildEmbedding(ModelConfigVo model,String templateModel);


}
