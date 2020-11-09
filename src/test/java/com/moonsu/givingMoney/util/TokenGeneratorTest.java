package com.moonsu.givingMoney.util;

import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TokenGeneratorTest {

    @Test
    public void 뿌리기_토큰_테스트() {
        TokenGenerator tokenGenerator = new TokenGenerator();
        String token = tokenGenerator.generateGivingMoneyOrderToken();
        LoggerFactory.getLogger(TokenGeneratorTest.class).debug("### GENERATED TOKEN : {}", token);
        assertEquals(token.length(), 3);
    }

}