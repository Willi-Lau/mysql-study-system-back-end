package com.lwy.demo.controller;


import com.lwy.demo.TO.ResultDTO;
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
    public ResultDTO login(@RequestParam String username,
                           @RequestParam String password){
        ResultDTO resultDto = new ResultDTO();
        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
            resultDto.setType(false);
        }
        return loginService.login(username,password);
    }

    @PostMapping("/managerLogin")
    public ResultDTO managerLogin(@RequestParam String username,
                                  @RequestParam String password){
        ResultDTO resultDto = new ResultDTO();
        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
            resultDto.setType(false);
        }
        return loginService.managerLogin(username,password);
    }
}
