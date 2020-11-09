package com.moonsu.givingMoney.controller;

import com.moonsu.givingMoney.dto.GivingMoneyCreateRequest;
import com.moonsu.givingMoney.dto.GivingMoneySummaryDto;
import com.moonsu.givingMoney.service.GivingMoneyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/giving-money")
public class GivingMoneyController {
    private GivingMoneyService givingMoneyService;
    public GivingMoneyController(@Autowired GivingMoneyService givingMoneyService) {
        this.givingMoneyService = givingMoneyService;
    }

    @PutMapping
    public String order(@RequestHeader("X-USER-ID") long userId,
                                    @RequestHeader("X-ROOM-ID") String roomId,
                                    @RequestBody GivingMoneyCreateRequest request) {
        return givingMoneyService.createOrder(roomId, userId, request.getReceivableCount(), request.getMoney());
    }

    @PostMapping
    public int receive(@RequestHeader("X-USER-ID") long userId,
                       @RequestHeader("X-ROOM-ID") String roomId,
                       @RequestParam("token") String token) {
        return givingMoneyService.receive(userId, roomId, token);
    }

    @GetMapping
    public GivingMoneySummaryDto getGivingMoneySummaryDto(@RequestHeader("X-USER-ID") long userId,
                                               @RequestHeader("X-ROOM-ID") String roomId,
                                               @RequestParam("token") String token) {
        return givingMoneyService.getGivingMoneySummaryDto(userId, roomId, token);
    }

}
