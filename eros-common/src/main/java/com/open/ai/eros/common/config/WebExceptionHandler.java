package com.open.ai.eros.common.config;

import com.open.ai.eros.common.exception.AIException;
import com.open.ai.eros.common.exception.BizException;
import com.open.ai.eros.common.exception.SeeConnectException;
import com.open.ai.eros.common.vo.ResultVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class WebExceptionHandler {


    private final static Logger logger = LoggerFactory.getLogger(WebExceptionHandler.class);


    @ExceptionHandler(AIException.class)
    public ResponseEntity<ResultVO<Void>> handleAIBusinessException(AIException exception) {
        logger.error("handleAIBusinessException AIException", exception);
        ResultVO<Void> result = ResultVO.fail(exception.getCode(), exception.getMessage());
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(SeeConnectException.class)
    public ResponseEntity<ResultVO<Void>> handleSeeConnectException(SeeConnectException exception) {
        logger.error("SeeConnectException exception", exception);
        ResultVO<Void> result = ResultVO.fail(exception.getCode(), exception.getMessage());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }



    @ExceptionHandler(BizException.class)
    public ResponseEntity<ResultVO<Void>> handleBusinessException(BizException exception) {
        logger.error("business exception", exception);
        ResultVO<Void> result = ResultVO.fail(exception.getCode(), exception.getMessage());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResultVO<Void>> handleIllegalArgumentException(IllegalArgumentException exception) {
        logger.error("process exception", exception);
        ResultVO<Void> result = ResultVO.fail(exception.getMessage());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResultVO<Void>> handleSystemException(Exception exception) {
        logger.error("system exception", exception);
        ResultVO<Void> result = ResultVO.fail(exception.getMessage());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ResultVO<Void>> handleMethodArgumentNotValidException(Exception validateException) {
        logger.error("method argument not valid exception", validateException);
        String errorMsg = getErrorMessage(validateException);
        ResultVO<Void> result = ResultVO.fail(errorMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    private String getErrorMessage(Exception exception) {
        FieldError fieldError = null;
        if(exception instanceof MethodArgumentNotValidException) {
            fieldError = ((MethodArgumentNotValidException) exception).getBindingResult().getFieldError();
        }
        else if(exception instanceof BindException) {
            fieldError = ((BindException) exception).getBindingResult().getFieldError();
        }
        if(fieldError == null) {
            return "参数校验错误";
        }
        return fieldError.getDefaultMessage();
    }

}
