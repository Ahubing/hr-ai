package com.open.ai.eros.text.match.controller;


import com.open.ai.eros.common.constants.BaseCodeEnum;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.text.match.config.TextMatchBaseController;
import com.open.ai.eros.text.match.manager.TextCheckManager;
import com.open.ai.eros.text.match.model.filterWord.bean.vo.TextMatchResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "文本匹配控制类" )
@Slf4j
@RestController
public class TextCheckController extends TextMatchBaseController {


    @Autowired
    private TextCheckManager textCheckManager;


    @ApiOperation("文本匹配")
    @GetMapping("/check/text")
    public ResultVO<TextMatchResult> textCheck(@RequestParam("content") String content){
        long startTime = System.currentTimeMillis();
        try {
            if(StringUtils.isEmpty(content)){
                return ResultVO.success();
            }
            content = content.toLowerCase();
            TextMatchResult textMatchResult = textCheckManager.checkText(content, 22L);
            return ResultVO.success(textMatchResult);
        }catch (Exception e){
            log.error("textCheck error  check content={}",content,e);
        }finally {
            log.info("textCheck check content={},cost={}",content,System.currentTimeMillis()-startTime);
        }
        return ResultVO.fail(BaseCodeEnum.SERVER_BUSY.getMsg());
    }






}
