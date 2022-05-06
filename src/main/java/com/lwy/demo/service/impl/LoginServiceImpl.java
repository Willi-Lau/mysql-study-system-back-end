package com.lwy.demo.service.impl;

import com.lwy.demo.TO.ResultDTO;
import com.lwy.demo.config.InfoConfig;
import com.lwy.demo.dao.mybatis.LoginHistoryDao;
import com.lwy.demo.dao.mybatis.ManagerDao;
import com.lwy.demo.dao.mybatis.UserDao;
import com.lwy.demo.entity.Manager;
import com.lwy.demo.entity.ManagerLoginHistory;
import com.lwy.demo.entity.User;
import com.lwy.demo.entity.UserLoginHistory;
import com.lwy.demo.service.LoginService;
import com.lwy.demo.utils.LocalHostUtil;
import com.lwy.demo.utils.RedisUtil;
import com.lwy.demo.utils.RedissionBloomFilters;
import com.lwy.demo.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.UUID;

@Service
@Transactional
public class LoginServiceImpl implements LoginService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ManagerDao managerDao;

    @Autowired
    private RedissionBloomFilters bloomFilter;

    @Autowired
    private LocalHostUtil localHostUtil;

    @Autowired
    private LoginHistoryDao loginHistoryDao;

    @Override
    public ResultDTO login(String username, String password) throws UnknownHostException {
        ResultDTO resultDTO = new ResultDTO();
        //首先判断username是否存在 利用布隆过滤器 因为存的是学号和身份证号不会更改
        boolean contains = bloomFilter.containsValue(username);
        if(!contains){
            resultDTO.setObject("bloomFilter拦截");
            resultDTO.setType(false);
            return resultDTO;
        }
        //首先去redis里查找
        User studentNumberRedisUser = (User)redisUtil.get(InfoConfig.REDIS_USER_STUDENT_NUMBER + username);
        User identityCardNumberRedisUser = (User)redisUtil.get(InfoConfig.REDIS_USER_IDENTITY_CARD_NUMBER + username);
        User redisUser = studentNumberRedisUser == null ? identityCardNumberRedisUser : studentNumberRedisUser;
        //查询userId
        Integer id = -1;
        //如果redis里没有去mysql找
        if(redisUser == null){
            User sqlUser = userDao.getUser(username);
            String sqlPassword = sqlUser.getPassword();
            //把信息存进redis中因为redis没有
            redisUtil.set(InfoConfig.REDIS_USER_STUDENT_NUMBER + sqlUser.getStudentNumber(),sqlUser);
            redisUtil.set(InfoConfig.REDIS_USER_IDENTITY_CARD_NUMBER + sqlUser.getIdentityCardNumber(),sqlUser);
            //验证密码正确型
            if(sqlPassword.equals(password)){
                id = sqlUser.getId();
                resultDTO.setType(true);
            }
            else {
                resultDTO.setType(false);
                return resultDTO;
            }
            if("0".equals(sqlUser.getState())){
                resultDTO.setType(true);
                resultDTO.setObject("拉黑");
                return resultDTO;
            }

        }
        else {
            //有的话判断是否相同
            if(password.equals(redisUser.getPassword())){
                id = redisUser.getId();
                resultDTO.setType(true);
            }
            else {
                resultDTO.setType(false);
                return resultDTO;
            }
            if("0".equals(redisUser.getState())){
                resultDTO.setType(true);
                resultDTO.setObject("拉黑");
                return resultDTO;
            }

        }
        //处理token
        String token = UUID.randomUUID().toString();
        //key为 uuid-token value 为user表的id 为了方便后续的验证操作
        redisUtil.set(InfoConfig.REDIS_TOKEN_LOGIN_INFO+token,id,60 * 60 * 24);
        resultDTO.setObject(token);
        //存进用户登录历史记录表中
        UserLoginHistory userLoginHistory = new UserLoginHistory();
        userLoginHistory.setLoginTime(TimeUtils.getNowTime().toString());
        userLoginHistory.setUserId(id);
        userLoginHistory.setLocalHostAddress(localHostUtil.getLocalHost());
        loginHistoryDao.addUserLoginHistory(userLoginHistory);
        return resultDTO;
    }

    @Override
    public ResultDTO managerLogin(String username, String password) throws UnknownHostException {
        ResultDTO resultDTO = new ResultDTO();
        //首先利用bloomfilter查看管理员账户是否存在
        boolean containsManager = bloomFilter.containsValue(InfoConfig.BLOOM_FILTER_MANAGER + username);
        if(!containsManager){
            resultDTO.setType(false);
            resultDTO.setObject("用户不存在");
            return resultDTO;
        }
        //去redis查找 redis key "ManagerNumber" value "password level"
        Manager redisManager =(Manager)redisUtil.get(InfoConfig.REDIS_MANAGER_NUMBER + username);
        //存放返回信息的map
        HashMap resultInfo = new HashMap(16);
        resultDTO.setMap(resultInfo);
        Integer id = 0;
        //如果不是空 判断
        if(!StringUtils.isEmpty(redisManager.getLevel())){
             //密码和redis里的一样 可以登录
             if(redisManager.getPassword().equals(password)){
                 resultDTO.setType(true);
                 resultInfo.put("level",redisManager.getLevel());
                 Manager manager = managerDao.getManager(username);
                 id = manager.getId();
             }
             else {
                 resultDTO.setType(false);
                 resultDTO.setObject("密码错误");
                 return resultDTO;
             }
        }
        //是空 去mysql查找
        else {
            Manager manager = managerDao.getManager(username);
            String mysqlPassword = manager.getPassword();
            if(mysqlPassword.equals(password)){
                resultDTO.setType(true);
                resultInfo.put("level",manager.getLevel());
                id = manager.getId();
                redisUtil.set(InfoConfig.REDIS_MANAGER_NUMBER + manager.getUsername(),manager);
            }
            else {
                resultDTO.setType(false);
                resultDTO.setObject("密码错误");
                return resultDTO;
            }
        }
        //去redis 黑名单查询是否存在
        boolean isBlackList = redisUtil.sHasKey(InfoConfig.MANAGER_BLACK_LIST, id);
        if(isBlackList){
            resultDTO.setType(true);
            resultDTO.setObject("拉黑");
            return resultDTO;
        }
        //最后根据账户是否正确决定是否分配token
        if(resultDTO.getType()){
            //处理token
            String token = UUID.randomUUID().toString();
            //key为 uuid-token value 为user表的id 为了方便后续的验证操作
            redisUtil.set(InfoConfig.REDIS_TOKEN_MANAGER_LOGIN_INFO+token,id,60 * 60 * 24);
            resultDTO.getMap().put("token",token);
            //存进用户登录历史记录表中
            ManagerLoginHistory managerLoginHistory = new ManagerLoginHistory();
            managerLoginHistory.setLoginTime(TimeUtils.getNowTime().toString());
            managerLoginHistory.setUserId(id);
            managerLoginHistory.setLocalHostAddress(localHostUtil.getLocalHost());
            loginHistoryDao.addManagerLoginHistory(managerLoginHistory);
        }
        return resultDTO;
    }
}
