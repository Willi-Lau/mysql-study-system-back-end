package com.lwy.demo.service.impl;

import com.lwy.demo.TO.ResultDTO;
import com.lwy.demo.TO.SqlResultDTO;
import com.lwy.demo.TO.UserDTO;
import com.lwy.demo.entity.User;
import com.lwy.demo.service.SqlService;
import com.lwy.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private SqlService sqlService;

    @Override
    public UserDTO getUserInfo(User user) throws SQLException {
        UserDTO userDTO = UserDTO.userTransformUserDTO(user);
        //获取执行sql次数 all
        ResultDTO resultDTO1 = sqlService.sqlSelect(user, "select count(*) from sqlrecord");
        List<Map<String, String>> list1 = (List<Map<String, String>>) resultDTO1.getMap().get("result");
        String s1 = list1.get(0).get("count(*)");
        userDTO.setSqlNumber(Integer.parseInt(s1));

        //获取执行sql次数 select
        ResultDTO resultDTO2 = sqlService.sqlSelect(user, "select count(*) from sqlrecord where type = 'select'");
        List<Map<String, String>> list2 = (List<Map<String, String>>) resultDTO2.getMap().get("result");
        String s2 = list2.get(0).get("count(*)");
        userDTO.setSelectNumber(Integer.parseInt(s2));

        //获取执行sql次数 update
        ResultDTO resultDTO3 = sqlService.sqlSelect(user, "select count(*) from sqlrecord where type = 'update'");
        List<Map<String, String>> list3 = (List<Map<String, String>>) resultDTO3.getMap().get("result");
        String s3 = list3.get(0).get("count(*)");
        userDTO.setUpdateNumber(Integer.parseInt(s3));

        //获取执行sql次数 delete
        ResultDTO resultDTO4 = sqlService.sqlSelect(user, "select count(*) from sqlrecord where type = 'delete'");
        List<Map<String, String>> list4 = (List<Map<String, String>>) resultDTO4.getMap().get("result");
        String s4 = list4.get(0).get("count(*)");
        userDTO.setDeleteNumber(Integer.parseInt(s4));

        //获取执行sql次数 insert
        ResultDTO resultDTO5 = sqlService.sqlSelect(user, "select count(*) from sqlrecord where type = 'insert'");
        List<Map<String, String>> list5 = (List<Map<String, String>>) resultDTO5.getMap().get("result");
        String s5 = list5.get(0).get("count(*)");
        userDTO.setInsertNumber(Integer.parseInt(s5));

        //获取执行sql次数 other
        ResultDTO resultDTO6 = sqlService.sqlSelect(user, "select count(*) from sqlrecord where type = 'other'");
        List<Map<String, String>> list6 = (List<Map<String, String>>) resultDTO6.getMap().get("result");
        String s6 = list6.get(0).get("count(*)");
        userDTO.setOtherNumber(Integer.parseInt(s6));

        //获取执行sql次数 success
        ResultDTO resultDTO7 = sqlService.sqlSelect(user, "select count(*) from sqlrecord where result = 'success'");
        List<Map<String, String>> list7 = (List<Map<String, String>>) resultDTO7.getMap().get("result");
        String s7 = list7.get(0).get("count(*)");
        userDTO.setSuccessNumber(Integer.parseInt(s7));

        //获取执行sql次数 error
        ResultDTO resultDTO8 = sqlService.sqlSelect(user, "select count(*) from sqlrecord where result = 'error'");
        List<Map<String, String>> list8 = (List<Map<String, String>>) resultDTO8.getMap().get("result");
        String s8 = list8.get(0).get("count(*)");
        userDTO.setErrorNumber(Integer.parseInt(s8));

        return userDTO;
    }

    @Override
    public List<SqlResultDTO> getSql(User user) throws Exception {
        ResultDTO resultDTO = sqlService.sqlSelect(user, "select * from sqlrecord order by time desc");
        List<HashMap<String, String>> list = (List<HashMap<String, String>>) resultDTO.getMap().get("result");
        List<SqlResultDTO> sqlResultDTOList = new ArrayList<>();
        for (HashMap<String,String> map : list){
            SqlResultDTO sqlResultDTO = new SqlResultDTO();
            sqlResultDTO.setId(Integer.parseInt(map.get("sqlrecordId")));
            sqlResultDTO.setSqlstatement(map.get("sqlrecordSqlstatement"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date date = sdf.parse(sdf.format(Long.parseLong(map.get("sqlrecordTime"))));
            sqlResultDTO.setTime( sdf.format(date));

            sqlResultDTO.setType(map.get("sqlrecordType"));
            sqlResultDTO.setResult(map.get("sqlrecordResult"));
            sqlResultDTOList.add(sqlResultDTO);
        }
        return sqlResultDTOList;
    }
}
