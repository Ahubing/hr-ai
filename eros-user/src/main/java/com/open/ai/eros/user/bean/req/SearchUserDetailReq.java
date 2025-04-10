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
public class SearchUserDetailReq {

    /**
     * id
     */
    private Long id;

    /**
     * 邮箱
     */
    private String email;

}
