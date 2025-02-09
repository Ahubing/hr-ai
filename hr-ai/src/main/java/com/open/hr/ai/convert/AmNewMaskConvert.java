package com.open.hr.ai.convert;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.db.mysql.hr.entity.AmMask;
import com.open.ai.eros.db.mysql.hr.entity.AmNewMask;
import com.open.hr.ai.bean.req.AmMaskAddReq;
import com.open.hr.ai.bean.req.AmMaskUpdateReq;
import com.open.hr.ai.bean.vo.AmMaskAIParamVo;
import com.open.hr.ai.bean.vo.AmMaskVo;
import com.open.hr.ai.bean.vo.AmNewMaskVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @Date 2025/1/4 13:55
 */
@Mapper
public interface AmNewMaskConvert {

    AmNewMaskConvert I = Mappers.getMapper(AmNewMaskConvert.class);



//




}
