package com.open.ai.eros.text.match.model.filterWord.base;

import com.open.ai.eros.db.mysql.text.entity.FilterWordInfoVo;
import com.open.ai.eros.text.match.constants.FilterWordConstant;
import com.open.ai.eros.text.match.model.filterWord.bean.vo.FilterWordNodeInfo;
import com.open.ai.eros.text.match.model.filterWord.bean.vo.FilterWordResultVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class FilterWordPrefixTree {


    private volatile ConcurrentHashMap<Long,LeafTreeNode> leafTreeNodeMap;


    public FilterWordPrefixTree(int filterWordCount) {
        root = new TreeNode(' ');
        leafTreeNodeMap = new ConcurrentHashMap<>(filterWordCount+50,1);
    }

    private volatile TreeNode root;


    public interface BeeWatcher {

        default void flower(final char[] tree, int start, int end, boolean isWhite){}


        default void flower(LeafTreeNode leafTreeNode){}

        /**
         * 匹配到了叶子节点
         * @param leafTreeNode
         */
        default void flower(final char[] tree, int start, int end, LeafTreeNode leafTreeNode){}

        /**
         * 获取命中的敏感词的信息  包括 命中的白词
         * @param leafTreeNode
         * @return
         */
        default FilterWordNodeInfo getFilterWordNodeInfo(LeafTreeNode leafTreeNode){
            return null;
        }

        default void leaf(final char[] tree, int start, int end){}

    }


    public void matchText(String peachTree, BeeWatcher beeWatcher) {
        char[] chars = peachTree.toLowerCase().toCharArray();

        int charLength = chars.length;
        // start要从此位置重新从根结点开始匹配
        for (int start = 0; start < charLength; ) {
            TreeNode parent = root;
            int lastFindEnd = 0;// 最后一次找到词的有效结束位置，不被包含
            TreeNode lastFindNode = null;    //最后一次找到词的结点.
            // 一次从根节点到叶节点的查找
            for (int i = start; i <= charLength; i++) {
                // i的判断条件必须大于等于 匹配文本的长度，如果不是的话，就需要在循环中对Length -1的情况做特殊的处理
                if (i < charLength) {// 这个条件判断也是必须的；如果去掉就报错，因为i的最大值会等于char数组的长度
                    char ch = chars[i];
                    TreeNode child = parent.sub(ch);
                    // 这里使用了贪心的思想，达到的效果是 白词会吃掉对应的黑词
                    if (child != null) {
                        // 能继续向下
                        if (child instanceof LeafTreeNode) {
                            /**
                             * 需要命中这个通道所对应上的敏感词
                             * 例如：  敏感词：大聪明 在通道1  敏感词2：大聪明好 对应通道2
                             * 入参   文本信息：我是大聪明好  channelId = 1 ;
                             * 如果不加这层判断，会出现 命中的敏感词是2，但是敏感词2没有对应到通道1；所以最后返回结果时未命中
                             * 正常情况是：命中，敏感词返回：大聪明
                             */
                            FilterWordNodeInfo filterWordNodeInfo = beeWatcher.getFilterWordNodeInfo((LeafTreeNode) child);
                            if(filterWordNodeInfo!=null){
                                // 是有效词节点
                                lastFindEnd = i + 1;
                                lastFindNode = child;
                            }
                        }
                        parent = child;
                        continue;
                    }
                }
                // 不能往下走了。
                if (lastFindEnd > 0) {
                    // 已经存在有效查找
                    beeWatcher.flower(chars,start,lastFindEnd, (LeafTreeNode) lastFindNode );
                    start = lastFindEnd;
                    break;
                }
                beeWatcher.leaf(chars, start, start + 1);
                start++;
                break;
            }
        }
    }

    /**
     * 用于正常敏感词匹配
     * 只要文本出现了 敏感词就会命中，简单粗暴
     * 例如：  text:  i am a student
     *       敏感词：dent           将会命中；会存在英文的误伤的情况
     * @param text
     * @return
     */
    public Set<FilterWordResultVo> generalMatching(String text, Long channelId) {
        //使用map是为了根据敏感词去重
        Set<FilterWordResultVo> wordResults = new HashSet<>(1,8);

        matchText(text, new BeeWatcher() {
            /**
             * 获取敏感词信息
             * @param leafTreeNode
             * @return
             */
            @Override
            public FilterWordNodeInfo getFilterWordNodeInfo(LeafTreeNode leafTreeNode) {
                FilterWordNodeInfo filterWordNodeInfo = null;
                if(leafTreeNode.type == FilterWordConstant.WRITE_WORD
                        && CollectionUtils.isNotEmpty(leafTreeNode.filterWordNodeInfoMap.values())){
                    //代表的是白词；白词是不会应用到其它的业务上面的；
                    filterWordNodeInfo = leafTreeNode.filterWordNodeInfoMap.values().iterator().next();
                }else if(leafTreeNode.type == FilterWordConstant.PINYIN_WORD){
                    //全拼如果命中的是全拼，直接return null
                }else{
                    filterWordNodeInfo = leafTreeNode.filterWordNodeInfoMap.get(channelId);
                }
                return filterWordNodeInfo;
            }

            /**
             * 封装匹配到敏感词信息
             * @param leafTreeNode
             */
            @Override
            public void flower(char[] tree, int start, int end, LeafTreeNode leafTreeNode) {
                FilterWordNodeInfo filterWordNodeInfo = getFilterWordNodeInfo(leafTreeNode);
                if(filterWordNodeInfo!=null){
                    FilterWordResultVo result = buildFilterWordResult(filterWordNodeInfo,leafTreeNode.type,new String(tree, start, (end - start)));
                    wordResults.add(result);
                }
            }
        });
        log.info("generalMatching text={} wordResults={}",text,wordResults);
        return wordResults;
    }


    /**
     * 往字典树中添加敏感词；敏感词的结尾那个字符构成是字典树的叶子节点
     * 构建字典树，不需要同步，因为每个实例都只会有一个线程去构建或者维护这个字典树，不需要考虑并发问题
     *
     * @param filterWordNodeInfoMap  敏感词和通道的信息
     */
    public void addFlower(Map<Long,FilterWordNodeInfo> filterWordNodeInfoMap,String wordContent,int type) {
        try {
            if ((filterWordNodeInfoMap == null) || (filterWordNodeInfoMap.size() <=0 )) {
                log.info("addFlower wordContent={},filterWordMapSize is 0",wordContent);
                return;
            }
            char[] chars = wordContent.toLowerCase().toCharArray();
            TreeNode parent = root;
            int length = chars.length;
            for (int i = 0; i < length; i++) {
                char ch = chars[i];
                TreeNode child = parent.sub(ch);
                if (child == null) {
                    if(i== (length-1) ){
                        //构建叶子节点
                        child = new LeafTreeNode( ch ,(short)type,filterWordNodeInfoMap,wordContent);
                        buildLeafTreeNodeMap((LeafTreeNode) child);
                    }else{
                        child = new TreeNode(ch);
                    }
                    parent.born(child);
                    parent = child;
                    continue;
                }else if( i== (length-1) ){// 如果同一个敏感词内容 节点已经有了，只需要将它对应的channelId的信息更新进去
                    if(child instanceof LeafTreeNode){
                        //保存的节点位置是叶子节点；也可能是树干；特殊情况
                        LeafTreeNode leafTreeNode = (LeafTreeNode) child;
                        Map<Long, FilterWordNodeInfo> nodeInfoMap = leafTreeNode.filterWordNodeInfoMap;
                        nodeInfoMap.putAll(filterWordNodeInfoMap);
                        buildLeafTreeNodeMap(leafTreeNode);//将叶子节点和敏感词用map关联
                    }else{
                        // 保存的节点位置是 枝干节点  需要将枝干转化为 （叶子+树枝） 节点
                        LeafTreeNode newLeafTreeNode = new LeafTreeNode(ch,(short)type,filterWordNodeInfoMap,wordContent);
//                        newLeafTreeNode.branch = child.branch;//
                        newLeafTreeNode.born(child.branch);// 该枝干的子节点赋值该叶子节点的子节点
                        parent.born(newLeafTreeNode);
                        buildLeafTreeNodeMap(newLeafTreeNode);//将叶子节点和敏感词用map关联
                    }
                }
                parent = child;
            }
        }catch (Exception e){
            log.error("addFlower error content={} e",wordContent,e);
        }

    }

    /**
     *    更新敏感词的时候，首先找到叶子节点的 id
     * @param filterWordId 敏感词的id
     * @return
     */
    public LeafTreeNode getLeafTreeNode(Long filterWordId){
        return leafTreeNodeMap.get(filterWordId);
    }

    /**
     * build叶子节点的信息
     * @param leafTreeNode
     */
    public void buildLeafTreeNodeMap(LeafTreeNode leafTreeNode){
        Map<Long, FilterWordNodeInfo> filterWordNodeInfoMap = leafTreeNode.filterWordNodeInfoMap;
        Collection<FilterWordNodeInfo> wordNodeInfos = filterWordNodeInfoMap.values();
        for (FilterWordNodeInfo wordNodeInfo : wordNodeInfos) {
            //会存在多个敏感词id对应一个叶子节点
            leafTreeNodeMap.put(wordNodeInfo.getId(),leafTreeNode);
        }
    }

    private FilterWordResultVo buildFilterWordResult(FilterWordNodeInfo filterWordNodeInfo, short type, String wordContent){
        FilterWordResultVo result = new FilterWordResultVo();
        result.setId(filterWordNodeInfo.getId());
        result.setRiskLevel(filterWordNodeInfo.getRiskLevel());
        result.setRiskType(filterWordNodeInfo.getRiskType());
        result.setType(type);
        Long replyId = filterWordNodeInfo.getReplyMap().get(filterWordNodeInfo.getId());
        result.setReplyId(replyId);
        result.setWordContent(wordContent);
        return result;
    }
    /**
     * 多个channel绑定同一个敏感词 ，其实是可以共享这个变量的
     * @param filterWord
     * @return
     */
    public FilterWordNodeInfo buildFilterWordNodeInfo(FilterWordInfoVo filterWord){
        LeafTreeNode leafTreeNode = getLeafTreeNode(filterWord.getId());
        if(leafTreeNode!=null){
            Map<Long, FilterWordNodeInfo> filterWordNodeInfoMap = leafTreeNode.filterWordNodeInfoMap;
            Collection<FilterWordNodeInfo> filterWordNodeInfos = filterWordNodeInfoMap.values();
            for (FilterWordNodeInfo wordNodeInfo : filterWordNodeInfos) {
                if(filterWord.getId().equals(wordNodeInfo.getId())){
                    // 每次都更新为最新的信息
                    leafTreeNode.type = (short) filterWord.getType();
                    wordNodeInfo.setRiskLevel(filterWord.getRiskLevel());
                    wordNodeInfo.setLanguage(filterWord.getLanguage());
                    wordNodeInfo.setRiskType(filterWord.getRiskType());
                    if(wordNodeInfo.getReplyMap()==null){
                        wordNodeInfo.setReplyMap(new ConcurrentHashMap<>());
                    }
                    wordNodeInfo.getReplyMap().put(filterWord.getId(),filterWord.getReplyId());
                    //找这个敏感词已经构建好的info返回出去 达到多个通道和一个敏感词绑定时，共用一个敏感词信息，减少内存的消耗
                    return wordNodeInfo;
                }
            }
        }
        FilterWordNodeInfo filterWordNodeInfo = new FilterWordNodeInfo();
        filterWordNodeInfo.setRiskLevel(filterWord.getRiskLevel());
        filterWordNodeInfo.setRiskType(filterWord.getRiskType());
        filterWordNodeInfo.setLanguage(filterWord.getLanguage());
        filterWordNodeInfo.setReplyMap(new ConcurrentHashMap<>());
        filterWordNodeInfo.getReplyMap().put(filterWord.getId(),filterWord.getReplyId());
        filterWordNodeInfo.setId(filterWord.getId());
        return filterWordNodeInfo;
    }


    class TreeNode {
        volatile char key;
        // 下级子节点
        volatile Map<Character, TreeNode> branch;

        public TreeNode(char key) {
            this.key = key;
        }

        public void born(TreeNode sub) {
            if(branch==null){
                branch = new ConcurrentHashMap<>(2);
            }
            branch.put(sub.key, sub);
        }

        public void born(Map<Character,TreeNode> child){
            if(child!=null){
                branch = new ConcurrentHashMap<>((int)(child.size()/0.75)+2);
                branch.putAll(child);
            }
        }

        public TreeNode sub(char key) {
            if(branch==null){
                return null;
            }
            return branch.get(key);
        }

    }

    /**
     * 叶子节点
     */
    class LeafTreeNode extends TreeNode {
        /**
         * 敏感词的类型，同一个敏感词只会出现一个类型中, 保存敏感词的type，是为了国外会根据不同的语言的敏感词的匹配规则不一样做准备
         *
         * @see FilterWordConstant
         */
        volatile short type = 1;

        /**
         * 敏感词
         */
        volatile String wordContent;

        /**
         * key是通道id
         * value是敏感词的信息 （不同的通道会复用这个敏感词的信息类）
         */
        volatile Map<Long, FilterWordNodeInfo> filterWordNodeInfoMap; // 注意：当这个变量为null的时候，代表这个叶子节点需要被清除掉了

        public LeafTreeNode(char key, short type, Map<Long, FilterWordNodeInfo> filterWordNodeInfoMap, String wordContent) {
            super(key);
            updateFilerWordIdSet(filterWordNodeInfoMap);
            this.type = type;
            this.wordContent = wordContent;
        }

        /**
         * 更新
         */
        public void updateFilerWordIdSet(Map<Long, FilterWordNodeInfo> filterWordNodeInfoMap) {
            this.filterWordNodeInfoMap = filterWordNodeInfoMap;
        }

        @Override
        public String toString() {
            return "LeafTreeNode{" +
                    "key=" + key +
                    ", branch=" + branch +
                    ", type=" + type +
                    ", wordContent='" + wordContent + '\'' +
                    ", filterWordNodeInfoMap=" + filterWordNodeInfoMap.keySet() +
                    '}';
        }
    }
}
