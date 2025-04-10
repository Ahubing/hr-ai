package com.open.ai.eros.ai.model.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author Administrator
 * @Date 2023/10/21 00:05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CatchOpenAiError {

    public OpenAiErrorDetails error;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OpenAiErrorDetails {
        /**
         * Human-readable error message
         */
        String message;

        /**
         * OpenAI error type, for example "invalid_request_error"
         * https://platform.openai.com/docs/guides/error-codes/python-library-error-types
         */
        String type;

        String param;

        /**
         * OpenAI error code, for example "invalid_api_key"
         */
        String code;

        Integer rcode;
    }
}
