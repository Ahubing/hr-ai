package com.open.ai.eros.ai.controller;

import com.open.ai.eros.ai.bean.req.ModelConfigAddReq;
import com.open.ai.eros.ai.bean.req.ModelConfigSearchReq;
import com.open.ai.eros.ai.bean.req.ModelConfigUpdateReq;
import com.open.ai.eros.ai.bean.vo.ModelGroupVo;
import com.open.ai.eros.ai.bean.vo.ModelVo;
import com.open.ai.eros.ai.config.AIBaseController;
import com.open.ai.eros.ai.constatns.ModelTemplateEnum;
import com.open.ai.eros.ai.manager.ModelConfigManager;
import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.constants.ModelPriceEnum;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.ai.entity.ModelConfigVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @类名：ModelConfigController
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/7 22:15
 */
@Api(tags = "AI渠道管理类")
@Slf4j
@RestController
public class ModelConfigController extends AIBaseController {


    @Autowired
    private ModelConfigManager modelConfigManager;


    @ApiOperation("获取支持的分组模型列表")
    @GetMapping("/model/group/list")
    public ResultVO<List<ModelGroupVo>> groupModel(@RequestParam(value = "template", required = false) String template) {
        List<ModelGroupVo> modelGroupVos = new ArrayList<>();
        for (ModelTemplateEnum value : ModelTemplateEnum.values()) {
            if (StringUtils.isNoneEmpty(template) && !value.getTemplate().equals(template)) {
                continue;
            }
            ModelGroupVo modelGroupVo = new ModelGroupVo();
            modelGroupVo.setTemplate(value.getTemplate());
            modelGroupVo.setDesc(value.getDesc());
            modelGroupVo.setModels(value.getModels());
            modelGroupVos.add(modelGroupVo);
        }
        return ResultVO.success(modelGroupVos);
    }


    @ApiOperation("获取支持的模型模版列表")
    @GetMapping("/model/list")
    public ResultVO<List<ModelVo>> model(@RequestParam(value = "template", required = false) String template) {
        List<ModelVo> modelVos = new ArrayList<>();
        for (ModelTemplateEnum value : ModelTemplateEnum.values()) {
            if (StringUtils.isNoneEmpty(template) && !value.getTemplate().equals(template)) {
                continue;
            }
            String modelTemplate = value.getTemplate();
            String desc = value.getDesc();
            for (String model : value.getModels()) {
                ModelVo modelVo = new ModelVo();
                modelVo.setName(String.format("%s:%s", desc, model));
                modelVo.setValue(String.format("%s:%s", modelTemplate, model));
                modelVos.add(modelVo);
            }
        }
        return ResultVO.success(modelVos);
    }



    @ApiOperation("前端的模型列表")
    @GetMapping("/model/config/c/list")
    public ResultVO<List<ModelVo>> c_model() {
        List<ModelVo> modelVos = new ArrayList<>();
        List<ModelTemplateEnum> templateEnums = Arrays.asList(ModelTemplateEnum.OPEN_AI_API_GPT,ModelTemplateEnum.GROK_API_GPT, ModelTemplateEnum.AWS_CLAUDE_API, ModelTemplateEnum.GEMINI_API, ModelTemplateEnum.COHERE_API_GPT);
        for (ModelTemplateEnum value : templateEnums) {
            String modelTemplate = value.getTemplate();
            String desc = value.getDesc();
            for (String model : value.getModels()) {
                if(model.contains("text")){
                    continue;
                }
                ModelVo modelVo = new ModelVo();
                modelVo.setName(String.format("%s:%s", desc, model));
                modelVo.setValue(String.format("%s:%s", modelTemplate, model));
                modelVos.add(modelVo);
            }
        }
        return ResultVO.success(modelVos);
    }

    @ApiOperation("获取支持的模型列表")
    @GetMapping("/model/config/list")
    public ResultVO<List<String>> model() {
        List<String> modelVos = new ArrayList<>();
        for (ModelPriceEnum value : ModelPriceEnum.values()) {
            modelVos.add(value.getModel());
        }
        return ResultVO.success(modelVos);
    }


    /**
     * 新增渠道
     *
     * @return
     */
    @ApiOperation("新增渠道")
    @VerifyUserToken(role = {RoleEnum.SYSTEM})
    @PostMapping("/model/config/add")
    public ResultVO addModelConfig(@Valid @RequestBody ModelConfigAddReq req) {
        return modelConfigManager.addModelConfig(getAccount(), req);
    }


    /**
     * 修改渠道
     *
     * @return
     */
    @ApiOperation("修改渠道")
    @VerifyUserToken(role = {RoleEnum.SYSTEM})
    @PostMapping("/model/config/update")
    public ResultVO updateModelConfig(@Valid @RequestBody ModelConfigUpdateReq req) {
        return modelConfigManager.updateModelConfig(getAccount(), req);
    }


    /**
     * 搜索 渠道
     *
     * @return
     */
    @ApiOperation("搜索渠道")
    @VerifyUserToken(role = {RoleEnum.SYSTEM})
    @PostMapping("/modelConfig/search")
    public ResultVO<PageVO<ModelConfigVo>> searchModelConfig(@RequestBody @Valid ModelConfigSearchReq req) {
        PageVO<ModelConfigVo> pageVO = modelConfigManager.searchModelConfig(req);
        return ResultVO.success(pageVO);
    }


    @ApiOperation("删除渠道")
    @VerifyUserToken(role = {RoleEnum.SYSTEM})
    @GetMapping("/modelConfig/delete")
    public ResultVO deleteModelConfig(@RequestParam("id") Long id) {
        return modelConfigManager.deleteModelConfig(id);
    }


}
