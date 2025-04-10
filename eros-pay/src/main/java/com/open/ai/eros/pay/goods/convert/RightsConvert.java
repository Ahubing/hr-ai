package com.open.ai.eros.pay.goods.convert;


import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.common.util.BalanceFormatUtil;
import com.open.ai.eros.common.util.DateUtils;
import com.open.ai.eros.db.constants.RightsTypeEnum;
import com.open.ai.eros.db.constants.UserRightsStatusEnum;
import com.open.ai.eros.db.mysql.pay.entity.Rights;
import com.open.ai.eros.db.mysql.pay.entity.RightsSnapshot;
import com.open.ai.eros.pay.goods.bean.req.RightsAddReq;
import com.open.ai.eros.pay.goods.bean.req.RightsUpdateReq;
import com.open.ai.eros.pay.goods.bean.vo.CRightsRuleVo;
import com.open.ai.eros.pay.goods.bean.vo.CRightsVo;
import com.open.ai.eros.pay.goods.bean.vo.RightsRuleVo;
import com.open.ai.eros.pay.goods.bean.vo.RightsVo;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RightsConvert {



    RightsConvert I = Mappers.getMapper(RightsConvert.class);

    @Mapping(target = "rule",source = "rule",qualifiedByName = "rightsRuleVoConvertJson")
    Rights convertRights(RightsAddReq req);

    @Mapping(target = "rule",source = "rule",qualifiedByName = "rightsRuleVoConvertJson")
    Rights convertRights(RightsUpdateReq req);

    @Mapping(target = "effectiveTimeDesc",source = "effectiveTime",qualifiedByName = "getEffectiveTimeDesc")
    @Mapping(target = "rule",source = "rule",qualifiedByName = "convertRightsRuleVo")
    @Mapping(target = "typeDesc",source = "type",qualifiedByName = "convertTypeDesc")
    RightsVo convertRights(RightsSnapshot rights);


    @Mapping(target = "effectiveTimeDesc",source = "effectiveTime",qualifiedByName = "getEffectiveTimeDesc")
    @Mapping(target = "rule",source = "rule",qualifiedByName = "convertRightsRuleVo")
    @Mapping(target = "typeDesc",source = "type",qualifiedByName = "convertTypeDesc")
    RightsVo convertRights(Rights rights);


    @Mapping(target = "rightsValue",source = "rightsVo",qualifiedByName = "convertRightsValue")
    @Mapping(target = "rule",source = "rule",qualifiedByName = "convertCRightsRuleVo")
    CRightsVo convertCRights(RightsVo rightsVo);


    @Named("convertRightsValue")
    default String convertRightsValue(RightsVo rightsVo){
        String type = rightsVo.getType();
        if(type.contains("BALANCE")){
            return BalanceFormatUtil.getUserBalance(Long.parseLong(rightsVo.getRightsValue())) +"$";
        }
        return rightsVo.getRightsValue()+" 次";
    }

    @Named("convertCRightsRuleVo")
    default CRightsRuleVo convertCRightsRuleVo(RightsRuleVo rule){
        return null;
    }



    @Named("convertTypeDesc")
    default String convertTypeDesc(String type){
        return RightsTypeEnum.getDesc(type);
    }



    @Named("convertStatusDesc")
    default String convertStatusDesc(Integer status){
        return UserRightsStatusEnum.getDesc(status);
    }


    @Named("convertRightsRuleVo")
    default RightsRuleVo convertRightsRuleVo(String rule){
        if(StringUtils.isEmpty(rule)){
            return null;
        }
        return JSONObject.parseObject(rule,RightsRuleVo.class);
    }



    @Named("rightsRuleVoConvertJson")
    default String rightsRuleVoConvertJson(RightsRuleVo rule){
        if(rule==null){
            return "";
        }
        return JSONObject.toJSONString(rule);
    }


    @Named("getEffectiveTimeDesc")
    default String getEffectiveTimeDesc(Long effectiveTime){
        return DateUtils.getDescDay(effectiveTime);
    }




}
