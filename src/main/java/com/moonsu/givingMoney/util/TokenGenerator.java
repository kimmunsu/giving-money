package com.moonsu.givingMoney.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public class TokenGenerator {

    private static final int GIVING_MONEY_ORDER_TOKEN_LENGTH = 3;

    public String generateGivingMoneyOrderToken() {
        return RandomStringUtils.randomAlphanumeric(GIVING_MONEY_ORDER_TOKEN_LENGTH);
    }

}
