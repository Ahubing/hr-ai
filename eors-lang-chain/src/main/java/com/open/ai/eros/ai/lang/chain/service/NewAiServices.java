package com.open.ai.eros.ai.lang.chain.service;


import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.agent.tool.ToolSpecifications;
import dev.langchain4j.service.AiServiceContext;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.DefaultToolExecutor;
import dev.langchain4j.spi.ServiceHelper;

import java.lang.reflect.Method;
import java.util.*;

public abstract class NewAiServices<T> extends AiServices<T> {
    public NewAiServices(AiServiceContext context) {
        super(context);
    }

    public static <T> NewAiServices<T> builder(Class<T> aiService) {
        AiServiceContext context = new AiServiceContext(aiService);
        Iterator var2 = ServiceHelper.loadFactories(NewAiServicesFactory.class).iterator();
        if (var2.hasNext()) {
            NewAiServicesFactory factory = (NewAiServicesFactory)var2.next();
            return factory.create(context);
        } else {
            return new NewDefaultAiServices<>(context);
        }
    }


    public AiServices<T> toolsByMethod(Map<Method,Object> methodMap) {
        if (this.context.toolSpecifications == null) {
            this.context.toolSpecifications = new ArrayList();
        }
        if (this.context.toolExecutors == null) {
            this.context.toolExecutors = new HashMap();
        }

        for (Map.Entry<Method,Object> objectMethodEntry : methodMap.entrySet()) {
            Method method = objectMethodEntry.getKey();
            if (method.isAnnotationPresent(Tool.class)) {
                toolsByMethod(objectMethodEntry.getValue(),method);
            }
        }
        return this;
    }


    public AiServices<T> toolsByMethod(Object objectWithTool,List<Method> methodList) {
        if (this.context.toolSpecifications == null) {
            this.context.toolSpecifications = new ArrayList();
        }
        if (this.context.toolExecutors == null) {
            this.context.toolExecutors = new HashMap();
        }

        for (Method method : methodList) {
            if (method.isAnnotationPresent(Tool.class)) {
                toolsByMethod(objectWithTool,method);
            }
        }
        return this;
    }



    public AiServices<T> toolsByMethod(Object objectWithTool,Method method) {
        if (this.context.toolSpecifications == null) {
            this.context.toolSpecifications = new ArrayList();
        }
        if (this.context.toolExecutors == null) {
            this.context.toolExecutors = new HashMap();
        }
        ToolSpecification toolSpecification = ToolSpecifications.toolSpecificationFrom(method);
        this.context.toolSpecifications.add(toolSpecification);
        this.context.toolExecutors.put(toolSpecification.name(), new DefaultToolExecutor(objectWithTool, method));
        return this;
    }



}
