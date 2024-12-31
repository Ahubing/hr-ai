package com.open.ai.eros.ai.manager;

import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.open.ai.eros.ai.bean.vo.DocsSource;
import com.open.ai.eros.ai.bean.vo.EmbeddingSearchResultVo;
import com.open.ai.eros.ai.bean.vo.SearchKnowledgeResult;
import com.open.ai.eros.ai.lang.chain.bean.SearchKnowledgeResultVo;
import com.open.ai.eros.ai.lang.chain.bean.TokenUsageVo;
import com.open.ai.eros.ai.lang.chain.constants.EmbedConst;
import com.open.ai.eros.ai.lang.chain.provider.EmbeddingProvider;
import com.open.ai.eros.ai.lang.chain.vector.VectorStoreApi;
import com.open.ai.eros.ai.lang.chain.vector.VectorStoreFactory;
import com.open.ai.eros.ai.model.bean.vo.gpt.ChatCompletionResult;
import com.open.ai.eros.ai.model.bean.vo.gpt.GptCompletionRequest;
import com.open.ai.eros.ai.util.GptChatModelUtil;
import com.open.ai.eros.ai.vector.process.CheckInferQuestionAIProcess;
import com.open.ai.eros.common.util.CountDownLatchWrapper;
import com.open.ai.eros.common.vo.ChatMessage;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.constants.AIRoleEnum;
import com.open.ai.eros.db.constants.KnowledgeConstant;
import com.open.ai.eros.db.mysql.knowledge.entity.DocsSlice;
import com.open.ai.eros.db.mysql.knowledge.entity.Knowledge;
import com.open.ai.eros.db.mysql.knowledge.entity.KnowledgeDocs;
import com.open.ai.eros.db.mysql.knowledge.service.impl.DocsSliceServiceImpl;
import com.open.ai.eros.db.mysql.knowledge.service.impl.KnowledgeDocsServiceImpl;
import com.open.ai.eros.db.mysql.knowledge.service.impl.KnowledgeServiceImpl;
import com.open.ai.eros.knowledge.config.KnowledgeAIConfig;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Component
@Slf4j
public class EmbeddingSearchService {


    @Autowired
    private KnowledgeServiceImpl knowledgeService;

    @Autowired
    private EmbeddingProvider embeddingProvider;

    @Autowired
    private DocsSliceServiceImpl docsSliceService;

    @Autowired
    private CheckInferQuestionAIProcess checkInferQuestionAIProcess;

    @Autowired
    private KnowledgeDocsServiceImpl knowledgeDocsService;

    @Autowired
    private KnowledgeAIConfig knowledgeAIConfig;

    private static ThreadFactory embeddingSearchThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("embedding_search-pool-%d").build();
    public static ThreadPoolExecutor embeddingSearchPool = new ThreadPoolExecutor(20, 20, 10, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(10000), embeddingSearchThreadFactory, ThreadPoolManager.getCallerRunsPolicyExecutionHandler());


