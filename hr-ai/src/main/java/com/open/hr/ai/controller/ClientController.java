package com.open.hr.ai.controller;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.hr.ai.bean.req.ClientBossNewMessageReq;
import com.open.hr.ai.bean.req.ClientFinishTaskReq;
import com.open.hr.ai.bean.req.ClientQrCodeReq;
import com.open.hr.ai.bean.req.ClientQueryReq;
import com.open.hr.ai.config.HrAIBaseController;
import com.open.hr.ai.manager.ClientManager;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Objects;

/**
 * @Date 2025/1/6 23:33
 */
@RestController
@Slf4j
public class ClientController extends HrAIBaseController {

    @Resource
    private ClientManager clientManager;

    @ApiOperation("客户端请求连接")
    @PostMapping("/connect/{platform}/{boss_id}/{connect_id}")
    public ResultVO connectClient(@PathVariable("platform") String platform,
                                  @PathVariable("boss_id") String bossId,
                                  @PathVariable("connect_id") String connectId) {
        log.info("connectClient platform={},bossId={},connectId={}", platform, bossId, connectId);
        if (StringUtils.isBlank(bossId) || StringUtils.isBlank(connectId)) {
            return ResultVO.fail("boss_id或connect_id不能为空");
        }
        return clientManager.connectClient(platform,bossId, connectId);
    }

    @ApiOperation("客户端请求更新二维码")
    @PostMapping("/login_qrcode/{platform}/{boss_id}/{connect_id}")
    public ResultVO updateQrCode(@PathVariable("platform") String platform,@PathVariable("boss_id") String bossId, @PathVariable("connect_id") String connectId, @RequestBody @Valid ClientQrCodeReq qrCodeRequest) {
        log.info("updateQrCode bossId={},connectId={},qrCodeRequest={}", bossId, connectId, qrCodeRequest);
        if (StringUtils.isBlank(bossId) || StringUtils.isBlank(connectId)) {
            return ResultVO.fail("boss_id或connect_id不能为空");
        }
        if (Objects.isNull(qrCodeRequest)) {
            return ResultVO.fail("qrCodeRequest不能为空");
        }
        return clientManager.loginQrCodeSave(platform,bossId, connectId, qrCodeRequest);
    }


    @ApiOperation("客户端请求更新状态")
    @PostMapping("/status/{platform}/{boss_id}/{connect_id}/{status}")
    public ResultVO updateClientStatus(@PathVariable("platform") String platform,@PathVariable("boss_id") String bossId, @PathVariable("connect_id") String connectId, @PathVariable("status") String status) {
        log.info("updateClientStatus bossId={},connectId={},status={}", bossId, connectId, status);
        if (StringUtils.isBlank(bossId) || StringUtils.isBlank(connectId) || StringUtils.isBlank(status)) {
            return ResultVO.fail("boss_id,status或connect_id不能为空");
        }
        return clientManager.updateClientStatus(platform,bossId, connectId, status);
    }

    @ApiOperation("客户端请求登录")
    @PostMapping("/login/{platform}/{boss_id}/{connect_id}/{ext_boss_id}")
    public ResultVO loginClient(@PathVariable("platform") String platform,@PathVariable("boss_id") String bossId, @PathVariable("connect_id") String connectId, @PathVariable("ext_boss_id") String extBossId) {
        log.info("loginClient bossId={},connectId={},extBossId={}", bossId, connectId, extBossId);
        if (StringUtils.isBlank(bossId) || StringUtils.isBlank(connectId) || StringUtils.isBlank(extBossId)) {
            return ResultVO.fail("boss_id,extBossId或connect_id不能为空");
        }
        return clientManager.loginClient(platform,bossId, connectId, extBossId);
    }
    @ApiOperation("客户端查询附件简历情况")
    @PostMapping("/query/attachment_resume/{platform}/{boss_id}/{connect_id}")
    public ResultVO queryAttachmentResume(@PathVariable("platform") String platform,@PathVariable("boss_id") String bossId, @PathVariable("connect_id") String connectId, @RequestBody @Valid ClientQueryReq clientQueryReq) {
        log.info("queryAttachmentResume bossId={},connectId={},userId={}", bossId, connectId, clientQueryReq.getUser_id());
        if (StringUtils.isBlank(bossId) || StringUtils.isBlank(connectId)) {
            return ResultVO.fail("boss_id,extBossId或connect_id不能为空");
        }

        return clientManager.queryAttachmentResume(platform,bossId, connectId, clientQueryReq.getUser_id());
    }

