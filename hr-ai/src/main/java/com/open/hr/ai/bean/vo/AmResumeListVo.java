package com.open.hr.ai.bean.vo;

import com.open.ai.eros.db.mysql.hr.entity.AmResume;
import lombok.Data;

import java.util.List;

/**
 * @Author 
 * @Date 2025/1/15 12:57
 */
@Data
public class AmResumeListVo {

    private List<List<AmResume>> amResumes;

    private List<AmPositionSectionVo> amPositionSectionVos;
}
