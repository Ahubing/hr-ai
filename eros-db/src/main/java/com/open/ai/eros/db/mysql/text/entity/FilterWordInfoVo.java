package com.open.ai.eros.db.mysql.text.entity;

import com.open.ai.eros.db.util.TextUtil;
import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
public class FilterWordInfoVo {

    private Long id;

    private String wordContent;

    private int type;

    private String channelStr;

    /**
     * 风险等级
     */
    private Integer riskType;

    /**
     * 自动回复的id
     */
    private Long replyId;

    /**
     * 风险等级
     */
    private Integer riskLevel;

    private int status;

    private Date createTime;

    private Date updateTime;

    private String language;

    public Set<Long> getChannelIds(){
        return TextUtil.getChannelIds(this.channelStr);
    }

}
