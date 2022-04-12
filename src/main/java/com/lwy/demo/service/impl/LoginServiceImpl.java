package com.lwy.demo.service.impl;

import com.lwy.demo.TO.ResultDTO;
import com.lwy.demo.config.InfoConfig;
import com.lwy.demo.config.RedisConfig;
import com.lwy.demo.dao.LoginDao;
import com.lwy.demo.entity.Manager;
import com.lwy.demo.service.LoginService;
import com.lwy.demo.utils.RedisUtil;
import com.lwy.demo.utils.RedissionBloomFilters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.xml.ws.ServiceMode;
import java.util.HashMap;
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
        redisUtil.set(InfoConfig.REDIS_TOKEN_LOGIN_INFO+token,id,60 * 60 * 24);
        resultDTO.setObject(token);
        //存进用户登录历史记录表中
        return resultDTO;
    }

    @Override
    public ResultDTO managerLogin(String username, String password) {
        ResultDTO resultDTO = new ResultDTO();
        //首先利用bloomfilter查看管理员账户是否存在
        boolean containsManager = bloomFilter.containsValue(InfoConfig.BLOOM_FILTER_MANAGER + username);
        if(!containsManager){
            resultDTO.setType(false);
            resultDTO.setObject("用户不存在");
            return resultDTO;
        }
        //去redis查找 redis key "ManagerNumber" value "password level"
        String redisManagerPasswordAndLevel =(String)redisUtil.get(InfoConfig.REDIS_MANAGER_NUMBER + username);
        String[] redisManagerInfoArray = redisManagerPasswordAndLevel.split(" ");
        String redisManagerPassword = null;
        String redisManagerLevel = null;
        //存放返回信息的map
        HashMap resultInfo = new HashMap(16);
        resultDTO.setMap(resultInfo);
        Integer id = 0;
        //如果不是空 判断
        if(!StringUtils.isEmpty(redisManagerPasswordAndLevel)){
             redisManagerPassword = redisManagerInfoArray[0];
             redisManagerLevel= redisManagerInfoArray[1];
             //密码和redis里的一样 可以登录
             if(redisManagerPassword.equals(password)){
                 resultDTO.setType(true);
                 resultInfo.put("level",redisManagerLevel);
                 id = loginDao.managerId(username);
             }
             else {
                 resultDTO.setType(false);
                 resultDTO.setObject("密码错误");
                 return resultDTO;
             }
        }
        //是空 去mysql查找
        else {
            Manager manager = loginDao.loginManager(username);
            String mysqlPassword = manager.getPassword();
            if(mysqlPassword.equals(password)){
                resultDTO.setType(true);
                resultInfo.put("level",manager.getLevel());
                id = manager.getId();
            }
            else {
                resultDTO.setType(false);
                resultDTO.setObject("密码错误");
                return resultDTO;
            }
        }
        //最后根据账户是否正确决定是否分配token
        if(resultDTO.getType()){
            //处理token
            String token = UUID.randomUUID().toString();
            //key为 uuid-token value 为user表的id 为了方便后续的验证操作
            redisUtil.set(InfoConfig.REDIS_TOKEN_MANAGER_LOGIN_INFO+token,id,60 * 60 * 24);
            resultDTO.getMap().put("token",token);
        }
        return resultDTO;
    }
}
