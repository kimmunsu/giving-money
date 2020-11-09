package com.moonsu.givingMoney.model;

import com.moonsu.givingMoney.exception.GivingMoneyException;
import com.moonsu.givingMoney.exception.GivingMoneyExceptionType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GivingMoneyOrderTest {

    @Test
    public void 뿌리기_시간_만료_체크_작동_테스트() {
        GivingMoneyOrder givingMoneyOrder = new GivingMoneyOrder();
        givingMoneyOrder.setCreateDtm(LocalDateTime.now().minusMinutes(GivingMoneyOrder.VALID_RECEIVE_TERM_MINUTE + 1));
        assertFalse(givingMoneyOrder.getReceiveExpireDtm().isAfter(LocalDateTime.now()));
    }

    @Test
    public void 받기_조회_테스트_비정상() {
        GivingMoneyOrder order = new GivingMoneyOrder();
        GivingMoneyReceive receive1 = GivingMoneyReceive.createForTest(1L, LocalDateTime.now().minusMinutes(5));
        GivingMoneyReceive receive2 = GivingMoneyReceive.createForTest(2L, LocalDateTime.now().minusMinutes(5));
        order.setGivingMoneyReceives(List.of(receive1, receive2));
        try {
            order.getNextGivingMoneyReceivable();
        } catch (GivingMoneyException gme) {
            assertEquals(gme.getExceptionType(), GivingMoneyExceptionType.EXCEED_RECEIVE_GIVING_MONEY);
        }

    }

    @Test
    public void 받기_조회_테스트_정상() {
        GivingMoneyOrder order = new GivingMoneyOrder();
        GivingMoneyReceive receive1 = GivingMoneyReceive.createForTest(1L, 1L, LocalDateTime.now().minusMinutes(5));
        GivingMoneyReceive receive2 = GivingMoneyReceive.createForTest(2L, 2L, LocalDateTime.now().minusMinutes(5));
        GivingMoneyReceive receive3 = GivingMoneyReceive.createForTest(3L, 0L, null);
        GivingMoneyReceive receive4 = GivingMoneyReceive.createForTest(4L, 0L, null);
        order.setGivingMoneyReceives(List.of(receive1, receive2, receive3, receive4));
        GivingMoneyReceive receivable1 = order.getNextGivingMoneyReceivable();
        assertEquals(receivable1.getId(), 3L);
        try {
            order.getNextGivingMoneyReceivable();
        } catch (GivingMoneyException gme) {
            assertEquals(gme.getExceptionType(), GivingMoneyExceptionType.EXCEED_RECEIVE_GIVING_MONEY);
        }
    }

}