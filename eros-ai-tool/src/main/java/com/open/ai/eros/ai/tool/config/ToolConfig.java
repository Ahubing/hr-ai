package com.open.ai.eros.ai.tool.config;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.agent.tool.ToolSpecifications;
import dev.langchain4j.service.tool.DefaultToolExecutor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @类名：ToolConfig
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/15 0:29
 */

@Configuration
public class ToolConfig implements BeanPostProcessor, BeanDefinitionRegistryPostProcessor {


    public static Map<String, ToolSpecification> methodMap = new ConcurrentHashMap<>();

    public static Map<ToolSpecification, DefaultToolExecutor> toolExecutorMap = new HashMap<>();

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Method[] methods = bean.getClass().getMethods();
        for (Method method : methods) {
            if(method.isAnnotationPresent(Tool.class)){
                Tool annotation = method.getAnnotation(Tool.class);
                String name = annotation.name();
                if(StringUtils.isEmpty(name)){
                    name = method.getName();
                }
                ToolSpecification toolSpecification = ToolSpecifications.toolSpecificationFrom(method);

                methodMap.put(name,toolSpecification);

                DefaultToolExecutor defaultToolExecutor = new DefaultToolExecutor(bean, method);
                toolExecutorMap.put(toolSpecification,defaultToolExecutor);
            }
        }
        return bean;
    }


}
