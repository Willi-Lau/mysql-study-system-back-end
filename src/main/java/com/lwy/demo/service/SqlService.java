package com.lwy.demo.service;

import com.lwy.demo.TO.ResultDTO;
import com.lwy.demo.entity.User;

import java.sql.SQLException;

public interface SqlService {

    ResultDTO sqlSelect(User user,String sql) throws SQLException;

    ResultDTO sqlUnSelect(User user,String sql) throws SQLException;

    User user(String token) throws Exception;

    ResultDTO showTables(User user) throws SQLException;

    ResultDTO getExampleSql();

}
