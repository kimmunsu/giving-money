package com.moonsu.givingMoney.service;

import com.moonsu.givingMoney.model.GivingMoneyOrder;
import com.moonsu.givingMoney.repository.GivingMoneyOrderRepository;
import com.moonsu.givingMoney.util.TokenGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TokenGenerateServiceTest {

    @InjectMocks
    private TokenGenerateService tokenGenerateService;

    @Mock
    private TokenGenerator tokenGenerator = mock(TokenGenerator.class);

    @Mock
    private GivingMoneyOrderRepository givingMoneyOrderRepository = mock(GivingMoneyOrderRepository.class);

    @BeforeEach
    public void setup() {
        this.tokenGenerateService = new TokenGenerateService(tokenGenerator, givingMoneyOrderRepository);
    }

    @Test
    public void 고유_토큰_발급_테스트() {
        long roomId = 1L;

        List<String> existTokens = List.of("aaa","AAA","aAa","EWd");
        List<GivingMoneyOrder> givingMoneyOrders = new ArrayList();
        for (int i=0; i<existTokens.size(); i++) {
            givingMoneyOrders.add(GivingMoneyOrder.create(roomId, existTokens.get(i)));
        }

        when(tokenGenerator.generateGivingMoneyOrderToken()).thenReturn("aaa","AAA","aAa","FFF");
        when(givingMoneyOrderRepository.findAllByRoomId(roomId)).thenReturn(givingMoneyOrders);

        String token = tokenGenerateService.generateToken(roomId);
        assertNotNull(token);
        assertEquals(token, "FFF");
    }

    @Test
    public void 고유_토큰_발급_테스트_처음_뿌리기_만들시() {
        long roomId = 1L;
        when(tokenGenerator.generateGivingMoneyOrderToken()).thenReturn("asf");
        when(givingMoneyOrderRepository.findAllByRoomId(roomId)).thenReturn(Collections.emptyList());
        String token = tokenGenerateService.generateToken(roomId);
        assertNotNull(token);
        assertEquals(token, "asf");
    }

}