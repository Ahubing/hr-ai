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
import com.open.ai.eros.common.exception.BizException;
import com.open.ai.eros.db.mysql.ai.entity.ModelConfigVo;
import dev.langchain4j.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.model.embedding.DimensionAwareEmbeddingModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author GB
 * @since 2024-08-19 10:08
 */
@Slf4j
@Component
public class QWenModelBuildHandler implements ModelBuildHandler {

    @Override
    public boolean whetherCurrentModel(String templateModel) {
        return templateModel.contains(ProviderEnum.OLLAMA.name());
    }

    @Override
    public boolean basicCheck(ModelConfigVo model) {
        if (StringUtils.isBlank(model.getBaseUrl())) {
            throw new BizException(ChatErrorEnum.BASE_URL_IS_NULL.getErrorCode(),
                    ChatErrorEnum.BASE_URL_IS_NULL.getErrorDesc(ProviderEnum.Q_WEN.name(), model.getName()));
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
            QwenEmbeddingModel qwenEmbeddingModel = QwenEmbeddingModel
                    .builder()
                    .apiKey(model.getToken())
                    // embedding模型标识
                    .modelName(split[1])
                    .build();
            return Pair.of(templateModel, qwenEmbeddingModel);
        } catch (BizException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("qian wen embedding 配置报错", e);
            return null;
        }
    }
}
