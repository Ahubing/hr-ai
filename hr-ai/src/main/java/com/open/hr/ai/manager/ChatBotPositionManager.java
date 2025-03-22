package com.open.hr.ai.manager;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.*;
import com.open.ai.eros.db.mysql.hr.service.impl.*;
import com.open.hr.ai.bean.req.*;
import com.open.hr.ai.bean.vo.AmPositionSectionVo;
import com.open.hr.ai.bean.vo.AmPositionVo;
import com.open.hr.ai.constant.PositionStatusEnums;
import com.open.hr.ai.convert.AmChatBotGreetConditionConvert;
import com.open.hr.ai.convert.AmChatBotGreetNewConditionConvert;
import com.open.hr.ai.convert.AmPositionConvert;
import com.open.hr.ai.convert.AmPositionSetionConvert;
import com.open.hr.ai.util.CompetencyModelPromptUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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
    public ResultVO<List<AmPositionSectionVo>> getStructures(Long adminId,String name,String positionPostName) {
        try {
            LambdaQueryWrapper<AmPositionSection> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AmPositionSection::getAdminId, adminId);
            if (StringUtils.isNotBlank(name)) {
                queryWrapper.like(AmPositionSection::getName, name);
            }
            List<AmPositionSection> amPositionSections = amPositionSectionService.list(queryWrapper);
            List<AmPositionSectionVo> amPositionSectionVos = amPositionSections.stream().map(AmPositionSetionConvert.I::converAmPositionSectionVo).collect(Collectors.toList());
            for (AmPositionSectionVo amPositionSectionVo : amPositionSectionVos) {
                LambdaQueryWrapper<AmPositionPost> lambdaQueryWrapper = new QueryWrapper<AmPositionPost>().lambda();
                lambdaQueryWrapper.eq(AmPositionPost::getSectionId, amPositionSectionVo.getId())
                                  .like(StringUtils.isNotEmpty(positionPostName),AmPositionPost::getName, positionPostName);
                List<AmPositionPost> amPositionPosts = amPositionPostService.list(lambdaQueryWrapper);
                amPositionSectionVo.setPost_list(amPositionPosts);
            }
            if(StringUtils.isNotEmpty(positionPostName) && CollectionUtils.isNotEmpty(amPositionSectionVos)){
                List<AmPositionSectionVo> sectionVos = amPositionSectionVos.stream()
                        .filter(vo -> CollectionUtils.isNotEmpty(vo.getPost_list())).collect(Collectors.toList());
                return ResultVO.success(sectionVos);
            }
            return ResultVO.success(amPositionSectionVos);
        } catch (Exception e) {
            log.error("获取失败 adminId={}", adminId, e);
            return ResultVO.fail("系统异常,更新失败");
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
    public ResultVO<List<AmPositionSection>> getSectionList(Long adminId) {
        try {
            if (Objects.isNull(adminId)) {
                return ResultVO.fail("adminId不能为空");
            }
            LambdaQueryWrapper<AmPositionSection> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AmPositionSection::getAdminId, adminId);
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
            AmPositionSection section = amPositionSectionService.getById(amPosition.getSectionId());
            if (Objects.isNull(section)) {
                return ResultVO.fail("部门不存在, 请先去建立部门");
            }
            MiniUniUser miniUniUser = miniUniUserService.getById(amPosition.getUid());
            if (Objects.isNull(miniUniUser)) {
                return ResultVO.fail("招聘用户不存在");
            }
            AmZpLocalAccouts amZpLocalAccouts = amZpLocalAccoutsService.getById(amPosition.getBossId());
            if (Objects.isNull(amZpLocalAccouts)) {
                return ResultVO.fail("boss账号不存在");
            }
            amPositionVo.setSection(section.getName());
            amPositionVo.setDetail(JSONObject.parseObject(amPosition.getExtendParams()));
            amPositionVo.setUserName(miniUniUser.getName());
            amPositionVo.setChannelName("BOSS直聘");
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

            LambdaQueryWrapper<AmPositionSection> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AmPositionSection::getAdminId, adminId);
            AmPositionSection serviceOne = amPositionSectionService.getOne(queryWrapper, false);
            if (Objects.isNull(serviceOne)) {
                return ResultVO.fail("部门不存在, 请先去建立部门");
            }
            LambdaQueryWrapper<AmPosition> amPositionQueryWrapper = new LambdaQueryWrapper<>();
            amPositionQueryWrapper.eq(AmPosition::getAdminId, adminId);
            // 查询未删除的数据
            amPositionQueryWrapper.eq(AmPosition::getIsDeleted, 0);

            if (Objects.nonNull(req.getSectionId())) {
                amPositionQueryWrapper.eq(AmPosition::getSectionId, req.getSectionId());
            }
            if (Objects.nonNull(req.getStatus())) {
                amPositionQueryWrapper.like(AmPosition::getStatus, req.getStatus());
            }
            if (Objects.nonNull(req.getIsOpen())) {
                amPositionQueryWrapper.like(AmPosition::getIsOpen, req.getIsOpen());
            }
            if (Objects.nonNull(req.getUid())) {
                amPositionQueryWrapper.like(AmPosition::getUid, req.getUid());
            }
            if (Objects.nonNull(req.getChannel())) {
                amPositionQueryWrapper.like(AmPosition::getChannel, req.getChannel());
            }
            if (Objects.nonNull(req.getSectionId())) {
                amPositionQueryWrapper.like(AmPosition::getSectionId, req.getSectionId());
            }
            if (Objects.nonNull(req.getPositionId())) {
                amPositionQueryWrapper.like(AmPosition::getPostId, req.getPositionId());
            }

            if(StringUtils.isNotEmpty(req.getPositionName())){
                amPositionQueryWrapper.like(AmPosition::getName, req.getPositionName());
            }

            if (Objects.nonNull(req.getAccountId())) {
                amPositionQueryWrapper.like(AmPosition::getBossId, req.getAccountId());
            } else {
                LambdaQueryWrapper<AmZpLocalAccouts> accoutsQueryWrapper = new LambdaQueryWrapper<>();
                accoutsQueryWrapper.eq(AmZpLocalAccouts::getAdminId, adminId);
                List<AmZpLocalAccouts> localAccouts = amZpLocalAccoutsService.list(accoutsQueryWrapper);
                List<String> bossIds = localAccouts.stream().map(AmZpLocalAccouts::getId).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(bossIds)) {
                    amPositionQueryWrapper.in(AmPosition::getBossId, bossIds);
                }
            }

