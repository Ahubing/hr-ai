package com.open.ai.eros.knowledge.bean.req;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author Eros-AI
 * @since 2024-09-12
 */
@ApiModel("新增知识库文档实体类")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class KnowledgeDocsAddReq implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 名称
     */
    @ApiModelProperty("名称")
    @NotEmpty(message = "名称不能为空")
    private String name;

    /**
     * 知识库id
     */
    @ApiModelProperty("知识库id")
    @NotNull(message = "知识库id不能为空")
    private Long knowledgeId;

    /**
     * 类型
     */
    @ApiModelProperty("类型")
    @NotEmpty(message = "类型不能为空")
    private String type;


    @ApiModelProperty("切割规则")
    private String sliceRule;


    /**
     * url
     */
    @ApiModelProperty("url")
    private String url;

    /**
     * 文档内容
     */
    @ApiModelProperty("文档内容- 如果是手动输入 该值必填")
    private String content;

    /**
     * 切割方式
     */
    @ApiModelProperty("切割方式")
    private String splitterType;


    public static void main(String[] args) {
        String t = "00丨开篇词丨为什么要学习Kafka\n" +
                "http://43.153.41.128/2024-09-17/459cf893870d4b42a7fac331828dc097.html    \n" +
                "01丨消息引擎系统ABC\n" +
                "http://43.153.41.128/2024-09-17/0529af02cbf248d09275e4172bc21b77.html    \n" +
                " 02丨一篇文章带你快速搞定Kafka术语.html\n" +
                "http://43.153.41.128/2024-09-17/54acca0f8daf4ff1a2318f7a9d3084f3.html      \n" +
                "03丨Kafka只是消息引擎系统吗？.html   \n" +
                "http://43.153.41.128/2024-09-17/ede84f0d9c054ff4b63fb5f5cd4be7ca.html\n" +
                "04 | 聊一聊kafka的版号   \n" +
                "http://43.153.41.128/2024-09-17/967e09fdec5a452481e7cde543ddc84e.html\n" +
                "kafka集群部署方案    \n" +
                "http://43.153.41.128/2024-09-17/02498a04af874510a317eb587d567248.html\n" +
                "07丨最最最重要的集群参数配置（上）\n" +
                "http://43.153.41.128/2024-09-17/bdafc3fef1aa4955b1cd93b62d813d17.html\n" +
                "08丨最最最重要的集群参数配置（下） \n" +
                "http://43.153.41.128/2024-09-17/81bdeb79b3c04df7a176f6ad786a9653.html\n" +
                "09丨生产者消息分区机制原理剖析 \n" +
                "http://43.153.41.128/2024-09-17/271e8ee7464744c7b43044e6f501614b.html\n" +
                "10丨生产者压缩算法面面观  \n" +
                "http://43.153.41.128/2024-09-17/0af7232f3d8949159923dd4e63f80edd.html\n" +
                "11丨无消息丢失配置怎么实现？ \n" +
                "http://43.153.41.128/2024-09-17/de27ee92a97d47d8bcba53b3ce71e815.html\n" +
                "12丨客户端都有哪些不常见但是很高级的功能？ \n" +
                "http://43.153.41.128/2024-09-17/db45f6226d9b47fe9783e7e6682d2441.html\n" +
                "13丨Java生产者是如何管理TCP连接的？ \n" +
                "http://43.153.41.128/2024-09-17/394a02f56d2944f895715cd1645beb93.html\n" +
                "14丨幂等生产者和事务生产者是一回事吗？ \n" +
                "http://43.153.41.128/2024-09-17/4788305a0522487cb3add1186a14a95e.html\n" +
                "15丨消费者组到底是什么？ \n" +
                "http://43.153.41.128/2024-09-17/e463ff155df94671a8215eadfe7be031.html\n" +
                "16丨揭开神秘的“位移主题”面纱  \n" +
                "http://43.153.41.128/2024-09-17/498d429c82e34525a4cbdd09bdfa1d8c.html\n" +
                "17丨消费者组重平衡能避免吗？\n" +
                "http://43.153.41.128/2024-09-17/bec10e040a4f403fab7e4ff7e90adac6.html\n" +
                "18丨Kafka中位移提交那些事儿.html\n" +
                "http://43.153.41.128/2024-09-17/508305e62bbf4e95a7f02f259df5e033.html\n" +
                "19丨CommitFailedException异常怎么处理？.html\n" +
                "http://43.153.41.128/2024-09-17/42613b070a434d63b1a964d74069c44a.html\n" +
                "20丨多线程开发消费者实例.html\n" +
                "http://43.153.41.128/2024-09-17/6841b9f5bcb04cd8b600428c4dec3c8b.html\n" +
                "21丨Java消费者是如何管理TCP连接的.html\n" +
                "http://43.153.41.128/2024-09-17/95c8d041b6b347ce945377b9fe2ef588.html\n" +
                "22丨消费者组消费进度监控都怎么实现？.html\n" +
                "http://43.153.41.128/2024-09-17/8320c7c211674215a12db5985c9416bd.html\n" +
                "23丨Kafka副本机制详解.html\n" +
                "http://43.153.41.128/2024-09-17/7120a471901b45cab579ce73d8c5f621.html\n" +
                "24丨请求是怎么被处理的？.html\n" +
                "http://43.153.41.128/2024-09-17/09b112de0bac45ecbeb5993c522dcc5b.html\n" +
                "25 丨 消费者组重平衡全流程解析.html\n" +
                "http://43.153.41.128/2024-09-17/e142338a34e24bb48a65fbffd19d3a73.html\n" +
                "26 丨 你一定不能错过的Kafka控制器.html\n" +
                "http://43.153.41.128/2024-09-17/ca7cf01bfea84fa797e857efc5dd4e48.html\n" +
                "27 丨 关于高水位和Leader Epoch的讨论.html\n" +
                "2024-09-17/74779a5925b04bdfada464cad847dd56.html\n" +
                "28丨主题管理知多少.html\n" +
                "http://43.153.41.128/2024-09-17/c33f252ad5d74847847dd7b07006603b.html\n" +
                "29丨Kafka动态配置了解下？.html\n" +
                "http://43.153.41.128/2024-09-17/24ac4a73c70943b0acf63cea70e916f6.html\n" +
                "30丨怎么重设消费者组位移？.html\n" +
                "http://43.153.41.128/2024-09-17/2b668e3759c543d2b96a5ec918db97c9.html\n" +
                "31丨常见工具脚本大汇总.html\n" +
                "http://43.153.41.128/2024-09-17/9f6461509f624c2d96867856833d70e9.html\n" +
                "32丨KafkaAdminClient：Kafka的运维利器.html\n" +
                "http://43.153.41.128/2024-09-17/fa587cf443a645cb9df7fa801cab9fe1.html\n" +
                "33丨Kafka认证机制用哪家？.html\n" +
                "http://43.153.41.128/2024-09-17/823dbe3a091742d1bd158e1874c11071.html\n" +
                "34丨云环境下的授权该怎么做？.html\n" +
                "http://43.153.41.128/2024-09-17/438a40db88604c9194b99c30c68cf706.html\n" +
                "35丨跨集群备份解决方案MirrorMaker.html\n" +
                "http://43.153.41.128/2024-09-17/db3174715e7c418ea4e4d4899e7a920b.html\n" +
                "36丨你应该怎么监控Kafka？.html\n" +
                "http://43.153.41.128/2024-09-17/a0a73605b3954ff88284ae4eb83207db.html\n" +
                "37丨主流的Kafka监控框架.html\n" +
                "http://43.153.41.128/2024-09-17/0507568e0f7a4640aad347431c47a763.html\n" +
                "38丨调优Kafka，你做到了吗？.html\n" +
                "http://43.153.41.128/2024-09-17/b89700b3418f4f18a96a5f2560289927.html\n" +
                "39丨从0搭建基于Kafka的企业级实时日志流处理平台.html\n" +
                "http://43.153.41.128/2024-09-17/67151b24e112400980d83fd28a6a0c60.html\n" +
                "40丨KafkaStreams与其他流处理平台的差异在哪里？.html\n" +
                "http://43.153.41.128/2024-09-17/0008782cbf4b4858809c1b878de52f02.html\n" +
                "41丨KafkaStreamsDSL开发实例.html\n" +
                "http://43.153.41.128/2024-09-17/4fea8268bcc145308a9bff1c86025dd4.html\n" +
                "42丨KafkaStreams在金融领域的应用.html\n" +
                "http://43.153.41.128/2024-09-17/873dee7da61744c5a85771d4eba8d599.html\n" +
                "加餐丨搭建开发环境、阅读源码方法、经典学习资料大揭秘.html\n" +
                "http://43.153.41.128/2024-09-17/1ad35cee1bab45dda8c4a39376e46f1f.html";
        String[] split = t.split("\n");

        List<KnowledgeDocsAddReq> reqs = new ArrayList<>();
        for (int i = 0; i < split.length; i++) {
            KnowledgeDocsAddReq req = new KnowledgeDocsAddReq();
            req.setUrl(split[i+1]);
            req.setName(split[i]);
            i++;
            reqs.add(req);
        }
        System.out.println(JSONObject.toJSONString(reqs));
    }

}
