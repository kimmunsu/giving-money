package com.moonsu.givingMoney.model;

import javax.persistence.*;

@Entity
@Table(name = "room_user_map", uniqueConstraints = @UniqueConstraint(columnNames = {"room_id", "user_id"}))
@SequenceGenerator(
        name = "ROOM_USR_MAP_SEQ_GEN",
        sequenceName = "ROOM_USR_MAP_SEQ",
        initialValue = 1,
        allocationSize = 1)
public class RoomUserMap {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ROOM_USR_MAP_SEQ_GEN")
    @Column(name = "id")
    private long id;

    @Column(name = "room_id")
    private long roomId;

    @Column(name = "user_id")
    private long userId;

    public static RoomUserMap create(long roomId, long userId) {
        RoomUserMap roomUserMap = new RoomUserMap();
        roomUserMap.setRoomId(roomId);
        roomUserMap.setUserId(userId);
        return roomUserMap;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
