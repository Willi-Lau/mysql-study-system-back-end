package com.lwy.demo.service;

import com.lwy.demo.TO.ResultDTO;
import com.lwy.demo.TO.SqlResultDTO;
import com.lwy.demo.TO.UserDTO;
import com.lwy.demo.entity.User;

import java.sql.SQLException;
import java.util.List;

public interface SqlService {

    ResultDTO sqlSelect(User user,String sql) throws SQLException;

    ResultDTO sqlUnSelect(User user,String sql) throws SQLException;

    User user(String token) throws Exception;

    ResultDTO showTables(User user) throws SQLException;

    ResultDTO getExampleSql();

    ResultDTO sqlExplain(User user,String sql) throws SQLException;

    ResultDTO sqlShowIndex(User user,String sql) throws SQLException;

}
