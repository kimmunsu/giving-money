package com.moonsu.givingMoney.controller;

import com.moonsu.givingMoney.service.PrepareService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/giving-money")
public class PrepareController {

    private PrepareService prepareService;

    private PrepareController(@Autowired PrepareService prepareService) {
        this.prepareService = prepareService;
    }

    @ApiOperation(value = "룸, 사용자, 룸-사용자 맵핑 data insert")
    @PostMapping("/prepare")
    public HttpStatus prepare() {
        System.out.println("#### ENTER THE PREPARE");
        prepareService.prepare();
        System.out.println("#### END OF PREPARE");
        return HttpStatus.OK;
    }

}
