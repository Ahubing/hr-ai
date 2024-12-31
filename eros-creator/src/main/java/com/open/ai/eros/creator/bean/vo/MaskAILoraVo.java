package com.open.ai.eros.creator.bean.vo;

import com.open.ai.eros.common.vo.ChatMessage;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@ApiModel("面具的彩蛋参数")
@Data
public class MaskAILoraVo {

    private List<String> keyword;

    private String title;

    private String content;

}
