package com.open.hr.ai.manager;

import com.open.ai.eros.db.mysql.hr.entity.AmZpLocalAccouts;
import com.open.ai.eros.db.mysql.hr.service.impl.AmZpLocalAccoutsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @类名：AmZpLocalAccoutsManager
 * @项目名：ai-recruitment
 * @description：
 * @创建人：陈臣
 * @创建时间：2025/1/4 0:52
 */

@Component
public class AmZpLocalAccoutsManager {


    @Autowired
    private AmZpLocalAccoutsServiceImpl amZpLocalAccoutsService;


}
