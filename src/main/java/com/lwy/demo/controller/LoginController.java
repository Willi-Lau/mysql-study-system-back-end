package com.lwy.demo.controller;


import com.lwy.demo.TO.ResultDTO;
import com.lwy.demo.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.net.UnknownHostException;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/LoginController")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public ResultDTO login(@RequestParam String username,
                           @RequestParam String password) throws UnknownHostException {
        ResultDTO resultDto = new ResultDTO();
        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
            resultDto.setType(false);
        }
        return loginService.login(username,password);
    }

    @PostMapping("/managerLogin")
    public ResultDTO managerLogin(@RequestParam String username,
                                  @RequestParam String password) throws UnknownHostException {
        ResultDTO resultDto = new ResultDTO();
        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
            resultDto.setType(false);
        }
        return loginService.managerLogin(username,password);
    }

    Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 测试接口
     * @return
     * @throws UnknownHostException
     */
    @GetMapping("/loginTest")
    public ResultDTO loginTest() throws UnknownHostException {
        String username = UUID.randomUUID().toString();
        String password =  UUID.randomUUID().toString();
        ResultDTO resultDTO = loginService.login(username, password);
        if(!resultDTO.getType() && "bloomFilter拦截".equals(resultDTO.getObject())){
            logger.error(" BloomFilter成功拦截 -------- username: " + username + "  password :" + password);
        }
        return resultDTO;
    }
}
