package com.moonsu.givingMoney.dto;

public class GivingMoneyCreateRequest {
    private int receivableCount;
    private int money;

    public int getReceivableCount() {
        return receivableCount;
    }

    public void setReceivableCount(int receivableCount) {
        this.receivableCount = receivableCount;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }
}
