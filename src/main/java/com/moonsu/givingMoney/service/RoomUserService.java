package com.moonsu.givingMoney.service;

import com.moonsu.givingMoney.exception.GivingMoneyException;
import com.moonsu.givingMoney.exception.GivingMoneyExceptionType;
import com.moonsu.givingMoney.model.Room;
import com.moonsu.givingMoney.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomUserService {

    private RoomRepository roomRepository;

    public RoomUserService(@Autowired RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public Room getJoinRoomByName(String roomName, long userId) {
        Room room = roomRepository.findByRoomName(roomName);
        if (room == null) {
            throw new GivingMoneyException(GivingMoneyExceptionType.NOT_EXIST_ROOM);
        }
        if (!room.hasUserId(userId)) {
            throw new GivingMoneyException(GivingMoneyExceptionType.NOT_ROOM_USER);
        }
        return room;
    }

}
