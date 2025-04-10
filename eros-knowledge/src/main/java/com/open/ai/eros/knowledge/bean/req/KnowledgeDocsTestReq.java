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
@ApiModel("测试知识库文档实体类")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class KnowledgeDocsTestReq implements Serializable {

    private static final long serialVersionUID=1L;

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
                "http://43.153.41.128/2024-09-17/bec10e040a4f403fab7e4ff7e90adac6.html";
        String[] split = t.split("\n");

        List<KnowledgeDocsTestReq> reqs = new ArrayList<>();
        for (int i = 0; i < split.length; i++) {
            KnowledgeDocsTestReq req = new KnowledgeDocsTestReq();
            i++;
            reqs.add(req);
        }
        System.out.println(JSONObject.toJSONString(reqs));
    }

}
