package com.moonsu.givingMoney.dto;

import com.moonsu.givingMoney.model.GivingMoneyOrder;
import com.moonsu.givingMoney.model.GivingMoneyReceive;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GivingMoneySummaryDto {
    private LocalDateTime orderDtm;
    private int totalMoney;
    private List<GivingMoneyReceiveDto> givingMoneyReceiveDtos;

    public static GivingMoneySummaryDto create(GivingMoneyOrder order) {
        GivingMoneySummaryDto summaryDto = new GivingMoneySummaryDto();

        summaryDto.setOrderDtm(order.getCreateDtm());
        summaryDto.setTotalMoney(order.getGivingMoneyReceives().stream().mapToInt(GivingMoneyReceive::getMoney).sum());
        summaryDto.setGivingMoneyReceiveDtos(new ArrayList<>());

        for (GivingMoneyReceive receive : order.getGivingMoneyReceives()) {
            if (receive.getReceiveUserId() != 0 && receive.getReceivedDtm() != null) {
                summaryDto.getGivingMoneyReceiveDtos().add(GivingMoneyReceiveDto.create(receive));
            }
        }
        return summaryDto;
    }

    public LocalDateTime getOrderDtm() {
        return orderDtm;
    }

    public void setOrderDtm(LocalDateTime orderDtm) {
        this.orderDtm = orderDtm;
    }

    public int getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(int totalMoney) {
        this.totalMoney = totalMoney;
    }

    public List<GivingMoneyReceiveDto> getGivingMoneyReceiveDtos() {
        return givingMoneyReceiveDtos;
    }

    public void setGivingMoneyReceiveDtos(List<GivingMoneyReceiveDto> givingMoneyReceiveDtos) {
        this.givingMoneyReceiveDtos = givingMoneyReceiveDtos;
    }
}
