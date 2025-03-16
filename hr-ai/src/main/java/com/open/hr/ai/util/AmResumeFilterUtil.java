package com.open.hr.ai.util;

import com.open.ai.eros.db.mysql.hr.entity.AmResume;
import com.open.hr.ai.bean.vo.AmGreetConditionVo;
import com.open.hr.ai.constant.AmIntentionEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;

/**
 * @Date 2025/3/16 00:23
 */
public class AmResumeFilterUtil {

//    public static boolean filterResume(AmResume resume, AmGreetConditionVo criteria) {
//
//        // 年龄
//        if (StringUtils.isNotBlank(criteria.getAge())) {
//            if (!criteria.getAge().equals("不限")) {
//                String[] ages = criteria.getAge().split("-");
//                if (ages.length == 2) {
//                    int minAge = Integer.parseInt(ages[0]);
//                    int maxAge = Integer.parseInt(ages[1]);
//                    if (resume.getAge() < minAge || resume.getAge() > maxAge) {
//                        return false;
//                    }
//                }
//            }
//        }
//
//        /**
//         * 求职意向(多选）；如：不限,离职/离校-正在找工作，在职/在校-考虑机会，在职/在校-寻找新工作
//         */
//        if (CollectionUtils.isNotEmpty(criteria.getIntention())) {
//            if (!criteria.getIntention().equals("不限")) {
//                String[] intentions = criteria.getIntention().split(",");
//                AmIntentionEnum.getValueByType()
//                if (intentions.length > 0) {
//                    boolean flag = false;
//                    for (String intention : intentions) {
//                        if (.contains(intention)){
//                            flag = true;
//                            break;
//                        }
//                    }
//                    if (!flag) {
//                        return false;
//                    }
//                }
//            }
//
//            // 经验要求
//            if (criteria.experienceRequirement != null) {
//                if (criteria.experienceRequirement.equals("在校/应届") && !resume.searchData.isStudent()) {
//                    return false;
//                }
//                if (criteria.experienceRequirement.equals("多少年以下") && resume.searchData.getWorkYears() >= criteria.maxYears) {
//                    return false;
//                }
//                if (criteria.experienceRequirement.equals("多少年以上") && resume.searchData.getWorkYears() <= criteria.minYears) {
//                    return false;
//                }
//            }
//
//            // 学历要求
//            if (criteria.degreeRequirement != null && !criteria.degreeRequirement.contains(resume.searchData.getDegree())) {
//                return false;
//            }
//
//            // 薪资待遇
//            if (criteria.salaryRange != null) {
//                if (resume.searchData.getLowSalary() < criteria.minSalary || resume.searchData.getHighSalary() > criteria.maxSalary) {
//                    return false;
//                }
//            }
//
//            // 求职意向
//            if (criteria.intentionRequirement != null && !criteria.intentionRequirement.contains(resume.searchData.getIntention())) {
//                return false;
//            }
//
//
//            // 性别
//            if (criteria.genderRequirement != null && !criteria.genderRequirement.equals(resume.searchData.getGender())) {
//                return false;
//            }
//
//            // 跳槽频率
//            if (criteria.jobChangeFrequency != null) {
//                int workCount = resume.workExperiences.size();
//                int years = criteria.recentYears;
//                if (criteria.jobChangeFrequency.equals("少于几份") && workCount >= criteria.maxJobs) {
//                    return false;
//                }
//                if (criteria.jobChangeFrequency.equals("平均每份工作大于几年") && (years / (double) workCount) <= criteria.minYearsPerJob) {
//                    return false;
//                }
//            }
//
//            // 院校
//            if (criteria.schoolRequirement != null && !criteria.schoolRequirement.contains(resume.educations.get(0).getSchool())) {
//                return false;
//            }
//
//            // 关键词筛选
//            if (criteria.includeKeywords != null) {
//                for (String keyword : criteria.includeKeywords) {
//                    if (!resume.selfIntroduction.contains(keyword)) {
//                        return false;
//                    }
//                }
//            }
//            if (criteria.excludeKeywords != null) {
//                for (String keyword : criteria.excludeKeywords) {
//                    if (resume.selfIntroduction.contains(keyword)) {
//                        return false;
//                    }
//                }
//            }
//
//            // 期望职位
//            if (criteria.expectedPosition != null && !resume.expectPosition.contains(criteria.expectedPosition)) {
//                return false;
//            }
//
//            // 工作及项目经历
//            if (criteria.workExperienceKeywords != null) {
//                for (String keyword : criteria.workExperienceKeywords) {
//                    if (resume.workExperiences.stream().noneMatch(we -> we.getResponsibilities().contains(keyword))) {
//                        return false;
//                    }
//                }
//            }
//            if (criteria.projectKeywords != null) {
//                for (String keyword : criteria.projectKeywords) {
//                    if (resume.projects.stream().noneMatch(p -> p.getDescription().contains(keyword))) {
//                        return false;
//                    }
//                }
//            }
//
//            // 专业技能
//            if (criteria.skillKeywords != null) {
//                for (String keyword : criteria.skillKeywords) {
//                    if (!resume.skills.contains(keyword)) {
//                        return false;
//                    }
//                }
//            }
//
//            return true;
//        }
//
//
//    }

}
