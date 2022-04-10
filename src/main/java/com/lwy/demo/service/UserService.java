package com.lwy.demo.service;

import com.lwy.demo.TO.SqlResultDTO;
import com.lwy.demo.TO.UserDTO;
import com.lwy.demo.entity.User;

import java.sql.SQLException;
import java.util.List;

public interface UserService {

    public UserDTO getUserInfo(User user) throws SQLException;

    public List<SqlResultDTO> getSql(User user) throws Exception;
}
