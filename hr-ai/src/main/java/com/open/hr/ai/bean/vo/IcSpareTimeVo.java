package com.open.hr.ai.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty("single-单面，group-群面")
    private String interviewType;

    @ApiModelProperty("空闲时间段")
    private List<SpareDateVo> SpareDateVos = new ArrayList<>();

    @Data
    public static class SpareDateVo{
        @ApiModelProperty("日期")
        private LocalDate localDate;

        @ApiModelProperty("群面空闲时间1-上午，2-下午，3-全天")
        private Integer groupSparePeriod;

        @ApiModelProperty("单面空闲时间段")
        private List<SparePeriodVo> singleSparePeriodVos = new ArrayList<>();

        public SpareDateVo(LocalDate localDate, Integer groupSparePeriod) {
            this.localDate = localDate;
            this.groupSparePeriod = groupSparePeriod;
        }

        public SpareDateVo(LocalDate localDate, List<SparePeriodVo> singleSparePeriodVos) {
            this.localDate = localDate;
            this.singleSparePeriodVos = singleSparePeriodVos;
        }
    }

    @Data
    public static class SparePeriodVo {
        @ApiModelProperty("时间段起始时间")
        private LocalDateTime startTime;

        @ApiModelProperty("时间段截止时间")
        private LocalDateTime endTime;
    }

}
