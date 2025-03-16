package com.open.hr.ai.util;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.db.mysql.hr.entity.AmResume;
import com.open.hr.ai.bean.vo.AmGreetConditionVo;
import com.open.hr.ai.constant.AmGreetDegreeEnum;
import com.open.hr.ai.constant.AmIntentionEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * @Date 2025/3/16 00:23
 */
@Slf4j
public class AmResumeFilterUtil {

    public static boolean filterResume(AmResume resume, AmGreetConditionVo criteria) {

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
        if (Objects.nonNull(criteria.getGender())) {
            if (!criteria.getGender().equals(resume.getGender())) {
                log.info("AmResumeFilterUtil uid={} 性别不符合", resume.getUid());
                return false;
            }
        }
        // 工作年限
        if (CollectionUtils.isNotEmpty(criteria.getWorkYears()) && resume.getWorkYears() != null) {
            if (Objects.nonNull(resume.getIsStudent())) {
                // 判断是否是学生
                if (resume.getIsStudent() == 1) {
                    if (!criteria.getWorkYears().contains("应届生")) {
                        log.info("AmResumeFilterUtil uid={} 不是应届生", resume.getUid());
                        return false;
                    }
                } else {
                    Integer workYear = resume.getWorkYears();
                    String convertedWorkYears = convertWorkYears(workYear);
                    if (!criteria.getWorkYears().contains(convertedWorkYears)) {
                        log.info("AmResumeFilterUtil uid={} 工作年限不符合", resume.getUid());
                        return false;
                    }
                }
            }
        }

        /**
         * 求职意向(多选）；如：不限,离职/离校-正在找工作，在职/在校-考虑机会，在职/在校-寻找新工作
         */
        if (CollectionUtils.isNotEmpty(criteria.getIntention())) {
            String valueByType = AmIntentionEnum.getValueByType(resume.getIntention());
            if (StringUtils.isNotBlank(valueByType)) {
                if (!criteria.getIntention().contains(valueByType)) {
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
                for (String workExperience : criteria.getExperience()) {
                    if (resume.getExperiences().contains(workExperience)) {
                        flag = true;
                        break;
                    }
                }
            }
            if (StringUtils.isNotBlank(resume.getProjects())) {
                for (String project : criteria.getExperience()) {
                    if (resume.getProjects().contains(project)) {
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
                for (String workExperience : criteria.getExperience()) {
                    if (resume.getExperiences().contains(workExperience)) {
                        flag = true;
                        break;
                    }
                }
            }
            if (StringUtils.isNotBlank(resume.getProjects())) {
                for (String project : criteria.getExperience()) {
                    if (resume.getProjects().contains(project)) {
                        flag = true;
                        break;
                    }
                }
            }
            if (flag) {
                log.info("AmResumeFilterUtil uid={} 工作经验不符合", resume.getUid());
                return false;
            }
        }

        /**
         * 技能要求
         */
        if (CollectionUtils.isNotEmpty(criteria.getSkills())) {
            boolean flag = false;
            if (StringUtils.isNotBlank(resume.getSkills())) {
                for (String skill : criteria.getSkills()) {
                    if (resume.getSkills().contains(skill)) {
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
        if (StringUtils.isNotBlank(criteria.getSalary())) {
            String[] salarys = criteria.getSalary().split("-");
            if (salarys.length == 2) {
                int minSalary = Integer.parseInt(salarys[0].replace("k", ""));
                int maxSalary = Integer.parseInt(salarys[1].replace("k", ""));
                if (resume.getLowSalary() < minSalary || resume.getHighSalary() > maxSalary) {
                    log.info("AmResumeFilterUtil uid={} 薪资不符合", resume.getUid());
                    return false;
                }
            } else {
                int salary = Integer.parseInt(salarys[0].replace("k", ""));
                if (resume.getLowSalary() > salary || resume.getHighSalary() < salary) {
                    log.info("AmResumeFilterUtil uid={} 薪资不符合", resume.getUid());
                    return false;
                }
            }
        }

        if (CollectionUtils.isNotEmpty(criteria.getDegree())) {
            if (Objects.nonNull(resume.getDegree())) {
                String valueByType = AmGreetDegreeEnum.getValueByType(resume.getDegree());
                if (StringUtils.isNotBlank(valueByType) && !criteria.getDegree().contains(valueByType)) {
                    log.info("AmResumeFilterUtil uid={} 学历要求不符合", resume.getUid());
                    return false;
                }
            }

        }


        /**
         * 期望的职位关键词
         */
        if (CollectionUtils.isNotEmpty(criteria.getExpectPosition())) {
            if (StringUtils.isNotBlank(resume.getExpectPosition())) {
                if (!criteria.getExpectPosition().contains(resume.getExpectPosition())) {
                    log.info("AmResumeFilterUtil uid={} 期望的职位关键词不符合", resume.getUid());
                    return false;
                }
            }

        }

        /**
         * 过滤的职位关键词
         */
        if (CollectionUtils.isNotEmpty(criteria.getFilterPosition())) {
            if (StringUtils.isNotBlank(resume.getExpectPosition())) {
                if (criteria.getFilterPosition().contains(resume.getExpectPosition())) {
                    log.info("AmResumeFilterUtil uid={} 命中过滤的职位关键词,不符合", resume.getUid());
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
    private static String convertWorkYears(Integer workYears) {
        if (workYears == 0) {
            return "1年以下";
        } else if (workYears == 1 || workYears == 2) {
            return "1-3年";
        } else if (workYears == 3 || workYears == 4 || workYears == 5) {
            return "3-5年";
        } else if (workYears == 6 || workYears == 7 || workYears == 8 || workYears == 9) {
            return "5-10年";
        } else if (workYears == 10) {
            return "10年以上";
        }
        return "不限";
    }


    public static void main(String[] args) {

        String jsonStr = "{\"uid\":\"546068715\",\"encryptGeekId\":\"2a08301407477d730nB829u1F1NV\",\"name\":\"谢岳辉\",\"avatar\":\"https://img.bosszhipin.com/beijin/upload/avatar/20230710/607f1f3d68754fd001b5ee16bf4f7cdf320d71a904c72a9ccb9b3f35b2a8b5959ff85d99bb09754e.png.webp\",\"gender\":\"男\",\"degree\":\"本科\",\"work_experience\":\"26年应届生\",\"email\":null,\"age\":\"21岁\",\"availability\":\"离校-随时到岗\",\"self_introduction\":\"学术优：西安财大大数据管理与应用本科，专业前 10%，学习与专业素养强。\\n技术强：熟练 Python、C++ ，精通 MySQL，会用 PyCharm、Dreamweaver 及 Streamlit等工具及 IBM SPSS Modeler，擅数据处理。\\n协作佳：桌游社经历，组织活动、跨社合作，团队与项目管理能力出色。\\n经验丰：慧科讯业实习，涉足导购、店员、小程序开发，积累多元经验。\\n创新足：参加双创、建模大赛，创新与解题能力突出。\\n责任强：志愿活动亮眼，大学运动会履职尽责，责任心和执行力高。\",\"work_experiences\":[{\"company\":\"慧科讯业（北京）网络科技有限公司南京分公司\",\"position\":\"其他技术职位\",\"responsibilities\":\"舆情监测\\n每日开启系统，定制方案追踪多品牌热点，全平台捕捉舆论风向。\\n日报上传\\n接手上汽大众日报，用 “舆情红绿灯” 助高层 5 分钟速抓关键，精准上传并核对。\\n新闻上传\\n严审新奥燃气稿件，依规排版配图，按时按需推送。\\n网页抓取\\n用工具全网监测含汽车类平台，设关键词捕捉奔腾汽车舆情。\\n监网\\n从多渠道收罗萝卜快跑负面数据，清洗 分类，核查内容、信息、来源，量化评定严重度。\",\"workPerformance\":\"舆情监测\\n实习时，我敏锐捕捉到塔斯汀负面舆情，社交媒体初现消费者吐槽新品口味差、有生肉问题的帖子，凭借高频监测，迅速察觉舆论恶化倾向，即刻整理预警报告提交。团队借此提前策划应对，最大程度降低风波对品牌形象与市场销量的冲击。\",\"time_range\":\"2024.07 - 2024.12 (5个月)\",\"is_internship\":\"实习\",\"department\":\"编辑部\",\"positionTitle\":\"\"},{\"company\":\"胜道体育\",\"position\":\"导购员/店员\",\"responsibilities\":\"1.负责为顾客推销服装，提升销售业绩\\n2.协助店面布局调整，优化购物环境\\n3.整理货物，快速响应顾客需求\",\"workPerformance\":\"\",\"time_range\":\"2023.08 - 2023.09 (1个月)\",\"is_internship\":\"全职\",\"department\":\"未指定部门\",\"positionTitle\":\"\"},{\"company\":\"恒丰鲜花资材\",\"position\":\"店员/营业员\",\"responsibilities\":\"接待顾客，解答咨询，提供优质的购物体验\\n管理库存，确保商品充足与新鲜\\n参与促销活动，提升门店销量\",\"workPerformance\":\"\",\"time_range\":\"2022.07 - 2022.08 (1个月)\",\"is_internship\":\"全职\",\"department\":\"未指定部门\",\"positionTitle\":\"\"}],\"projects\":[{\"name\":\"校帮帮小程序\",\"link\":null,\"role\":\"成员\",\"time_range\":\"2023.05 - 2023.07  (2个月)\",\"description\":\"技术实现：\\n小程序架构：基于微信小程序开发，确保跨平台兼容性和用户友好性。\"}],\"educations\":[{\"school\":\"西安财经大学\",\"major\":\"大数据管理与 应用\",\"degree\":\"本科\",\"time_range\":\"2022 - 2026\",\"ranking\":\"专业前10%\",\"courses\":\"python、数据采集与清洗、社交网络与文本分析、数据库系统概论、概率论与数理统计\"}],\"skills\":\"编程语言：熟练掌握Python 与 C++，前者灵活用于多场景编程，后者专注高性能底层开发。\",\"expect_position\":\"数据标注/AI训练师 | 西安 | 2-3K\",\"search_data\":{\"student\":false,\"workYears\":2,\"degree\":0,\"age\":21,\"city\":\"西安\",\"lowSalary\":2,\"highSalary\":3,\"gender\":1,\"intention\":0,\"toPosition\":\"数据标注/AI训练师\"}}";
        JSONObject resumeJSONObject = JSONObject.parseObject(jsonStr);


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
        criteria.setGender(1);
        criteria.setWorkYears(Collections.singletonList("1-3年"));
//        criteria.setExperience(Collections.singletonList("舆情监测"));
//        criteria.setFilterExperience(Collections.singletonList("导购员/店员"));
        criteria.setDegree(Collections.singletonList("初中及以下"));
        criteria.setSalary("2-3k");
        criteria.setIntention(Collections.singletonList("离职/离校-正在找工作"));
        criteria.setSkills(Collections.singletonList("Python"));
        criteria.setExpectPosition(Collections.singletonList("数据标注/AI训练师"));
//        criteria.setFilterPosition(Collections.singletonList("数据标注/AI训练师"));

        System.out.println(filterResume(amResume, criteria));

    }



}
