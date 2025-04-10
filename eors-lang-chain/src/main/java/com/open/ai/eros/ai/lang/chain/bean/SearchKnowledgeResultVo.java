package com.open.ai.eros.ai.lang.chain.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @类名：SearchKnowledgeResultVo
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/16 23:33
 */

//@ApiModel("知识库搜索结果")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SearchKnowledgeResultVo {

    //@ApiModelProperty("切片id")
    private Long sliceId;

    //@ApiModelProperty("用户问题")
    private String question;

    //@ApiModelProperty("切片内容")
    private String content;

}
