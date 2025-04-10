package com.open.ai.eros.creator.bean.req;

import com.open.ai.eros.creator.bean.vo.MaskAIParamVo;
import com.open.ai.eros.creator.bean.vo.loraVo;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
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

@ApiModel("新增面具信息类")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MaskAddReq implements Serializable {

    private static final long serialVersionUID=1L;


    /**
     * 语言
     */
    @NotEmpty(message = "语言不能为空")
    private String language;

    /**
     * 头像
     */
    @NotEmpty(message = "头像不能为空")
    private String avatar;

    /**
     * 面具名称
     */
    @NotEmpty(message = "面具名称")
    private String name;

    /**
     * 面具类别
     */
    private String type;

    /**
     * 模型来源  aws  az gpt  claude
     */
    @NotEmpty(message = "渠道模版不能为空")
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
    private List<String> maskIds;

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
