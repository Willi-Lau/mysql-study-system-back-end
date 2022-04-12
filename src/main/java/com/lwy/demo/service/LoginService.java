package com.lwy.demo.service;

import com.lwy.demo.TO.ResultDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.UnknownHostException;


public interface LoginService {

    public ResultDTO login(String username, String password) throws UnknownHostException;

    public ResultDTO managerLogin(String username, String password) throws UnknownHostException;
}
