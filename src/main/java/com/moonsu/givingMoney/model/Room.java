package com.moonsu.givingMoney.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "room")
@SequenceGenerator(
        name = "ROOM_SEQ_GEN",
        sequenceName = "ROOM_SEQ",
        initialValue = 1,
        allocationSize = 1)
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ROOM_SEQ_GEN")
    @Column(name = "id")
    private long id;

    @Column(name = "room_name")
    private String roomName;    // unique (=id)

    @OneToMany(mappedBy = "room", cascade = CascadeType.PERSIST)
    private List<User> users = new ArrayList<>();

    public static Room create(long id, String roomName, List<User> users) {
        Room room = new Room();
        room.setId(id);
        room.setRoomName(roomName);
        room.setUsers(users);
        return room;
    }

    public boolean hasUserId(long userId) {
        for (User user: getUsers()) {
            if (user.getId() == userId) {
                return true;
            }
        }
        return false;
    }

    public void addUser(User user) {
        this.users.add(user);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

}
