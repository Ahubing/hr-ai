package com.open.ai.eros.text.match.convert;


import com.open.ai.eros.db.mysql.text.entity.FilterWordChannelInfo;
import com.open.ai.eros.text.match.bean.FilterWordChannelAddReq;
import com.open.ai.eros.text.match.bean.FilterWordChannelUpdateReq;
import com.open.ai.eros.text.match.bean.FilterWordChannelVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ChannelConvert {


    ChannelConvert I = Mappers.getMapper(ChannelConvert.class);


    FilterWordChannelInfo convertFilterWordChannelInfo(FilterWordChannelAddReq req);


    FilterWordChannelInfo convertFilterWordChannelInfo(FilterWordChannelUpdateReq req);


    FilterWordChannelVo convertFilterWordChannelVo(FilterWordChannelInfo info);

}
