package com.open.ai.eros.ai.bean.dto;

import com.open.ai.eros.ai.bean.vo.DocsSource;
import com.open.ai.eros.ai.lang.chain.bean.TokenUsageVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @类名：AITextResponse
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/12/9 0:12
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AITextResponse {


    /**
     * 所有的ai操作计费
     * 类似 函数调用 向量化
     */
    private List<TokenUsageVo> tokenUsages;


    /**
     * 最后一次推送的内容
     */
    private List<DocsSource> docsSources;




    public List<TokenUsageVo> getTokenUsages(){
        if(this.tokenUsages==null){
            this.tokenUsages = new ArrayList<>();
        }
        return this.tokenUsages;
    }


}
