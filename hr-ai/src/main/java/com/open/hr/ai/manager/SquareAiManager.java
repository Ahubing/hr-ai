package com.open.hr.ai.manager;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.AmPrompt;
import com.open.ai.eros.db.mysql.hr.entity.AmSquareRoles;
import com.open.ai.eros.db.mysql.hr.service.impl.AmPromptServiceImpl;
import com.open.ai.eros.db.mysql.hr.service.impl.AmSquareRolesServiceImpl;
import com.open.hr.ai.bean.req.AddOrUpdateAmPromptReq;
import com.open.hr.ai.bean.req.AddOrUpdateSquareReq;
import com.open.hr.ai.bean.vo.AmSquareListVo;
import com.open.hr.ai.bean.vo.AmSquareRolesVo;
import com.open.hr.ai.convert.AmSquareAIConvert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 逻辑按照php处理的
 *
 * @Date 2025/1/6 20:00
 */
@Slf4j
@Component
public class SquareAiManager {

    @Resource
    private AmSquareRolesServiceImpl amSquareRolesService;

    private static final List<String> KEYWORD_LIST = Arrays.asList("java", "后端", "python", "c#", "php", "分布式", "nginx", "HR", "销售", "运维", "助理", "化学", "物理");

    public ResultVO<AmSquareListVo> getSquareList(String name, Long adminId) {
        try {
            AmSquareListVo amSquareListVo = new AmSquareListVo();
            LambdaQueryWrapper<AmSquareRoles> queryWrapper = new LambdaQueryWrapper<>();
            if (StringUtils.isNotBlank(name)) {
                queryWrapper.like(AmSquareRoles::getName, name);
            }
            queryWrapper.orderByAsc(AmSquareRoles::getId);
            List<AmSquareRoles> amSquareRoles = amSquareRolesService.list(queryWrapper);
            List<AmSquareRolesVo> squareRolesVos = amSquareRoles.stream().map(AmSquareAIConvert.I::converAmSquareRolesVo).collect(Collectors.toList());
            amSquareListVo.setAmSquareRolesVos(squareRolesVos);
            amSquareListVo.setKeywordList(KEYWORD_LIST);
            return ResultVO.success(amSquareListVo);
        } catch (Exception e) {
            log.error("获取模型训练广场列表 type={},adminId={}", name, adminId, e);
        }
        return ResultVO.fail("获取模型训练广场列表异常");
    }

    public ResultVO<AmSquareRoles> getSquareDetail(Integer id) {
        try {
            AmSquareRoles amSquareRoles = amSquareRolesService.getById(id);
            return ResultVO.success(amSquareRoles);
        } catch (Exception e) {
            log.error("获取角色详情异常 id={}", id, e);
        }
        return ResultVO.fail("获取角色详情异常");
    }


    public ResultVO addOrUpdateSquare(AddOrUpdateSquareReq req, Long adminId) {
        try {
            AmSquareRoles amSquareRoles = new AmSquareRoles();
            if (Objects.isNull(req.getId())) {
                amSquareRoles.setAdminId(adminId);
                amSquareRoles.setName(req.getName());
                amSquareRoles.setKeywords(req.getKeywords());
                amSquareRoles.setDescription(req.getDescription());
                amSquareRoles.setProfession(req.getProfession());
                amSquareRoles.setUpdateTime(LocalDateTime.now());
                amSquareRoles.setCreateTime(LocalDateTime.now());
                boolean result = amSquareRolesService.save(amSquareRoles);
                log.info("新增角色广场结果 result={}", result);
            } else {
                amSquareRoles = amSquareRolesService.getById(req.getId());
                if (StringUtils.isNotBlank(req.getName())) {
                    amSquareRoles.setName(req.getName());
                }
                if (StringUtils.isNotBlank(req.getKeywords())) {
                    amSquareRoles.setKeywords(req.getKeywords());
                }
                if (StringUtils.isNotBlank(req.getDescription())) {
                    amSquareRoles.setDescription(req.getDescription());
                }
                if (StringUtils.isNotBlank(req.getProfession())) {
                    amSquareRoles.setProfession(req.getProfession());
                }
                amSquareRoles.setUpdateTime(LocalDateTime.now());
                boolean result = amSquareRolesService.updateById(amSquareRoles);
                log.info("更新角色广场结果 result={}", result);
            }
            return ResultVO.success();
        } catch (Exception e) {
            log.error("角色广场操作 req={}", JSONObject.toJSONString(req), e);
        }
        return ResultVO.fail("角色广场新增或修改异常");
    }


    public ResultVO deleteSquareRolesById(Integer id) {
        try {
            boolean result = amSquareRolesService.removeById(id);
            return result ? ResultVO.success() : ResultVO.fail("删除角色失败");
        } catch (Exception e) {
            log.error("删除角色 id={}", id, e);
        }
        return ResultVO.fail("删除角色异常");
    }

}
