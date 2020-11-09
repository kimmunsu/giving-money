package com.moonsu.givingMoney.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DistributorTest {

    @Test
    public void 분배테스트() {

        int receiveCount = 5;
        int money = 500;
        List<Integer> result = DistributorUtil.distribute(receiveCount, money);
        assertEquals(receiveCount, result.size());
        assertEquals(money, result.stream().mapToInt(Integer::intValue).sum());

        receiveCount = 1;
        money = 500;
        result = DistributorUtil.distribute(receiveCount, money);
        assertEquals(receiveCount, result.size());
        assertEquals(money, result.stream().mapToInt(Integer::intValue).sum());

        receiveCount = 3;
        money = 500;
        result = DistributorUtil.distribute(receiveCount, money);
        assertEquals(receiveCount, result.size());
        assertEquals(money, result.stream().mapToInt(Integer::intValue).sum());

        receiveCount = 5;
        money = 2;
        result = DistributorUtil.distribute(receiveCount, money);
        assertEquals(receiveCount, result.size());
        assertEquals(money, result.stream().mapToInt(Integer::intValue).sum());
    }

}