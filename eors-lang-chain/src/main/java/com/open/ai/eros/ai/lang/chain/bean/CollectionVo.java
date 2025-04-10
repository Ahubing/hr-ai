package com.open.ai.eros.ai.lang.chain.bean;

import com.open.ai.eros.ai.lang.chain.provider.build.ModelVectorEnum;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @类名：CollectionVo
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/14 14:21
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CollectionVo {


    private String collectionName;

    private Integer dimension;


    public static void main(String[] args) {

        String t = "在Java 8中，将会提供Lambda支持，将会极大改善目前Java语言不适合函数式编程的现状（目前\n" +
                "Java语言使用函数式编程并不是不可以，只是会显得很臃肿），函数式编程的一个重要优点就是这样\n" +
                "的程序天然地适合并行运行，这样对Java语言在多核时代继续保持主流语言的地位有很大帮助。\n" +
                "另外并行计算中必须提及的还有OpenJDK的子项目Sumatra[5]，目前显卡的算术运算能力、并行\n" +
                "能力已经远远超过了CPU，在图形领域以外发掘显卡的潜力是近几年计算机发展的方向之一，例如C\n" +
                "语言的CUDA。Sumatra项目就是为Java提供使用GPU（Graphics Processing Unit）和APU（Accelerated\n" +
                "Processing Unit）运算能力的工具，以后它将会直接提供Java语言层面的API，或者为Lambda和其他\n" +
                "JVM语言提供底层的并行运算支持。\n" +
                "在JDK外围，也出现了专为实现并行计算需求的计算框架，如Apache的Hadoop Map/Reduce，这\n" +
                "是一个简单易懂的并行框架，能够运行在由上千个商用机器组成的大型集群上，并能以一种可靠的容\n" +
                "错方式并行处理TB级别的数据集。另外，还出现了诸如Scala、Clojure及Erlang等天生就具备并行计算\n" +
                "能力的语言。\n" +
                "B.4　进一步丰富语法\n" +
                "Java 5曾经对Java语法进行了一次扩充，这次扩充加入了自动装箱、泛型、动态注解、枚举、可变\n" +
                "长参数、遍历循环等语法，使得Java语言的精确性和易用性有了很大的进步。在Java 7（由于进度压\n" +
                "力，许多改进已被推迟至Java 8）中，对Java语法进行了另一次大规模的扩充。Sun（Oracle）专门为改\n" +
                "进Java语法在OpenJDK中建立了Coin子项目[6]来统一处理Java语法的细节修改，如对二进制数的原生\n" +
                "支持、在switch语句中支持字符串、“<>”操作符、异常处理的改进、简化变长参数方法调用、面向资\n" +
                "源的try-catch-finally语句等都是在Coin项目之中提交的内容。\n" +
                "除了Coin项目之外，JSR-335（Lambda Expressions for the JavaTM Programming Language）中定义\n" +
                "的Lambda表达式[7]，也将对Java的语法和语言习惯产生很大的影响，面向函数方式的编程可能会成为\n" +
                "主流。\n" +
                "B.5　64位虚拟机\n" +
                "几年之前，主流的CPU就开始支持64位架构。Java虚拟机也在很早之前就推出了支持64位系统的\n" +
                "版本。但Java程序运行在64位虚拟机上需要付出比较大的额外代价：首先是内存问题，由于指针膨胀\n" +
                "和各种数据类型对齐补白的原因，运行于64位系统上的Java应用需要消耗更多的内存，通常要比32位\n" +
                "系统额外增加10%～30%的内存消耗；其次是多个机构的测试结果显示，64位虚拟机的运行速度在各\n" +
                "个测试项上几乎都全面落后于32位虚拟机，两者大约有15%的性能差距。\n" +
                "但是在Java EE方面，企业级应用经常需要使用超过4GB的内存，对于64位虚拟机的需求是非常迫\n" +
                "切的，但由于上述的原因，许多企业应用都仍然选择使用虚拟集群等方式继续在32位虚拟机中进行部\n" +
                "署。Sun也注意到了这些问题，并做出了一些改善，在JDK 1.6 Update 14之后，提供了普通对象指针压\n" +
                "缩功能（-XX：+UseCompressedOops，这个参数不建议显式设置，建议维持默认由虚拟机的\n" +
                "Ergonomics机制自动开启），在执行代码时，动态植入压缩指令以节省内存消耗。但是开启压缩指针\n" +
                "会增加执行代码数量，因为所有在Java堆里的、指向Java堆内对象的指针都会被压缩，这些指针的访问\n" +
                "就需要更多的代码才可以实现，而且并不仅只是读写字段才受影响，在实例方法调用、子类型检查等\n" +
                "操作中也受影响，因为对象实例指向对象类型的引用也被压缩了。随着硬件的进一步发展，计算机终\n" +
                "究会完全过渡到64位的时代，这是一件毫无疑问的事情，主流的虚拟机应用也终究会从32位发展至64\n" +
                "位，而虚拟机对64位的支持也将会进一步完善。\n" +
                "[1] 如果读者对Java模块化之争感兴趣，可以阅读作者的另外一本书《深入理解OSGi》的第1章。\n" +
                "[2] 在同一个虚拟机上跑的其他语言与Java之间的交互一般都比较容易，但非Java语言之间的交互一般\n" +
                "都比较烦琐。dynalang项目（http://dynalang.sourceforge.net/）就是为了解决这个问题而出现的。";
        DocumentSplitter recursive = DocumentSplitters.recursive(400, 100, new OpenAiTokenizer(ModelVectorEnum.text_embedding_small_3.getEncodingForModel()));
        Document document = new Document(t);
        List<TextSegment> textSegments = recursive.split(document);
        for (TextSegment textSegment : textSegments) {
            System.out.println(textSegment.text());
            System.out.println();
        }

    }

}
