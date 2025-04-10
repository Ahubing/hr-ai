package com.open.ai.eros.knowledge.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.open.ai.eros.ai.lang.chain.provider.build.ModelVectorEnum;
import com.open.ai.eros.common.util.DateUtils;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentByRegexSplitter;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 文档切片表
 * </p>
 *
 * @author Eros-AI
 * @since 2024-09-12
 */
@ApiModel("文档切片实体类")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DocsSliceVo implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
    @ApiModelProperty("id")
    @JsonSerialize(using = ToStringSerializer.class)
      private Long id;

    /**
     * 向量库的ID
     */
    @ApiModelProperty("向量库的ID")
    private String vectorId;

    /**
     * 文档ID
     */
    @ApiModelProperty("文档ID")
    private Long docsId;

    /**
     * 知识库ID
     */
    @ApiModelProperty("知识库ID")
    private Long knowledgeId;

    /**
     * 文档名称
     */
    @ApiModelProperty("文档名称")
    private String name;


    /**
     * 用户id
     */
    @ApiModelProperty("用户id")
    private Long userId;

    /**
     * 状态 1: 未向量  2：向量化
     */
    @ApiModelProperty("状态")
    private Integer status;

    /**
     * 切片内容
     */
    @ApiModelProperty("切片内容")
    private String content;

    /**
     * 字符数
     */
    @ApiModelProperty("字符数")
    private Integer wordNum;


    /**
     * 跟随文档的type
     */
    @ApiModelProperty("文档的type")
    private String type;



    /**
     * 创建时间
     */
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    public static void main(String[] args) {

        String url = "C:\\Users\\陈臣\\Desktop\\q\\21nj1.DOC";
        Document document = FileSystemDocumentLoader.loadDocument(url, new ApacheTikaDocumentParser());

        DocumentByRegexSplitter documentByParagraphSplitter = new DocumentByRegexSplitter("\\s([一二三四五六七八九十]+、|\\d+\\.)+","",200,0,new OpenAiTokenizer(ModelVectorEnum.text_embedding_small_3.getEncodingForModel()));
        String[] split = documentByParagraphSplitter.split(document.text());
        System.out.println("切割多少分数："+split.length);
        for (String string : split) {
            System.out.println(string);
            System.out.println("------------------------------------------");
        }
    }

}
