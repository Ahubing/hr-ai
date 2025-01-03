package com.open.ai.eros.db.mysql.hr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("mini_uni_user_info")
public class MiniUniUserInfo implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * user表的用户id
     */
    private Integer uid;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 身份证/学生证/通知书/校园卡等
     */
    private String idPic;

    /**
     * 创建时间，以秒为单位的时间戳
     */
    private Integer createTime;

    /**
     * 更新时间
     */
    private Integer updateTime;


}
