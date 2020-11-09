package com.moonsu.givingMoney.service;

import com.moonsu.givingMoney.dto.GivingMoneyReceiveDto;
import com.moonsu.givingMoney.dto.GivingMoneySummaryDto;
import com.moonsu.givingMoney.exception.GivingMoneyException;
import com.moonsu.givingMoney.exception.GivingMoneyExceptionType;
import com.moonsu.givingMoney.model.GivingMoneyOrder;
import com.moonsu.givingMoney.model.GivingMoneyReceive;
import com.moonsu.givingMoney.model.Room;
import com.moonsu.givingMoney.model.User;
import com.moonsu.givingMoney.repository.GivingMoneyOrderRepository;
import com.moonsu.givingMoney.util.DistributorUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GivingMoneyServiceTest {

    @InjectMocks
    private GivingMoneyService givingMoneyService;

    @Mock
    private TokenGenerateService tokenGenerateService;
    @Mock
    private GivingMoneyOrderRepository givingMoneyOrderRepository;
    @Mock
    private RoomUserService roomUserService;
    @Mock
    private RedissonClient redissonClient;

    @BeforeEach
    public void setup() {
        givingMoneyService = new GivingMoneyService(tokenGenerateService, givingMoneyOrderRepository, roomUserService, redissonClient);
    }

    @Test
    public void 뿌리기방_방_valid_check() {
        String roomName = "문수네";
        long userId = 1L;
        int receivableCount = 5;
        int money = 500;

        when (roomUserService.getJoinRoomByName(roomName, userId)).thenReturn(Room.create(1L, roomName, List.of(User.create(1L), User.create(2L))));

        try {
            givingMoneyService.createOrder(roomName, userId, receivableCount, money);
            fail();
        } catch (GivingMoneyException exception) {
            assertEquals(exception.getExceptionType(), GivingMoneyExceptionType.EXCEED_RECEIVE_COUNT_MORE_THEN_ROOM_USER_COUNT);
        }
    }

    @Test
    public void 뿌리기방생성() {
        String roomName = "문수네";
        long userId = 1L;
        int receivableCount = 5;
        int money = 500;

        User givingUser = User.create(userId);
        User receiverUser1 = User.create(2L);
        User receiverUser2 = User.create(3L);
        User receiverUser3 = User.create(4L);
        User receiverUser4 = User.create(5L);
        User receiverUser5 = User.create(6L);
        when (roomUserService.getJoinRoomByName(roomName, userId)).thenReturn(Room.create(1L, roomName, List.of(givingUser, receiverUser1, receiverUser2, receiverUser3, receiverUser4, receiverUser5)));

        GivingMoneyOrder order = GivingMoneyOrder.create(1L, userId, receivableCount, "tok", LocalDateTime.now());
        List<Integer> distributedMoneys = DistributorUtil.distribute(receivableCount, money);
        for (Integer distributedMoney : distributedMoneys) {
            GivingMoneyReceive givingMoneyReceive = new GivingMoneyReceive();
            givingMoneyReceive.setMoney(distributedMoney);
            order.getGivingMoneyReceives().add(givingMoneyReceive);
        }

        when(tokenGenerateService.generateToken(1L)).thenReturn("tok");
        when(givingMoneyOrderRepository.save(any())).thenReturn(order);
        String token = givingMoneyService.createOrder(roomName, userId, receivableCount, money);
        assertEquals(token.length(), 3);

        verify(givingMoneyOrderRepository, Mockito.times(1)).save(any());
    }

    @Test
    public void 수령_뿌리기_토큰으로_뿌리기_못찾을때_예외() {
        long userId = 2L;
        String roomName = "1";
        String token = "asc";
        long roomId = 1L;
        when (roomUserService.getJoinRoomByName(roomName, userId)).thenReturn(Room.create(roomId, roomName, List.of(User.create(1L), User.create(2L))));
        when(givingMoneyOrderRepository.findByRoomIdAndToken(roomId, token)).thenReturn(null);
        try {
            givingMoneyService.receive(userId, roomName, token);
            fail();
        } catch (GivingMoneyException exception) {
            assertEquals(exception.getExceptionType(), GivingMoneyExceptionType.NOT_EXIST_GIVING_MONEY_ORDER);
        }
    }

    @Test
    public void 수령_뿌리기_생성자_수령시_예외() {
        long userId = 1L;
        String roomName = "1";
        String token = "asc";
        long roomId = 1L;
        when (roomUserService.getJoinRoomByName(roomName, userId)).thenReturn(Room.create(roomId, roomName, List.of(User.create(1L), User.create(2L))));

        GivingMoneyOrder order = makeOrder(roomId, userId, 3, token, 500);

        when(givingMoneyOrderRepository.findByRoomIdAndToken(roomId, token)).thenReturn(order);
        try {
            givingMoneyService.receive(userId, roomName, token);
            fail();
        } catch (GivingMoneyException exception) {
            assertEquals(exception.getExceptionType(), GivingMoneyExceptionType.NOT_ABLE_RECEIVE_CREATE_USER);
        }
    }

    @Test
    public void 수령할_대상이_없을때_예외() throws InterruptedException {
        long userId = 5L;
        String roomName = "1";
        String token = "asc";
        long roomId = 1L;
        when (roomUserService.getJoinRoomByName(roomName, userId)).thenReturn(Room.create(roomId, roomName, List.of(User.create(1L), User.create(2L), User.create(3L), User.create(4L), User.create(5L))));

        GivingMoneyOrder order = makeOrder(roomId, 1L, 3, token, 500);
        List<GivingMoneyReceive> receives = order.getGivingMoneyReceives();
        receives.get(0).setReceiveUserId(2L);
        receives.get(0).setReceivedDtm(LocalDateTime.now());
        receives.get(1).setReceiveUserId(3L);
        receives.get(1).setReceivedDtm(LocalDateTime.now());
        receives.get(2).setReceiveUserId(4L);
        receives.get(2).setReceivedDtm(LocalDateTime.now());

        when(givingMoneyOrderRepository.findByRoomIdAndToken(roomId, token)).thenReturn(order);
        RLock mockRLock = mock(RLock.class);
        when(redissonClient.getLock("receive_lock_key")).thenReturn(mockRLock);
        when(mockRLock.tryLock(500L, 500L, TimeUnit.MILLISECONDS)).thenReturn(true);
        try {
            givingMoneyService.receive(userId, roomName, token);
            fail();
        } catch (GivingMoneyException exception) {
            assertEquals(exception.getExceptionType(), GivingMoneyExceptionType.EXCEED_RECEIVE_GIVING_MONEY);
        }
    }

    @Test
    public void 수령_뿌리기_만료_예외() {
        long userId = 5L;
        String roomName = "1";
        String token = "asc";
        long roomId = 1L;
        when (roomUserService.getJoinRoomByName(roomName, userId)).thenReturn(Room.create(roomId, roomName, List.of(User.create(1L), User.create(2L), User.create(3L), User.create(4L), User.create(5L))));

        GivingMoneyOrder order = makeOrder(roomId, 1L, 3, token, 500);
        order.setCreateDtm(LocalDateTime.now().minusMinutes(GivingMoneyOrder.VALID_RECEIVE_TERM_MINUTE + 1));
        when(givingMoneyOrderRepository.findByRoomIdAndToken(roomId, token)).thenReturn(order);
        try {
            givingMoneyService.receive(userId, roomName, token);
            fail();
        } catch (GivingMoneyException exception) {
            assertEquals(exception.getExceptionType(), GivingMoneyExceptionType.EXPIRED_GIVING_MONEY);
        }
    }

    @Test
    public void 수령_수령_1회_초과_시도_예외() {
        long userId = 2L;
        String roomName = "1";
        String token = "asc";
        long roomId = 1L;
        when(roomUserService.getJoinRoomByName(roomName, userId)).thenReturn(Room.create(roomId, roomName, List.of(User.create(1L), User.create(2L), User.create(3L), User.create(4L), User.create(5L))));

        GivingMoneyOrder order = makeOrder(roomId, 1L, 3, token, 500);
        List<GivingMoneyReceive> receives = order.getGivingMoneyReceives();
        receives.get(0).setReceiveUserId(2L);
        receives.get(0).setReceivedDtm(LocalDateTime.now());
        receives.get(1).setReceiveUserId(3L);
        receives.get(1).setReceivedDtm(LocalDateTime.now());
        receives.get(2).setReceiveUserId(4L);
        receives.get(2).setReceivedDtm(LocalDateTime.now());

        when(givingMoneyOrderRepository.findByRoomIdAndToken(roomId, token)).thenReturn(order);
        try {
            givingMoneyService.receive(userId, roomName, token);
            fail();
        } catch (GivingMoneyException exception) {
            assertEquals(exception.getExceptionType(), GivingMoneyExceptionType.ALREADY_RECEIVED_GIVING_MONEY);
        }
    }

    @Test
    public void 수령_정상() throws InterruptedException {
        long userId = 5L;
        String roomName = "1";
        String token = "asc";
        long roomId = 1L;
        when(roomUserService.getJoinRoomByName(roomName, userId)).thenReturn(Room.create(roomId, roomName, List.of(User.create(1L), User.create(2L), User.create(3L), User.create(4L), User.create(5L))));

        GivingMoneyOrder order = makeOrder(roomId, 1L, 4, token, 500);
        List<GivingMoneyReceive> receives = order.getGivingMoneyReceives();
        receives.get(0).setReceiveUserId(2L);
        receives.get(0).setReceivedDtm(LocalDateTime.now());
        receives.get(1).setReceiveUserId(3L);
        receives.get(1).setReceivedDtm(LocalDateTime.now());
        receives.get(2).setReceiveUserId(4L);
        receives.get(2).setReceivedDtm(LocalDateTime.now());
        when(givingMoneyOrderRepository.findByRoomIdAndToken(roomId, token)).thenReturn(order);
        RLock mockRLock = mock(RLock.class);
        when(redissonClient.getLock("receive_lock_key")).thenReturn(mockRLock);
        when(mockRLock.tryLock(500L, 500L, TimeUnit.MILLISECONDS)).thenReturn(true);
        int money = givingMoneyService.receive(userId, roomName, token);

        assertEquals(money, receives.get(3).getMoney());
    }

    private GivingMoneyOrder makeOrder(long roomId, long userId, int receivableCount, String token, int money) {
        GivingMoneyOrder order = GivingMoneyOrder.create(roomId, userId, receivableCount, token, LocalDateTime.now());

        List<Integer> distributedMoneys = DistributorUtil.distribute(receivableCount, money);
        for (Integer distributedMoney : distributedMoneys) {
            GivingMoneyReceive givingMoneyReceive = new GivingMoneyReceive();
            givingMoneyReceive.setMoney(distributedMoney);
            order.getGivingMoneyReceives().add(givingMoneyReceive);
        }

        return order;
    }

    @Test
    public void 조회_예외_뿌리기_토큰으로_뿌리기_조회_실패() {
        String roomName = "문수네";
        long userId = 1L;
        String token = "asc";
        long roomId = 1L;

        when (roomUserService.getJoinRoomByName(roomName, userId)).thenReturn(Room.create(1L, roomName, List.of(User.create(userId), User.create(2L), User.create(3L), User.create(4L), User.create(5L), User.create(6L))));
        when(givingMoneyOrderRepository.findByRoomIdAndToken(roomId, token)).thenReturn(null);

        try {
            givingMoneyService.getGivingMoneySummaryDto(userId, roomName, token);
            fail();
        } catch (GivingMoneyException exception) {
            assertEquals(exception.getExceptionType(), GivingMoneyExceptionType.NOT_EXIST_GIVING_MONEY_ORDER);
        }
    }

    @Test
    public void 조회_예외_뿌리기_생성자가_아닌_사용자가_조회_실패() {
        String roomName = "문수네";
        long userId = 1L;
        String token = "asc";
        long roomId = 1L;

        when (roomUserService.getJoinRoomByName(roomName, userId)).thenReturn(Room.create(1L, roomName, List.of(User.create(userId), User.create(2L), User.create(3L), User.create(4L), User.create(5L), User.create(6L))));

        GivingMoneyOrder order = makeOrder(roomId, 2L, 4, token, 500);
        when(givingMoneyOrderRepository.findByRoomIdAndToken(roomId, token)).thenReturn(order);

        try {
            givingMoneyService.getGivingMoneySummaryDto(userId, roomName, token);
            fail();
        } catch (GivingMoneyException exception) {
            assertEquals(exception.getExceptionType(), GivingMoneyExceptionType.ACCESS_DENIED_GIVING_MONEY_ORDER);
        }
    }

    @Test
    public void 조회_예외_뿌리기_조회_시간_만료_실패() {
        String roomName = "문수네";
        long userId = 1L;
        String token = "asc";
        long roomId = 1L;

        when (roomUserService.getJoinRoomByName(roomName, userId)).thenReturn(Room.create(1L, roomName, List.of(User.create(userId), User.create(2L), User.create(3L), User.create(4L), User.create(5L), User.create(6L))));

        GivingMoneyOrder order = makeOrder(roomId, 1L, 4, token, 500);
        order.setCreateDtm(order.getReceiveExpireDtm().minusDays(GivingMoneyOrder.VALID_SEARCH_TERM_DAYS + 1));
        when(givingMoneyOrderRepository.findByRoomIdAndToken(roomId, token)).thenReturn(order);

        try {
            givingMoneyService.getGivingMoneySummaryDto(userId, roomName, token);
            fail();
        } catch (GivingMoneyException exception) {
            assertEquals(exception.getExceptionType(), GivingMoneyExceptionType.NOT_EXIST_GIVING_MONEY_ORDER);
        }
    }

    @Test
    public void 조회_정상() {
        String roomName = "문수네";
        long userId = 1L;
        String token = "asc";
        long roomId = 1L;

        when (roomUserService.getJoinRoomByName(roomName, userId)).thenReturn(Room.create(1L, roomName, List.of(User.create(userId), User.create(2L), User.create(3L), User.create(4L), User.create(5L), User.create(6L))));

        GivingMoneyOrder order = makeOrder(roomId, 1L, 4, token, 500);
        order.setCreateDtm(LocalDateTime.now());
        List<GivingMoneyReceive> receives = order.getGivingMoneyReceives();
        receives.get(0).setReceiveUserId(2L);
        receives.get(0).setReceivedDtm(LocalDateTime.now());
        receives.get(1).setReceiveUserId(3L);
        receives.get(1).setReceivedDtm(LocalDateTime.now());

        when(givingMoneyOrderRepository.findByRoomIdAndToken(roomId, token)).thenReturn(order);

        GivingMoneySummaryDto summaryDto = givingMoneyService.getGivingMoneySummaryDto(userId, roomName, token);
        assertEquals(summaryDto.getOrderDtm(), order.getCreateDtm());
        assertEquals(summaryDto.getTotalMoney(), order.getGivingMoneyReceives().stream().mapToInt(GivingMoneyReceive::getMoney).sum());
        System.out.println("##### " + order.getGivingMoneyReceives().stream().mapToInt(GivingMoneyReceive::getMoney).sum());

        List<GivingMoneyReceiveDto> filtered = order.getGivingMoneyReceives().stream()
                .filter(it -> ((it.getReceiveUserId() != 0) && (it.getReceivedDtm() != null)))
                .map(GivingMoneyReceiveDto::create)
                .collect(Collectors.toList());
        assertEquals(summaryDto.getGivingMoneyReceiveDtos(), filtered);
    }

}