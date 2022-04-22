package com.lwy.demo.service;

import com.lwy.demo.TO.ResultDTO;
import com.lwy.demo.entity.School;
import com.lwy.demo.entity.User;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

public interface ManagerService {

    public ResultDTO getUserList();

    public void changeState(Integer id,Boolean state);

    public ResultDTO getSchoolList() throws ParseException;

    public ResultDTO getConditionStudent(HashMap<String,String> map);

    public ResultDTO getUserLoginHistory(Integer id) throws ParseException;

    public ResultDTO getUserLoginHistoryByDate(Integer id,String startTime,String endTime) throws ParseException;

    public ResultDTO getAllUserLoginHistory() throws ParseException;

    public ResultDTO getAllUserLoginHistoryByDate(String startTime,String endTime) throws ParseException;

    public ResultDTO renewSchool(String token,Integer schoolId,String renewDuration);

    public ResultDTO insertSchool(String name , String deadline) throws ParseException;

    public ResultDTO getSchoolByName(String name) throws Exception;
}
