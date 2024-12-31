package com.open.ai.eros.text.match.manager;

import com.open.ai.eros.text.match.model.filterWord.bean.vo.TextMatchResult;
import com.open.ai.eros.text.match.model.filterWord.service.FilterWordTextMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TextCheckManager {

    @Autowired
    private FilterWordTextMatchService filterWordTextMatchService;


    public TextMatchResult checkText(String content, Long channelId){
        content = content.toLowerCase();
        return filterWordTextMatchService.filterWordTextMatch(channelId, content);
    }

}
