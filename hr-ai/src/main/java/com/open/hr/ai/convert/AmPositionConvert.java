package com.open.hr.ai.convert;

import com.open.ai.eros.db.mysql.hr.entity.AmChatbotPositionOption;
import com.open.ai.eros.db.mysql.hr.entity.AmPosition;
import com.open.hr.ai.bean.vo.AmChatbotPositionOptionVo;
import com.open.hr.ai.bean.vo.AmPositionVo;
import org.mapstruct.factory.Mappers;

/**
 * @Date 2025/1/4 13:55
 */
public interface AmPositionConvert {

    AmPositionConvert I = Mappers.getMapper(AmPositionConvert.class);


    AmPositionVo converAmPositionVo(AmPosition amPosition);



}
