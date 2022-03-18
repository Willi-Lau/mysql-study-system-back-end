package com.lwy.demo.controller;


import com.lwy.demo.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/LoginController")
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
