package com.moonsu.givingMoney.service;

import com.moonsu.givingMoney.model.Room;
import com.moonsu.givingMoney.model.RoomUserMap;
import com.moonsu.givingMoney.model.User;
import com.moonsu.givingMoney.repository.RoomRepository;
import com.moonsu.givingMoney.repository.RoomUserMapRepository;
import com.moonsu.givingMoney.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Service
public class PrepareService {

    private RoomRepository roomRepository;
    private UserRepository userRepository;
    private RoomUserMapRepository roomUserMapRepository;

    public PrepareService(@Autowired RoomRepository roomRepository,
                          @Autowired UserRepository userRepository,
                          @Autowired RoomUserMapRepository roomUserMapRepository) {
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.roomUserMapRepository = roomUserMapRepository;
    }


    public void prepare() {
        for (int i=0; i<3; i++) {

            Room room = new Room();
            room.setRoomName(String.valueOf(i+1));

            for (int j=0; j<3; j++) {
                User user = new User();
                user.setRoom(room);
                room.addUser(user);
            }
            roomRepository.save(room);
        }

        List<Room> rooms = roomRepository.findAll();
        for (Room room : rooms) {
            List<User> users = room.getUsers();
            for (User user : users) {
                System.out.println("#### room id : " + room.getId() + " / user id : " + user.getId());
                roomUserMapRepository.save(RoomUserMap.create(room.getId(), user.getId()));
            }
        }

        System.out.println("#### END OF PREPARE ####");
    }
}
