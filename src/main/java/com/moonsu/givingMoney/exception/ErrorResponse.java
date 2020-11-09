package com.moonsu.givingMoney.exception;

import org.springframework.http.HttpStatus;

public class ErrorResponse {
    private HttpStatus status;
    private int statusCode;
    private String message;
    private String errorCode;

    public ErrorResponse(GivingMoneyExceptionType exceptionType) {
        status = exceptionType.getHttpStatus();
        statusCode = exceptionType.getHttpStatus().value();
        message = exceptionType.name();
        errorCode = exceptionType.getErrorCode();
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
