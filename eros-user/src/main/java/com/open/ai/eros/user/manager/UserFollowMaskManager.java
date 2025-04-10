package com.open.ai.eros.user.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.creator.bean.vo.CMaskVo;
import com.open.ai.eros.creator.convert.MaskConvert;
import com.open.ai.eros.db.mysql.creator.entity.Mask;
import com.open.ai.eros.db.mysql.creator.service.impl.MaskServiceImpl;
import com.open.ai.eros.db.mysql.user.entity.UserFollowMask;
import com.open.ai.eros.db.mysql.user.service.impl.UserFollowMaskServiceImpl;
import com.open.ai.eros.user.bean.req.FollowMaskOpReq;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @类名：UserFollowMaskManager
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/16 22:47
 */

@Component
public class UserFollowMaskManager {



    @Autowired
    private UserFollowMaskServiceImpl userFollowMaskService;

    @Autowired
    private MaskServiceImpl maskService;

    /**
     *
     * 关注面具操作
     *
     * @return
     */
    public ResultVO followMask(Long userId, FollowMaskOpReq req){
        Long maskId = req.getMaskId();
        int op = req.getOp();
        UserFollowMask userFollowMask = userFollowMaskService.getUserFollowMask(userId, maskId);
        if(op==1){
            // 关注
            if(userFollowMask!=null){
                return ResultVO.fail("已经关注！");
            }
            userFollowMask = new UserFollowMask();
            userFollowMask.setUserId(userId);
            userFollowMask.setMaskId(maskId);
            userFollowMask.setCreateTime(LocalDateTime.now());
            boolean saveResult = userFollowMaskService.save(userFollowMask);
            if(!saveResult){
                return ResultVO.fail("关注失败！");
            }
            // 新增面具的收藏数
            maskService.updateMaskCollectNum(maskId);
            return ResultVO.success();
        }
        // 取消关注
        if(userFollowMask==null){
            return ResultVO.success();
        }
        boolean removeResult = userFollowMaskService.removeById(userFollowMask.getId());
        if(!removeResult){
            return ResultVO.fail("取消关注失败！");
        }
        return ResultVO.success();
    }


    /**
     * 获取用户的关注列表
     *
     * @param userId
     * @return
     */
    public ResultVO<List<CMaskVo>> getFollowList(Long userId){
        // 获取关注列表
        LambdaQueryWrapper<UserFollowMask> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserFollowMask::getUserId,userId);
        List<UserFollowMask> followMasks = userFollowMaskService.list(lambdaQueryWrapper);
        List<Long> maskIds = followMasks.stream().map(UserFollowMask::getMaskId).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(maskIds)){
            return ResultVO.success(Collections.emptyList());
        }
        List<Mask> masks = maskService.listByIds(maskIds);
        List<CMaskVo> cMaskVos = masks.stream().map(MaskConvert.I::convertCMaskVo).collect(Collectors.toList());
        return ResultVO.success(cMaskVos);
    }




}
