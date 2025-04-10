package com.open.ai.eros.db.mysql.social.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AnnouncementSearchVo {

    private Long id;

    private String appId;

    private Integer type;

    private Integer status;

    private String  title;

    private Integer pageIndex = 0;

    private Integer pageSize = 20;

}
