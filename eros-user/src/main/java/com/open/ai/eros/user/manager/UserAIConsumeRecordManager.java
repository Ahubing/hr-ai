package com.open.ai.eros.user.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.creator.manager.MaskManager;
import com.open.ai.eros.db.mysql.ai.entity.UserAiConsumeRecord;
import com.open.ai.eros.db.mysql.ai.service.impl.UserAiConsumeRecordServiceImpl;
import com.open.ai.eros.db.mysql.creator.entity.Mask;
import com.open.ai.eros.user.bean.req.UserAIConsumeRecordQueryReq;
import com.open.ai.eros.user.bean.vo.UserAIConsumeRecordQueryResultVo;
import com.open.ai.eros.user.bean.vo.UserAiConsumeRecordVo;
import com.open.ai.eros.user.convert.UserAiConsumeRecordConvert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @类名：UserAIConsumeRecordManager
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/18 13:07
 */

@Slf4j
@Component
public class UserAIConsumeRecordManager {

    @Autowired
    private UserAiConsumeRecordServiceImpl userAiConsumeRecordService;

    @Autowired
    private MaskManager maskManager;


    /**
     * 搜索用户记录
     *
     * @param req
     * @return
     */
    public ResultVO<UserAIConsumeRecordQueryResultVo> searchRecord(Long userId,UserAIConsumeRecordQueryReq req){

        UserAIConsumeRecordQueryResultVo resultVo = new UserAIConsumeRecordQueryResultVo();

        LambdaQueryWrapper<UserAiConsumeRecord> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        lambdaQueryWrapper.eq(UserAiConsumeRecord::getUserId,userId);
        Page<UserAiConsumeRecord> page = new Page<>(req.getPageNum(),req.getPageSize());

        lambdaQueryWrapper.orderByDesc(UserAiConsumeRecord::getCreateTime);
        Page<UserAiConsumeRecord> recordPage = userAiConsumeRecordService.page(page, lambdaQueryWrapper);

        List<UserAiConsumeRecord> records = recordPage.getRecords();
        boolean lastPage = req.getPageSize()>records.size();

        List<Long> maskIds = records.stream()
                .map(UserAiConsumeRecord::getMaskId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<Mask> masks = maskManager.batchGetCacheMask(maskIds);
        Map<Long, Mask> maskMap = masks.stream().collect(Collectors.toMap(Mask::getId, e -> e, (k1, k2) -> k1));

        List<UserAiConsumeRecordVo> aiConsumeRecordVos = records.stream().map(e->{
            UserAiConsumeRecordVo userAiConsumeRecordVo = UserAiConsumeRecordConvert.I.convertUserAiConsumeRecordVo(e);
            Long maskId = e.getMaskId();
            String model = String.format("(%s)",e.getModel());
            userAiConsumeRecordVo.setMaskName("自定义聊天"+model);
            if(maskId!=null){
                Mask mask = maskMap.get(maskId);
                if(mask!=null){
                    userAiConsumeRecordVo.setMaskName(mask.getName()+model);
                }
            }else{
                userAiConsumeRecordVo.setMaskId(0L);
            }
            return userAiConsumeRecordVo;
        }).collect(Collectors.toList());
        resultVo.setLastPage(lastPage);
        resultVo.setRecordVos(aiConsumeRecordVos);
        return ResultVO.success(resultVo);
    }





}
