package com.open.ai.eros.db.mysql.social.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.open.ai.eros.db.mysql.social.entity.Announcement;
import com.open.ai.eros.db.mysql.social.entity.AnnouncementSearchVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 公告公共表 Mapper 接口
 * </p>
 *
 * @author linyous
 * @since 2024-02-07
 */
@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {


    /**
     * 查询公告
     */
    @Select({
            "<script> ",
            "select * from announcement where  status = 0 " +
                    " order by create_time desc  limit 1" +
                    "</script> "
    })
    Announcement getBestAnnouncement();



    /**
     * 查询公告
     */
    @Select({
            "<script> ",
            "select * from announcement where  status = 0 " +
                    " order by create_time desc  " +
            "</script> "
    })
    List<Announcement> selectListAnnouncementConfig();



    @Select({
            "<script> ",
            "select count(*) from announcement where 1 = 1  " +
                    "  <if test=\"id != null \"> ",
            "    and id = #{id}  ",
            "  </if>" +
                    "  <if test=\"status != null \"> ",
            "    and status = #{status}  ",
            "  </if>" +
                    "  <if test=\"type != null \"> ",
            "    and  type = #{type}  ",
            "  </if>" +
                    "  <if test=\"title != null and title != ''\"> ",
            "    and title like concat('%',#{title},'%') ",
            "  </if>" +
                    "</script> "
    })
    int searchAnnouncementConfigCount(AnnouncementSearchVo req);




    @Select({
            "<script> ",
            "select * from announcement where 1 = 1  " +
                "  <if test=\"id != null \"> ",
            "    and id = #{id}  ",
            "  </if>" +
                    "  <if test=\"status != null \"> ",
            "    and status = #{status}  ",
            "  </if>" +
                    "  <if test=\"type != null \"> ",
            "    and  type = #{type}  ",
            "  </if>" +
                    "  <if test=\"title != null and title != ''\"> ",
            "    and title like concat('%',#{title},'%') ",
            "  </if>" +
                    "  order by create_time desc limit #{pageIndex},#{pageSize}   ",
            "</script> "
    })
    List<Announcement> searchAnnouncementConfig(AnnouncementSearchVo req);
}
