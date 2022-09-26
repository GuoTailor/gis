package com.gyh.gis.config;

import com.gyh.gis.dto.ResponseInfo;
import com.gyh.gis.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理，处理可预见的异常
 *
 * @author zqj
 */
@Slf4j
@RestControllerAdvice
public class RestExceptionTranslator {

    @ExceptionHandler(BusinessException.class)
    public ResponseInfo<Void> handleError(BusinessException e) {
        log.error("业务异常:{}", e.getMessage());
        return ResponseInfo.failed(e.getMessage());
    }

}