//            LambdaQueryWrapper<MiniUniUser> miniUniUserQueryWrapper = new LambdaQueryWrapper<>();
//            miniUniUserQueryWrapper.eq(MiniUniUser::getAdminId, adminId);
//            List<MiniUniUser> miniUniUsers = miniUniUserService.list(miniUniUserQueryWrapper);


            LambdaQueryWrapper<AmZpLocalAccouts> accoutsQueryWrapper = new LambdaQueryWrapper<>();
            accoutsQueryWrapper.eq(AmZpLocalAccouts::getAdminId, adminId);
            List<AmZpLocalAccouts> localAccouts = amZpLocalAccoutsService.list(accoutsQueryWrapper);


            LambdaQueryWrapper<AmZpPlatforms> platformsQueryWrapper = new LambdaQueryWrapper<>();
            List<AmZpPlatforms> amZpPlatforms = amZpPlatformsService.list(platformsQueryWrapper);


            LambdaQueryWrapper<AmPositionSection> sectionQueryWrapper = new LambdaQueryWrapper<>();
            sectionQueryWrapper.eq(AmPositionSection::getAdminId, adminId);
            List<AmPositionSection> amPositionSections = amPositionSectionService.list(sectionQueryWrapper);

//            LambdaQueryWrapper<AmPosition> positionQueryWrapper = new LambdaQueryWrapper<>();
//            positionQueryWrapper.eq(AmPosition::getAdminId, adminId);
//            positionQueryWrapper.eq(AmPosition::getIsDeleted, 0);
//            List<AmPosition> amPositions = amPositionService.list(positionQueryWrapper);
//
//            LambdaQueryWrapper<AmNewMask> rolesQueryWrapper = new LambdaQueryWrapper<>();
//            rolesQueryWrapper.eq(AmNewMask::getAdminId, adminId);
//            List<AmNewMask> amNewMasks = amNewMaskService.list(rolesQueryWrapper);

            Page<AmPosition> page = new Page<>(req.getPage(), req.getSize());
            Page<AmPosition> amPositionPage = amPositionService.page(page, amPositionQueryWrapper);
            List<AmPositionVo> amPositionVos = amPositionPage.getRecords().stream().map(AmPositionConvert.I::converAmPositionVo).collect(Collectors.toList());
            for (AmPositionVo amPositionVo : amPositionVos) {
                amPositionVo.setSection("");
                MiniUniUser miniUniUser = miniUniUserService.getById(amPositionVo.getUid());
                if (Objects.isNull(miniUniUser)) {
                    continue;
                }
                AmChatbotGreetConditionNew conditionNewServiceOne = amChatbotGreetConditionNewService.getOne(new LambdaQueryWrapper<AmChatbotGreetConditionNew>().eq(AmChatbotGreetConditionNew::getPositionId, amPositionVo.getId()), false);
                if (Objects.nonNull(conditionNewServiceOne)) {
                    amPositionVo.setConditionVo(AmChatBotGreetNewConditionConvert.I.convertGreetConditionVo(conditionNewServiceOne));
                }

                String name = miniUniUser.getName();
                if ( amPositionVo.getAiAssitantId() == 0L) {
                    amPositionVo.setAiAssitantId(null);
                }
                amPositionVo.setUserName(StringUtils.isNotBlank(name) ? name : "");
                amPositionVo.setChannelName("");
                amPositionVo.setBossAccount("");
                amPositionVo.setDetail(amPositionVo.getExtendParams());
                for (AmPositionSection amPositionSection : amPositionSections) {
                    if (Objects.equals(amPositionSection.getId(), amPositionVo.getSectionId())) {
                        amPositionVo.setSection(amPositionSection.getName());
                    }
                }
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

//            int total = amPositionService.list(amPositionQueryWrapper).size();
//            jsonObject.put("total", amPositionPage.getTotal());
//            jsonObject.put("current_page", req.getPage());
//            jsonObject.put("size", req.getSize());
//            jsonObject.put("recruiters", miniUniUsers);
//            jsonObject.put("accouts", localAccouts);
//            jsonObject.put("platforms", amZpPlatforms);
//            jsonObject.put("sections", amPositionSections);
//            jsonObject.put("positions", amPositionVos);
//            jsonObject.put("ais", amSquareRoles);
            return ResultVO.success(PageVO.build(amPositionPage.getTotal(), amPositionVos));

        } catch (Exception e) {
            log.error("查询职位详情异常 req={}", JSONObject.toJSONString(req), e);
            return ResultVO.fail("系统异常, 获取成功失败");
        }
    }


}
