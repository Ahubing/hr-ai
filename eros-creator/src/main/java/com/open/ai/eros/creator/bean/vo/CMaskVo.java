package com.open.ai.eros.creator.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
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
public class CMaskVo implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty("id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 语言
     */
    @ApiModelProperty("语言")
    private String language;

    /**
     * 头像
     */
    @ApiModelProperty("头像")
    private String avatar;

    /**
     * 面具名称
     */
    @ApiModelProperty("面具名称")
    private String name;

    /**
     * 面具类别
     */
    @ApiModelProperty("面具类别")
    private String type;

    /**
     * 模型来源  aws  az gpt  claude
     */
    @ApiModelProperty("模型来源")
    private List<String> templateModel;

    /**
     * 面具的详情说明
     */
    @ApiModelProperty("面具的详情说明")
    private String introDesc;

    /**
     * 面具的简单说明
     */
    @ApiModelProperty("面具的简单说明")
    private String intro;


    /**
     * 面具的标签   逗号分开  游戏,性感
     */
    @ApiModelProperty("面具的标签")
    private List<String> tags;


    /**
     * 是否群聊
     */
    private String isGroupChat;

    /**
     * 群聊的面具id 逗号分开
     */
    @ApiModelProperty("群聊的面具id")
    private List<String> maskIds;

    /**
     * 热度
     */
    @ApiModelProperty("热度")
    private Long heat;


    /**
     * 收藏数
     */
    @ApiModelProperty("收藏数")
    private Long collectNum;

    /**
     * 打招呼 逗号分开
     */
    @ApiModelProperty("打招呼")
    private List<String> greeting;

    /**
     * 所属大类  游戏  陪聊
     */
    @ApiModelProperty("所属大类")
    private String maskType;

    /**
     * 问题列表
     */
    @ApiModelProperty("问题列表")
    private List<String> questionList;

    /**
     * 提示列表
     */
    @ApiModelProperty("提示列表")
    private List<String> tips;


    /**
     * 背景图
     */
    @ApiModelProperty("背景图")
    private String bgCover;

    /**
     * 声音
     */
    @ApiModelProperty("声音")
    private String sound;


    @ApiModelProperty("关注")
    private boolean follow;


    /**
     * 知识库id
     */
    @ApiModelProperty("知识库id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long knowledgeId;

    /**
     * 知识库回答等级 1 严格, 2 宽松
     */
    @ApiModelProperty("知识库回答等级")
    private Integer knowledgeStrict;


    @ApiModelProperty("用户id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;


    @ApiModelProperty("用户头像")
    private String userAvatar;

    @ApiModelProperty("用户昵称")
    private String username;

    /**
     * 工具
     */
    private List<String> tool;

}
