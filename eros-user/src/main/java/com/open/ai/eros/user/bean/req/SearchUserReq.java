package com.open.ai.eros.user.bean.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @since 2023-09-07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchUserReq {

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户角色
     */
    private String role;

    /**
     * 用户状态
     */
    private String status;

    /**
     * 页码
     */
    private Integer page = 1;

    /**
     * 每页页数
     */
    private Integer pageSize = 20;

}
