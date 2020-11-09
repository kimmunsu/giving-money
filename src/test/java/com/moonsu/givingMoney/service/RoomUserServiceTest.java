package com.moonsu.givingMoney.service;

import com.moonsu.givingMoney.exception.GivingMoneyException;
import com.moonsu.givingMoney.exception.GivingMoneyExceptionType;
import com.moonsu.givingMoney.model.Room;
import com.moonsu.givingMoney.model.User;
import com.moonsu.givingMoney.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomUserServiceTest {

    @InjectMocks
    private RoomUserService roomUserService;

    @Mock
    private RoomRepository roomRepository;

    @BeforeEach
    public void setup() {
        roomUserService = new RoomUserService(roomRepository);
    }

    @Test
    public void 참가중인_방_조회_테스트() {

        String roomName = "문수네";
        long userId = 1L;

        when(roomRepository.findByRoomName(roomName)).thenReturn(
                null,
                Room.create(1L, roomName, List.of(User.create(10L), User.create(2L))),
                Room.create(1L, roomName, List.of(User.create(1L), User.create(2L)))
        );
        try {
            roomUserService.getJoinRoomByName(roomName, userId);
            fail();
        } catch (GivingMoneyException exception) {
            assertEquals(exception.getExceptionType(), GivingMoneyExceptionType.NOT_EXIST_ROOM);
        }

        try {
            roomUserService.getJoinRoomByName(roomName, userId);
            fail();
        } catch (GivingMoneyException exception) {
            assertEquals(exception.getExceptionType(), GivingMoneyExceptionType.NOT_ROOM_USER);
        }

        Room room = roomUserService.getJoinRoomByName(roomName, userId);
        assertEquals(room.getRoomName(), roomName);
        assertTrue(room.hasUserId(userId));

    }

}