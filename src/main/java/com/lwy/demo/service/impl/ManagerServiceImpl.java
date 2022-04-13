package com.lwy.demo.service.impl;

import com.lwy.demo.TO.ResultDTO;
import com.lwy.demo.TO.SchoolDTO;
import com.lwy.demo.TO.UserDTO;
import com.lwy.demo.dao.mybatis.ManagerDao;
import com.lwy.demo.dao.mybatis.SchoolDao;
import com.lwy.demo.dao.mybatis.UserDao;
import com.lwy.demo.entity.School;
import com.lwy.demo.entity.User;
import com.lwy.demo.service.ManagerService;
import com.lwy.demo.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional
public class ManagerServiceImpl implements ManagerService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private SchoolDao schoolDao;

    /**
     * 获取所有的用户列表
     * @return
     */
    @Override
    public ResultDTO getUserList() {
        ResultDTO resultDTO = new ResultDTO();
        HashMap<Object,Object> map = new HashMap<>();
        List<User> userList = userDao.getUserList();
        List<UserDTO> userDTOList=  new ArrayList<>();
        for (User user : userList){
            UserDTO userDTO = UserDTO.userTransformUserDTO(user);
            userDTOList.add(userDTO);
        }
        map.put("userList",userDTOList);
        resultDTO.setMap(map);
        return resultDTO;
    }

    /**
     * 修改用户状态
     * @param id
     * @param state
     */
    @Override
    public void changeState(Integer id, Boolean state) {
        Integer state1 = 0;
        if(state){
            state1 = 1;
        }
        HashMap map = new HashMap(16);
        map.put("state",state1);
        map.put("id",id);
        //修改mysql里的状态
        userDao.changeState(map);
    }

    @Override
    public ResultDTO getSchoolList() throws ParseException {
        List<School> schoolList = schoolDao.getSchoolList();
        List<SchoolDTO> schoolDTOList = new ArrayList<>();
        for (School school : schoolList){
            //没过期添加
            if (Long.parseLong(school.getDeadline()) > System.currentTimeMillis()){
                schoolDTOList.add(SchoolDTO.schoolTransformSchoolDTO(school));
            }
        }
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setMap(new HashMap<>(16));
        resultDTO.getMap().put("schoolList",schoolDTOList);
        return resultDTO;
    }
}