    @ApiOperation("客户端获取任务")
    @PostMapping("/get_task/{platform}/{boss_id}/{connect_id}")
    public ResultVO getClientTask(@PathVariable("platform") String platform,@PathVariable("boss_id") String bossId, @PathVariable("connect_id") String connectId) {
        log.info("getClientTask bossId={},connectId={}", bossId, connectId);
        if (StringUtils.isBlank(bossId) || StringUtils.isBlank(connectId)) {
            return ResultVO.fail("boss_id,extBossId或connect_id不能为空");
        }
        return clientManager.getClientTask(platform,bossId, connectId);
    }

    @ApiOperation("客户端结束任务")
    @PostMapping("/finish_task/{platform}/{boss_id}/{connect_id}")
    public ResultVO finishClientTask(@PathVariable("platform") String platform,@PathVariable("boss_id") String bossId, @PathVariable("connect_id") String connectId, @RequestBody @Valid ClientFinishTaskReq clientFinishTaskReq) {
        log.info("finishClientTask bossId={},connectId={},clientFinishTaskReq={}", bossId, connectId, clientFinishTaskReq);
        if (StringUtils.isBlank(bossId) || StringUtils.isBlank(connectId)) {
            return ResultVO.fail("boss_id,extBossId或connect_id不能为空");
        }
        return clientManager.finishClientTask(platform,bossId, connectId, clientFinishTaskReq);
    }


    @ApiOperation("客户端发送消息")
    @PostMapping("/listen/boss_new_message/{platform}/{boss_id}/{connect_id}")
    public ResultVO bossNewMessage(@PathVariable("platform") String platform,@PathVariable("boss_id") String bossId, @PathVariable("connect_id") String connectId, @RequestBody @Valid ClientBossNewMessageReq clientBossNewMessageReq) {
        log.info("bossNewMessage bossId={},connectId={},clientBossNewMessageReq={}", bossId, connectId, clientBossNewMessageReq);
        if (StringUtils.isBlank(bossId) || StringUtils.isBlank(connectId)) {
            return ResultVO.fail("boss_id,extBossId或connect_id不能为空");
        }
        return clientManager.bossNewMessage(platform,bossId, connectId, clientBossNewMessageReq);
    }



    @ApiOperation("客户端请求过滤简历")
    @PostMapping("/filter/resume/{platform}/{boss_id}/{connect_id}/{encrypt_id}/{is_greet}")
    public ResultVO filterResume(@PathVariable("platform") String platform,@PathVariable("boss_id") String bossId, @PathVariable("connect_id") String connectId,@PathVariable("encrypt_id") String encryptId,@PathVariable("is_greet") Boolean isGreet , @RequestBody @Valid JSONObject resume) {
        log.info("filterResume bossId={},connectId={},encryptId={},isGreet={} ,clientBossNewMessageReq={}", bossId, connectId, encryptId,isGreet,resume);
        if (StringUtils.isBlank(bossId) || StringUtils.isBlank(connectId)) {
            return ResultVO.fail("boss_id,extBossId或connect_id不能为空");
        }
        return clientManager.filterAmResume(platform,bossId, connectId,encryptId, resume,isGreet);
    }




    @ApiOperation("客户端打招呼请求过滤简历")
    @PostMapping("/greet/resume/{platform}/{boss_id}/{connect_id}/{encrypt_id}/{greet_task_id}")
    public ResultVO greetAndSaveResume(@PathVariable("platform") String platform,@PathVariable("boss_id") String bossId, @PathVariable("connect_id") String connectId,@PathVariable("encrypt_id") String encryptId,@PathVariable("greet_task_id") String greet_task_id,@RequestBody @Valid JSONObject resume) {
        log.info("greetAndSaveResume bossId={},connectId={},encryptId={} ,greet_task_id={} ,clientBossNewMessageReq={}", bossId, connectId, encryptId,greet_task_id,resume);
        if (StringUtils.isBlank(bossId) || StringUtils.isBlank(connectId)) {
            return ResultVO.fail("boss_id,extBossId或connect_id不能为空");
        }
        return clientManager.filterAndSaveAmResume(platform,bossId, connectId,encryptId, resume,greet_task_id);
    }


}
