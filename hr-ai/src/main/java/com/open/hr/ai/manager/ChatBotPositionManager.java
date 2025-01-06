package com.open.hr.ai.manager;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.*;
import com.open.ai.eros.db.mysql.hr.service.impl.*;
import com.open.hr.ai.bean.req.*;
import com.open.hr.ai.bean.vo.AmChatbotOptionsVo;
import com.open.hr.ai.bean.vo.AmPositionVo;
import com.open.hr.ai.convert.AmChatBotOptionConvert;
import com.open.hr.ai.convert.AmPositionConvert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 逻辑按照php处理的, 暂时未调试
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
    private AmSquareRolesServiceImpl amSquareRolesService;

    @Resource
    private AmZpLocalAccoutsServiceImpl amZpLocalAccoutsService;

    @Resource
    private AmZpPlatformsServiceImpl amZpPlatformsService;


    /**
     * 批量删除岗位
     *
     * @param ids
     * @param adminId
     * @return
     */
    public ResultVO batchDeletePosition(List<String> ids, Long adminId) {
        try {
            QueryWrapper<AmPosition> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("admin_id", adminId);
            queryWrapper.in("id", ids);
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
    public ResultVO batchClosePosition(List<String> ids, Long adminId) {
        try {
            if (CollectionUtils.isEmpty(ids)) {
                return ResultVO.fail("id不能为空");
            }
            QueryWrapper<AmPosition> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("admin_id", adminId);
            queryWrapper.in("id", ids);
            AmPosition amPosition = new AmPosition();
            amPosition.setIsOpen(0);
            boolean result = amPositionService.update(amPosition, queryWrapper);
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
    public ResultVO batchOpenPosition(List<String> ids, Long adminId) {
        try {
            if (CollectionUtils.isEmpty(ids)) {
                return ResultVO.fail("id不能为空");
            }
            QueryWrapper<AmPosition> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("admin_id", adminId);
            queryWrapper.in("id", ids);
            AmPosition amPosition = new AmPosition();
            amPosition.setIsOpen(1);
            boolean result = amPositionService.update(amPosition, queryWrapper);
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
    public ResultVO getStructures(Long adminId) {
        try {
            QueryWrapper<AmPosition> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("admin_id", adminId);
            List<AmPosition> amPositions = amPositionService.list(queryWrapper);
            List<AmPositionVo> amPositionVos = amPositions.stream().map(AmPositionConvert.I::converAmPositionVo).collect(Collectors.toList());
            for (AmPositionVo amPosition : amPositionVos) {
                QueryWrapper<AmPositionPost> amPositionPostQueryWrapper = new QueryWrapper<>();
                queryWrapper.in("section_id", amPosition.getId());
                List<AmPositionPost> amPositionPosts = amPositionPostService.list(amPositionPostQueryWrapper);
                amPosition.setAmPositionPost(amPositionPosts);
            }
            return ResultVO.success(amPositionVos);
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
            QueryWrapper<AmPosition> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("position_id", req.getPositionId());
            AmPosition amPosition = amPositionService.getOne(queryWrapper, false);
            if (Objects.isNull(amPosition)) {
                return ResultVO.fail("职位不存在");
            }
            MiniUniUser miniUniUser = miniUniUserService.getById(req.getUid());
            if (Objects.isNull(miniUniUser)) {
                return ResultVO.fail("ai助手不存在");
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
            QueryWrapper<AmPosition> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("position_id", req.getPositionId());
            AmPosition amPosition = amPositionService.getOne(queryWrapper, false);
            if (Objects.isNull(amPosition)) {
                return ResultVO.fail("职位不存在");
            }

            AmSquareRoles amSquareRoles = amSquareRolesService.getById(req.getAiAssistantId());
            if (Objects.isNull(amSquareRoles)) {
                return ResultVO.fail("ai助手不存在");
            }
            amPosition.setAiAssitantId(req.getAiAssistantId());
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
            QueryWrapper<AmPosition> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("position_id", req.getPositionId());
            AmPosition amPosition = amPositionService.getOne(queryWrapper, false);
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
    public ResultVO savePost(AddPositionReq req) {
        try {
            if (Objects.nonNull(req.getId())) {
                AmPositionPost amPositionPost = amPositionPostService.getById(req.getId());
                if (Objects.isNull(amPositionPost)) {
                    return ResultVO.fail("岗位不存在");
                }
                amPositionPost.setSectionId(req.getSectionId());
                amPositionPost.setName(req.getName());
                boolean result = amPositionPostService.updateById(amPositionPost);
                return result ? ResultVO.success("编辑-岗位成功") : ResultVO.fail("编辑-岗位失败");
            }
            QueryWrapper<AmPositionPost> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("section_id", req.getSectionId());
            queryWrapper.eq("name", req.getName());
            AmPositionPost amPositionPost = amPositionPostService.getOne(queryWrapper, false);
            if (Objects.nonNull(amPositionPost)) {
                return ResultVO.fail("岗位已经存在");
            }
            AmPositionPost newAmPosition = new AmPositionPost();
            newAmPosition.setName(req.getName());
            newAmPosition.setSectionId(req.getSectionId());
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
    public ResultVO getPostList(Integer sectionId) {
        try {
            QueryWrapper<AmPositionPost> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("section_id", sectionId);
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
    public ResultVO getSectionList(Long adminId) {
        try {
            if (Objects.isNull(adminId)) {
                return ResultVO.fail("adminId不能为空");
            }
            QueryWrapper<AmPositionSection> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("admin_id", adminId);
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
    public ResultVO editSection(AddOrUpdateSectionReq req) {
        try {
            if (Objects.nonNull(req.getId())) {
                AmPositionSection section = amPositionSectionService.getById(req.getId());
                if (Objects.isNull(section)) {
                    return ResultVO.fail("部门不存在");
                }
                section.setName(req.getName());
                boolean result = amPositionSectionService.updateById(section);
                return result ? ResultVO.success("编辑-部门成功") : ResultVO.fail("编辑-部门失败");
            }
            AmPositionSection amPositionSection = new AmPositionSection();
            amPositionSection.setName(req.getName());
            amPositionSection.setAdminId(req.getAdminId());
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
    public ResultVO getPositionDetail(Integer id) {
        try {
            AmPosition amPosition = amPositionService.getById(id);
            if (Objects.isNull(amPosition)) {
                return ResultVO.fail("职位不存在");
            }
            AmPositionVo amPositionVo = AmPositionConvert.I.converAmPositionVo(amPosition);
            AmPositionSection section = amPositionSectionService.getById(amPosition.getSectionId());
            MiniUniUser miniUniUser = miniUniUserService.getById(amPosition.getUid());
            AmZpLocalAccouts amZpLocalAccouts = amZpLocalAccoutsService.getById(amPosition.getBossId());
            amPositionVo.setSection(section.getName());
            amPositionVo.setUserName(miniUniUser.getName());
            amPositionVo.setChannelName("BOSS直聘");
            amPositionVo.setBossAccount(amZpLocalAccouts.getAccount());
            amPositionVo.setAiAssistant("");
            amPositionVo.setDetail(JSONObject.parseObject(amPositionVo.getExtendParams()));
            return ResultVO.success(amPositionVo);
        } catch (Exception e) {
            log.error("查询职位详情异常 id={}", id, e);
            return ResultVO.fail("系统异常, 新增/编辑-部门失败");
        }
    }

    /**
     * todo 根据php 代码,获取职位列表 先用json 处理
     *
     * @return
     */
    public ResultVO getPositionList(SearchPositionListReq req) {
        try {
            JSONObject jsonObject = new JSONObject();

            QueryWrapper<AmPositionSection> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("admin_id", req.getAdminId());
            AmPositionSection serviceOne = amPositionSectionService.getOne(queryWrapper, false);
            if (Objects.isNull(serviceOne)) {
                return ResultVO.fail("部门不存在");
            }
            QueryWrapper<AmPosition> amPositionQueryWrapper = new QueryWrapper<>();
            amPositionQueryWrapper.eq("admin_id", req.getAdminId());

            if (Objects.nonNull(req.getSectionId())) {
                amPositionQueryWrapper.eq("section_id", req.getSectionId());
            }
            if (Objects.nonNull(req.getStatus())) {
                amPositionQueryWrapper.like("status", req.getStatus());
            }
            if (Objects.nonNull(req.getIsOpen())) {
                amPositionQueryWrapper.like("is_open", req.getIsOpen());
            }
            if (Objects.nonNull(req.getUid())) {
                amPositionQueryWrapper.like("uid", req.getUid());
            }
            if (Objects.nonNull(req.getChannel())) {
                amPositionQueryWrapper.like("channel", req.getChannel());
            }
            if (Objects.nonNull(req.getSectionId())) {
                amPositionQueryWrapper.like("section_id", req.getSectionId());
            }
            if (Objects.nonNull(req.getPositionId())) {
                amPositionQueryWrapper.like("position_id", req.getPositionId());
            }
            if (Objects.nonNull(req.getAccountId())) {
                amPositionQueryWrapper.like("boss_id", req.getAccountId());
            } else {
                QueryWrapper<AmZpLocalAccouts> accoutsQueryWrapper = new QueryWrapper<>();
                accoutsQueryWrapper.eq("admin_id", req.getAdminId());
                List<AmZpLocalAccouts> localAccouts = amZpLocalAccoutsService.list(accoutsQueryWrapper);
                List<String> bossIds = localAccouts.stream().map(AmZpLocalAccouts::getId).collect(Collectors.toList());
                amPositionQueryWrapper.in("boss_id", bossIds);
            }

            QueryWrapper<MiniUniUser> miniUniUserQueryWrapper = new QueryWrapper<>();
            miniUniUserQueryWrapper.eq("admin_id", req.getAdminId());
            List<MiniUniUser> miniUniUsers = miniUniUserService.list(miniUniUserQueryWrapper);


            QueryWrapper<AmZpLocalAccouts> accoutsQueryWrapper = new QueryWrapper<>();
            accoutsQueryWrapper.eq("admin_id", req.getAdminId());
            List<AmZpLocalAccouts> localAccouts = amZpLocalAccoutsService.list(accoutsQueryWrapper);


            QueryWrapper<AmZpPlatforms> platformsQueryWrapper = new QueryWrapper<>();
            platformsQueryWrapper.gt("id", 0);
            List<AmZpPlatforms> amZpPlatforms = amZpPlatformsService.list(platformsQueryWrapper);


            QueryWrapper<AmPositionSection> sectionQueryWrapper = new QueryWrapper<>();
            sectionQueryWrapper.eq("admin_id", req.getAdminId());
            List<AmPositionSection> amPositionSections = amPositionSectionService.list(sectionQueryWrapper);

            QueryWrapper<AmPosition> positionQueryWrapper = new QueryWrapper<>();
            positionQueryWrapper.eq("admin_id", req.getAdminId());
            List<AmPosition> amPositions = amPositionService.list(positionQueryWrapper);

            QueryWrapper<AmSquareRoles> rolesQueryWrapper = new QueryWrapper<>();
            rolesQueryWrapper.eq("admin_id", req.getAdminId());
            List<AmSquareRoles> amSquareRoles = amSquareRolesService.list(rolesQueryWrapper);

            Page<AmPosition> page = new Page<>(req.getPage(), req.getSize());
            Page<AmPosition> amPositionPage = amPositionService.page(page, amPositionQueryWrapper);
            List<AmPositionVo> amPositionVos = amPositionPage.getRecords().stream().map(AmPositionConvert.I::converAmPositionVo).collect(Collectors.toList());
            for (AmPositionVo amPositionVo : amPositionVos) {
                amPositionVo.setSection("");
                String name = miniUniUserService.getById(amPositionVo.getUid()).getName();
                amPositionVo.setUserName(StringUtils.isNotBlank(name) ? name : "");
                amPositionVo.setChannelName("");
                amPositionVo.setBossAccount("");
                amPositionVo.setAiAssistant("");
                amPositionVo.setDetail(JSONObject.parseObject(amPositionVo.getExtendParams()));
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

            int total = amPositionService.list(amPositionQueryWrapper).size();
            jsonObject.put("total", total);
            jsonObject.put("current_page", amPositionVos);
            jsonObject.put("size", req.getSize());
            jsonObject.put("recruiters", miniUniUsers);
            jsonObject.put("accouts", localAccouts);
            jsonObject.put("platforms", amZpPlatforms);
            jsonObject.put("sections", amPositionSections);
            jsonObject.put("positions", amPositions);
            jsonObject.put("ais", amSquareRoles);
            return ResultVO.success(jsonObject);
        } catch (Exception e) {
            log.error("查询职位详情异常 req={}", JSONObject.toJSONString(req), e);
            return ResultVO.fail("系统异常, 获取成功失败");
        }
    }


}
