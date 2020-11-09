package com.moonsu.givingMoney.repository;

import com.moonsu.givingMoney.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Room findByRoomName(String roomName);
}
