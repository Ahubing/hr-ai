package com.open.hr.ai.util;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.db.mysql.hr.entity.AmResume;
import com.open.hr.ai.bean.vo.AmGreetConditionVo;
import com.open.hr.ai.constant.AmGreetDegreeEnum;
import com.open.hr.ai.constant.AmIntentionEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @Date 2025/3/16 00:23
 */
@Slf4j
public class AmResumeFilterUtil {

    public static boolean filterResume(AmResume resume, AmGreetConditionVo criteria,Boolean isGreet) {

        // 年龄
        if (StringUtils.isNotBlank(criteria.getAge())) {
            if (!criteria.getAge().equals("不限")) {
                String[] ages = criteria.getAge().split("-");
                if (ages.length == 2) {
                    int minAge = Integer.parseInt(ages[0]);
                    int maxAge = Integer.parseInt(ages[1]);
                    if (resume.getAge() < minAge || resume.getAge() > maxAge) {
                        log.info("AmResumeFilterUtil uid={} 年龄不符合 ,要求的年龄={}, 实际的年龄={}", resume.getUid(), criteria.getAge(), resume.getAge());
                        return false;
                    }
                }
            }
        }
        // 性别
        if (Objects.nonNull(criteria.getGender()) && criteria.getGender() != -1) {
            if (!criteria.getGender().equals(resume.getGender())) {
                log.info("AmResumeFilterUtil uid={} 性别不符合", resume.getUid());
                return false;
            }
        }
        // 工作年限
        if (CollectionUtils.isNotEmpty(criteria.getWorkYears()) && !criteria.getWorkYears().contains("不限") && resume.getWorkYears() != null) {
            if (Objects.nonNull(resume.getIsStudent())) {
                // 判断是否是学生
                if (resume.getIsStudent() == 1) {
                    if (!criteria.getWorkYears().contains("在校/应届")) {
                        log.info("AmResumeFilterUtil uid={} 不是应届生", resume.getUid());
                        return false;
                    }
                } else {
                    Integer workYear = resume.getWorkYears();
                    List<String> strings = convertWorkYears(workYear);
                    boolean flag = false;
                    for (String convertedWorkYears : strings) {
                        if (criteria.getWorkYears().contains(convertedWorkYears)) {
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        log.info("AmResumeFilterUtil uid={} 工作年限不符合, 年龄为={} 筛选条件为={}", resume.getUid(), workYear, criteria.getWorkYears());
                        return false;
                    }

                }
            }
        }

        /**
         * 求职意向(多选）；如：不限,离职/离校-正在找工作，在职/在校-考虑机会，在职/在校-寻找新工作
         */
        if (CollectionUtils.isNotEmpty(criteria.getIntention())) {
            if (Objects.nonNull(resume.getIntention())  && resume.getIntention() != -1 && !criteria.getIntention().contains(AmIntentionEnum.UM_LIMITED.getType())) {
                if (!criteria.getIntention().contains(resume.getIntention())) {
                    log.info("AmResumeFilterUtil uid={} 求职意向不符合", resume.getUid());
                    return false;
                }
            }
        }

        /**
         * 通过resume的work_experiences和projects判断
         */
        if (CollectionUtils.isNotEmpty(criteria.getExperience())) {
            boolean flag = false;
            if (StringUtils.isNotBlank(resume.getExperiences())) {
                String experiencesLowerCase = resume.getExperiences().toLowerCase();
                for (String workExperience : criteria.getExperience()) {
                    if (experiencesLowerCase.contains(workExperience.toLowerCase())) {
                        flag = true;
                        break;
                    }
                }
            }
            if (StringUtils.isNotBlank(resume.getProjects())) {
                String projectsLowerCase = resume.getProjects().toLowerCase();
                for (String project : criteria.getExperience()) {
                    if (projectsLowerCase.contains(project.toLowerCase())) {
                        flag = true;
                        break;
                    }
                }
            }
            if (!flag) {
                log.info("AmResumeFilterUtil uid={} 工作经验不符合", resume.getUid());
                return false;
            }
        }

        /**
         * 通过resume的work_experiences和projects判断
         */
        if (CollectionUtils.isNotEmpty(criteria.getFilterExperience())) {
            boolean flag = false;
            if (StringUtils.isNotBlank(resume.getExperiences())) {
                String experiencesLowerCase = resume.getExperiences().toLowerCase();
                for (String workExperience : criteria.getFilterExperience()) {
                    if (experiencesLowerCase.contains(workExperience)) {
                        flag = true;
                        break;
                    }
                }
            }
            if (StringUtils.isNotBlank(resume.getProjects())) {
                String projectsLowerCase = resume.getProjects().toLowerCase();
                for (String project : criteria.getFilterExperience()) {
                    if (projectsLowerCase.contains(project)) {
                        flag = true;
                        break;
                    }
                }
            }
            if (flag) {
                log.info("AmResumeFilterUtil uid={} 工作经验不符合,命中过滤词", resume.getUid());
                return false;
            }
        }

        /**
         * 技能要求
         */
        if (CollectionUtils.isNotEmpty(criteria.getSkills())) {
            boolean flag = false;
            if (StringUtils.isNotBlank(resume.getSkills())) {
                String resumeLowerCase = resume.getSkills().toLowerCase();
                for (String skill : criteria.getSkills()) {
                    if (resumeLowerCase.contains(skill.toLowerCase())) {
                        flag = true;
                        break;
                    }
                }
            }
            if (!flag) {
                log.info("AmResumeFilterUtil uid={} 技能要求不符合", resume.getUid());
                return false;
            }
        }

        /**
         * 薪资要求
         */
        if (StringUtils.isNotBlank(criteria.getSalary()) && !criteria.getSalary().equals("不限")) {
            String[] salarys = criteria.getSalary().split("-");
            if (salarys.length == 2) {
                // 存在薪资交集就可以
                int minSalary = Integer.parseInt(salarys[0].replace("k", ""));
                int maxSalary = Integer.parseInt(salarys[1].replace("k", ""));
                // 获取简历中的薪资范围
                int resumeMinSalary = resume.getLowSalary();
                int resumeMaxSalary = resume.getHighSalary();

                // 判断是否存在交集
                if (resumeMaxSalary < minSalary || resumeMinSalary > maxSalary) {
                    log.info("AmResumeFilterUtil uid={} 薪资不符合，无交集", resume.getUid());
                    return false; // 无交集
                }
                // 如果存在交集
                log.info("AmResumeFilterUtil uid={} 薪资符合，有交集", resume.getUid());
            } else {
                // 用正则过滤 3k以下 k以下后面这些数据
                String salaryStr = salarys[0];
                int salary = Integer.parseInt(salarys[0].replaceAll("[^0-9]", ""));
                if (salaryStr.contains("以下")) {
                    if (resume.getLowSalary() > salary) {
                        log.info("AmResumeFilterUtil uid={} 薪资不符合,工资要求为{},用户最低工资为low={}", resume.getUid(),salary,resume.getLowSalary());
                        return false;
                    }
                } else {
                    if (resume.getHighSalary() < salary) {
                        log.info("AmResumeFilterUtil uid={} 薪资不符合,工资要求为{} , 用户最高工资为{}", resume.getUid(), salary, resume.getHighSalary());
                        return false;
                    }
                }
            }
        }

        if (CollectionUtils.isNotEmpty(criteria.getDegree()) && !criteria.getDegree().contains(-1)) {
            if (Objects.nonNull(resume.getDegree())) {
                if (!criteria.getDegree().contains(resume.getDegree())) {
                    log.info("AmResumeFilterUtil uid={} 学历要求不符合", resume.getUid());
                    return false;
                }
            }

        }


        /**
         * 期望的职位关键词
         */
        // 如果主动打招呼,并且开启了没有开启打招呼特殊处理
        if (CollectionUtils.isNotEmpty(criteria.getExpectPosition())) {
            // 需要处理主动打招呼, 并且开启了没有开启打招呼特殊处理
            if (!isGreet ||  criteria.getGreetHandle() != 1){
                if (StringUtils.isNotBlank(resume.getExpectPosition())) {
                    String ExpectPositionLowerCase = resume.getExpectPosition().toLowerCase();
                    // 匹配期望的职位关键词
                    boolean flag = false;
                    for (String expectPosition : criteria.getExpectPosition()) {
                        if (ExpectPositionLowerCase.contains(expectPosition.toLowerCase())) {
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        log.info("AmResumeFilterUtil uid={} 期望的职位关键词不符合", resume.getUid());
                        return false;
                    }
                }
            }


        }

        /**
         * 过滤的职位关键词
         */
        if (CollectionUtils.isNotEmpty(criteria.getFilterPosition())) {
            if (StringUtils.isNotBlank(resume.getExpectPosition())) {
                String ExpectPositionLowerCase = resume.getExpectPosition().toLowerCase();
                // 匹配期望的职位关键词
                boolean flag = false;
                for (String expectPosition : criteria.getFilterPosition()) {
                    if (ExpectPositionLowerCase.contains(expectPosition.toLowerCase())) {
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    log.info("AmResumeFilterUtil uid={} 过滤的职位关键词不符合", resume.getUid());
                    return false;
                }
            }
        }
        return true;

    }


    /**
     * 将workYears 转化为工作年限
     * 如果workYears 为 0 则返回一年以内
     * 如果workYears 为 1,2 则返回1-3年
     * 如果workYears 为 3,4,5 则返回3-5年
     * 如果workYears 为 6,7,8,9 则返回5-10年
     * 如果workYears 为 10 则返回10年以上
     */
    private static List<String> convertWorkYears(Integer workYears) {
        List<String> workYearsRange = new ArrayList<>();

        if (workYears == null) {
            workYearsRange.add("不限");
            return workYearsRange;
        }
        switch (workYears) {
            case 0:
                workYearsRange.add("一年以内");
                break;
            case 1:
                workYearsRange.add("一年以内");
                workYearsRange.add("1-3年");
                break;
            case 2:
                workYearsRange.add("1-3年");
                break;
            case 3:
                workYearsRange.add("1-3年");
                workYearsRange.add("3-5年");
                break;
            case 4:
                workYearsRange.add("3-5年");
                break;
            case 5:
                workYearsRange.add("5-10年");
                workYearsRange.add("3-5年");
                break;
            case 6: case 7: case 8: case 9:
                workYearsRange.add("5-10年");
                break;
            case 10:
                workYearsRange.add("5-10年");
                workYearsRange.add("10年以上");
                break;
            default:
                workYearsRange.add("不限");
                break;
        }
        if (workYears > 10) {
            workYearsRange.add("10年以上");
        }

        return workYearsRange;
    }



    public static void main(String[] args) {

        String jsonStr = "{\"uid\":\"546068715\",\"encryptGeekId\":\"2a08301407477d730nB829u1F1NV\",\"name\":\"谢岳辉\",\"avatar\":\"https://img.bosszhipin.com/beijin/upload/avatar/20230710/607f1f3d68754fd001b5ee16bf4f7cdf320d71a904c72a9ccb9b3f35b2a8b5959ff85d99bb09754e.png.webp\",\"gender\":\"男\",\"degree\":\"本科\",\"work_experience\":\"26年应届生\",\"email\":null,\"age\":\"21岁\",\"availability\":\"离校-随时到岗\",\"self_introduction\":\"学术优：西安财大大数据管理与应用本科，专业前 10%，学习与专业素养强。\\n技术强：熟练 Python、C++ ，精通 MySQL，会用 PyCharm、Dreamweaver 及 Streamlit等工具及 IBM SPSS Modeler，擅数据处理。\\n协作佳：桌游社经历，组织活动、跨社合作，团队与项目管理能力出色。\\n经验丰：慧科讯业实习，涉足导购、店员、小程序开发，积累多元经验。\\n创新足：参加双创、建模大赛，创新与解题能力突出。\\n责任强：志愿活动亮眼，大学运动会履职尽责，责任心和执行力高。\",\"work_experiences\":[{\"company\":\"慧科讯业（北京）网络科技有限公司南京分公司\",\"position\":\"其他技术职位\",\"responsibilities\":\"舆情监测\\n每日开启系统，定制方案追踪多品牌热点，全平台捕捉舆论风向。\\n日报上传\\n接手上汽大众日报，用 “舆情红绿灯” 助高层 5 分钟速抓关键，精准上传并核对。\\n新闻上传\\n严审新奥燃气稿件，依规排版配图，按时按需推送。\\n网页抓取\\n用工具全网监测含汽车类平台，设关键词捕捉奔腾汽车舆情。\\n监网\\n从多渠道收罗萝卜快跑负面数据，清洗 分类，核查内容、信息、来源，量化评定严重度。\",\"workPerformance\":\"舆情监测\\n实习时，我敏锐捕捉到塔斯汀负面舆情，社交媒体初现消费者吐槽新品口味差、有生肉问题的帖子，凭借高频监测，迅速察觉舆论恶化倾向，即刻整理预警报告提交。团队借此提前策划应对，最大程度降低风波对品牌形象与市场销量的冲击。\",\"time_range\":\"2024.07 - 2024.12 (5个月)\",\"is_internship\":\"实习\",\"department\":\"编辑部\",\"positionTitle\":\"\"},{\"company\":\"胜道体育\",\"position\":\"导购员/店员\",\"responsibilities\":\"1.负责为顾客推销服装，提升销售业绩\\n2.协助店面布局调整，优化购物环境\\n3.整理货物，快速响应顾客需求\",\"workPerformance\":\"\",\"time_range\":\"2023.08 - 2023.09 (1个月)\",\"is_internship\":\"全职\",\"department\":\"未指定部门\",\"positionTitle\":\"\"},{\"company\":\"恒丰鲜花资材\",\"position\":\"店员/营业员\",\"responsibilities\":\"接待顾客，解答咨询，提供优质的购物体验\\n管理库存，确保商品充足与新鲜\\n参与促销活动，提升门店销量\",\"workPerformance\":\"\",\"time_range\":\"2022.07 - 2022.08 (1个月)\",\"is_internship\":\"全职\",\"department\":\"未指定部门\",\"positionTitle\":\"\"}],\"projects\":[{\"name\":\"校帮帮小程序\",\"link\":null,\"role\":\"成员\",\"time_range\":\"2023.05 - 2023.07  (2个月)\",\"description\":\"技术实现：\\n小程序架构：基于微信小程序开发，确保跨平台兼容性和用户友好性。\"}],\"educations\":[{\"school\":\"西安财经大学\",\"major\":\"大数据管理与 应用\",\"degree\":\"本科\",\"time_range\":\"2022 - 2026\",\"ranking\":\"专业前10%\",\"courses\":\"python、数据采集与清洗、社交网络与文本分析、数据库系统概论、概率论与数理统计\"}],\"skills\":\"编程语言：熟练掌握Python 与 C++，前者灵活用于多场景编程，后者专注高性能底层开发。\",\"expect_position\":\"数据标注/AI训练师 | 西安 | 2-3K\",\"search_data\":{\"student\":true,\"workYears\":4,\"degree\":2,\"age\":21,\"city\":\"西安\",\"lowSalary\":3,\"highSalary\":6,\"gender\":1,\"intention\":0,\"toPosition\":\"数据标注/AI训练师\"}}";
        JSONObject resumeJSONObject = JSONObject.parseObject(jsonStr);


        String search_data = "PYSthon 工程师";
        System.out.println(search_data.toLowerCase());

        JSONObject searchData = resumeJSONObject.getJSONObject("search_data");
        AmResume amResume = new AmResume();
        amResume.setZpData(resumeJSONObject.toJSONString());


        // ---- begin 从resume search_data数据结构提取数据 ----
        amResume.setCity(Objects.nonNull(searchData.get("city")) ? searchData.get("city").toString() : "");
        amResume.setAge(Objects.nonNull(searchData.get("age")) ? Integer.parseInt(searchData.get("age").toString()) : 0);
        amResume.setLowSalary(Objects.nonNull(searchData.get("lowSalary")) ? Integer.parseInt(searchData.get("lowSalary").toString()) : 0);
        amResume.setHighSalary(Objects.nonNull(searchData.get("highSalary")) ? Integer.parseInt(searchData.get("highSalary").toString()) : 0);
        amResume.setGender(Objects.nonNull(searchData.get("gender")) ? Integer.parseInt(searchData.get("gender").toString()) : 0);
        amResume.setWorkYears(Objects.nonNull(searchData.get("workYears")) ? Integer.parseInt(searchData.get("workYears").toString()) : 0);
        amResume.setExpectPosition(Objects.nonNull(searchData.get("toPosition")) ? searchData.get("toPosition").toString() : "");
        amResume.setIsStudent(Objects.nonNull(searchData.get("student")) ? searchData.get("student").equals(true) ? 1 : 0  : null);
        amResume.setDegree(Objects.nonNull(searchData.get("degree")) ? Integer.parseInt(searchData.get("degree").toString())  : null);
        amResume.setIntention(Objects.nonNull(searchData.get("intention")) ? Integer.parseInt(searchData.get("intention").toString()) : null);
        // ---- end 从resume search_data数据结构提取数据  ----

        // ---- begin 从resume数据结构提取数据  ----
        amResume.setUid(Objects.nonNull(resumeJSONObject.get("uid")) ? resumeJSONObject.get("uid").toString() : "");
        amResume.setName(Objects.nonNull(resumeJSONObject.get("name")) ? resumeJSONObject.get("name").toString() : "");
        amResume.setEmail(Objects.nonNull(resumeJSONObject.get("email")) ? resumeJSONObject.get("email").toString() : "");
        amResume.setApplyStatus(Objects.nonNull(resumeJSONObject.get("availability")) ? resumeJSONObject.get("availability").toString() : "");
        amResume.setAvatar(Objects.nonNull(resumeJSONObject.get("avatar")) ? resumeJSONObject.get("avatar").toString() : "");
        amResume.setEducation(Objects.nonNull(resumeJSONObject.get("educations")) ? resumeJSONObject.getJSONArray("educations").toJSONString() : "");
        amResume.setExperiences(Objects.nonNull(resumeJSONObject.get("work_experiences")) ? resumeJSONObject.getJSONArray("work_experiences").toJSONString() : "");
        amResume.setProjects(Objects.nonNull(resumeJSONObject.get("projects")) ? resumeJSONObject.getJSONArray("projects").toJSONString() : "");
        amResume.setEncryptGeekId(Objects.nonNull(resumeJSONObject.get("encryptGeekId")) ? resumeJSONObject.get("encryptGeekId").toString() : "");
        amResume.setSkills(Objects.nonNull(resumeJSONObject.get("skills")) ? resumeJSONObject.get("skills").toString() : "");
        // ---- end 从resume数据结构提取数据  ----

        System.out.println(amResume);

        AmGreetConditionVo criteria = new AmGreetConditionVo();
        criteria.setAge("18-35");
        criteria.setGender(-1);
        criteria.setWorkYears(Collections.singletonList("在校/应届"));
//        criteria.setExperience(Collections.singletonList("java"));
        criteria.setFilterExperience(Collections.singletonList("导购员/店员"));
        criteria.setDegree(Collections.singletonList(2));
        criteria.setSalary("5k以上");
        criteria.setIntention(Collections.singletonList(0));
        criteria.setSkills(Collections.singletonList("Python"));
        criteria.setExpectPosition(Collections.singletonList("数据"));
//        criteria.setFilterPosition(Collections.singletonList("数据"));

        System.out.println(filterResume(amResume, criteria,false));

        JSONObject conditions = new JSONObject();
        conditions.put("学历要求", criteria.getDegree() != null ? criteria.getDegree() : Collections.singletonList(-1));
        conditions.put("薪资待遇", criteria.getSalary() != null ?criteria.getSalary()  : "不限");
        conditions.put("经验要求", criteria.getWorkYears() != null ? criteria.getWorkYears() :  Collections.singletonList("不限"));
        conditions.put("求职意向", criteria.getIntention() != null ?criteria.getIntention() : Collections.singletonList(-1));
        conditions.put("年龄", criteria.getAge() != null ? criteria.getAge() : -1);
        conditions.put("性别", criteria.getGender() != null ? criteria.getGender() : "不限");

        System.out.println(conditions.toJSONString());


    }



}
