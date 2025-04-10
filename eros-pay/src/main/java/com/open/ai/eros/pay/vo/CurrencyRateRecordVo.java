package com.open.ai.eros.pay.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @类名：CurrencyRateRecordVo
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/4 15:02
 */
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Data
public class CurrencyRateRecordVo {

    private String date;

    private List<String>values;


}
