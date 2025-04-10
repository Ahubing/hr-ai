package com.open.ai.eros.file.controller.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class RequestInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userToken = null;
        try {
            //放行OPTIONS请求
            String method = request.getMethod();
            if("OPTIONS".equals(method)){
                return true;
            }

            return false;
        }catch (Exception e){
            log.error("LoginInterceptor error token ={},uri={}", userToken ,request.getRequestURI(),e);
        }
        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //一次请求后需要删除线程变量，否则会造成内存泄漏
    }
}
