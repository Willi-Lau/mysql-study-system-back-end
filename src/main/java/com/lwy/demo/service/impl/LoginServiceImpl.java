package com.lwy.demo.service.impl;

import com.lwy.demo.TO.ResultDTO;
import com.lwy.demo.config.InfoConfig;
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
import java.util.UUID;

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
    public ResultDTO login(String username, String password){
        ResultDTO resultDTO = new ResultDTO();
        //首先判断username是否存在 利用布隆过滤器 因为存的是学号和身份证号不会更改
        boolean contains = bloomFilter.containsValue(username);
        if(!contains){
            resultDTO.setType(false);
            return resultDTO;
        }
        //首先去redis里查找
        String studentNumberRedisPassword = (String)redisUtil.get(InfoConfig.REDIS_USER_STUDENT_NUMBER + username);
        String identityCardNumberRedisPassword = (String)redisUtil.get(InfoConfig.REDIS_USER_IDENTITY_CARD_NUMBER + username);
        //如果redis里没有去mysql找
        if(StringUtils.isEmpty(studentNumberRedisPassword) && StringUtils.isEmpty(identityCardNumberRedisPassword)){
            String mysqlPassword = loginDao.login(username);
            if(mysqlPassword.equals(password)){
                resultDTO.setType(true);
            }
            else {
                resultDTO.setType(false);
                return resultDTO;
            }
        }
        else {
            //有的话判断是否相同
            if(password.equals(studentNumberRedisPassword) || password.equals(identityCardNumberRedisPassword)){
                resultDTO.setType(true);
            }
            else {
                resultDTO.setType(false);
                return resultDTO;
            }
        }
        //查询userId
        Integer id = loginDao.id(username);
        //处理token
        String token = UUID.randomUUID().toString();
        //key为 uuid-token value 为user表的id 为了方便后续的验证操作
        redisUtil.set(InfoConfig.REDIS_TOKEN_LOGIN_INFO+token,id,1000 * 60 * 60 * 24);
        resultDTO.setObject(token);
        return resultDTO;
    }
}
