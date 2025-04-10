package com.open.ai.eros.db.mysql.creator.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("mask")
public class Mask implements Serializable {

    private static final long serialVersionUID=1L;

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
    private String templateModel;

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
    private String tags;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 记忆上下文条数
     */
    private Integer contentsNumber;

    /**
     * 是否群聊
     */
    private String isGroupChat;

    /**
     * 群聊的面具id 逗号分开
     */
    private String maskIds;

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
    private String greeting;

    /**
     * 所属大类  游戏  陪聊
     */
    private String maskType;

    /**
     * 问题列表
     */
    private String questionList;

    /**
     * 提示列表
     */
    private String tips;

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
    private String bannedWords;

    /**
     * 彩蛋
     */
    private String lora;

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
    private String aiParam;

    /**
     * 声音
     */
    private String sound;

    /**
     * 状态  1 发布 2 待发布 3：删除
     */
    private Integer status;

    /**
     * 知识库id 以逗号分割
     */
    private String knowledgeId;


    /**
     * 知识库面具回答等级 1: 严格, 2宽松
     */
    private Integer knowledgeStrict;


    /**
     * 工具的链表 以逗号分开
     */
    private String tool;



    /**
     * 文本匹配的通道id
     */
    private Long textMatchChannel;

}
