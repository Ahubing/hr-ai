package com.open.ai.eros.user.bean.req;

import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Data
public class FeedbackSubmitReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 反馈类型ID
     */
    @NotNull(message = "反馈类型不能为空")
    private Integer typeId;

    /**
     * 反馈内容
     */
    @NotBlank(message = "反馈内容不能为空")
    @Size(max = 500, message = "反馈内容长度不能超过500个字符")
    private String content;

    /**
     * 联系方式（可选）
     */
    @Size(max = 50, message = "联系方式长度不能超过50个字符")
    private String contact;

    /**
     * 附件（可选）
     */
    private List<String> fileList;

    /**
     * 设备信息（可选）
     */
    @Size(max = 100, message = "设备信息长度不能超过100个字符")
    private String deviceInfo;


    /**
     * 星星数
     */
    private Integer star;


}
