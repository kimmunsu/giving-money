package com.moonsu.givingMoney.repository;

import com.moonsu.givingMoney.model.GivingMoneyOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GivingMoneyOrderRepository extends JpaRepository<GivingMoneyOrder, Long> {
    List<GivingMoneyOrder> findAllByRoomId(long roomId);
    GivingMoneyOrder findByRoomIdAndToken(long roomId, String token);
}
