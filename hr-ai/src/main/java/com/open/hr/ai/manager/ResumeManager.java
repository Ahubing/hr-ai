package com.open.hr.ai.manager;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.ai.eros.ai.manager.CommonAIManager;
import com.open.ai.eros.common.util.AIJsonUtil;
import com.open.ai.eros.common.vo.ChatMessage;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.*;
import com.open.ai.eros.db.mysql.hr.mapper.AmResumeMapper;
import com.open.ai.eros.db.mysql.hr.service.impl.*;
import com.open.hr.ai.bean.req.AddAmResumeParseReq;
import com.open.hr.ai.bean.req.AmUploadResumeSearchReq;
import com.open.hr.ai.bean.req.SearchAmResumeReq;
import com.open.hr.ai.bean.req.UploadAmResumeUpdateReq;
import com.open.hr.ai.bean.vo.AmPositionSectionVo;
import com.open.hr.ai.bean.vo.AmResumeCountDataVo;
import com.open.ai.eros.db.mysql.hr.vo.AmResumeVo;
import com.open.hr.ai.bean.vo.UploadAmResumeVo;
import com.open.hr.ai.constant.AmResumeEducationEnums;
import com.open.hr.ai.constant.AmResumeWorkYearsEnums;
import com.open.hr.ai.convert.AmPositionSetionConvert;
import com.open.hr.ai.convert.AmResumeConvert;
import com.open.hr.ai.convert.AmUploadResumeConvert;
import com.open.hr.ai.util.CompetencyModelPromptUtil;
import com.open.hr.ai.util.ResumeParseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 逻辑按照php处理的, 暂时未调试
 *
 * @Date 2025/1/6 20:00
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ResumeManager {

    @Resource
    private AmResumeServiceImpl amResumeService;

    @Resource
    private UploadAmResumeServiceImpl uploadAmResumeService;

    @Resource
    private AmPositionSectionServiceImpl amPositionSectionService;

    @Resource
    private AmPositionPostServiceImpl amPositionPostService;
    @Resource
    private AmPositionServiceImpl amPositionService;

    @Resource
    private AmZpLocalAccoutsServiceImpl amZpLocalAccoutsService;

    @Resource
    private AmZpPlatformsServiceImpl platformsService;


    @Resource
    private CommonAIManager commonAIManager;

    @Resource
    private CompetencyModelPromptUtil competencyModelPromptUtil;

    @Resource
    private AmResumeMapper resumeMapper;

    public ResultVO<AmResumeVo> resumeDetail(Integer id) {
        try {
            AmResume amResume = amResumeService.getById(id);
            AmResumeVo amResumeVo = AmResumeConvert.I.convertAmResumeVo(amResume);
            String accountId = amResumeVo.getAccountId();
            AmZpLocalAccouts amZpLocalAccouts = amZpLocalAccoutsService.getById(accountId);
            amResumeVo.setRecruiterId(amZpLocalAccouts.getExtBossId());
            return ResultVO.success(amResumeVo);
        } catch (Exception e) {
            log.error("获取简历详情 id={}", id, e);
        }
        return ResultVO.fail("获取简历详情异常");
    }


    /**
     * 获取简历列表
     *
     * @param type
     * @param post_id
     * @param name
     * @param page
     * @param size
     * @return
     */
    public ResultVO<PageVO<AmResumeVo>> resumeList(Long adminId, Integer type, Integer post_id, String name,
                                                   Integer pageNum, Integer size, LocalDate startTime,
                                                   LocalDate endTime, String expectPosition, String postName,
                                                   Integer platformId, BigDecimal score, Integer deptId,
                                                   String deptName,Integer positionId, String positionName,
                                                   String platform, Map<String, Integer> sortMap) {
        try {
            Page<AmResumeVo> page = new Page<>(pageNum, size);
            LocalDateTime startDateTime = null;
            LocalDateTime endDateTime = null;
            if (startTime != null) {
                startDateTime = startTime.atStartOfDay();
            }
            if (endTime != null) {
                endDateTime = endTime.atStartOfDay().plusDays(1);
            }
            IPage<AmResumeVo> iPage = resumeMapper.resumeList(page, adminId, type, post_id, name, startDateTime,
                    endDateTime, expectPosition, postName, platformId, score,
                    deptId, deptName, positionId, positionName, platform, sortMap);
            return ResultVO.success(PageVO.build(iPage.getTotal(), iPage.getRecords()));
        } catch (Exception e) {
            log.error("获取简历详情 ", e);
        }
        return ResultVO.fail("获取简历详情异常");
    }

    /**
     * 获取简历列表
     *
     * @return
     */
    public ResultVO<List<AmResumeCountDataVo>> resumeData(Long adminId, Integer post_id, String name,
                                                          LocalDate startTime, LocalDate endTime, String expectPosition,
                                                          String postName, Integer platformId, BigDecimal score,
                                                          Integer deptId, String deptName,Integer positionId,
                                                          String positionName, String platform) {
        try {
            LocalDateTime startDateTime = null;
            LocalDateTime endDateTime = null;
            if (startTime != null) {
                startDateTime = startTime.atStartOfDay();
            }
            if (endTime != null) {
                endDateTime = endTime.atStartOfDay().plusDays(1);
            }
            List<AmResumeCountDataVo> amResumeCountDataVos = new ArrayList<>();
            // 全部简历
            int allAccount = 0;
            for (int i = -1; i < 6; i++) {
                int count = resumeMapper.countByType(adminId, i, post_id, name, startDateTime,
                        endDateTime, expectPosition, postName, platformId, score,
                        deptId, deptName, positionId, positionName, platform);
                AmResumeCountDataVo countDataVo = new AmResumeCountDataVo();
                countDataVo.setType(i);
                countDataVo.setTotal(count);
                amResumeCountDataVos.add(countDataVo);
                allAccount += count;
            }
            AmResumeCountDataVo countDataVo = new AmResumeCountDataVo();
            countDataVo.setType(6);
            countDataVo.setTotal(allAccount);
            amResumeCountDataVos.add(countDataVo);
            return ResultVO.success(amResumeCountDataVos);
        } catch (Exception e) {
            log.error("获取简历详情 ", e);
        }
        return ResultVO.fail("获取简历详情异常");
    }


    public ResultVO<List<AmPositionSectionVo>> getStructures(Long adminId) {
        LambdaQueryWrapper<AmPositionSection> queryWrapper = new QueryWrapper<AmPositionSection>().lambda();
        queryWrapper.eq(AmPositionSection::getAdminId, adminId);
        List<AmPositionSection> amPositionSections = amPositionSectionService.list(queryWrapper);
        List<AmPositionSectionVo> amPositionSectionVos = amPositionSections.stream().map(AmPositionSetionConvert.I::converAmPositionSectionVo).collect(Collectors.toList());
        for (AmPositionSectionVo amPositionSection : amPositionSectionVos) {
            LambdaQueryWrapper<AmPositionPost> lambdaQueryWrapper = new QueryWrapper<AmPositionPost>().lambda();
            lambdaQueryWrapper.eq(AmPositionPost::getSectionId, amPositionSection.getId());
            List<AmPositionPost> amPositionPosts = amPositionPostService.list(lambdaQueryWrapper);
            amPositionSection.setPost_list(amPositionPosts);
        }
        return ResultVO.success(amPositionSectionVos);
    }


    public ResultVO<List<AmResumeVo>> resumeSearch(SearchAmResumeReq searchAmResumeReq, Long adminId) {
        LambdaQueryWrapper<AmResume> queryWrapper = new QueryWrapper<AmResume>().lambda();
        queryWrapper.eq(AmResume::getAdminId, adminId);
        if (Objects.nonNull(searchAmResumeReq.getPosition_id())) {
            queryWrapper.eq(AmResume::getPostId, searchAmResumeReq.getPosition_id());
        }
        if (Objects.nonNull(searchAmResumeReq.getEducation())) {
            AmResumeEducationEnums amResumeEducationEnums = AmResumeEducationEnums.getByCode(searchAmResumeReq.getEducation());
            if (Objects.nonNull(amResumeEducationEnums)) {
                queryWrapper.like(AmResume::getEducation, searchAmResumeReq.getEducation());
            } else {
                log.error("学历类型不存在 education={}", searchAmResumeReq.getEducation());
            }
        }
        if (Objects.nonNull(searchAmResumeReq.getExperience())) {
            AmResumeWorkYearsEnums byCode = AmResumeWorkYearsEnums.getByCode(searchAmResumeReq.getExperience());
            if (Objects.nonNull(byCode)) {
                queryWrapper.ge(AmResume::getWorkYears, byCode.getBegin());
                queryWrapper.le(AmResume::getWorkYears, byCode.getEnd());
            }
        }
        if (Objects.nonNull(searchAmResumeReq.getTec())) {
            queryWrapper.like(AmResume::getSkills, searchAmResumeReq.getTec());
        }
        List<AmResume> amResumeList = amResumeService.list(queryWrapper);
        List<AmResumeVo> resumeVos = amResumeList.stream().map(AmResumeConvert.I::convertAmResumeVo).collect(Collectors.toList());
        return ResultVO.success(resumeVos);
    }


    /**
     * 用户上传简历解析
     */
    public ResultVO resumeAnalysis(AddAmResumeParseReq addAmResumeParseReq, Long adminId) {
        String resumeUrl = addAmResumeParseReq.getResumeUrl();
        try {
            List<ChatMessage> chatMessages = ResumeParseUtil.buildPrompt(resumeUrl);
            if (chatMessages.isEmpty()) {
                return ResultVO.fail("解析失败");
            }
            // 添加对模型空回复或者抛异常的重试，重试10次（请求模型参数异常等情况也会轮询10次）
            int end = 3;
            AmResume uploadAmResume = null;
            for (int i = 0; i < end; i++) {
                try {
                    String aiText = commonAIManager.aiNoStreamWithResume(chatMessages, "OpenAI:gpt-4o-all", 0.8);
                    log.info("AI解析结果 data={}", aiText);
                    String jsonContent = AIJsonUtil.getJsonContent(aiText);
                    if (StringUtils.isBlank(jsonContent)) {
                        return ResultVO.fail("解析失败");
                    }

                    uploadAmResume = JSONObject.parseObject(jsonContent, AmResume.class);
                    if (Objects.nonNull(uploadAmResume)) {
                        uploadAmResume.setAdminId(adminId);
                        uploadAmResume.setAttachmentResume(resumeUrl);
                        uploadAmResume.setCreateTime(LocalDateTime.now());
                        uploadAmResume.setPlatform(addAmResumeParseReq.getPlatForm());
                        uploadAmResume.setResumeType(2);
                        // 保存解析结果
                        boolean result = amResumeService.save(uploadAmResume);
                        uploadAmResume.setId(uploadAmResume.getId());
                        log.info("简历解析结果保存结果 data={},result={}", JSONObject.toJSONString(uploadAmResume), result);
                        break;
                    }
                } catch (Exception e) {
                    log.error("AI解析异常", e);
                }
            }
            AmResumeVo amResumeVo = AmResumeConvert.I.convertAmResumeVo(uploadAmResume);
            return Objects.nonNull(amResumeVo) ? ResultVO.success(amResumeVo) : ResultVO.fail("解析失败");
        } catch (Exception e) {
            log.error("解析异常 url={}", addAmResumeParseReq.getResumeUrl(), e);
        }
        return ResultVO.fail("解析失败");

    }


    /**
     * 用户修改上传简历解析
     */
    public ResultVO updateUploadAmResume(UploadAmResumeUpdateReq uploadAmResume, Long adminId) {
        // 添加对模型空回复或者抛异常的重试，重试10次（请求模型参数异常等情况也会轮询10次）
        try {
            if (Objects.nonNull(uploadAmResume)) {
                // 保存解析结果
                AmResume amResumeServiceById = amResumeService.getById(uploadAmResume.getId());
                if (Objects.isNull(amResumeServiceById) || amResumeServiceById.getResumeType() == 1) {
                    return ResultVO.fail("简历不存在 或 不允许修改");
                }
                uploadAmResume.setAdminId(adminId);
                AmResume amResume = AmUploadResumeConvert.I.convertUpdateUploadAmResume(uploadAmResume);
                boolean result = amResumeService.updateById(amResume);
                log.info("简历解析结果修改 data={},result={}", JSONObject.toJSONString(amResume), result);
            }
        } catch (Exception e) {
            log.error("简历修改异常", e);
        }
        return Objects.nonNull(uploadAmResume) ? ResultVO.success(uploadAmResume) : ResultVO.fail("更新失败");
    }


    /**
     * 用户修改上传简历解析
     */
    public ResultVO<PageVO<UploadAmResumeVo>> UploadAmResumeSearch(AmUploadResumeSearchReq req, Long adminId) {
        try {
            Integer pageNum = req.getPage();
            Integer pageSize = req.getPageSize();
            String keywords = req.getKeywords();
            String position = req.getPosition();
            Page<UploadAmResume> page = new Page<>(pageNum, pageSize);
            LambdaQueryWrapper<UploadAmResume> queryWrapper = new QueryWrapper<UploadAmResume>().lambda();
            queryWrapper.eq(UploadAmResume::getAdminId, adminId);
            if (StringUtils.isNotBlank(keywords)) {
                queryWrapper.like(UploadAmResume::getName, keywords);
            }
            if (StringUtils.isNotBlank(position)) {
                queryWrapper.like(UploadAmResume::getPosition, position);
            }
            Page<UploadAmResume> uploadAmResumePage = uploadAmResumeService.page(page, queryWrapper);
            List<UploadAmResumeVo> uploadAmResumeVos = uploadAmResumePage.getRecords().stream().map(AmUploadResumeConvert.I::convertAmResumeVo).collect(Collectors.toList());
            return ResultVO.success(PageVO.build(uploadAmResumePage.getTotal(), uploadAmResumeVos));
        } catch (Exception e) {
            log.error("查询异常", e);
        }
        return ResultVO.fail("查询失败");
    }



    /**
     * 执行胜任力模型
     */
    public ResultVO competencyModel(Integer id, Long adminId) {
        // 添加对模型空回复或者抛异常的重试，重试10次（请求模型参数异常等情况也会轮询10次）
        try {
                // 保存解析结果
            AmResume amResume = amResumeService.getById(id);
            if (Objects.isNull(amResume)) {
                return ResultVO.fail("简历不存在");
            }
            if (StringUtils.isNotBlank(amResume.getCompetencyModel())){
                return ResultVO.fail("已经存在胜任力模型评估数据");
            }
            Integer postId = amResume.getPostId();
            AmPosition amPosition = amPositionService.getById(postId);
            if (Objects.isNull(amPosition)){
                return ResultVO.fail("岗位不存在");
            }
            String jobStandard = amPosition.getJobStandard();
            if (StringUtils.isBlank(jobStandard)) {
                return ResultVO.fail("人才画像和评分标准不存在,请先生成");
            }
            if (StringUtils.isBlank(amResume.getZpData())){
                return ResultVO.fail("缺少相关简历数据");
            }
            String amResumeCompetencyModel = competencyModelPromptUtil.dealAmResumeCompetencyModel(jobStandard, amResume.getZpData());

            if (StringUtils.isBlank(amResumeCompetencyModel)){
                return ResultVO.fail("生成失败!, 请稍后重试");
            }
            JSONObject jsonObject = JSONObject.parseObject(amResumeCompetencyModel);
            JSONObject obj = jsonObject.getObject("专业技能", JSONObject.class);
            String score = obj.getString("分数");
            amResume.setScore(new BigDecimal(score));
            amResume.setCompetencyModel(amResumeCompetencyModel);
            boolean result = amResumeService.updateById(amResume);
            log.info("执行胜任力模型 data={},result={}", JSONObject.toJSONString(amResume), result);
            return result ? ResultVO.success("执行成功") : ResultVO.fail("执行失败");
        } catch (Exception e) {
            log.error("执行胜任力模型异常", e);
        }
        return ResultVO.fail("执行胜任力模型异常");
    }

}
