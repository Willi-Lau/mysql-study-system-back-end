package com.lwy.demo.service;

import com.lwy.demo.TO.ResultDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


public interface LoginService {

    public ResultDTO login(String username, String password);

    public ResultDTO managerLogin(String username, String password);
}
