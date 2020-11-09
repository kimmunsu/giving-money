package com.moonsu.givingMoney.aspect;

import com.moonsu.givingMoney.exception.ErrorResponse;
import com.moonsu.givingMoney.exception.GivingMoneyException;
import com.moonsu.givingMoney.exception.GivingMoneyExceptionType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ExceptionAdvice extends ResponseEntityExceptionHandler {
    /**
     * catch 되지 않은 오류 타입에 대한 500 응답
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> throwable(Throwable e) {
        logger.error(e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse(GivingMoneyExceptionType.INTERNAL_ERROR);
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    /**
     * 캐치된 서버 내부 오류
     */
    @ExceptionHandler(GivingMoneyException.class)
    public ResponseEntity<ErrorResponse> givingMoneyApiInternalError(GivingMoneyException e) {
        logger.error(e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse(e.getExceptionType());
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

}
