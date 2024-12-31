package com.open.ai.eros.knowledge.controller;


import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.knowledge.bean.req.KnowledgeDocsAddReq;
import com.open.ai.eros.knowledge.bean.req.KnowledgeDocsSearchReq;
import com.open.ai.eros.knowledge.bean.req.KnowledgeDocsTestReq;
import com.open.ai.eros.knowledge.bean.req.KnowledgeDocsUpdateReq;
import com.open.ai.eros.knowledge.bean.vo.KnowledgeDocsVo;
import com.open.ai.eros.knowledge.config.KnowledgeBaseController;
import com.open.ai.eros.knowledge.manager.KnowledgeDocsManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Eros-AI
 * @since 2024-09-12
 */

@Api(tags = "知识库文档控制类")
@RestController
public class KnowledgeDocsController extends KnowledgeBaseController {


    @Autowired
    private KnowledgeDocsManager knowledgeDocsManager;


    private String docs = "[{\"name\":\"00丨开篇词丨为什么要学习Kafka\",\"url\":\"http://43.153.41.128/2024-09-17/459cf893870d4b42a7fac331828dc097.html    \"},{\"name\":\"01丨消息引擎系统ABC\",\"url\":\"http://43.153.41.128/2024-09-17/0529af02cbf248d09275e4172bc21b77.html    \"},{\"name\":\" 02丨一篇文章带你快速搞定Kafka术语.html\",\"url\":\"http://43.153.41.128/2024-09-17/54acca0f8daf4ff1a2318f7a9d3084f3.html      \"},{\"name\":\"03丨Kafka只是消息引擎系统吗？.html   \",\"url\":\"http://43.153.41.128/2024-09-17/ede84f0d9c054ff4b63fb5f5cd4be7ca.html\"},{\"name\":\"04 | 聊一聊kafka的版号   \",\"url\":\"http://43.153.41.128/2024-09-17/967e09fdec5a452481e7cde543ddc84e.html\"},{\"name\":\"kafka集群部署方案    \",\"url\":\"http://43.153.41.128/2024-09-17/02498a04af874510a317eb587d567248.html\"},{\"name\":\"07丨最最最重要的集群参数配置（上）\",\"url\":\"http://43.153.41.128/2024-09-17/bdafc3fef1aa4955b1cd93b62d813d17.html\"},{\"name\":\"08丨最最最重要的集群参数配置（下） \",\"url\":\"http://43.153.41.128/2024-09-17/81bdeb79b3c04df7a176f6ad786a9653.html\"},{\"name\":\"09丨生产者消息分区机制原理剖析 \",\"url\":\"http://43.153.41.128/2024-09-17/271e8ee7464744c7b43044e6f501614b.html\"},{\"name\":\"10丨生产者压缩算法面面观  \",\"url\":\"http://43.153.41.128/2024-09-17/0af7232f3d8949159923dd4e63f80edd.html\"},{\"name\":\"11丨无消息丢失配置怎么实现？ \",\"url\":\"http://43.153.41.128/2024-09-17/de27ee92a97d47d8bcba53b3ce71e815.html\"},{\"name\":\"12丨客户端都有哪些不常见但是很高级的功能？ \",\"url\":\"http://43.153.41.128/2024-09-17/db45f6226d9b47fe9783e7e6682d2441.html\"},{\"name\":\"13丨Java生产者是如何管理TCP连接的？ \",\"url\":\"http://43.153.41.128/2024-09-17/394a02f56d2944f895715cd1645beb93.html\"},{\"name\":\"14丨幂等生产者和事务生产者是一回事吗？ \",\"url\":\"http://43.153.41.128/2024-09-17/4788305a0522487cb3add1186a14a95e.html\"},{\"name\":\"15丨消费者组到底是什么？ \",\"url\":\"http://43.153.41.128/2024-09-17/e463ff155df94671a8215eadfe7be031.html\"},{\"name\":\"16丨揭开神秘的“位移主题”面纱  \",\"url\":\"http://43.153.41.128/2024-09-17/498d429c82e34525a4cbdd09bdfa1d8c.html\"},{\"name\":\"17丨消费者组重平衡能避免吗？\",\"url\":\"http://43.153.41.128/2024-09-17/bec10e040a4f403fab7e4ff7e90adac6.html\"},{\"name\":\"18丨Kafka中位移提交那些事儿.html\",\"url\":\"http://43.153.41.128/2024-09-17/508305e62bbf4e95a7f02f259df5e033.html\"},{\"name\":\"19丨CommitFailedException异常怎么处理？.html\",\"url\":\"http://43.153.41.128/2024-09-17/42613b070a434d63b1a964d74069c44a.html\"},{\"name\":\"20丨多线程开发消费者实例.html\",\"url\":\"http://43.153.41.128/2024-09-17/6841b9f5bcb04cd8b600428c4dec3c8b.html\"},{\"name\":\"21丨Java消费者是如何管理TCP连接的.html\",\"url\":\"http://43.153.41.128/2024-09-17/95c8d041b6b347ce945377b9fe2ef588.html\"},{\"name\":\"22丨消费者组消费进度监控都怎么实现？.html\",\"url\":\"http://43.153.41.128/2024-09-17/8320c7c211674215a12db5985c9416bd.html\"},{\"name\":\"23丨Kafka副本机制详解.html\",\"url\":\"http://43.153.41.128/2024-09-17/7120a471901b45cab579ce73d8c5f621.html\"},{\"name\":\"24丨请求是怎么被处理的？.html\",\"url\":\"http://43.153.41.128/2024-09-17/09b112de0bac45ecbeb5993c522dcc5b.html\"},{\"name\":\"25 丨 消费者组重平衡全流程解析.html\",\"url\":\"http://43.153.41.128/2024-09-17/e142338a34e24bb48a65fbffd19d3a73.html\"},{\"name\":\"26 丨 你一定不能错过的Kafka控制器.html\",\"url\":\"http://43.153.41.128/2024-09-17/ca7cf01bfea84fa797e857efc5dd4e48.html\"},{\"name\":\"27 丨 关于高水位和Leader Epoch的讨论.html\",\"url\":\"http://43.153.41.128/2024-09-17/74779a5925b04bdfada464cad847dd56.html\"},{\"name\":\"28丨主题管理知多少.html\",\"url\":\"http://43.153.41.128/2024-09-17/c33f252ad5d74847847dd7b07006603b.html\"},{\"name\":\"29丨Kafka动态配置了解下？.html\",\"url\":\"http://43.153.41.128/2024-09-17/24ac4a73c70943b0acf63cea70e916f6.html\"},{\"name\":\"30丨怎么重设消费者组位移？.html\",\"url\":\"http://43.153.41.128/2024-09-17/2b668e3759c543d2b96a5ec918db97c9.html\"},{\"name\":\"31丨常见工具脚本大汇总.html\",\"url\":\"http://43.153.41.128/2024-09-17/9f6461509f624c2d96867856833d70e9.html\"},{\"name\":\"32丨KafkaAdminClient：Kafka的运维利器.html\",\"url\":\"http://43.153.41.128/2024-09-17/fa587cf443a645cb9df7fa801cab9fe1.html\"},{\"name\":\"33丨Kafka认证机制用哪家？.html\",\"url\":\"http://43.153.41.128/2024-09-17/823dbe3a091742d1bd158e1874c11071.html\"},{\"name\":\"34丨云环境下的授权该怎么做？.html\",\"url\":\"http://43.153.41.128/2024-09-17/438a40db88604c9194b99c30c68cf706.html\"},{\"name\":\"35丨跨集群备份解决方案MirrorMaker.html\",\"url\":\"http://43.153.41.128/2024-09-17/db3174715e7c418ea4e4d4899e7a920b.html\"},{\"name\":\"36丨你应该怎么监控Kafka？.html\",\"url\":\"http://43.153.41.128/2024-09-17/a0a73605b3954ff88284ae4eb83207db.html\"},{\"name\":\"37丨主流的Kafka监控框架.html\",\"url\":\"http://43.153.41.128/2024-09-17/0507568e0f7a4640aad347431c47a763.html\"},{\"name\":\"38丨调优Kafka，你做到了吗？.html\",\"url\":\"http://43.153.41.128/2024-09-17/b89700b3418f4f18a96a5f2560289927.html\"},{\"name\":\"39丨从0搭建基于Kafka的企业级实时日志流处理平台.html\",\"url\":\"http://43.153.41.128/2024-09-17/67151b24e112400980d83fd28a6a0c60.html\"},{\"name\":\"40丨KafkaStreams与其他流处理平台的差异在哪里？.html\",\"url\":\"http://43.153.41.128/2024-09-17/0008782cbf4b4858809c1b878de52f02.html\"},{\"name\":\"41丨KafkaStreamsDSL开发实例.html\",\"url\":\"http://43.153.41.128/2024-09-17/4fea8268bcc145308a9bff1c86025dd4.html\"},{\"name\":\"42丨KafkaStreams在金融领域的应用.html\",\"url\":\"http://43.153.41.128/2024-09-17/873dee7da61744c5a85771d4eba8d599.html\"},{\"name\":\"加餐丨搭建开发环境、阅读源码方法、经典学习资料大揭秘.html\",\"url\":\"http://43.153.41.128/2024-09-17/1ad35cee1bab45dda8c4a39376e46f1f.html\"}]";


