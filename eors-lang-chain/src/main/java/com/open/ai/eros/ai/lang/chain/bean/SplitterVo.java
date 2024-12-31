package com.open.ai.eros.ai.lang.chain.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @类名：SplitterVo
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/29 16:53
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SplitterVo {

    private String name;

    private String value;

}
