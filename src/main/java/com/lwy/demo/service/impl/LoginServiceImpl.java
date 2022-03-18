package com.lwy.demo.service.impl;

import com.lwy.demo.config.RedisConfig;
import com.lwy.demo.dao.LoginDao;
import com.lwy.demo.service.LoginService;
import com.lwy.demo.utils.RedisUtil;
import com.lwy.demo.utils.RedissionBloomFilters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.xml.ws.ServiceMode;

@Service
@Transactional
public class LoginServiceImpl implements LoginService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private LoginDao loginDao;

    @Autowired
    private RedissionBloomFilters bloomFilter;

    @Override
    public boolean login(String username, String password){
        //首先判断username是否存在 利用布隆过滤器 因为存的是学号和身份证号不会更改
        boolean contains = bloomFilter.containsValue(username);
        if(!contains){
            return false;
        }
        //首先去redis里查找
        String redisPassword = (String)redisUtil.get(username);
        //如果redis里没有去mysql找
        if(StringUtils.isEmpty(redisPassword)){
            String mysqlPassword = loginDao.login(username);
            if(mysqlPassword.equals(password)){
                return true;
            }
            else {
                return false;
            }
        }
        else {
            //有的话判断是否相同
            if(password.equals(redisPassword)){
                return true;
            }
            else {
                return false;
            }
        }
    }
}
