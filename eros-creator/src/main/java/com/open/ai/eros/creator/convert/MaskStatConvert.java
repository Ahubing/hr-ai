package com.open.ai.eros.creator.convert;


import com.open.ai.eros.common.util.BalanceFormatUtil;
import com.open.ai.eros.creator.bean.req.MasksInfoSearchVo;
import com.open.ai.eros.creator.bean.vo.MaskStatDayVo;
import com.open.ai.eros.creator.bean.vo.MaskStatListVo;
import com.open.ai.eros.creator.bean.vo.MasksInfoVo;
import com.open.ai.eros.db.mysql.ai.entity.MaskStatDay;
import com.open.ai.eros.db.mysql.ai.entity.MaskStatList;
import com.open.ai.eros.db.mysql.ai.entity.UserAiConsumeRecordStatVo;
import com.open.ai.eros.db.mysql.ai.entity.UserAiMasksRecordStatVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MaskStatConvert {

    MaskStatConvert I = Mappers.getMapper(MaskStatConvert.class);

    MaskStatDay convertMaskStatDay(UserAiConsumeRecordStatVo recordStatVo);

    @Mapping(target = "costPoints", source = "cost", qualifiedByName = "convertMaskIncome")
    MaskStatDayVo convertMaskStatDayVo(UserAiConsumeRecordStatVo recordStatVo);

    MaskStatDayVo convertMaskStatDayVo(MaskStatDay maskStatDay);


    @Mapping(target = "costPoints", source = "cost", qualifiedByName = "convertMaskIncome")
    MasksInfoVo convertMasksInfoVo(UserAiMasksRecordStatVo userAiMasksRecordStatVo);

    @Mapping(target = "cost", source = "cost", qualifiedByName = "convertMaskIncome")
    MaskStatListVo convertMasksStatListVo(MaskStatList userAiMasksRecordStatVo);

    default String convertCostPoints(Long costPoints) {
        return BalanceFormatUtil.getUserBalance(costPoints);
    }


    @Named("convertMaskCost")
    default String convertMaskCost(Long cost) {
        if (cost == null) {
            return null;
        }
        return cost.toString();
    }

    @Named("convertMaskIncome")
    default String convertMaskIncome(Long cost) {
        return "$ " + BalanceFormatUtil.getUserBalance(cost);
    }


}
