package com.lwy.demo.service;

import com.lwy.demo.TO.ResultDTO;
import com.lwy.demo.entity.User;

import java.text.ParseException;
import java.util.List;

public interface ManagerService {

    public ResultDTO getUserList();

    public void changeState(Integer id,Boolean state);

    public ResultDTO getSchoolList() throws ParseException;
}
