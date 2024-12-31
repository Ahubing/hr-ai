package com.open.ai.eros.text.match.model.filterWord.base;

import com.open.ai.eros.db.mysql.text.mapper.FilterWordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Slf4j
@Component
public final class FilterWordServerFactory {

    @Resource
    private FilterWordMapper filterWordMapper;

    private FilterWordServer filterWordServer;


    /**
     * 项目启动就会开始构建敏感词
     */
    @PostConstruct
    public void init(){
        try {
            this.filterWordServer = new FilterWordServer(filterWordMapper);
        }catch (Exception e){
            log.error("FilterWordServerFactory init e",e);
        }
    }

    /**
     * 获取敏感词服务
     * @return
     */
    public FilterWordServer getFilterWordServer() {
       if(this.filterWordServer==null){
           log.error("getFilterWordServer error,FilterWordServer is null");
       }
       return this.filterWordServer;
    }
}
