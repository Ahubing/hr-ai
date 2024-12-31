package com.open.ai.eros.ai.job;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.ai.convert.ModelConfigConvert;
import com.open.ai.eros.ai.manager.ModelConfigManager;
import com.open.ai.eros.db.mysql.ai.entity.ModelConfig;
import com.open.ai.eros.db.mysql.ai.entity.ModelConfigVo;
import com.open.ai.eros.db.mysql.ai.service.impl.ModelConfigServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @类名：InitModelConfigJob
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/21 15:03
 */

@Component
@Slf4j
@EnableScheduling
public class InitModelConfigJob {


    @Autowired
    private ModelConfigServiceImpl modelConfigService;


    @Autowired
    private ModelConfigManager modelConfigManager;

    /**
     * 初始化渠道
     */
    @Scheduled(fixedDelay = 200000  )
    public void initModelConfig(){
        log.info("initModelConfig 开始检测渠道缓存。。。。。");
        List<ModelConfig> modelConfigs = modelConfigService.list();
        for (ModelConfig modelConfig : modelConfigs) {
            if(modelConfig.getStatus()!=1){
                continue;
            }
            ModelConfigVo modelConfigVo = ModelConfigConvert.I.convertModelConfigVo(modelConfig);
            List<String> templateModels = modelConfigVo.getTemplateModel();
            for (String templateModel : templateModels) {
                try {
                    ModelConfigVo configVo = modelConfigManager.getModelConfig(templateModel);
                    if(configVo==null){
                        log.info("initModelConfig 该渠道({})中{}不存在缓存，重新加载。。。。。。",modelConfigVo.getName(),templateModel);
                        modelConfigManager.addModelConfigToTemplate(modelConfig.getId(),modelConfig.getWeight(),modelConfig.getTemplateModel());
                        log.info("initModelConfig 该渠道({})中加载成功。。。。。。",modelConfigVo.getName());
                        break;
                    }
                }catch (Exception e){
                    log.error("initModelConfig error  modelConfig={}", JSONObject.toJSONString(modelConfig),e);
                }
            }
        }
        log.info("initModelConfig 检测渠道完成缓存。。。。。");
    }

}
