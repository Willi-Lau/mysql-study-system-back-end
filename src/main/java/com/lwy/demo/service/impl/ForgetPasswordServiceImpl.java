package com.lwy.demo.service.impl;

import com.lwy.demo.dao.ForgetPasswordDao;
import com.lwy.demo.service.ForgetPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Service
@Transactional
public class ForgetPasswordServiceImpl implements ForgetPasswordService {

    @Autowired
    private ForgetPasswordDao forgetPasswordDao;

    @Override
    public void changePassword(String phone, String password) {
        HashMap<String,String> map = new HashMap<>();
        map.put("phone",phone);
        map.put("password",password);
        forgetPasswordDao.changePassword(map);
    }
}
