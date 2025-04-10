package com.open.ai.eros.knowledge.controller;


import com.open.ai.eros.ai.bean.vo.ModelVo;
import com.open.ai.eros.ai.constatns.ModelTemplateEnum;
import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.knowledge.bean.req.DocsSliceAddReq;
import com.open.ai.eros.knowledge.bean.req.DocsSliceDeleteReq;
import com.open.ai.eros.knowledge.bean.req.DocsSliceSearchReq;
import com.open.ai.eros.knowledge.bean.req.DocsSliceUpdateReq;
import com.open.ai.eros.knowledge.bean.vo.DocsSliceVo;
import com.open.ai.eros.knowledge.config.KnowledgeBaseController;
import com.open.ai.eros.knowledge.manager.DocsSliceManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 文档切片表 前端控制器
 * </p>
 *
 * @author Eros-AI
 * @since 2024-09-12
 */
@Api(tags = "文档切片控制类")
@RestController
public class DocsSliceController extends KnowledgeBaseController {


    @Autowired
    private DocsSliceManager docsSliceManager;

    @VerifyUserToken(role = {RoleEnum.CREATOR,RoleEnum.SYSTEM})
    @ApiOperation("获取支持的向量模型列表")
    @GetMapping("/docs/slice/model/list")
    public ResultVO<List<ModelVo>> model(@RequestParam(value = "template" ,required = false) String template){
        List<ModelVo> modelVos = new ArrayList<>();
        for (ModelTemplateEnum value : ModelTemplateEnum.values()) {
            if(StringUtils.isNoneEmpty(template) && !value.getTemplate().equals(template)){
                continue;
            }
            String modelTemplate = value.getTemplate();
            String desc = value.getDesc();
            for (String model : value.getModels()) {
                ModelVo modelVo = new ModelVo();
                modelVo.setName(String.format("%s:%s",desc,model));
                modelVo.setValue(String.format("%s:%s",modelTemplate,model));
                modelVos.add(modelVo);
            }
        }
        return ResultVO.success(modelVos);
    }


    @GetMapping("/docs/slice/page")
    @VerifyUserToken(role = {RoleEnum.CREATOR,RoleEnum.SYSTEM})
    @ApiOperation("查询文档切片")
    public ResultVO<PageVO<DocsSliceVo>> list(@Valid DocsSliceSearchReq req){
        if(!RoleEnum.SYSTEM.getRole().equals(getRole())){
            req.setUserId(getUserId());
        }
        return docsSliceManager.list(req);
    }



    @PostMapping("/docs/slice/delete")
    @VerifyUserToken(role = {RoleEnum.CREATOR,RoleEnum.SYSTEM})
    @ApiOperation("删除文档切片")
    public ResultVO deleteDocsSlice(@RequestBody @Valid DocsSliceDeleteReq req){
        Long docsId = req.getDocsId();
        Long id = req.getId();
        if( docsId==null && id == null ){
            return ResultVO.fail("删除参数为空");
        }
        return docsSliceManager.deleteDocsSlice(getUserId(),req,getRole());
    }



    @PostMapping("/docs/slice/add")
    @VerifyUserToken(role = {RoleEnum.CREATOR,RoleEnum.SYSTEM})
    @ApiOperation("新增文档切片")
    public ResultVO addDocsSlice(@RequestBody @Valid DocsSliceAddReq req){
        return docsSliceManager.addDocsSlice(getUserId(),req);
    }



    @PostMapping("/docs/slice/update")
    @VerifyUserToken(role = {RoleEnum.CREATOR,RoleEnum.SYSTEM})
    @ApiOperation("修改文档切片")
    public ResultVO updateDocsSlice(@RequestBody @Valid DocsSliceUpdateReq req){
        return docsSliceManager.updateDocsSlice(getUserId(),req,getRole());
    }






}

