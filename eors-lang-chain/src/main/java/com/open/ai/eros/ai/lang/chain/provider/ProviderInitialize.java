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

package com.open.ai.eros.ai.lang.chain.provider;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjectUtil;
import com.open.ai.eros.ai.lang.chain.constants.EmbedConst;
import com.open.ai.eros.ai.lang.chain.convert.ModelConfigConvert;
import com.open.ai.eros.ai.lang.chain.provider.build.ModelBuildHandler;
import com.open.ai.eros.common.config.SpringContextHolder;
import com.open.ai.eros.db.mysql.ai.entity.ModelConfig;
import com.open.ai.eros.db.mysql.ai.entity.ModelConfigVo;
import com.open.ai.eros.db.mysql.ai.service.impl.ModelConfigServiceImpl;
import dev.langchain4j.model.embedding.DimensionAwareEmbeddingModel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author tycoding
 * @since 2024/6/16
 */
@Configuration
@AllArgsConstructor
@Slf4j
public class ProviderInitialize implements ApplicationContextAware {
    private final ModelConfigServiceImpl modelConfigService;
    private final SpringContextHolder contextHolder;
    private List<ModelBuildHandler> modelBuildHandlers;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        init();
    }

    public void init() {
        // un register embedding model
        contextHolder.unregisterBean(EmbedConst.CLAZZ_NAME_OPENAI);
        contextHolder.unregisterBean(EmbedConst.CLAZZ_NAME_AZURE_OPENAI);
        contextHolder.unregisterBean(EmbedConst.CLAZZ_NAME_QIANFAN);
        contextHolder.unregisterBean(EmbedConst.CLAZZ_NAME_ZHIPU);
        contextHolder.unregisterBean(EmbedConst.CLAZZ_NAME_QIANWEN);
        contextHolder.unregisterBean(EmbedConst.CLAZZ_NAME_OLLAMA);

        List<ModelConfig> list = modelConfigService.list();
        //if (Objects.equals(model.getBaseUrl(), "")) {
        //    model.setBaseUrl(null);
        //}
        //// Uninstall previously registered beans before registering them
        //contextHolder.unregisterBean(model.getId());
        //imageHandler(model);
        list.forEach(this::embeddingHandler);
    }


    private void embeddingHandler(ModelConfig model) {
        try {
            String type = model.getTemplateModel();
            if (!type.contains("embedding")) {
                return;
            }
            ModelConfigVo modelConfigVo = ModelConfigConvert.I.convertModelConfigVo(model);

            modelBuildHandlers.forEach(x -> {
                List<String> templateModels = modelConfigVo.getTemplateModel();
                for (String templateModel : templateModels) {
                    String key = modelConfigVo.getBaseUrl() + ":" + modelConfigVo.getToken() + ":" + templateModel;
                    if(EmbeddingProvider.embeddingModelMap.get(key)!=null){
                        continue;
                    }
                    Pair<String, DimensionAwareEmbeddingModel> embeddingModelPair = x.buildEmbedding(modelConfigVo,templateModel);
                    if (ObjectUtil.isNotEmpty(embeddingModelPair)) {
                        //contextHolder.registerBean(templateModel, embeddingModelPair.getValue());
                        EmbeddingProvider.embeddingModelMap.put(key, embeddingModelPair.getValue());
                    }
                }
            });

        } catch (Exception e) {
            log.error("model 【id{} name{}】 embedding 配置报错", model.getId(), model.getName());
        }
    }

}
