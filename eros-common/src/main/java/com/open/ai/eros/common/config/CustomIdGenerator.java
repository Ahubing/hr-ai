package com.open.ai.eros.common.config;

import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import org.springframework.stereotype.Component;

/**
 * @类名：CustomIdGenerator
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/4 2:23
 */

@Component
public class CustomIdGenerator{

    DefaultIdentifierGenerator defaultIdentifierGenerator = new DefaultIdentifierGenerator();

    public long nextId(){
        return defaultIdentifierGenerator.nextId(CustomIdGenerator.class);
    }

    public long nextId(Object entity){
        return defaultIdentifierGenerator.nextId(entity);
    }

}

