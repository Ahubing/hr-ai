package com.open.ai.eros.social.convert;


import com.open.ai.eros.db.mysql.social.entity.PushMessage;
import com.open.ai.eros.social.bean.vo.LastMessagePushMessageVo;
import com.open.ai.eros.social.bean.req.PushMessageAddReq;
import com.open.ai.eros.social.bean.req.PushMessageUpdateReq;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PushMessageConvert {


    PushMessageConvert I = Mappers.getMapper(PushMessageConvert.class);

    LastMessagePushMessageVo convertLastMessagePushMessageVo(PushMessage pushMessage);


    @Mapping(target = "userId" ,source = "targetUserId")
    PushMessage convertPushMessage(PushMessageAddReq  req);


    @Mapping(target = "userId" ,source = "targetUserId")
    PushMessage convertPushMessage(PushMessageUpdateReq req);

}
