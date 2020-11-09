package com.moonsu.givingMoney.exception;

public class GivingMoneyException extends RuntimeException {

    private GivingMoneyExceptionType exceptionType;

    public GivingMoneyException(GivingMoneyExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    public GivingMoneyExceptionType getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(GivingMoneyExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

}
