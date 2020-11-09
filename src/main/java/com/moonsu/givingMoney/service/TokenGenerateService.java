package com.moonsu.givingMoney.service;

import com.moonsu.givingMoney.model.GivingMoneyOrder;
import com.moonsu.givingMoney.repository.GivingMoneyOrderRepository;
import com.moonsu.givingMoney.repository.RoomRepository;
import com.moonsu.givingMoney.util.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TokenGenerateService {

    private TokenGenerator tokenGenerator;
    private GivingMoneyOrderRepository givingMoneyOrderRepository;

    public TokenGenerateService(@Autowired TokenGenerator tokenGenerator,
                                @Autowired GivingMoneyOrderRepository givingMoneyOrderRepository) {
        this.tokenGenerator = tokenGenerator;
        this.givingMoneyOrderRepository = givingMoneyOrderRepository;
    }

    public String generateToken(long roomId) {

        String token = tokenGenerator.generateGivingMoneyOrderToken();
        while (existToken(roomId, token)) {
            token = tokenGenerator.generateGivingMoneyOrderToken();
        }

        return token;
    }

    private boolean existToken(long roomId, String token) {
        List<GivingMoneyOrder> givingMoneyOrders = givingMoneyOrderRepository.findAllByRoomId(roomId);
        for (GivingMoneyOrder order: givingMoneyOrders) {
            if (order.getToken().equals(token)) {
                return true;
            }

        }
        return false;
    }
}
