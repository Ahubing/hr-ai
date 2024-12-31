package com.open.ai.eros.ai.controller;

import com.open.ai.eros.ai.manager.AIManager;
import com.open.ai.eros.ai.model.bean.vo.gpt.GptCompletionRequest;
import com.open.ai.eros.ai.util.ResponseUtil;
import com.open.ai.eros.ai.util.SendMessageUtil;
import com.open.ai.eros.common.constants.BaseCodeEnum;
import com.open.ai.eros.common.constants.ModelPriceEnum;
import com.open.ai.eros.common.util.HttpUtil;
import com.open.ai.eros.common.vo.ChatMessage;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.LinkedList;

/**
 * @类名：ApiAIController
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/25 22:55
 */
@Slf4j
@RestController
public class ApiAIController {


    @Autowired
    private AIManager aiManager;


    /**
     * 转发gpt
     *
     * @param req
     * @param request
     * @param response
     */
    @ApiOperation(value = "gpt聊天接口")
    @PostMapping(value = {"/v1/chat/completions"})
    public void api(@Valid @RequestBody GptCompletionRequest req, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String token = request.getHeader("Authorization");
            if (StringUtils.isEmpty(token)) {
                ResponseUtil.errorMsg(BaseCodeEnum.NO_TOKEN_PARAM.getMsg(), 401, response);
                return;
            }
            log.info(" api request token={} model={},ip={}", token, req.getModel(), HttpUtil.getIpAddress());
            String model = req.getModel();
            // 兼容gpts模型
            if (model.contains(ModelPriceEnum.gpt_4_gizmo.getModel())) {
                req.setModel(ModelPriceEnum.gpt_4_gizmo.getModel());
                if (StringUtils.isEmpty(req.getGizmo_id())) {
                    String gizmo_id = model.replace(ModelPriceEnum.gpt_4_gizmo.getModel() + "-", "");
                    req.setGizmo_id(gizmo_id);
                }
            }

            LinkedList<ChatMessage> messages = req.getMessages();
            if (CollectionUtils.isEmpty(messages)) {
                log.info("api messages is null token={}", token);
                ResponseUtil.errorMsg(BaseCodeEnum.NO_CHAT_MESSAGE.getMsg(), 401, response);
                return;
            }

            ModelPriceEnum modelPriceEnum = ModelPriceEnum.getModelPrice(req.getModel());
            if (modelPriceEnum == null) {
                ResponseUtil.errorMsg(BaseCodeEnum.MODEL_NO_OPEN.getMsg(), 403, response);
                return;
            }
            token = token.replace("Bearer", "").trim();
            if (req.getStream()) {
                response.setContentType("text/event-stream");
            } else {
                response.setContentType("application/json");
            }
            if (req.getResponse_format() != null) {
                // 设置响应内容类型为JSON
                response.setContentType("application/json");
            }
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Cache-Control", "no-cache");
            SendMessageUtil sendMessageUtil = new SendMessageUtil(response);
            aiManager.apiStartAIChat(token, req, sendMessageUtil);
        } catch (Exception e) {
            log.error("api error req={}", req, e);
            ResponseUtil.errorMsg(BaseCodeEnum.SERVER_BUSY.getMsg(), 500, response);
        }
    }


}
