package com.moonsu.givingMoney.model;

import javax.persistence.*;

@Entity
@Table(name = "user")
@SequenceGenerator(
        name = "USER_SEQ_GEN",
        sequenceName = "USER_SEQ",
        initialValue = 1,
        allocationSize = 1)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_SEQ_GEN")
    @Column(name = "id")
    private long id;

    @ManyToOne
    @JoinColumn(name="room_id")
    private Room room;

    public static User create(long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}
