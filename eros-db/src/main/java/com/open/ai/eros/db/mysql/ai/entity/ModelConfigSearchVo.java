package com.open.ai.eros.db.mysql.ai.entity;

import com.open.ai.eros.db.privacy.annotation.FieldEncrypt;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModelConfigSearchVo {

    private Long id;

    private Integer status;

    private String template;

    private Integer pageIndex = 0;

    private Integer pageSize = 20;

    @FieldEncrypt
    private String token;

    /**
     * 搜索的keywords
     */
    private String modelConfigName;

}
