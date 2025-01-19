package com.open.hr.ai.bean.vo;

import com.open.ai.eros.common.vo.ChatMessage;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.LinkedList;

/**
 * @类名：MaskAIParamVo
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/13 23:28
 */
@ApiModel("面具的ai参数")
@Data
public class AmMaskAIParamVo {

    private LinkedList<ChatMessage> messages;

    private Double temperature;

}
