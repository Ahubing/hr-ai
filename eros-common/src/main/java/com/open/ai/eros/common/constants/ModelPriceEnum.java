package com.open.ai.eros.common.constants;


import java.util.HashMap;
import java.util.Map;

/**
 * 目前系统支持的模型type
 */
public enum ModelPriceEnum {

    GPT_3_TURBO( "gpt-3.5-turbo", 15L,20L,3600,-1L),
    GPT_3_TURBO_0301( "gpt-3.5-turbo-0301", 15L,20L,3600,-1L),
    GPT_3_TURBO_0613( "gpt-3.5-turbo-0613", 15L,20L,3600,-1L),
    GPT_3_TURBO_16K( "gpt-3.5-turbo-16k",  30L,40L,8000,-1L),
    GPT_3_TURBO_16K_1106( "gpt-3.5-turbo-1106",  10L,20L,12000,-1L),
    GPT_3_TURBO_16K_0613( "gpt-3.5-turbo-16k-0613", 3000L,40L,12000,-1L),
    GPT_4( "gpt-4", 300L,600L,7000,-1L),
    command_r( "command-r", 100L,300L,160000,-1L),
    command_r_plus( "command-r-plus", 100L,300L,160000,-1L),
    command_r_plus_08_2024( "command-r-plus-08-2024", 100L,300L,160000,-1L),
    gpt_4_1106_preview( "gpt-4-1106-preview", 100L,300L,160000,-1L),
    gpt_4_vision_preview( "gpt-4-vision-preview", 100L,300L,1600000000,-1L),
    dall_e_3( "dall-e-3", 10000L,100L,60,-1L),
    claude_1_100k( "claude-1-100k", 10L,10L,1000000,100000L),
    claude_instant_100k( "claude-instant-100k", 100L,100L,1000000,100000L),
    claude_2_100k( "claude-2-100k", 100L,100L,1000000,300000L),
    //    M_J( "Midjourney", 600000L,0L,380000),
    gpt_4_all( "gpt-4-all", 300L,600L,20000,-1L),
    gpt_3_5_turbo_1106("gpt-3.5-turbo-1106",10L,20L,12000,-1L),
    tts_1("tts-1",10L,10L,6000,-1L),
    whisper_1("whisper-1",10L,10L,10000,-1L),
    gpt_4_gizmo("gpt-4-gizmo",300L,600L,5000,-1L),
    mid_journey("Midjourney",1500000L,1L,5000,1500000L),
    suno_v3("suno-v3",2000000L,1L,5000,2000000L),
    gemini_pro( "gemini-pro", 100L,100L,12000,100000L),
    gemini_pro_vision( "gemini-pro-vision", 100L,100L,12000,200000L),
    bing_precise( "bing-precise", 100L,100L,1200000,600000L),
    bing_balanced( "bing-balanced", 100L,100L,1200000,600000L),
    bing_creative( "bing-creative", 100L,100L,1200000,600000L),
    //扩大十倍
    text_embedding_3_small( "text-embedding-3-small", 2L,2L,1200000,-1L),
    //获取大了 10倍
    text_embedding_3_large( "text-embedding-3-large", 13L,13L,1200000,-1L),
    // text-embedding-ada-002
    text_embedding_ada_002( "text-embedding-ada-002", 10L,10L,1200000,-1L),
    // 0.0005
    gpt_3_5_turbo_0125( "gpt-3.5-turbo-0125", 5L,15L,3600,-1L),

    claude_3_opus( "claude-3-opus-20240229", 150L,750L,500000000,-1L),
    claude_3_sonnet( "claude-3-sonnet-20240229", 30L,150L,500000000,-1L),
    claude_3_sonnet20240620( "claude-3-5-sonnet-20240620", 30L,150L,500000000,-1L),
    claude_3_haiku( "claude-3-haiku-20240307", 10L,15L,500000000,-1L),
    //$8.00
    gpt_4_turbo_2024_04_09( "gpt-4-turbo-2024-04-09", 100L,300L,2000000000,-1L),
    //$24.0
    gpt_4o( "gpt-4o", 50L,150L,2000000000,-1L),
    gpt_4o_2024_05_13( "gpt-4o-2024-05-13", 50L,150L,2000000000,-1L),
    gpt_4o_2024_08_06( "gpt-4o-2024-08-06", 50L,150L,2000000000,-1L),
    gpt_4o_all( "gpt-4o-all", 300L,600L,20000,-1L),
    gemini_1_5_flash( "gemini-1.5-flash", 10L,30L,20000,-1L),
    gemini_1_5_pro( "gemini-1.5-pro", 50L,150L,2000000000,-1L),
    gemini_1_5_pro_last( "gemini-1.5-pro-latest", 50L,150L,2000000000,-1L),
    eros4_5( "eros-4.5", 5L,15L,3600,-1L),
    gpt_4o_mini( "gpt-4o-mini", 1L,1L,2000000000,-1L),
    grok_beta("grok-beta",50L,150L,2000000000,-1L),
    gemini_exp_1114( "gemini-exp-1114", 50L,150L,2000000000,-1L),
    claude_2_1( "claude-2.1", 8L,24L,100000000,-1L);

    /**
     * claude-1-100k	0.006/次	0.006/次	0.006/次
     * claude-2-100k	0.03/次	0.03/次	0.03/次
     */

    private String model;
    private Long inPrice;
    private Long outPrice;
    private Integer maxInTokenNumber; // 限制问题的token
    private Long lockPrice;//固定价格

    ModelPriceEnum(String model, Long inPrice, Long outPrice, Integer maxInTokenNumber, Long lockPrice) {
        this.model = model;
        this.inPrice = inPrice;
        this.outPrice = outPrice;
        this.maxInTokenNumber = maxInTokenNumber;
        this.lockPrice = lockPrice;
    }

    public final static Map<String,ModelPriceEnum> modelPriceMap = new HashMap<>();

    static {
        for (ModelPriceEnum value : values()) {
            modelPriceMap.put(value.getModel(),value);
        }
    }

    /**
     * 如果当模型不存在的时候，直接返回gpt3的计费规则
     * @param model
     * @return
     */
    public static ModelPriceEnum getModelPrice(String model){
        if(model.contains(gpt_4_gizmo.getModel())){
            return gpt_4_gizmo;
        }
        ModelPriceEnum modelPriceEnum = modelPriceMap.get(model);
        if(modelPriceEnum==null){
            return GPT_4;
        }
        return modelPriceEnum;
    }


    public String getModel() {
        return model;
    }

    public Long getInPrice() {
        return inPrice;
    }

    public Long getOutPrice() {
        return outPrice;
    }

    public Integer getMaxInTokenNumber() {
        return maxInTokenNumber;
    }

    public Long getLockPrice() {
        return lockPrice;
    }
}
