package com.lwy.demo;


import com.lwy.demo.config.InfoConfig;
import com.lwy.demo.dao.mybatis.ManagerDao;
import com.lwy.demo.dao.mybatis.UserDao;
import com.lwy.demo.entity.Manager;
import com.lwy.demo.entity.User;
import com.lwy.demo.utils.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class AddManagerToBloomFilter {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ManagerDao managerDao;

    @Test
    public void Test() throws JSONException {

        List<User> userList = userDao.getUserList();
        for (User user : userList){
            redisUtil.set(InfoConfig.REDIS_USER_STUDENT_NUMBER+user.getStudentNumber(),user);
            redisUtil.set(InfoConfig.REDIS_USER_IDENTITY_CARD_NUMBER+user.getIdentityCardNumber(),user);
        }

        List<Manager> managerList = managerDao.getManagerList();
        for (Manager manager : managerList){
            redisUtil.set(InfoConfig.REDIS_MANAGER_NUMBER + manager.getManagerNumber() , manager);
        }

    }
}
