package com.open.ai.eros.user.convert;

import com.open.ai.eros.db.mysql.user.entity.Feedback;
import com.open.ai.eros.user.bean.req.FeedbackSubmitReq;
import com.open.ai.eros.user.bean.vo.FeedbackVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@Mapper
public interface FeedbackConvert {

    FeedbackConvert I = Mappers.getMapper(FeedbackConvert.class);


    @Mapping(target = "fileList",source = "fileList")
    Feedback convertFeedback(FeedbackSubmitReq request);

    FeedbackVO convertFeedbackVO(Feedback feedback);


    default String getFileList(List<String> fileList) {
        if(CollectionUtils.isEmpty(fileList)){
            return null;
        }
        return String.join(",",fileList);
    }


    default List<String> getFileList(String fileList) {
        if(StringUtils.isEmpty(fileList)){
            return Collections.emptyList();
        }

        return Arrays.asList(fileList.split(","));
    }

}
