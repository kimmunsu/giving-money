package com.moonsu.givingMoney.exception;

import org.springframework.http.HttpStatus;

public enum GivingMoneyExceptionType {
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GME_000000")
    , EXCEED_RECEIVE_GIVING_MONEY(HttpStatus.BAD_REQUEST, "GME_000001")
    , NOT_EXIST_ROOM(HttpStatus.NOT_FOUND, "GME_000002")
    , NOT_ROOM_USER(HttpStatus.BAD_REQUEST, "GME_000003")
    , EXCEED_RECEIVE_COUNT_MORE_THEN_ROOM_USER_COUNT(HttpStatus.BAD_REQUEST, "GME_000004")
    , NOT_EXIST_GIVING_MONEY_ORDER(HttpStatus.NOT_FOUND, "GME_000005")
    , NOT_ABLE_RECEIVE_CREATE_USER(HttpStatus.BAD_REQUEST, "GME_000006")
    , EXPIRED_GIVING_MONEY(HttpStatus.BAD_REQUEST, "GME_000007")
    , ALREADY_RECEIVED_GIVING_MONEY(HttpStatus.BAD_REQUEST, "GME_000008")
    , ACCESS_DENIED_GIVING_MONEY_ORDER(HttpStatus.UNAUTHORIZED, "GME_000009");

    private HttpStatus httpStatus;
    private String errorCode;

    GivingMoneyExceptionType(HttpStatus httpStatus, String errorCode) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
