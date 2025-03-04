package com.open.hr.ai.controller;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.privacy.utils.CryptoUtil;
import com.open.hr.ai.bean.req.HrAddUserReq;
import com.open.hr.ai.bean.req.HrLoginReq;
import com.open.hr.ai.config.HrAIBaseController;
import com.open.hr.ai.manager.AmAdminManager;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Base64;

/**
 * @类名：LoginController
 * @项目名：ai-recruitment
 * @description：
 * @创建人：陈臣
 * @创建时间：2025/1/4 11:46
 */
@Slf4j
@RestController
public class LoginController extends HrAIBaseController {


}
