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
import com.open.ai.eros.common.vo.SqlSortParam;
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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
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
                                                   String platform, String sortMap) {
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
            List<SqlSortParam> sortParams = new ArrayList<>();
            if(StringUtils.isNotEmpty(sortMap)){
                String[] split = sortMap.split(",");
                for (String s : split) {
                    SqlSortParam sortParam = new SqlSortParam();
                    String[] sp = s.split(":");
                    sortParam.setField(sp[0]);
                    sortParam.setOrder(Integer.parseInt(sp[1]));
                    sortParams.add(sortParam);
                }
            }
            IPage<AmResumeVo> iPage = resumeMapper.resumeList(page, adminId, type, post_id, name, startDateTime,
                    endDateTime, expectPosition, postName, platformId, score,
                    deptId, deptName, positionId, positionName, platform, sortParams);
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

    public void exportResumesToExcel(HttpServletResponse response,
                                     Long adminId, List<Integer> ids,
                                     Integer type, Integer post_id, String name,
                                     LocalDate startDateTime, LocalDate endDateTime, String expectPosition,
                                     String postName, Integer platformId, BigDecimal score, Integer deptId,
                                     String deptName, Integer positionId, String positionName,
                                     String platform, Map<String, Integer> sortMap,
                                     String exportFields) {
        Workbook workbook = new XSSFWorkbook();
        try {
            // 查询数据库的简历
            List<AmResume> list = resumeMapper.exportResume(adminId, ids, type, post_id, name, startDateTime,
                    endDateTime, expectPosition, postName, platformId, score,
                    deptId, deptName, positionId, positionName, platform, sortMap);
            if (list.isEmpty()) {
                response.setContentType("text/plain; charset=UTF-8");
                response.getWriter().write("未找到符合条件的简历数据");
                log.warn("导出简历时，查询结果为空");
                return;
            }
            // 创建一个工作簿
            Sheet sheet = workbook.createSheet("简历数据");

            // 定义所有可导出字段的映射
            Map<String, String> allFields = new LinkedHashMap<>();
            allFields.put("name", "姓名");
            allFields.put("gender", "性别");
            allFields.put("phone", "手机");
            allFields.put("wechat", "微信");
            allFields.put("platform", "平台");
            allFields.put("expectPosition", "预期职位");
            allFields.put("score", "分数");
            allFields.put("createTime", "创建时间");
            allFields.put("education", "学历");
            allFields.put("workYears", "工作年限");
            allFields.put("skills", "技能");
            allFields.put("email", "邮箱");
            allFields.put("age", "年龄");
            allFields.put("postName", "职位名称");

            // 解析前端传来的导出字段
            List<String> selectedFields = new ArrayList<>();
            if (StringUtils.isNotBlank(exportFields)) {
                selectedFields = Arrays.asList(exportFields.split(","));
                selectedFields = new ArrayList<>(Arrays.asList(exportFields.split(",")));
            } else {
                // 如果前端没有指定导出字段，则使用默认字段
                selectedFields = new ArrayList<>(Arrays.asList("name", "gender", "phone", "wechat", "platform", "expectPosition", "score", "createTime"));
            }

            // 强制加入 wechat 和 phone
            if (!selectedFields.contains("wechat")) {
                selectedFields.add("wechat");
            }
            if (!selectedFields.contains("phone")) {
                selectedFields.add("phone");
            }

            // 创建标题行
            Row headerRow = sheet.createRow(0);

            // 准备实际导出的字段和标题
            List<String> finalExportFields = new ArrayList<>();
            List<String> headers = new ArrayList<>();
            for (String field : selectedFields) {
                if (allFields.containsKey(field)) {
                    finalExportFields.add(field);
                    headers.add(allFields.get(field));
                }
            }
            // 写入标题行
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
            }
            // 填充简历数据
            for (int i = 0; i < list.size(); i++) {
                AmResume resume = list.get(i);
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < finalExportFields.size(); j++) {
                    String field = finalExportFields.get(j);
                    Cell cell = row.createCell(j);

                    // 根据字段名动态获取和格式化数据
                    switch (field) {
                        case "name":
                            cell.setCellValue(resume.getName() != null ? resume.getName() : "无");
                            break;
                        case "gender":
                            cell.setCellValue(resume.getGender() != null ? (resume.getGender() == 1 ? "男" : "女") : "无");
                            break;
                        case "phone":
                            cell.setCellValue(resume.getPhone() != null ? resume.getPhone() : "无");
                            break;
                        case "wechat":
                            cell.setCellValue(resume.getWechat() != null ? resume.getWechat() : "无");
                            break;
                        case "platform":
                            cell.setCellValue(resume.getPlatform() != null ? resume.getPlatform() : "无");
                            break;
                        case "expectPosition":
                            cell.setCellValue(resume.getExpectPosition() != null ? resume.getExpectPosition() : "无");
                            break;
                        case "score":
                            cell.setCellValue(resume.getScore() != null ? resume.getScore().doubleValue() : 0.0);
                            break;
                        case "createTime":
                            cell.setCellValue(resume.getCreateTime() != null ? resume.getCreateTime().toString() : "无");
                            break;
                        case "age":
                            cell.setCellValue(resume.getAge() != null ? resume.getAge() : -1);
                            break;
                        case "company":
                            cell.setCellValue(resume.getCompany() != null ? resume.getCompany() : "无");
                            break;
                        case "city":
                            cell.setCellValue(resume.getCity() != null ? resume.getCity() : "无");
                            break;
                        case "education":
                            cell.setCellValue(resume.getEducation() != null ? resume.getEducation() : "无");
                            break;
                        case "experiences":
                            cell.setCellValue(resume.getExperiences() != null ? resume.getExperiences() : "无");
                            break;
                        case "projects":
                            cell.setCellValue(resume.getProjects() != null ? resume.getProjects() : "无");
                            break;
                        case "postId":
                            cell.setCellValue(resume.getPostId() != null ? resume.getPostId() : -1);
                            break;
                        case "applyStatus":
                            cell.setCellValue(resume.getApplyStatus() != null ? resume.getApplyStatus() : "无");
                            break;
                        case "zpData":
                            cell.setCellValue(resume.getZpData() != null ? resume.getZpData() : "无");
                            break;
                        case "email":
                            cell.setCellValue(resume.getEmail() != null ? resume.getEmail() : "无");
                            break;
                        case "lowSalary":
                            cell.setCellValue(resume.getLowSalary() != null ? resume.getLowSalary() : 0);
                            break;
                        case "highSalary":
                            cell.setCellValue(resume.getHighSalary() != null ? resume.getHighSalary() : 0);
                            break;
                        case "workYears":
                            cell.setCellValue(resume.getWorkYears() != null ? resume.getWorkYears() : -1);
                            break;
                        case "skills":
                            cell.setCellValue(resume.getSkills() != null ? resume.getSkills() : "无");
                            break;
                        case "resumeType":
                            cell.setCellValue(resume.getResumeType() != null ? (resume.getResumeType() == 1 ? "系统自动获取" : "用户自定义上传") : "无");
                            break;
                        case "competencyModel":
                            cell.setCellValue(resume.getCompetencyModel() != null ? resume.getCompetencyModel() : "无");
                            break;
                        case "intention":
                            cell.setCellValue(resume.getIntention() != null
                                    ? (resume.getIntention() == 0 ? "离职/离校-正在找工作"
                                    : resume.getIntention() == 1 ? "在职/在校-考虑机会"
                                    : resume.getIntention() == 2 ? "在职/在校-寻找新工作"
                                    : "未知")
                                    : "未知");
                            break;
                        case "degree":
                            cell.setCellValue(resume.getDegree() != null
                                    ? (resume.getDegree() == 0 ? "初中及以下"
                                    : resume.getDegree() == 1 ? "中专/技校"
                                    : resume.getDegree() == 2 ? "高中"
                                    : resume.getDegree() == 3 ? "大专"
                                    : resume.getDegree() == 4 ? "本科"
                                    : resume.getDegree() == 5 ? "硕士"
                                    : resume.getDegree() == 6 ? "博士"
                                    : "未知")
                                    : "未知");
                            break;
                        case "isStudent":
                            cell.setCellValue(resume.getIsStudent() != null ? (resume.getIsStudent() == 1 ? "是学生" : "不是学生") : "未知");
                            break;
                        default:
                            cell.setCellValue("无");
                            break;
                    }
                }
            }


            // 设置响应头，告诉浏览器下载文件
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=resumes.xlsx");
            response.setCharacterEncoding("UTF-8");

            // 将Excel写入输出流
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            workbook.close();
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            log.error("导出简历失败", e);
        }
    }

    public Map<String, String> getExportFieldsMap() {
        Map<String, String> allFields = new LinkedHashMap<>();
        allFields.put("name", "姓名");
        allFields.put("gender", "性别");
        allFields.put("phone", "手机");
        allFields.put("wechat", "微信");
        allFields.put("platform", "平台");
        allFields.put("expectPosition", "预期职位");
        allFields.put("score", "分数");
        allFields.put("createTime", "创建时间");
        allFields.put("age", "年龄");
        allFields.put("company", "公司");
        allFields.put("city", "城市");
        allFields.put("education", "学历");
        allFields.put("experiences", "工作经验");
        allFields.put("projects", "项目经验");
        allFields.put("postId", "职位 ID");
        allFields.put("applyStatus", "申请状态");
        allFields.put("zpData", "招聘信息");
        allFields.put("email", "邮箱");
        allFields.put("lowSalary", "最低薪资");
        allFields.put("highSalary", "最高薪资");
        allFields.put("workYears", "工作年限");
        allFields.put("skills", "技能");
        allFields.put("resumeType", "简历类型");
        allFields.put("competencyModel", "胜任力模型数据");
        allFields.put("intention", "求职意向");
        allFields.put("degree", "学历等级");
        allFields.put("isStudent", "是否为学生");
        return allFields;
    }


}

