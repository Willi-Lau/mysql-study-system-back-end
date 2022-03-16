package com.lwy.demo.controller;


import com.lwy.demo.config.RedisConfig;
import com.lwy.demo.service.LoginService;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public boolean login(@RequestParam String username,
                         @RequestParam String password){
        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
            return false;
        }
        return loginService.login(username,password);
    }
}
