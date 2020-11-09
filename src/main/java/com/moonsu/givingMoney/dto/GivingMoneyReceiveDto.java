package com.moonsu.givingMoney.dto;

import com.moonsu.givingMoney.model.GivingMoneyReceive;

import java.util.Objects;

public class GivingMoneyReceiveDto {
    private int receiveMoney;
    private long receiveUserId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GivingMoneyReceiveDto that = (GivingMoneyReceiveDto) o;
        return receiveMoney == that.receiveMoney && receiveUserId == that.receiveUserId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(receiveMoney, receiveUserId);
    }

    public static GivingMoneyReceiveDto create(GivingMoneyReceive receive) {
        GivingMoneyReceiveDto dto = new GivingMoneyReceiveDto();

        dto.setReceiveMoney(receive.getMoney());
        dto.setReceiveUserId(receive.getReceiveUserId());

        return dto;
    }

    public int getReceiveMoney() {
        return receiveMoney;
    }

    public void setReceiveMoney(int receiveMoney) {
        this.receiveMoney = receiveMoney;
    }

    public long getReceiveUserId() {
        return receiveUserId;
    }

    public void setReceiveUserId(long receiveUserId) {
        this.receiveUserId = receiveUserId;
    }
}
