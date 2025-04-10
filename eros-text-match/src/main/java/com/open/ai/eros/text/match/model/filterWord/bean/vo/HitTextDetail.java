package com.open.ai.eros.text.match.model.filterWord.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @类名：HitTextDetail
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/22 22:42
 */
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
public class HitTextDetail {

    private String hitText;

    private Long id;

    private String replyTemplate;
}