    @VerifyUserToken
    @PostMapping("/docs/test")
    public ResultVO testDoc(@Valid @RequestBody KnowledgeDocsTestReq req){
        Long knowledgeId = req.getKnowledgeId();
        String type = req.getType();
        String sliceRule = req.getSliceRule();

        List<KnowledgeDocsAddReq> reqs = JSONObject.parseArray(docs, KnowledgeDocsAddReq.class);

        int i = 0;
        for (KnowledgeDocsAddReq knowledgeDocsAddReq : reqs) {
            knowledgeDocsAddReq.setKnowledgeId(knowledgeId);
            knowledgeDocsAddReq.setType(type);
            knowledgeDocsAddReq.setSliceRule(sliceRule);
            knowledgeDocsManager.addKnowledgeDocs(getUserId(),knowledgeDocsAddReq);
        }
        return ResultVO.success();
    }





    @VerifyUserToken(role = {RoleEnum.CREATOR,RoleEnum.SYSTEM})
    @ApiOperation("获取文档根据id")
    @GetMapping("/docs/id")
    public ResultVO<KnowledgeDocsVo> findById(@RequestParam(value = "id",required = true) Long id) {
        return knowledgeDocsManager.findById(getUserId(),id);
    }


    @VerifyUserToken(role = {RoleEnum.CREATOR,RoleEnum.SYSTEM})
    @ApiOperation("新增文档")
    @PostMapping("/docs/add")
    public ResultVO addKnowledgeDocs(@RequestBody @Valid KnowledgeDocsAddReq req) {
        return knowledgeDocsManager.addKnowledgeDocs(getUserId(),req);
    }


