package com.lwy.demo.controller;

import com.lwy.demo.TO.ResultDTO;
import com.lwy.demo.entity.User;
import com.lwy.demo.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/ManagerController")
public class ManagerController {

    @Autowired
    private ManagerService managerService;

    @PostMapping("/getUserList")
    public ResultDTO getUserList(){
        return managerService.getUserList();
    }

    @PostMapping("/changeState")
    public void changeState(@RequestParam Integer id,
                            @RequestParam Boolean state){

        managerService.changeState(id,state);
    }

    @PostMapping("/getSchoolList")
    public ResultDTO getSchoolList() throws ParseException {
        return managerService.getSchoolList();
    }
}