    /**
     * @param knowledgeId
     * @param prompt
     * @return
     */
    public ResultVO<SearchKnowledgeResult> searchKnowledgeContent(Long knowledgeId, String prompt, int number, double minScore) {


        SearchKnowledgeResult searchKnowledgeResult = new SearchKnowledgeResult();

        String collectName = String.format(KnowledgeConstant.knowledgeName, knowledgeId);
        Knowledge knowledge = knowledgeService.getCacheById(knowledgeId);
        if (knowledge == null) {
            log.error("searchKnowledgeContent knowledge is null knowledgeId={}", knowledgeId);
            return ResultVO.fail("知识库不存在！");
        }

        VectorStoreApi vectorStoreApi = VectorStoreFactory.getVectorStoreApi(knowledge.getVectorDatabase()).getKnowledgeVectorStore(collectName, knowledge.getDimension());

        EmbeddingModel embedProvider = embeddingProvider.embed(knowledge.getTemplateModel());
        Response<Embedding> embedded = embedProvider.embed(prompt);

        Embedding content = embedded.content();

        //Filter filter = MetadataFilterBuilder.metadataKey("userId").isEqualTo("1");
        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                // 相似性搜索文本
                .queryEmbedding(content)
                .maxResults(number)
                .minScore(minScore)
                .build();

        TokenUsage tokenUsage = embedded.tokenUsage();
        TokenUsageVo tokenUsageVo = new TokenUsageVo();
        tokenUsageVo.setModel(knowledge.getTemplateModel().split(":")[1]);
        tokenUsageVo.setInputTokenCount(tokenUsage.inputTokenCount());
        tokenUsageVo.setOutputTokenCount(tokenUsage.outputTokenCount());
        tokenUsageVo.setTotalTokenCount(tokenUsage.totalTokenCount());

        searchKnowledgeResult.setTokenUsage(tokenUsageVo);

        //new IsIn("userId", Arrays.asList("1", "2","3"));
        EmbeddingSearchResult<TextSegment> result = vectorStoreApi.search(request);

        List<Long> sliceIds = new ArrayList<>();
        for (EmbeddingMatch<TextSegment> embeddingMatch : result.matches()) {
            TextSegment textSegment = embeddingMatch.embedded();
            String text = textSegment.text();
            log.info("searchKnowledgeContent prompt={} 命中问题：{}", prompt, text);
            Long sliceId = textSegment.metadata().getLong(EmbedConst.SPLICE_ID);
            sliceIds.add(sliceId);
        }
        if (CollectionUtils.isEmpty(sliceIds)) {
            return ResultVO.success();
        }
        Set<Long> sliceIdSet = new HashSet<>(sliceIds);
        List<DocsSlice> docsSlices = docsSliceService.listByIds(sliceIdSet);
        if (docsSlices.isEmpty()) {
            log.info("searchKnowledgeContent docsSlices isEmpty knowledgeId={} prompt={}", knowledgeId, prompt);
            return ResultVO.success();
        }
        List<String> collected = docsSlices.stream().map(DocsSlice::getContent).collect(Collectors.toList());

        List<Long> docsIds = docsSlices.stream().map(DocsSlice::getDocsId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(docsIds)) {
            List<KnowledgeDocs> knowledgeDocs = knowledgeDocsService.getBaseMapper().selectBatchIds(docsIds);
            if (CollectionUtils.isNotEmpty(knowledgeDocs)) {
                searchKnowledgeResult.setSource(new ArrayList<>());
                Map<Long, KnowledgeDocs> knowledgeDocsMap = knowledgeDocs.stream().collect(Collectors.toMap(KnowledgeDocs::getId, v -> v, (k1, k2) -> k1));

                for (DocsSlice docsSlice : docsSlices) {
                    DocsSource.DocsSourceBuilder builder = DocsSource.builder();
                    KnowledgeDocs docs = knowledgeDocsMap.get(docsSlice.getDocsId());
                    if (docs != null) {
                        builder.url(docs.getUrl())
                                .name(docs.getName());
                    } else {
                        builder.name("自定义切片");
                    }
                    builder.chunk(docsSlice.getContent());
                    DocsSource docsSource = builder.build();
                    searchKnowledgeResult.getSource().add(docsSource);
                }
            }
        }
        searchKnowledgeResult.setContents(collected);
        return ResultVO.success(searchKnowledgeResult);
    }


    public static List<KnowledgeDocs> removeDuplicates(List<KnowledgeDocs> docsList) {
        Set<Long> seenIds = new HashSet<>();
        List<KnowledgeDocs> uniqueDocsList = new ArrayList<>();
        for (KnowledgeDocs doc : docsList) {
            if (!seenIds.contains(doc.getId())) {
                seenIds.add(doc.getId());
                uniqueDocsList.add(doc);
            }
        }
        return uniqueDocsList;
    }

