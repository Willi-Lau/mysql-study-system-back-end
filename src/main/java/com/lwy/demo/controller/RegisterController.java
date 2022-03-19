package com.lwy.demo.controller;

import com.lwy.demo.TO.ResultDTO;
import com.lwy.demo.entity.User;
import com.lwy.demo.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Administrator
 */
@CrossOrigin
@RestController
@RequestMapping("/RegisterController")
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    @PostMapping("/register")
    public ResultDTO register(@RequestBody User user) throws Exception {
        return registerService.register(user);
    }
}
