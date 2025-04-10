package com.open.ai.eros.social.convert;

import com.open.ai.eros.db.mysql.social.entity.Announcement;
import com.open.ai.eros.social.bean.vo.AnnouncementVo;
import org.springframework.stereotype.Component;

@Component
public class AnnouncementAdapter {

    public AnnouncementVo convert(Announcement announcement) {
        return AnnouncementVo.builder()
                .id(announcement.getId())
                .title(announcement.getTitle())
                .content(announcement.getContent())
                .createUser(announcement.getCreateUser())
                .createTime(announcement.getCreateTime())
                .updateTime(announcement.getUpdateTime())
                .type(announcement.getType())
                .status(announcement.getStatus())
                .duration(announcement.getDuration())
                .build();
    }
}
