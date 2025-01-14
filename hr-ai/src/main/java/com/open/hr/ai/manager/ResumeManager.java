package com.open.hr.ai.manager;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.AmPositionPost;
import com.open.ai.eros.db.mysql.hr.entity.AmPositionSection;
import com.open.ai.eros.db.mysql.hr.entity.AmPrompt;
import com.open.ai.eros.db.mysql.hr.entity.AmResume;
import com.open.ai.eros.db.mysql.hr.service.impl.AmPositionPostServiceImpl;
import com.open.ai.eros.db.mysql.hr.service.impl.AmPositionSectionServiceImpl;
import com.open.ai.eros.db.mysql.hr.service.impl.AmPromptServiceImpl;
import com.open.ai.eros.db.mysql.hr.service.impl.AmResumeServiceImpl;
import com.open.hr.ai.bean.req.AddOrUpdateAmPromptReq;
import com.open.hr.ai.bean.vo.AmPositionSectionVo;
import com.open.hr.ai.convert.AmPositionSetionConvert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 逻辑按照php处理的, 暂时未调试
 * @Date 2025/1/6 20:00
 */
@Slf4j
@Component
public class ResumeManager {

    @Resource
    private AmResumeServiceImpl amResumeService;

    @Resource
    private AmPositionSectionServiceImpl   amPositionSectionService;

    @Resource
    private AmPositionPostServiceImpl amPositionPostService;



    public ResultVO<AmResume> resumeDetail(Integer id) {
        try {
            AmResume amResume = amResumeService.getById(id);
            return ResultVO.success(amResume);
        }catch (Exception e){
            log.error("获取简历详情 id={}",id,e);
        }
        return ResultVO.fail("获取简历详情异常");
    }


    /**
     * 获取简历列表
     * @param type
     * @param post_id
     * @param name
     * @param page
     * @param size
     * @return
     */
    public ResultVO<List<AmResume>> resumeList(Integer type, Integer post_id, String name, Integer page, Integer size) {
        try {
            LambdaQueryWrapper<AmResume> queryWrapper = new QueryWrapper<AmResume>().lambda();
            if (Objects.nonNull(type)) {
                queryWrapper.eq(AmResume::getType, type);
            }
            if (Objects.nonNull(post_id)) {
                queryWrapper.eq(AmResume::getPostId, post_id);
            }
            if (StringUtils.isNotBlank(name)) {
                queryWrapper.like(AmResume::getName, name);
            }
            queryWrapper.orderByDesc(AmResume::getCreateTime);
            queryWrapper.last("limit " + (page - 1) * size + "," + size);
            List<AmResume> amResumes = amResumeService.list(queryWrapper);
            return ResultVO.success(amResumes);
        }catch (Exception e){
            log.error("获取简历详情 ",e);
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
                amPositionSection.setAmPositionPosts(amPositionPosts);
            }
            return ResultVO.success(amPositionSectionVos);
    }



}
