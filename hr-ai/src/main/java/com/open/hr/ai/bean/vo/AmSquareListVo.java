package com.open.hr.ai.bean.vo;

import com.open.ai.eros.db.mysql.hr.entity.AmZpPlatforms;
import lombok.Data;

import java.util.List;

/**
 * @Author liuzilin
 * @Date 2025/1/15 02:05
 */
@Data
public class AmSquareListVo {

    private List<AmSquareRolesVo> amSquareRolesVos;

    private List<String> keywordList;
}
