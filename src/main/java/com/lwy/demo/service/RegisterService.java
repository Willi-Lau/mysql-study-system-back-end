package com.lwy.demo.service;

import com.lwy.demo.TO.ResultDTO;
import com.lwy.demo.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


public interface RegisterService {

    public ResultDTO register(User user) throws Exception;

}
