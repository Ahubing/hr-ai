package com.open.ai.eros.db.mysql.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.open.ai.eros.db.mysql.user.entity.User;
import com.open.ai.eros.db.mysql.user.vo.SearchUserReqVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 用户表，存储用户的基本信息，包括通过谷歌账号登录的用户 Mapper 接口
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-20
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {


    @Select("select  * from user where email = #{account} and `status` = 'ACTIVE'  limit 1  ")
    User getUserByAccount(@Param("account") String account);


    @Select({
            "<script> ",
            "select count(*) from user where 1 = 1 ",
            "  <if test=\"userName != null and userName != ''\"> ",
            "    and  user_name like concat('%',#{userName},'%') ",
            "  </if> ",
            "  <if test=\"email != null and email != ''\"> ",
            "    and  email like concat('%',#{email},'%') ",
            "  </if> ",
            "  <if test=\"role != null and role != ''\"> ",
            "    and role = #{role} ",
            "  </if>",
            "  <if test=\"status != null and status != ''\"> ",
            "    and status = #{status} ",
            "  </if>",
            "</script> "
    })
    Integer searchUserCount(SearchUserReqVo searchUserReqVo);

    @Select({
            "<script> ",
            "select * from user where 1 = 1 ",
            "  <if test=\"userName != null and userName != ''\"> ",
            "    and  user_name like concat('%',#{userName},'%') ",
            "  </if> ",
            "  <if test=\"email != null and email != ''\"> ",
            "    and  email like concat('%',#{email},'%') ",
            "  </if> ",
            "  <if test=\"role != null and role != ''\"> ",
            "    and role = #{role} ",
            "  </if>",
            "  <if test=\"status != null and status != ''\"> ",
            "    and status = #{status} ",
            "  </if>",
            "  order by created_at desc limit #{pageIndex},#{pageSize}  ",
            "</script> "
    })
    List<User> searchUser(SearchUserReqVo searchUserReqVo);

    @Select({
            "<script> ",
            "select * from user where 1 = 1 ",
            "  <if test=\"id != null and id != ''\"> ",
            "    and  id  = #{id}",
            "  </if> ",
            "  <if test=\"email != null and email != ''\"> ",
            "    and  email like concat('%',#{email},'%') ",
            "  </if> ",
            "</script> "
    })
    User searchUserDetail(SearchUserReqVo searchUserReqVo);



    @Select("select  id , avatar , user_name  from user where  `status` = 'ACTIVE'  limit 2000  ")
    List<User> getAllUser();


    /**
     * 通过邀请码获取用户信息
     *
     * @param invitationCode
     * @return
     */
    @Select("select  * from user where invitation_code = #{invitationCode} and `status` = 'ACTIVE'  limit 1  ")
    User getUserByInvitationCode(@Param("invitationCode") String invitationCode);



    @Select("select  count(*) from user")
    Long getAllUserSum();


    @Select("SELECT COUNT(*)  FROM user WHERE `role` = 'creator' or  `role` = 'system' ")
    Long getAllCreatorSum();


    @Select("SELECT COUNT(*)  FROM user WHERE `role` = 'user' ")
    Long getAllCommonUserSum();


    @Select(
            "<script> "+
            "SELECT COUNT(*)  FROM user " +
            "         <![CDATA[ where created_at >= #{startTime} ]]> "+
            "</script> "
    )
    Long getTodayRegister(@Param("startTime") Date startTime);



}

