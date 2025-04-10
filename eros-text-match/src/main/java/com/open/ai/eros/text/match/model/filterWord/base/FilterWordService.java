package com.open.ai.eros.text.match.model.filterWord.base;

import com.open.ai.eros.text.match.model.filterWord.service.FilterWordTextMatchService;
import com.open.ai.eros.text.match.model.filterWord.bean.vo.TextMatchResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FilterWordService {


    @Autowired
    private FilterWordTextMatchService filterWordTextMatchService;


    /**
     * 对外提供敏感词服务
     * @param channelId
     * @param text
     */
    public boolean filterWordText(long channelId, String text) {
        if(channelId<=0 || StringUtils.isEmpty(text)){
            log.info("filterWordText param error channelId={},text={}",channelId,text);
            return false;
        }
        text = text.replace("\r","").replace("\n","").replace(" ","");
        long startTime = System.currentTimeMillis();
        TextMatchResult result = filterWordTextMatchService.filterWordTextMatch(channelId, text);
        boolean isHit = result.isHit();
        log.info("filterWordText channelId={},text={},isHit={},cost ={}",channelId,text,isHit,System.currentTimeMillis()-startTime);
        return isHit;
    }
}
