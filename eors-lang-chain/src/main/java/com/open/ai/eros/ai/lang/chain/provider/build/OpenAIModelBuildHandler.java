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
import com.open.ai.eros.ai.lang.chain.constants.ChatErrorEnum;
import com.open.ai.eros.ai.lang.chain.constants.ProviderEnum;
import com.open.ai.eros.ai.lang.chain.provider.embedding.openai.ErosOpenAIEmbeddingModel;
import com.open.ai.eros.common.constants.ModelPriceEnum;
import com.open.ai.eros.common.exception.BizException;
import com.open.ai.eros.db.mysql.ai.entity.ModelConfigVo;
import dev.langchain4j.model.embedding.DimensionAwareEmbeddingModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @author GB
 * @since 2024-08-19 10:08
 */
@Slf4j
@Component
public class OpenAIModelBuildHandler implements ModelBuildHandler {

    static Set<String> embeddinModelSet = new HashSet<>();
    static {
        embeddinModelSet.add(ModelPriceEnum.text_embedding_3_large.getModel());
        embeddinModelSet.add(ModelPriceEnum.text_embedding_3_small.getModel());
        embeddinModelSet.add(ModelPriceEnum.text_embedding_ada_002.getModel());
    }


    @Override
    public boolean whetherCurrentModel(String templateModel) {
        for (String embeddingModel : embeddinModelSet) {
            if(templateModel.contains(embeddingModel)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean basicCheck(ModelConfigVo model) {
        if (StringUtils.isBlank(model.getBaseUrl())) {
            throw new BizException(ChatErrorEnum.BASE_URL_IS_NULL.getErrorCode(),
                    ChatErrorEnum.BASE_URL_IS_NULL.getErrorDesc(ProviderEnum.OPENAI.name(), model.getName()));
        }
        return true;
    }


    @Override
    public Pair<String, DimensionAwareEmbeddingModel> buildEmbedding(ModelConfigVo model,String templateModel) {
        try {
            if (!whetherCurrentModel(templateModel)) {
                return null;
            }
            if (!basicCheck(model)) {
                return null;
            }
            String[] split = templateModel.split(":");
            ModelVectorEnum modelVectorEnum = ModelVectorEnum.modelVectorEnumMap.get(split[1]);
            ErosOpenAIEmbeddingModel openAiEmbeddingModel = new ErosOpenAIEmbeddingModel(model.getBaseUrl(),model.getToken(),split[1],modelVectorEnum.getVectorLength(),null,true,true);
            return Pair.of(templateModel, openAiEmbeddingModel);
        } catch (BizException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("openai embedding 模型配置报错", e);
            return null;
        }
    }
}
