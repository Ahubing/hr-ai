package com.open.hr.ai.manager;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.*;
import com.open.ai.eros.db.mysql.hr.mapper.AmPositionMapper;
import com.open.ai.eros.db.mysql.hr.req.SearchPositionListReq;
import com.open.ai.eros.db.mysql.hr.service.impl.*;
import com.open.hr.ai.bean.req.*;
import com.open.hr.ai.bean.vo.AmPositionSectionVo;
import com.open.ai.eros.db.mysql.hr.vo.AmPositionVo;
import com.open.hr.ai.constant.PositionStatusEnums;
import com.open.hr.ai.convert.AmChatBotGreetNewConditionConvert;
import com.open.hr.ai.convert.AmPositionConvert;
import com.open.hr.ai.convert.AmPositionSetionConvert;
import com.open.hr.ai.util.CompetencyModelPromptUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 逻辑按照php处理的, 暂时未调试
 *
 * @Date 2025/1/4 23:28
 */
@Component
@Slf4j
public class ChatBotPositionManager {


    @Resource
    private AmPositionServiceImpl amPositionService;

    @Resource
    private AmPositionPostServiceImpl amPositionPostService;

    @Resource
    private AmPositionSectionServiceImpl amPositionSectionService;

    @Resource
    private MiniUniUserServiceImpl miniUniUserService;

    @Resource
    private AmNewMaskServiceImpl amNewMaskService;

    @Resource
    private AmZpLocalAccoutsServiceImpl amZpLocalAccoutsService;

    @Resource
    private CompetencyModelPromptUtil competencyModelPromptUtil;
    @Resource
    private AmZpPlatformsServiceImpl amZpPlatformsService;

    @Resource
    private AmClientTaskManager amClientTaskManager;

    @Resource
    private AmPositionMapper positionMapper;


    @Resource
    private AmChatbotGreetConditionNewServiceImpl amChatbotGreetConditionNewService;



