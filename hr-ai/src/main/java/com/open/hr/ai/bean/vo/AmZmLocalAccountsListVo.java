package com.open.hr.ai.bean.vo;

import com.open.ai.eros.db.mysql.hr.entity.AmZpPlatforms;
import lombok.Data;

import java.util.List;

/**
 * @Author 
 * @Date 2025/1/15 02:05
 */
@Data
public class AmZmLocalAccountsListVo {

    private List<AmZpLocalAccoutsVo> localAccountsList;

    private List<AmZpPlatforms> platforms;

    private List<String> citys;
}
