package com.open.ai.eros.ai.lang.chain.controller;

import com.open.ai.eros.ai.lang.chain.bean.SplitterVo;
import com.open.ai.eros.ai.lang.chain.config.AiToolBaseController;
import com.open.ai.eros.ai.lang.chain.provider.splitter.Splitter;
import com.open.ai.eros.common.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @类名：SplitterController
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/29 16:06
 */
@RestController
public class SplitterController  extends AiToolBaseController {


    @Autowired
    private List<Splitter> splitterList;


    @GetMapping("/text/splitter")
    public ResultVO<List<SplitterVo>> getSplitterList(){
        List<SplitterVo> splitterVos = new ArrayList<>();
        for (Splitter splitter : splitterList) {
            splitterVos.add(
                    SplitterVo.builder()
                            .name(splitter.getName())
                            .value(splitter.getClass().getSimpleName())
                            .build()
            );
        }

        return ResultVO.success(splitterVos);
    }


}