    /**
     * @param knowledgeId
     * @param prompt
     * @return
     */
    public ResultVO<EmbeddingSearchResultVo> searchKnowledgeContentTest(Long knowledgeId, String prompt) {
        EmbeddingSearchResultVo embeddingSearchResultVo = new EmbeddingSearchResultVo();
        String collectName = String.format(KnowledgeConstant.knowledgeName, knowledgeId);
        Knowledge knowledge = knowledgeService.getCacheById(knowledgeId);
        if (knowledge == null) {
            return ResultVO.fail("知识库不存在！");
        }

        VectorStoreApi vectorStoreApi = VectorStoreFactory.getVectorStoreApi(knowledge.getVectorDatabase()).getKnowledgeVectorStore(collectName, knowledge.getDimension());

        EmbeddingModel embedProvider = embeddingProvider.embed(knowledge.getTemplateModel());
        Response<Embedding> embedded = embedProvider.embed(prompt);

        Embedding content = embedded.content();

        //Filter filter = MetadataFilterBuilder.metadataKey("userId").isEqualTo("1");
        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                // 相似性搜索文本
                .queryEmbedding(content)
                .maxResults(5)
                .minScore(0.6)
                .build();

        //new IsIn("userId", Arrays.asList("1", "2","3"));
        EmbeddingSearchResult<TextSegment> result = vectorStoreApi.search(request);

        List<Long> sliceIds = new ArrayList<>();
        for (EmbeddingMatch<TextSegment> embeddingMatch : result.matches()) {
            TextSegment textSegment = embeddingMatch.embedded();
            String text = textSegment.text();
            log.info("searchKnowledgeContent prompt={} 命中问题：{}", prompt, text);
            Long sliceId = textSegment.metadata().getLong(EmbedConst.SPLICE_ID);
            sliceIds.add(sliceId);
        }

        if (sliceIds.isEmpty()) {
            return ResultVO.success();
        }

        Set<Long> sliceIdSet = sliceIds.stream().collect(Collectors.toSet());

        List<DocsSlice> docsSlices = docsSliceService.listByIds(sliceIdSet);
        if (docsSlices.isEmpty()) {
            return ResultVO.success();
        }

        LinkedList<ChatMessage> messages = new LinkedList<>();

        String userPrompt = "你是专属机器人,请基于下面最近" + docsSlices.size() + "条对话来回答用户的最新的问题。";

        messages.add(new ChatMessage(AIRoleEnum.SYSTEM.getRoleName(), userPrompt));

        int i = 1;
        for (DocsSlice vo : docsSlices) {
            messages.add(new ChatMessage(AIRoleEnum.USER.getRoleName(), "这是背景" + i));
            messages.add(new ChatMessage(AIRoleEnum.ASSISTANT.getRoleName(), vo.getContent()));
            i++;
        }
        messages.add(new ChatMessage(AIRoleEnum.USER.getRoleName(), prompt));

        GptCompletionRequest completionRequest = GptCompletionRequest.builder()
                .stream(false)
                .model(knowledgeAIConfig.getCheckInferModel())
                .messages(messages).temperature(1.0).build();
        ChatCompletionResult chatCompletionResult = GptChatModelUtil.startChatWithNoStream(completionRequest, knowledgeAIConfig.getToken(), knowledgeAIConfig.getUrl());
        if (chatCompletionResult != null) {
            String string = chatCompletionResult.getChoices().get(0).getMessage().getContent().toString();
            embeddingSearchResultVo.setMessage(string);
            return ResultVO.success(embeddingSearchResultVo);
        }
        //List<SearchKnowledgeResultVo> newResultVos = new ArrayList<>();
        //
        //CountDownLatchWrapper countDownLatchWrapper = getCountDownLatchWrapper(resultVos, newResultVos);
        //countDownLatchWrapper.await();
        return ResultVO.success();
    }


    @NotNull
    private CountDownLatchWrapper getCountDownLatchWrapper(List<SearchKnowledgeResultVo> resultVos, List<SearchKnowledgeResultVo> newResultVos) {
        CountDownLatchWrapper countDownLatchWrapper = new CountDownLatchWrapper(embeddingSearchPool, 3 * 1000000, resultVos.size());
        for (SearchKnowledgeResultVo resultVo : resultVos) {
            countDownLatchWrapper.submit(() -> {
                try {
                    boolean updated = checkInferQuestionAIProcess.checkInferQuestion(resultVo.getQuestion(), resultVo.getContent());
                    if (updated) {
                        newResultVos.add(resultVo);
                    } else {
                        log.info("不匹配答案 resultVo={} ", JSONObject.toJSONString(resultVo));
                    }
                } catch (Exception e) {
                    log.error("searchKnowledgeContent resultVo={}", resultVo, e);
                }
            });
        }
        return countDownLatchWrapper;
    }

}
