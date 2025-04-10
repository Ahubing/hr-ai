package com.open.ai.eros.db.mysql.social.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.open.ai.eros.db.mysql.social.entity.Announcement;
import com.open.ai.eros.db.mysql.social.mapper.AnnouncementMapper;
import com.open.ai.eros.db.mysql.social.service.IAnnouncementService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 公告公共表 服务实现类
 * </p>
 *
 * @author linyous
 * @since 2024-02-07
 */
@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements IAnnouncementService {

}
