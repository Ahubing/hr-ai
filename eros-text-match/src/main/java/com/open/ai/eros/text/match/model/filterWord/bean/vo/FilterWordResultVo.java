package com.open.ai.eros.text.match.model.filterWord.bean.vo;

import lombok.Data;

import java.util.Objects;

@Data
public class FilterWordResultVo {

    private Long id;

    private int type;

    private String wordContent;

    private Integer riskLevel;

    private Integer riskType;

    /**
     * 自动回复的id
     */
    private Long replyId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FilterWordResultVo result = (FilterWordResultVo) o;
        return id.equals(result.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
