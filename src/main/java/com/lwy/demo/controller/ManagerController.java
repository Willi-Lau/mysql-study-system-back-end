package com.lwy.demo.controller;


import com.alibaba.fastjson.JSONArray;
import com.lwy.demo.TO.ResultDTO;
import com.lwy.demo.entity.School;
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

    /**
     * 指定用户登录历史记录 根据 id
     */
    @PostMapping("/getUserLoginHistory")
    public ResultDTO getUserLoginHistory(@RequestParam(defaultValue = "0") Integer id) throws ParseException {
        return managerService.getUserLoginHistory(id);
    }


    /**
     * 指定用户登录历史记录 根据 id
     */
    @PostMapping("/getUserLoginHistoryByDate")
    public ResultDTO getUserLoginHistoryByDate(@RequestParam(defaultValue = "0") Integer id,
                                               @RequestParam(defaultValue = "2020-01-01") String startTime,
                                               @RequestParam(defaultValue = "2080-04-04") String endTime
                                               ) throws ParseException {
        return managerService.getUserLoginHistoryByDate(id,startTime,endTime);
    }

    /**
     * 所有用户登录历史记录
     */
    @PostMapping("/getAllUserLoginHistory")
    public ResultDTO getAllUserLoginHistory() throws ParseException {
        return managerService.getAllUserLoginHistory();
    }

    /**
     * 所有用户指定日期内登录历史记录
     */
    @PostMapping("/getAllUserLoginHistoryByDate")
    public ResultDTO getAllUserLoginHistoryByDate (@RequestParam(defaultValue = "2020-01-01") String startTime,
                                                   @RequestParam(defaultValue = "2080-04-04") String endTime) throws ParseException {
        return managerService.getAllUserLoginHistoryByDate(startTime, endTime);
    }

    /**
     * 给学校续费
     * @param renewDuration 以天为单位
     */
    @PostMapping("/renewSchool")
    public ResultDTO renewSchool(
                                 @RequestParam String token,
                                 @RequestParam String schoolIdList,
                                 @RequestParam String renewDuration) throws ParseException {
        //特殊处理 json list
        List<Integer> list = JSONArray.parseArray(schoolIdList, Integer.class);
        for (Integer schoolId : list){
            managerService.renewSchool(token,schoolId,renewDuration);
        }
        return new ResultDTO();
    }

    /**
     * 新建学校
     * @param
     */
    @PostMapping("/insertSchool")
    public ResultDTO insertSchool(@RequestParam String name,
                                  @RequestParam String deadline) throws ParseException {
        System.out.println(deadline);
        return managerService.insertSchool(name,deadline);
    }

    /**
     * 根据名字去es查找
     */
    @PostMapping("/getSchoolByName")
    public ResultDTO getSchoolByName(@RequestParam String name) throws Exception{
        return managerService.getSchoolByName(name);
    }

}
