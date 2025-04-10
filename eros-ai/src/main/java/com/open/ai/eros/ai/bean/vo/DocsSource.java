package com.open.ai.eros.ai.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @类名：DocsSource
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/28 17:44
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DocsSource {

    /**
     * url
     */
    private String url;

    /**
     * 文档名称
     */
    private String name;

    /**
     * 命中的分片信息
     */
    private String chunk;

    /**
     * 命中分数
     */
    private String score;

}
