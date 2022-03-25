package com.lwy.demo.controller;

import com.lwy.demo.TO.ResultDTO;
import com.lwy.demo.entity.User;
import com.lwy.demo.service.RegisterService;
import com.lwy.demo.service.SqlService;
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

    @Autowired
    private SqlService sqlService;

    @PostMapping("/register")
    public ResultDTO register(@RequestBody User user) throws Exception {
        ResultDTO register = registerService.register(user);
        if(register.getType() == true){
            //默认 user表
            sqlService.sqlUnSelect(user,"create table user(id int(10) primary key auto_increment,name varchar(10),age int(10));");
            sqlService.sqlUnSelect(user,"insert into user (name,age) values ('张三',18)");
            sqlService.sqlUnSelect(user,"insert into user (name,age) values ('李四',19)");
            sqlService.sqlUnSelect(user,"insert into user (name,age) values ('成龙',50)");
            sqlService.sqlUnSelect(user,"insert into user (name,age) values ('马云',51)");
            //sql查询记录表
            sqlService.sqlUnSelect(user,"create table sqlRecord(id int(10) primary key auto_increment,sqlstatement varchar(255),time varchar(20),type varchar(20),result varchar(20),errorlog varchar(255));");
        }

        return register;
    }
}
