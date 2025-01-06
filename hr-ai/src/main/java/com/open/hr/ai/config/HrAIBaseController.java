package com.open.hr.ai.config;

import com.open.ai.eros.common.config.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Description：
 * @项目名：blue-cat-api
 * @创建人：Administrator
 * @创建时间：2023/12/14 14:41
 */
@Slf4j
@RequestMapping("/api/v1/")
public class HrAIBaseController extends BaseController {

    protected boolean isEmpty(String str) {
        return StringUtils.isEmpty(str);
    }

    protected boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
}
