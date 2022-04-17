package com.lwy.demo.controller;

import com.lwy.demo.TO.ResultDTO;
import com.lwy.demo.TO.SqlResultDTO;
import com.lwy.demo.TO.UserDTO;
import com.lwy.demo.entity.User;
import com.lwy.demo.service.SqlService;
import com.lwy.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/UserController")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SqlService sqlService;

    @PostMapping("/getUserInfoAndSqlData")
    public ResultDTO getUserInfoAndSqlData(@RequestParam String token) throws Exception {
        ResultDTO resultDTO = new ResultDTO();
        //验证token 根据token找到user信息
        User user = sqlService.user(token);
        UserDTO userInfo = userService.getUserInfo(user);
        List<SqlResultDTO> sql = userService.getSql(user);
        HashMap<Object,Object> map = new HashMap<>(16);
        map.put("userInfo",userInfo);
        map.put("sql",sql);
        resultDTO.setMap(map);
        return resultDTO;
    }

    @PostMapping("/getUserInfoAndSqlDataById")
    public ResultDTO getUserInfoAndSqlDataById(@RequestParam Integer id) throws  Exception{
        ResultDTO resultDTO = new ResultDTO();
        User user = userService.getUserById(id);
        UserDTO userInfo = userService.getUserInfo(user);
        List<SqlResultDTO> sql = userService.getSql(user);
        HashMap<Object,Object> map = new HashMap<>(16);
        map.put("userInfo",userInfo);
        map.put("sql",sql);
        resultDTO.setMap(map);
        return resultDTO;
    }


}
