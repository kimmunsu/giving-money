package com.moonsu.givingMoney.service;

import com.moonsu.givingMoney.dto.GivingMoneySummaryDto;
import com.moonsu.givingMoney.exception.GivingMoneyException;
import com.moonsu.givingMoney.exception.GivingMoneyExceptionType;
import com.moonsu.givingMoney.model.GivingMoneyOrder;
import com.moonsu.givingMoney.model.GivingMoneyReceive;
import com.moonsu.givingMoney.model.Room;
import com.moonsu.givingMoney.repository.GivingMoneyOrderRepository;
import com.moonsu.givingMoney.util.DistributorUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Service
@Transactional
public class GivingMoneyService {

    private static final String RECEIVE_LOCK_KEY = "receive_lock_key";

    private TokenGenerateService tokenGenerateService;
    private GivingMoneyOrderRepository givingMoneyOrderRepository;
    private RoomUserService roomUserService;
    private RedissonClient redissonClient;

    public GivingMoneyService(
            @Autowired TokenGenerateService tokenGenerateService,
            @Autowired GivingMoneyOrderRepository givingMoneyOrderRepository,
            @Autowired RoomUserService roomUserService,
            @Autowired RedissonClient redissonClient) {
        this.tokenGenerateService = tokenGenerateService;
        this.givingMoneyOrderRepository = givingMoneyOrderRepository;
        this.roomUserService = roomUserService;
        this.redissonClient = redissonClient;
    }

    public String createOrder(String roomName, long givingUserId, int receivableCount, int money) {
        Room room = roomUserService.getJoinRoomByName(roomName, givingUserId);
        if (!room.hasUserId(givingUserId)) {
            throw new GivingMoneyException(GivingMoneyExceptionType.NOT_ROOM_USER);
        }
        if (room.getUsers().size() - 1 < receivableCount) {
            throw new GivingMoneyException(GivingMoneyExceptionType.EXCEED_RECEIVE_COUNT_MORE_THEN_ROOM_USER_COUNT);
        }

        LocalDateTime now = LocalDateTime.now();
        String token = tokenGenerateService.generateToken(room.getId());
        GivingMoneyOrder order = GivingMoneyOrder.create(room.getId(), givingUserId, receivableCount, token, now);
        List<Integer> distributedMoneys = DistributorUtil.distribute(receivableCount, money);
        for (Integer distributedMoney : distributedMoneys) {
            GivingMoneyReceive givingMoneyReceive = new GivingMoneyReceive();
            givingMoneyReceive.setMoney(distributedMoney);
            givingMoneyReceive.setGivingMoneyOrder(order);
            order.getGivingMoneyReceives().add(givingMoneyReceive);
        }

        givingMoneyOrderRepository.save(order);
        return order.getToken();
    }

    public int receive(long userId, String roomName, String token) {
        Room room = roomUserService.getJoinRoomByName(roomName, userId);
        GivingMoneyOrder order = givingMoneyOrderRepository.findByRoomIdAndToken(room.getId(), token);
        if (order == null) {
            throw new GivingMoneyException(GivingMoneyExceptionType.NOT_EXIST_GIVING_MONEY_ORDER);
        }
        if (order.getGivingUserId() == userId) {
            throw new GivingMoneyException(GivingMoneyExceptionType.NOT_ABLE_RECEIVE_CREATE_USER);
        }
        if (order.getReceiveExpireDtm().isBefore(LocalDateTime.now())) {
            throw new GivingMoneyException(GivingMoneyExceptionType.EXPIRED_GIVING_MONEY);
        }
        if (order.isAleadyReceived(userId)) {
            throw new GivingMoneyException(GivingMoneyExceptionType.ALREADY_RECEIVED_GIVING_MONEY);
        }

        RLock receiveLock = redissonClient.getLock(RECEIVE_LOCK_KEY);
        try {
            if (receiveLock.tryLock(500L,500L, TimeUnit.MILLISECONDS)) {
                GivingMoneyReceive receivable = order.getNextGivingMoneyReceivable();
                receivable.setReceivedDtm(LocalDateTime.now());
                receivable.setReceiveUserId(userId);
                return receivable.getMoney();
            }
        } catch (InterruptedException ite) {
            throw new GivingMoneyException(GivingMoneyExceptionType.INTERNAL_ERROR);
        } finally {
            receiveLock.unlock();
        }

        throw new GivingMoneyException(GivingMoneyExceptionType.INTERNAL_ERROR);

    }

    public GivingMoneySummaryDto getGivingMoneySummaryDto(long userId, String roomName, String token) {
        Room room = roomUserService.getJoinRoomByName(roomName, userId);
        GivingMoneyOrder order = givingMoneyOrderRepository.findByRoomIdAndToken(room.getId(), token);
        if (order == null) {
            throw new GivingMoneyException(GivingMoneyExceptionType.NOT_EXIST_GIVING_MONEY_ORDER);
        }
        if (order.getGivingUserId() != userId) {
            throw new GivingMoneyException(GivingMoneyExceptionType.ACCESS_DENIED_GIVING_MONEY_ORDER);
        }
        if (order.getSearchExpireDtm().isBefore(LocalDateTime.now())) {
            throw new GivingMoneyException(GivingMoneyExceptionType.NOT_EXIST_GIVING_MONEY_ORDER);
        }

        return GivingMoneySummaryDto.create(order);
    }

}
