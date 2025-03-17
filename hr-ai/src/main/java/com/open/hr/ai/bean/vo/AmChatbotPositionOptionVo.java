package com.open.hr.ai.bean.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.open.ai.eros.db.mysql.hr.entity.AmChatbotOptions;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 打招呼条件
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AmChatbotPositionOptionVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 账号id
     */
    private String accountId;

    /**
     * 职位id
     */
    private Integer positionId;

    /**
     * 广场AI角色
     */
    private Long amMaskId;

    /**
     * 复聊方案id
     */
    private Integer rechatOptionId;

    /**
     * 创建时间
     */
    private Integer createTime;


    private AmMaskVo amMaskVo;

    private AmChatbotOptions amChatbotOptions;

    private AmGreetConditionVo amGreetConditionVo;


}
