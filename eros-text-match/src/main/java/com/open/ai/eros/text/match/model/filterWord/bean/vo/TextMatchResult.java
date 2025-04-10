package com.open.ai.eros.text.match.model.filterWord.bean.vo;

import lombok.Data;

import java.util.List;

@Data
public class TextMatchResult {
    private boolean isHit;
    private String source;

    // 命中文本详情
    private List<HitTextDetail> hitTextDetails;

    public TextMatchResult(String source) {
        this.source = source;
    }
}
