package com.open.ai.eros.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class MenuVo {
    private String name;
    private String displayName;
    private MenuMetaVo meta;
    private List<MenuVo> children;
}
