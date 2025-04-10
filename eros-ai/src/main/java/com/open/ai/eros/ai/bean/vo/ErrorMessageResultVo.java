package com.open.ai.eros.ai.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorMessageResultVo {

    private ErrorMessage error;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ErrorMessage {
        private String message;
        private String type;
    }

}
