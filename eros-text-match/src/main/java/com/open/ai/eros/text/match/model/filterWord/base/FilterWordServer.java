package com.open.ai.eros.text.match.model.filterWord.base;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.open.ai.eros.db.mysql.text.mapper.FilterWordMapper;
import com.open.ai.eros.db.mysql.text.entity.FilterWordInfoVo;
import com.open.ai.eros.text.match.model.filterWord.bean.vo.FilterWordNodeInfo;
import com.open.ai.eros.text.match.model.filterWord.bean.vo.FilterWordResultVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class FilterWordServer {

    private FilterWordMapper filterWordMapper;

    /**
     * 目前重构，所有的敏感词都只会维护一个字典树
     */
    private volatile FilterWordPrefixTree prefixTree;



    public FilterWordServer(FilterWordMapper filterWordMapper) {
        Objects.requireNonNull(filterWordMapper, "black word source can't be null!");

        this.filterWordMapper = filterWordMapper;
        int wordCount = filterWordMapper.getFilterWordCount();
        if(wordCount<=0){
            return;
        }
        log.info("init filterWordCount={}",wordCount);
        long startTime = System.currentTimeMillis();
        //调用无参构造方法 ，初始化字典树的root
        this.prefixTree = new FilterWordPrefixTree(wordCount);
        initBuildTree();//初始化字典树
        log.info("init filterWordCount={},cost={}",wordCount,System.currentTimeMillis()-startTime);
    }


    /**
     * 匹配敏感词
     * @param text
     * @return
     */
    public Set<FilterWordResultVo> generalMatching(String text, Long channelId) {
        return prefixTree.generalMatching(text, channelId);
    }

    /**
     * 初始化 字典树
     */
    private void initBuildTree(){
        int page = 1;
        int pageSize = 2000;
        while (true){
            int offset = (page-1)*pageSize;
            /**
             * 由于业务那边 同一个敏感词 需要在不用业务上应用，会出现风险等级或风险类型不相同的
             */
            List<FilterWordInfoVo> filterWords = filterWordMapper.getFilterWord(offset, pageSize);
            if(CollectionUtils.isEmpty(filterWords)){
                break;
            }
            for (FilterWordInfoVo filterWord : filterWords) {

                String channelStr = filterWord.getChannelStr();
                Set<Long> channelIds = getChannelIds(channelStr);
                for (Long channelId : channelIds) {

                    FilterWordPrefixTree.LeafTreeNode leafTreeNode = this.prefixTree.getLeafTreeNode(filterWord.getId());
                    Map<Long, FilterWordNodeInfo> filterWordNodeInfoMap = null;
                    if(leafTreeNode==null){
                        // 为了合理分配叶子节点的map的桶的内存
//                    filterWordNodeInfoMap = new ConcurrentHashMap<>(channelCount/2,channelMapFactor);
                        filterWordNodeInfoMap = new ConcurrentHashMap<>(2);
                    }else{
                        filterWordNodeInfoMap = leafTreeNode.filterWordNodeInfoMap;
                    }
                    filterWordNodeInfoMap.put(channelId,this.prefixTree.buildFilterWordNodeInfo(filterWord));
                    prefixTree.addFlower(filterWordNodeInfoMap,filterWord.getWordContent(),filterWord.getType());
                }
            }
            page++;
        }
    }

    public static String buildChannelIdsStr(List<Long>channelIds){
        if(org.apache.commons.collections.CollectionUtils.isEmpty(channelIds)){
            return ",,";
        }

        String join = channelIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        return String.format(",%s,",join);
    }

    public static Set<Long> getChannelIds(String channelStr){
        Set<Long> channelSet = new HashSet<>();
        try {
            if(StringUtils.isEmpty(channelStr)||",,".equals(channelStr)|| channelStr.length()<=2){
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


    public void addNewWord(FilterWordInfoVo addFilterWord){
        Map<Long, FilterWordNodeInfo> filterWordNodeInfoMap = getFilterWordNodeInfoMap(addFilterWord);
        //在这里维护新增的敏感词，将他们放到字典树中去
        this.prefixTree.addFlower(filterWordNodeInfoMap,addFilterWord.getWordContent(),addFilterWord.getType());
    }


    private Map<Long,FilterWordNodeInfo> getFilterWordNodeInfoMap(FilterWordInfoVo filterWordInfo){

        Map<Long,FilterWordNodeInfo> filterWordNodeInfoMap = null;
        Set<Long> channelIds = getChannelIds(filterWordInfo.getChannelStr());
        if(CollectionUtils.isNotEmpty(channelIds)){
            filterWordNodeInfoMap = new ConcurrentHashMap<>(channelIds.size());
            FilterWordNodeInfo wordNodeInfo = this.prefixTree.buildFilterWordNodeInfo(filterWordInfo);
            for (Long channelId : channelIds) {
                filterWordNodeInfoMap.put(channelId,wordNodeInfo);
            }
        }
        return filterWordNodeInfoMap;
    }
}
