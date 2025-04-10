package com.open.ai.eros.ai.model.manager;

import com.open.ai.eros.ai.bean.vo.AITextChatVo;
import com.open.ai.eros.ai.model.ModelMessageAdapt;
import com.open.ai.eros.creator.bean.vo.MaskAIParamVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @类名：MessageManager
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/8 23:16
 */

@Component
public class MessageManager {


    @Autowired
    private List<ModelMessageAdapt> modelMessageAdapts;


    /**
     * 转化各种 ai模型的输入格式
     *
     * @param maskAIParamVo
     * @param model
     * @return
     */
    public String getMessage(AITextChatVo req, MaskAIParamVo maskAIParamVo, String model){
        for (ModelMessageAdapt adapt : modelMessageAdapts) {
            boolean match = adapt.match(model);
            if(match){
                return adapt.modelMessage(req, maskAIParamVo,model);
            }
        }
        return null;
    }



}
