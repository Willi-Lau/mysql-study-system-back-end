package com.lwy.demo.controller;

import com.lwy.demo.TO.ResultDTO;
import com.lwy.demo.entity.User;
import com.lwy.demo.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.HashMap;
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

    @PostMapping("/getConditionStudent")
    public ResultDTO getConditionStudent(@RequestParam(defaultValue = "查询所有") String idOptionsValue,
                                         @RequestParam String id,
                                         @RequestParam(defaultValue = "查询所有") String nameOptionsValue,
                                         @RequestParam String name,
                                         @RequestParam String studentNumber,
                                         @RequestParam String identityCardNumber,
                                         @RequestParam String phone,
                                         @RequestParam(defaultValue = "查询所有") String university,
                                         @RequestParam(defaultValue = "查询所有") String state
                                         ) {
        HashMap<String,String> map = new HashMap<>(16);
        map.put("idOptionsValue",idOptionsValue);
        map.put("id",id);
        map.put("nameOptionsValue",nameOptionsValue);
        map.put("name",name);
        map.put("studentNumber",studentNumber);
        map.put("identityCardNumber",identityCardNumber);
        map.put("phone",phone);
        map.put("university",university);
        if(state.equals("否")){
            map.put("state","1");
        }
        else if(state.equals("是")){
            map.put("state","0");
        }
        else {
            map.put("state","3");
        }
        return managerService.getConditionStudent(map);
    }
}
