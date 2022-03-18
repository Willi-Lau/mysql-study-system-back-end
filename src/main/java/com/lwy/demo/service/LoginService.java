package com.lwy.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


public interface LoginService {

    public boolean login(String username,String password);
}
