package com.open.ai.eros.ai.tool.function;

import com.open.ai.eros.common.util.DateUtils;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @类名：TimeTool
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/15 0:28
 */
@Component
public class TimeFunction {

    @Tool(name = "add")
    public double add(int a, int b) {
        return a + b;
    }


    @Tool(name = "nowTime",value = {"当前时间"})
    public String nowTime(){
        return DateUtils.formatDate(new Date(),DateUtils.FORMAT_YYYY_MM_DD_HHMMSS);
    }


}
