package com.lwy.demo.service;

import com.lwy.demo.TO.ResultDTO;
import com.lwy.demo.entity.User;

import java.sql.SQLException;

public interface TestService {
    ResultDTO getTestDiscribe(Integer id) throws SQLException;

    ResultDTO getAllTestId();

    ResultDTO runSQL(Integer userId,Integer testId,String userSQL, User user) throws SQLException;
}