    @VerifyUserToken(role = {RoleEnum.CREATOR,RoleEnum.SYSTEM})
    @ApiOperation("修改文档")
    @PostMapping("/docs/update")
    public ResultVO updateKnowledgeDocs(@RequestBody @Valid KnowledgeDocsUpdateReq req) {
        return knowledgeDocsManager.updateKnowledgeDocs(getUserId(),req,getRole());
    }


    @VerifyUserToken(role = {RoleEnum.CREATOR,RoleEnum.SYSTEM})
    @ApiOperation("删除文档")
    @PostMapping("/docs/delete")
    public ResultVO deleteKnowledgeDocs(@RequestParam(value = "id",required = true) Long id) {
        return knowledgeDocsManager.deleteKnowledgeDocs(getUserId(),id,getRole());
    }




    @VerifyUserToken(role = {RoleEnum.CREATOR,RoleEnum.SYSTEM})
    @ApiOperation("搜索文档")
    @GetMapping("/docs/search")
    public ResultVO<PageVO<KnowledgeDocsVo>> searchDocs(@Valid KnowledgeDocsSearchReq req){
        if(!RoleEnum.SYSTEM.getRole().equals(getRole())){
            req.setUserId(getUserId());
        }
        return knowledgeDocsManager.searchDocs(req);
    }







}

