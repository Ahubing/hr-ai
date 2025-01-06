package com.open.hr.ai.controller;

import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.hr.ai.bean.req.*;
import com.open.hr.ai.config.HrAIBaseController;
import com.open.hr.ai.manager.ChatBotPositionManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * @Date 2025/1/4 23:19
 */

@Api(tags = "Message管理类")
@Slf4j
@RestController
public class MessageController extends HrAIBaseController {

}
