package com.open.ai.eros.db.util;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @类名：TextUtil
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/17 11:40
 */
@Slf4j
public class TextUtil {


    public static Set<Long> getChannelIds(String channelStr){
        Set<Long> channelSet = new HashSet<>();
        try {
            if(StringUtils.isEmpty(channelStr)||",,".equals(channelStr)||channelStr.length()<=2){
                return channelSet;
            }
            List<String> channelIds = Arrays.asList(channelStr.substring(1, channelStr.length() - 1).split(","));
            if(CollectionUtils.isEmpty(channelIds)){
                return channelSet;
            }
            for (String channelId : channelIds) {
                if(StringUtils.isNotEmpty(channelId)){
                    channelSet.add(Long.parseLong(channelId));
                }
            }
        }catch (Exception e){
            log.error("getChannelIds error channelStr={}",channelSet,e);
        }
        return channelSet;
    }
}
