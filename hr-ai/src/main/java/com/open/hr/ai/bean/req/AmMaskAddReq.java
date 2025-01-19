package com.open.hr.ai.bean.req;

import com.open.ai.eros.creator.bean.vo.MaskAIParamVo;
import com.open.hr.ai.bean.vo.AmMaskAIParamVo;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
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
public class AmMaskAddReq {


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
     * 面具的标签   逗号分开  游戏,性感
     */
    private List<String> tags;

    /**
     * 记忆上下文条数
     */
    private Integer contentsNumber;

    /**
     * ai参数 json
     */
    private AmMaskAIParamVo aiParam;


}
