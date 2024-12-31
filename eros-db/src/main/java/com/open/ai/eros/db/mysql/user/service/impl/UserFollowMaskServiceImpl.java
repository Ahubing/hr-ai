package com.open.ai.eros.db.mysql.user.service.impl;

import com.open.ai.eros.db.mysql.user.entity.UserFollowMask;
import com.open.ai.eros.db.mysql.user.mapper.UserFollowMaskMapper;
import com.open.ai.eros.db.mysql.user.service.IUserFollowMaskService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-16
 */
@Service
public class UserFollowMaskServiceImpl extends ServiceImpl<UserFollowMaskMapper, UserFollowMask> implements IUserFollowMaskService {


    public UserFollowMask getUserFollowMask(Long userId,Long maskId){
        return this.getBaseMapper().getUserFollowMask(userId,maskId);
    }


}
