package com.open.ai.eros.ai.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ApiModel("可用面试时间")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class IcSpareTimeVo {

    @ApiModelProperty("空闲时间段")
    private List<SpareDateVo> SpareDateVos = new ArrayList<>();

    @Data
    public static class SpareDateVo{
        @ApiModelProperty("日期")
        private LocalDate localDate;

        @ApiModelProperty("空闲时间段")
        private List<SparePeriodVo> sparePeriodVos;

        public SpareDateVo(LocalDate localDate, List<SparePeriodVo> singleSparePeriodVos) {
            this.localDate = localDate;
            this.sparePeriodVos = singleSparePeriodVos;
        }
    }

    @Data
    @AllArgsConstructor
    public static class SparePeriodVo {
        @ApiModelProperty("时间段起始时间")
        private LocalDateTime startTime;

        @ApiModelProperty("时间段截止时间")
        private LocalDateTime endTime;
    }

}
