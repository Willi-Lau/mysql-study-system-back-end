package com.lwy.demo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lwy.demo.TO.ResultDTO;
import com.lwy.demo.dao.jdbc.SqlDao;
import com.lwy.demo.dao.mybatis.AllTestDiscribeDao;
import com.lwy.demo.dao.mybatis.TestDao;
import com.lwy.demo.entity.*;
import com.lwy.demo.service.SqlService;
import com.lwy.demo.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.*;

@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private AllTestDiscribeDao allTestDiscribeDao;

    @Autowired
    private TestDao testDao;

    @Autowired
    private SqlDao sqlDao;

    @Autowired
    private SqlService sqlService;

    @Override
    public ResultDTO getTestDiscribe(Integer id) throws SQLException {
        ResultDTO resultDTO = new ResultDTO();
        HashMap map = new HashMap(16);
        AllTestDiscribe allTestDiscribe = allTestDiscribeDao.getAllTestDiscribe(id);
        map.put("discribe",allTestDiscribe.getTitleDiscribe());
        String tableColumn = allTestDiscribe.getTableColumn();
        List<Column> columns = JSON.parseArray(tableColumn, Column.class);
        map.put("column",columns);
        map.put("trueSQL",allTestDiscribe.getTrueSQL());
//        List<Column> list = new ArrayList<>();
//        Column column = new Column("id", "int");
//        Column column1 = new Column("name", "varchar");
//        Column column2 = new Column("age", "varchar");
//        Column column3 = new Column("englishResult", "int");
//        Column column4 = new Column("mathResult", "int");
//        Collections.addAll(list,column,column1,column2,column3,column4);
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("list",list);
//        System.out.println(jsonObject.get("list"));
//        JSONArray list1 = jsonObject.getJSONArray("list");
//        HashMap<String,String> map1 = new HashMap<>();
//        map1.put("id","2");
//        map1.put("tableColumn",JSON.toJSONString(list));
//        allTestDiscribeDao.hahah(map1);
        // [{"":"","":""}]
       // System.out.println(list1);
        //获取期望结果
        List<String> testColumn = sqlDao.getTestColumn(allTestDiscribe.getTableName());
        Object test = sqlDao.getTest(allTestDiscribe.getTrueSQL(), testColumn);

        map.put("trueResult",test);
        resultDTO.setMap(map);
        return resultDTO;
    }

    @Override
    public ResultDTO getAllTestId() {
        ResultDTO resultDTO = new ResultDTO();
        HashMap map = new HashMap(16);
        map.put("result",allTestDiscribeDao.getAllId());
        resultDTO.setMap(map);
        return resultDTO;
    }

    @Override
    public ResultDTO runSQL(Integer userId, Integer testId, String userSQL, User user) throws SQLException {
        ResultDTO resultDTO = new ResultDTO();
        if(userSQL.startsWith("select") || userSQL.startsWith("SELECT")){
            HashMap map = new HashMap(16);
            //首先获取此题的所有信息
            AllTestDiscribe allTestDiscribe = allTestDiscribeDao.getAllTestDiscribe(testId);
            List<String> testColumn = sqlDao.getTestColumn(allTestDiscribe.getTableName());
            Object test = sqlDao.getTest(userSQL, testColumn);
            map.put("userResult",test);
            resultDTO.setMap(map);
            resultDTO.setType(true);
            //写入用户执行记录
            SqlResult sqlResult = new SqlResult();
            sqlResult.setSqlstatement(userSQL);
            sqlResult.setTime(Long.toString(System.currentTimeMillis()));
            sqlResult.setType("select");
            sqlResult.setResult("success");
            String sqlResultSql = "insert into sqlrecord(sqlstatement,time,type,result) values( ' ";
            sqlResultSql += sqlResult.getSqlstatement() + "','" + sqlResult.getTime() + "','" + sqlResult.getType() + "','" + sqlResult.getResult() + "')";
            sqlService.sqlUnSelect(user, sqlResultSql);
        }
        else{
            resultDTO.setType(false);
        }



        return resultDTO;
    }
}
