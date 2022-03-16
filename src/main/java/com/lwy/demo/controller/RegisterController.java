package com.lwy.demo.controller;

import com.lwy.demo.entity.User;
import com.lwy.demo.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Administrator
 */
@CrossOrigin
@RestController
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    @PostMapping
    public boolean register(@RequestBody User user){
        return registerService.register(user);
    }
}
