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
import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.ai.lang.chain.provider.build.ModelBuildHandler;
import com.open.ai.eros.ai.lang.chain.provider.splitter.SplitterUtil;
import com.open.ai.eros.common.exception.BizException;
import com.open.ai.eros.db.mysql.ai.entity.ModelConfigVo;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.model.embedding.DimensionAwareEmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModelName;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author tycoding
 * @since 2024/3/8
 */
@Slf4j
@Component
@AllArgsConstructor
public class EmbeddingProvider {

    private final ModelConfigService modelConfigManager;
    private List<ModelBuildHandler> modelBuildHandlers;

    public static Map<String,EmbeddingModel> embeddingModelMap = new ConcurrentHashMap<>();


    /**
     * 获取文档切割类
     *
     * @return
     */
    public static DocumentSplitter splitter(String templateModel,String name) {

        String[] split = templateModel.split(":");
        String model = split[1];
        OpenAiEmbeddingModelName openAiEmbeddingModelName= null;
        for (OpenAiEmbeddingModelName value : OpenAiEmbeddingModelName.values()) {
            if(value.toString().equals(model)){
                openAiEmbeddingModelName = value;
                break;
            }
        }

        if(openAiEmbeddingModelName==null){
            throw new BizException("未知的向量值");
        }

        return SplitterUtil.getSplitter(name);
        //ModelVectorEnum modelVectorEnum = ModelVectorEnum.getModelVectorEnum(openAiEmbeddingModelName.toString());
        //if (modelVectorEnum.getModelSource().equals(ProviderEnum.OPENAI.name())) {
        //    return DocumentSplitters.recursive(200, 0, new OpenAiTokenizer(modelVectorEnum.getEncodingForModel()));
        //}
        //return DocumentSplitters.recursive(200, 0);
    }



    public EmbeddingModel getEmbeddingModel(ModelConfigVo modelConfigVo, String templateModel){
        String key = modelConfigVo.getBaseUrl() + ":" + modelConfigVo.getToken() + ":" + templateModel;
        for (ModelBuildHandler modelBuildHandler : modelBuildHandlers) {
            EmbeddingModel embeddingModel = EmbeddingProvider.embeddingModelMap.get(key);
            if(embeddingModel!=null){
                return embeddingModel;
            }
            Pair<String, DimensionAwareEmbeddingModel> embeddingModelPair = modelBuildHandler.buildEmbedding(modelConfigVo,templateModel);
            if (ObjectUtil.isNotEmpty(embeddingModelPair)) {
                //contextHolder.registerBean(templateModel, embeddingModelPair.getValue());
                EmbeddingProvider.embeddingModelMap.put(key, embeddingModelPair.getValue());
                return embeddingModelPair.getValue();
            }
        }
        return new BgeSmallEnV15QuantizedEmbeddingModel();
    }

    /**
     * 获取向量化的类
     *
     * @param templateModel
     * @return
     */
    public EmbeddingModel embed(String templateModel) {
        ModelConfigVo modelConfig = modelConfigManager.getModelConfig(templateModel);
        log.info("embed templateModel={},modelConfig={}",templateModel, JSONObject.toJSONString(modelConfig));
        if(modelConfig==null){
            return new BgeSmallEnV15QuantizedEmbeddingModel();
        }
        return getEmbeddingModel(modelConfig,templateModel);
    }
}
