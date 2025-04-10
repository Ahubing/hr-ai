package com.open.ai.eros.creator.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.open.ai.eros.common.util.DateUtils;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-13
 */

@ApiModel("面具信息类")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BMaskVo implements Serializable {

    private static final long serialVersionUID=1L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 语言
     */
    private String language;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 面具名称
     */
    private String name;

    /**
     * 面具类别
     */
    private String type;

    /**
     * 模型来源  aws  az gpt  claude
     */
    private List<String> templateModel;

    /**
     * 面具的详情说明
     */
    private String introDesc;

    /**
     * 面具的简单说明
     */
    private String intro;

    /**
     * 权限 1 公开 2私有
     */
    private Integer permission;

    /**
     * 面具的标签   逗号分开  游戏,性感
     */
    private List<String> tags;

    /**
     * 用户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    private LocalDateTime updateTime;

    /**
     * 记忆上下文条数
     */
    private Integer contentsNumber;

    /**
     * 是否群聊
     */
    private Boolean isGroupChat;

    /**
     * 群聊的面具id 逗号分开
     */
    private List<String> maskIds;

    /**
     * 热度
     */
    private Long heat;


    /**
     * 收藏数
     */
    private Long collectNum;


    /**
     * 打招呼 逗号分开
     */
    private List<String> greeting;

    /**
     * 所属大类  游戏  陪聊
     */
    private String maskType;

    /**
     * 问题列表
     */
    private List<String> questionList;

    /**
     * 提示列表
     */
    private List<String> tips;


    /**
     * 用户问题前缀
     */
    private String userPrefix;

    /**
     * 用户问题的后缀
     */
    private String userSuffix;

    /**
     * 禁用词 用户问题出现 将会被拦截掉 以逗号分开
     */
    private List<String> bannedWords;

    /**
     * 彩蛋
     */
    private List<loraVo> lora;

    /**
     * 预设词
     */
    private String presetWords;

    /**
     * 背景图
     */
    private String bgCover;

    /**
     * ai参数 json
     */
    private MaskAIParamVo aiParam;

    /**
     * 声音
     */
    private String sound;

    /**
     * 状态  1 发布 2 待发布 3：删除
     */
    private Integer status;


    /**
     * 知识库id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long knowledgeId;


    /**
     * 知识库面具回答等级 1: 严格, 2宽松
     */
    private Integer knowledgeStrict;


    /**
     * 工具
     */
    private List<String> tool;

    /**
     * 文本匹配的通道id
     */
    private Long textMatchChannel;

}
