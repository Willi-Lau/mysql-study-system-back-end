package com.lwy.demo.service.impl;

import com.lwy.demo.TO.ResultDTO;
import com.lwy.demo.config.InfoConfig;
import com.lwy.demo.dao.jdbc.RegisterDao;
import com.lwy.demo.dao.mybatis.SchoolDao;
import com.lwy.demo.dao.mybatis.UserDao;
import com.lwy.demo.entity.User;
import com.lwy.demo.service.RegisterService;
import com.lwy.demo.utils.RedisUtil;
import com.lwy.demo.utils.RedissionBloomFilters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Transactional
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RegisterDao registerDao;

    @Autowired
    private RedissionBloomFilters redissonBloomFilter;

    @Autowired
    private SchoolDao schoolDao;

    @Autowired
    private UserDao userDao;

    @Override
    public ResultDTO register(User user) throws Exception {
        ResultDTO resultDTO = new ResultDTO();
        //为空
        if(StringUtils.isEmpty(user.getClassName()) || StringUtils.isEmpty(user.getIdentityCardNumber()) ||
        StringUtils.isEmpty(user.getPassword()) || StringUtils.isEmpty(user.getPhone()) ||
        StringUtils.isEmpty(user.getStudentNumber()) || StringUtils.isEmpty(user.getUniversity())
        ){
            resultDTO.setObject("信息不全已注册");
            resultDTO.setType(false);
            return resultDTO;
        }
        //查询是否注册过  redis查询
        //查询学号
        if(redisUtil.sHasKey("USER_STUDENT_NUMBER_SET",user.getStudentNumber())){
            resultDTO.setObject("此学号已注册");
            resultDTO.setType(false);
            return resultDTO;
        }
        //查询身份证号
        if(redisUtil.sHasKey("USER_ID_NUMBER_SET",user.getIdentityCardNumber())){
            resultDTO.setObject("此身份证号已注册");
            resultDTO.setType(false);
            return resultDTO;
        }
        //查询电话号 待改进  放到redis set里
        if(redisUtil.sHasKey("USER_PHONE_NUMBER_SET",user.getPhone())){
            resultDTO.setObject("此电话号已注册");
            resultDTO.setType(false);
            return resultDTO;
        }
//        List<User> userList = userDao.getUserList();
//        for (User user1 : userList){
//            if(user1.getPhone().equals(user.getPhone())){
//                resultDTO.setObject("此电话号已注册");
//                resultDTO.setType(false);
//                return resultDTO;
//            }
//            else if(user1.getIdentityCardNumber().equals(user.getIdentityCardNumber())){
//                resultDTO.setObject("此身份证号已注册");
//                resultDTO.setType(false);
//                return resultDTO;
//            }
//            else if(user1.getStudentNumber().equals(user.getStudentNumber())){
//                resultDTO.setObject("此学号已注册");
//                resultDTO.setType(false);
//                return resultDTO;
//            }
//        }
        //开始创建
        user.setState("1");
        boolean registerMysqlResult = registerDao.register(user);
        if(!registerMysqlResult){
            resultDTO.setObject("超时，请重试");
            resultDTO.setType(false);
            return resultDTO;
        }
        //存入redis
//        redisUtil.set(InfoConfig.REDIS_USER_STUDENT_NUMBER+user.getStudentNumber(),user);
//        redisUtil.set(InfoConfig.REDIS_USER_IDENTITY_CARD_NUMBER+user.getIdentityCardNumber(),user);
        //存入bloom-filter
        redissonBloomFilter.addValue(user.getIdentityCardNumber());
        redissonBloomFilter.addValue(user.getStudentNumber());
        resultDTO.setType(true);
        //注册到对应的set中
        redisUtil.sSet("USER_ID_NUMBER_SET",user.getIdentityCardNumber());
        redisUtil.sSet("USER_STUDENT_NUMBER_SET",user.getStudentNumber());
        redisUtil.sSet("USER_PHONE_NUMBER_SET",user.getPhone());
        //把学校的人数+1
        schoolDao.addSchoolNum(user.getUniversity());
        return resultDTO;
    }
}