    /**
     * 更新岗位
     *
     * @param req
     * @param adminId
     * @return
     */
    public ResultVO updatePosition(updatePositionReq req, Long adminId) {
        try {
            LambdaQueryWrapper<AmPosition> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AmPosition::getAdminId, adminId);
            queryWrapper.in(AmPosition::getId, req.getId());
            AmPosition positionServiceOne = amPositionService.getOne(queryWrapper, false);
            if (Objects.isNull(positionServiceOne)) {
                return ResultVO.fail("职位不存在");
            }
            positionServiceOne.setAmDescribe(req.getDesc());
            boolean result = amPositionService.updateById(positionServiceOne);
            return result ? ResultVO.success("更新成功") : ResultVO.fail("更新失败");
        } catch (Exception e) {
            log.error("删除失败 id={}", JSONObject.toJSONString(req), e);
        }
        return ResultVO.fail("系统异常,删除失败");
    }


    /**
     *生成岗位人才画像和岗位胜任力模型的评价标准和打分权重规则
     *
     * @param req
     * @param adminId
     * @return
     */
    public ResultVO competencyModel(Integer id, Long adminId) {
        try {
            LambdaQueryWrapper<AmPosition> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AmPosition::getAdminId, adminId);
            queryWrapper.in(AmPosition::getId, id);
            AmPosition positionServiceOne = amPositionService.getOne(queryWrapper, false);
            if (Objects.isNull(positionServiceOne)) {
                return ResultVO.fail("职位不存在");
            }
            if (StringUtils.isBlank(positionServiceOne.getAmDescribe())){
                return ResultVO.fail("岗位描述为空, 请先完善岗位描述");
            }

            String dealJobDescription = competencyModelPromptUtil.dealJobDescription(positionServiceOne.getName(), positionServiceOne.getAmDescribe());
            if (StringUtils.isBlank(dealJobDescription)){
                return ResultVO.fail("生成失败!, 请稍后重试");
            }
            positionServiceOne.setJobStandard(dealJobDescription);
            boolean result = amPositionService.updateById(positionServiceOne);
            return result ? ResultVO.success("生成成功") : ResultVO.fail("生成失败");
        } catch (Exception e) {
            log.error("更新失败 id={}", id, e);
        }
        return ResultVO.fail("系统异常,删除失败");
    }




    /**
     * 批量删除岗位
     *
     * @param ids
     * @param adminId
     * @return
     */
    public ResultVO batchDeletePosition(List<String> ids, Long adminId) {
        try {
            LambdaQueryWrapper<AmPosition> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AmPosition::getAdminId, adminId);
            queryWrapper.in(AmPosition::getId, ids);
            boolean result = amPositionService.remove(queryWrapper);
            return result ? ResultVO.success("删除成功") : ResultVO.fail("删除失败");
        } catch (Exception e) {
            log.error("删除失败 id={}", JSONObject.toJSONString(ids), e);
        }
        return ResultVO.fail("系统异常,删除失败");
    }


    /**
     * 批量关闭岗位
     *
     * @param ids
     * @param adminId
     * @return
     */
    public ResultVO batchClosePosition(List<Integer> ids, Long adminId) {
        try {
            if (CollectionUtils.isEmpty(ids)) {
                return ResultVO.fail("id不能为空");
            }
            LambdaQueryWrapper<AmPosition> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AmPosition::getAdminId, adminId);
            queryWrapper.in(AmPosition::getId, ids);
            List<AmPosition> amPositions = amPositionService.list(queryWrapper);
            LambdaUpdateWrapper<AmPosition> updateWrapper = new LambdaUpdateWrapper<>();
            // 先存
            updateWrapper.eq(AmPosition::getAdminId, adminId).in(AmPosition::getId, ids).set(AmPosition::getIsSyncing, 1);
            boolean result = amPositionService.update(updateWrapper);
            if (result) {
                for (AmPosition amPosition : amPositions) {
                    Boolean batchResult = amClientTaskManager.batchCloseOrOpenPosition(amPosition.getBossId(), amPosition, PositionStatusEnums.POSITION_CLOSE.getStatus());
                    log.info("close position bossId={}, positionId={} batchResult={}",amPosition.getBossId(),amPosition.getId(), batchResult);
                }
            }
            return result ? ResultVO.success("更新成功") : ResultVO.fail("更新失败");
        } catch (Exception e) {
            log.error("更新失败 ids={}", JSONObject.toJSONString(ids), e);
            return ResultVO.fail("系统异常,更新失败");
        }
    }


    /**
     * 批量打开岗位
     *
     * @param ids
     * @param adminId
     * @return
     */
    public ResultVO batchOpenPosition(List<Integer> ids, Long adminId) {
        try {
            if (CollectionUtils.isEmpty(ids)) {
                return ResultVO.fail("id不能为空");
            }
            LambdaQueryWrapper<AmPosition> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AmPosition::getAdminId, adminId);
            queryWrapper.in(AmPosition::getId, ids);
            List<AmPosition> amPositions = amPositionService.list(queryWrapper);
            LambdaUpdateWrapper<AmPosition> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(AmPosition::getAdminId, adminId).in(AmPosition::getId, ids).set(AmPosition::getIsSyncing, 1);;
            boolean result = amPositionService.update(updateWrapper);
            if (result) {
                for (AmPosition amPosition : amPositions) {
                    Boolean batchResult = amClientTaskManager.batchCloseOrOpenPosition(amPosition.getBossId(), amPosition, PositionStatusEnums.POSITION_OPEN.getStatus());
                    log.info("open position bossId={}, positionId={} batchResult={}",amPosition.getBossId(),amPosition.getId(), batchResult);
                }
            }
            return result ? ResultVO.success("更新成功") : ResultVO.fail("更新失败");
        } catch (Exception e) {
            log.error("更新失败 ids={}", JSONObject.toJSONString(ids), e);
            return ResultVO.fail("系统异常,更新失败");
        }
    }


    /**
     * 获取组织架构
     *
     * @param adminId
     * @return
     */
    public ResultVO<List<AmPositionSectionVo>> getStructures(Long adminId,String name) {
        try {

            List<AmPositionSectionVo> amPositionSectionVos = new ArrayList<>();

            LambdaQueryWrapper<AmPositionSection> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AmPositionSection::getAdminId, adminId);
            List<AmPositionSection> amPositionSections = amPositionSectionService.list(queryWrapper);
            List<AmPositionPost> amPositionPosts = amPositionPostService.list();

            if(StringUtils.isEmpty(name)){
                amPositionSectionVos = amPositionSections.stream().map(AmPositionSetionConvert.I::converAmPositionSectionVo).collect(Collectors.toList());
                for (AmPositionSectionVo amPositionSection : amPositionSectionVos) {
                    amPositionSection.setPost_list(amPositionPosts.stream().filter(amPositionPost ->
                            amPositionPost.getSectionId().equals(amPositionSection.getId())).collect(Collectors.toList()));
                }
                return ResultVO.success(amPositionSectionVos);
            }

            if(CollectionUtils.isNotEmpty(amPositionSections)){
                List<AmPositionSection> sections = amPositionSections.stream()
                        .filter(amPositionSection -> amPositionSection.getName().contains(name)).collect(Collectors.toList());
                List<AmPositionSectionVo> sectionVos = sections.stream()
                        .map(AmPositionSetionConvert.I::converAmPositionSectionVo).collect(Collectors.toList());
                for (AmPositionSectionVo amPositionSectionVo : sectionVos) {
                    if(CollectionUtils.isNotEmpty(amPositionPosts)){
                        amPositionSectionVo.setPost_list(amPositionPosts.stream().filter(amPositionPost ->
                                amPositionPost.getSectionId().equals(amPositionSectionVo.getId())).collect(Collectors.toList()));
                        amPositionSectionVos.add(amPositionSectionVo);
                    }
                }
            }

            if(CollectionUtils.isNotEmpty(amPositionPosts)){
                List<AmPositionPost> posts = amPositionPosts.stream().filter(amPositionPost -> amPositionPost.getName().contains(name)).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(posts)){
                    for (AmPositionPost post : posts){
                        boolean flag = false;
                        for (AmPositionSectionVo amPositionSectionVo : amPositionSectionVos){
                            if(post.getSectionId().equals(amPositionSectionVo.getId())){
                                List<AmPositionPost> postList = amPositionSectionVo.getPost_list();
                                if(CollectionUtils.isNotEmpty(postList)){
                                    postList.add(post);
                                    amPositionSectionVo.setPost_list(postList);
                                }else{
                                    List<AmPositionPost> positionPost = new ArrayList<>();
                                    positionPost.add(post);
                                    amPositionSectionVo.setPost_list(positionPost);
                                }
                                flag = true;
                            }
                        }
                        if(!flag){
                            if(CollectionUtils.isNotEmpty(amPositionSections)){
                                AmPositionSection section = amPositionSections.stream()
                                        .filter(amPositionSection -> amPositionSection.getId().equals(post.getSectionId())).findFirst().orElse(null);
                                if(section != null){
                                    AmPositionSectionVo sectionVo = AmPositionSetionConvert.I.converAmPositionSectionVo(section);
                                    sectionVo.setPost_list(Collections.singletonList(post));
                                    amPositionSectionVos.add(sectionVo);
                                }
                            }
                        }
                    }
                }
            }
            return ResultVO.success(amPositionSectionVos);
        } catch (Exception e) {
            log.error("获取失败 adminId={}", adminId, e);
            return ResultVO.fail("系统异常,部门岗位信息获取失败");
        }
    }


    /**
     * 招聘人员跟进职位
     *
     * @return
     */
    public ResultVO positionBindUid(BindPositionUidReq req) {
        try {
            AmPosition amPosition = amPositionService.getById(req.getPositionId());
            if (Objects.isNull(amPosition)) {
                return ResultVO.fail("职位不存在");
            }
            MiniUniUser miniUniUser = miniUniUserService.getById(req.getUid());
            if (Objects.isNull(miniUniUser)) {
                return ResultVO.fail("招聘用户不存在");
            }
            amPosition.setUid(req.getUid());
            boolean result = amPositionService.updateById(amPosition);
            return result ? ResultVO.success("更新成功") : ResultVO.fail("更新失败");
        } catch (Exception e) {
            log.error("更新失败 req={}", JSONObject.toJSONString(req), e);
            return ResultVO.fail("系统异常,更新失败");
        }
    }

    /**
     * 职位绑定AI助手
     *
     * @return
     */
    public ResultVO bindAiAssistant(BindAiAssistantReq req) {
        try {
            AmPosition amPosition = amPositionService.getById(req.getPositionId());
            if (Objects.isNull(amPosition)) {
                return ResultVO.fail("职位不存在");
            }

            AmNewMask amNewMask = amNewMaskService.getById(req.getAiAssistantId());
            if (Objects.isNull(amNewMask)) {
                return ResultVO.fail("ai助手不存在");
            }
            amPosition.setAiAssitantId(amNewMask.getId());
            boolean result = amPositionService.updateById(amPosition);
            return result ? ResultVO.success("职位绑定AI助手成功") : ResultVO.fail("职位绑定AI助手失败");
        } catch (Exception e) {
            log.error("职位绑定AI助手失败 req={}", JSONObject.toJSONString(req), e);
            return ResultVO.fail("系统异常,职位绑定AI助手失败");
        }
    }

    /**
     * 职位关联岗位
     *
     * @return
     */
    public ResultVO bindPost(BindPositionPostReq req) {
        try {

            AmPosition amPosition = amPositionService.getById(req.getPositionId());
            if (Objects.isNull(amPosition)) {
                return ResultVO.fail("职位不存在");
            }

            AmPositionPost amPositionPost = amPositionPostService.getById(req.getPostId());
            if (Objects.isNull(amPositionPost)) {
                return ResultVO.fail("岗位不存在");
            }
            amPosition.setPostId(req.getPostId());
            boolean result = amPositionService.updateById(amPosition);
            return result ? ResultVO.success("职位关联岗位成功") : ResultVO.fail("职位关联岗位失败");
        } catch (Exception e) {
            log.error("职位关联岗位失败 req={}", JSONObject.toJSONString(req), e);
            return ResultVO.fail("系统异常,职位关联岗位失败");
        }
    }

    /**
     * 新增/编辑-岗位
     *
     * @return
     */
    public ResultVO savePost(AddPositionPostReq req) {
        try {
            if (Objects.nonNull(req.getId())) {
                AmPositionPost amPositionPost = amPositionPostService.getById(req.getId());
                if (Objects.isNull(amPositionPost)) {
                    return ResultVO.fail("岗位不存在");
                }
                amPositionPost.setSectionId(req.getSection_id());
                amPositionPost.setName(req.getName());
                boolean result = amPositionPostService.updateById(amPositionPost);
                return result ? ResultVO.success("编辑-岗位成功") : ResultVO.fail("编辑-岗位失败");
            }
            LambdaQueryWrapper<AmPositionPost> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AmPositionPost::getSectionId, req.getSection_id());
            queryWrapper.eq(AmPositionPost::getName, req.getName());
            AmPositionPost amPositionPost = amPositionPostService.getOne(queryWrapper, false);
            if (Objects.nonNull(amPositionPost)) {
                return ResultVO.fail("岗位已经存在");
            }
            AmPositionPost newAmPosition = new AmPositionPost();
            newAmPosition.setName(req.getName());
            newAmPosition.setSectionId(req.getSection_id());
            boolean result = amPositionPostService.save(newAmPosition);
            return result ? ResultVO.success("新增-岗位成功") : ResultVO.fail("新增-岗位失败");
        } catch (Exception e) {
            log.error(" 新增/编辑-岗位失败 req={}", JSONObject.toJSONString(req), e);
            return ResultVO.fail("系统异常, 新增/编辑-岗位失败");
        }
    }


    /**
     * 获取岗位列表
     *
     * @return
     */
    public ResultVO<List<AmPositionPost>> getPostList(Integer sectionId) {
        try {
            LambdaQueryWrapper<AmPositionPost> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AmPositionPost::getSectionId, sectionId);
            List<AmPositionPost> amPositionPosts = amPositionPostService.list(queryWrapper);
            return ResultVO.success(amPositionPosts);
        } catch (Exception e) {
            log.error("获取岗位列表失败 sectionId={}", sectionId, e);
            return ResultVO.fail("系统异常, 获取岗位列表失败");
        }
    }

    /**
     * 获取部门列表
     *
     * @return
     */
    public ResultVO<List<AmPositionSection>> getSectionList(Long adminId, String deptName) {
        try {
            if (Objects.isNull(adminId)) {
                return ResultVO.fail("adminId不能为空");
            }
            LambdaQueryWrapper<AmPositionSection> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AmPositionSection::getAdminId, adminId)
                        .like(StringUtils.isNotEmpty(deptName), AmPositionSection::getName, deptName);
            List<AmPositionSection> amPositionSections = amPositionSectionService.list(queryWrapper);
            return ResultVO.success(amPositionSections);
        } catch (Exception e) {
            log.error("获取部门列表失败 sectionId={}", adminId, e);
            return ResultVO.fail("系统异常, 获取部门列表失败");
        }
    }


    /**
     * 新增/编辑-部门
     *
     * @return
     */
    public ResultVO editSection(AddOrUpdateSectionReq req, Long adminId) {
        try {
            if (Objects.nonNull(req.getId())) {
                AmPositionSection section = amPositionSectionService.getById(req.getId());
                if (Objects.isNull(section)) {
                    return ResultVO.fail("部门不存在, 请先去建立部门");
                }
                section.setName(req.getName());
                boolean result = amPositionSectionService.updateById(section);
                return result ? ResultVO.success("编辑-部门成功") : ResultVO.fail("编辑-部门失败");
            }
            AmPositionSection amPositionSection = new AmPositionSection();
            amPositionSection.setName(req.getName());
            amPositionSection.setAdminId(adminId);
            boolean result = amPositionSectionService.save(amPositionSection);
            return result ? ResultVO.success("新增-部门成功") : ResultVO.fail("新增-部门失败");
        } catch (Exception e) {
            log.error(" 新增/编辑-部门失败 req={}", JSONObject.toJSONString(req), e);
            return ResultVO.fail("系统异常, 新增/编辑-部门失败");
        }
    }

    /**
     * 查询职位详情, 暂时用bean去返回
     *
     * @return
     */
    public ResultVO<AmPositionVo> getPositionDetail(Integer id) {
        try {
            AmPosition amPosition = amPositionService.getById(id);
            if (Objects.isNull(amPosition)) {
                return ResultVO.fail("职位不存在");
            }
            AmPositionVo amPositionVo = AmPositionConvert.I.converAmPositionVo(amPosition);
//            AmPositionSection section = amPositionSectionService.getById(amPosition.getSectionId());
//            if (Objects.isNull(section)) {
//                return ResultVO.fail("部门不存在, 请先去建立部门");
//            }
//            MiniUniUser miniUniUser = miniUniUserService.getById(amPosition.getUid());
//            if (Objects.isNull(miniUniUser)) {
//                return ResultVO.fail("招聘用户不存在");
//            }
            AmZpLocalAccouts amZpLocalAccouts = amZpLocalAccoutsService.getById(amPosition.getBossId());
            if (Objects.isNull(amZpLocalAccouts)) {
                return ResultVO.fail("boss账号不存在");
            }

            AmZpPlatforms platforms = amZpPlatformsService.getById(amPosition.getChannel());
            if(Objects.isNull(platforms)){
                return ResultVO.fail("平台不存在");
            }

            AmPositionPost positionPost = amPositionPostService.getById(amPosition.getPostId());
            if(Objects.isNull(positionPost)){
                return ResultVO.fail("岗位不存在");
            }

            AmPositionSection section = amPositionSectionService.getById(positionPost.getSectionId());
            if(Objects.isNull(section)){
                return ResultVO.fail("部门不存在");
            }

//            amPositionVo.setSection(section.getName());
            amPositionVo.setPostName(positionPost.getName());
            amPositionVo.setSectionName(section.getName());
            amPositionVo.setSectionId(section.getId());
            amPositionVo.setDetail(JSONObject.parseObject(amPosition.getExtendParams()));
            amPositionVo.setUserName("");
            amPositionVo.setChannelName(platforms.getName());
            amPositionVo.setBossAccount(amZpLocalAccouts.getAccount());
            amPositionVo.setIsDeleted(amPosition.getIsDeleted());
            amPositionVo.setIsOpen(amPosition.getIsOpen());
            amPositionVo.setStatus(amPosition.getStatus());
            return ResultVO.success(amPositionVo);
        } catch (Exception e) {
            log.error("查询职位详情异常 id={}", id, e);
            return ResultVO.fail("系统异常, 新增/编辑-部门失败");
        }
    }

    /**
     * todo 根据php 代码,获取职位列表
     *
     * @return
     */
    public ResultVO<PageVO<AmPositionVo>> getPositionList(SearchPositionListReq req, Long adminId) {
        try {
            req.setAdminId(adminId);
            LambdaQueryWrapper<AmZpLocalAccouts> accoutsQueryWrapper = new LambdaQueryWrapper<>();
            accoutsQueryWrapper.eq(AmZpLocalAccouts::getAdminId, adminId);
            accoutsQueryWrapper.eq(AmZpLocalAccouts::getStatus, 1);
            List<AmZpLocalAccouts> localAccouts = amZpLocalAccoutsService.list(accoutsQueryWrapper);

            List<AmZpPlatforms> amZpPlatforms = amZpPlatformsService.list();

            Page<AmPositionVo> page = new Page<>(req.getPage(), req.getSize());
            IPage<AmPositionVo> iPage = positionMapper.pagePosition(page,req);

            List<AmPositionVo> amPositionVos = iPage.getRecords();
            for (AmPositionVo amPositionVo : amPositionVos) {
                amPositionVo.setExtendParams(JSONObject.parseObject(amPositionVo.getExtendParamsStr()));
                amPositionVo.setJobStandard(JSONObject.parseObject(amPositionVo.getJobStandardStr()));
//                MiniUniUser miniUniUser = miniUniUserService.getById(amPositionVo.getUid());
//                if (Objects.isNull(miniUniUser)) {
//                    continue;
//                }
                AmChatbotGreetConditionNew conditionNewServiceOne = amChatbotGreetConditionNewService.getOne(new LambdaQueryWrapper<AmChatbotGreetConditionNew>().eq(AmChatbotGreetConditionNew::getPositionId, amPositionVo.getId()), false);
                if (Objects.nonNull(conditionNewServiceOne)) {
                    amPositionVo.setConditionVo(AmChatBotGreetNewConditionConvert.I.convertGreetConditionVo(conditionNewServiceOne));
                }

                String name = "";
                if ( amPositionVo.getAiAssitantId() == 0L) {
                    amPositionVo.setAiAssitantId(null);
                }
                amPositionVo.setUserName(StringUtils.isNotBlank(name) ? name : "");
                amPositionVo.setChannelName("");
                amPositionVo.setBossAccount("");
                amPositionVo.setDetail(amPositionVo.getExtendParams());
                for (AmZpLocalAccouts localAccout : localAccouts) {
                    if (Objects.equals(localAccout.getId(), amPositionVo.getBossId())) {
                        amPositionVo.setBossAccount(localAccout.getAccount());
                    }
                }
                for (AmZpPlatforms amZpPlatform : amZpPlatforms) {
                    if (Objects.equals(amZpPlatform.getId(), amPositionVo.getChannel())) {
                        amPositionVo.setChannelName(amZpPlatform.getName());
                    }
                }
            }
            return ResultVO.success(PageVO.build(iPage.getTotal(), amPositionVos));

        } catch (Exception e) {
            log.error("查询职位详情异常 req={}", JSONObject.toJSONString(req), e);
            return ResultVO.fail("系统异常, 获取成功失败");
        }
    }


}
