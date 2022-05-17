package com.lwy.demo.controller;

import com.lwy.demo.TO.ResultDTO;
import com.lwy.demo.TO.SqlResultDTO;
import com.lwy.demo.TO.UserDTO;
import com.lwy.demo.dao.mybatis.AllTestDiscribeDao;
import com.lwy.demo.entity.SqlResult;
import com.lwy.demo.entity.User;
import com.lwy.demo.service.SqlService;
import com.lwy.demo.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.xml.transform.Result;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

@CrossOrigin
@RestController
@RequestMapping("/SqlController")
public class SqlController {

    @Autowired
    private SqlService sqlService;

    @Autowired
    private TestService testService;

    @PostMapping("/executeSql")
    public ResultDTO executeSql(@RequestParam String sql,
                                @RequestParam String token,
                                @RequestParam(defaultValue = "0") int isMySql) throws Exception {
        ResultDTO resultDTO = new ResultDTO();
        sql = sql.trim();
        //sql执行日志
        SqlResult sqlResult = new SqlResult();
        sqlResult.setSqlstatement(sql);
        sqlResult.setTime(Long.toString(System.currentTimeMillis()));

        //判断为空
        if (StringUtils.isEmpty(sql) || StringUtils.isEmpty(token)) {
            resultDTO.setType(false);
            return resultDTO;
        }
        //验证token 根据token找到user信息
        User user = sqlService.user(token);
        if (StringUtils.isEmpty(user.getIdentityCardNumber())) {
            resultDTO.setObject("获取用户失败");
            return resultDTO;
        }
        //验证sql类型 分开 select / 非 查询语句
        boolean isSelect = Pattern.matches("^select.+", sql);
        if (isSelect) {
            resultDTO = sqlService.sqlSelect(user, sql);
        } else if (sql.trim().equals("show tables") || sql.trim().equals("show tables;")) {
            resultDTO = sqlService.showTables(user);
        } else if (Pattern.matches("^explain.*", sql)) {
            resultDTO = sqlService.sqlExplain(user, sql);
        }
        else if (sql.trim().startsWith("show index")) {
            resultDTO = sqlService.sqlShowIndex(user, sql);
        }
        else {
            resultDTO = sqlService.sqlUnSelect(user, sql);
        }
        //对 结果进行评估
        if ("sql success".equals(resultDTO.getObject())) {
            sqlResult.setResult("success");
        } else {
            sqlResult.setResult("error");

        }
        //解析sql类型
        String[] sqlArr = sql.trim().split(" ");
        String firstSql = sqlArr[0];
        if (firstSql.equals("select")) {
            sqlResult.setType("select");
        } else if (firstSql.equals("insert")) {
            sqlResult.setType("insert");
        } else if (firstSql.equals("update")) {
            sqlResult.setType("update");
        } else if (firstSql.equals("delete")) {
            sqlResult.setType("delete");
        } else {
            sqlResult.setType("other");
        }
        //插入sqlresult
        String sqlResultSql = "insert into sqlrecord(sqlstatement,time,type,result) values( ' ";
        sqlResultSql += sqlResult.getSqlstatement() + "','" + sqlResult.getTime() + "','" + sqlResult.getType() + "','" + sqlResult.getResult() + "')";
        //不是sql输入框执行的sql不添加进日志
        if (isMySql == 1) {
            sqlService.sqlUnSelect(user, sqlResultSql);
        }
        // System.out.println(sqlResultSql);
        return resultDTO;
    }

    @PostMapping("/getExampleSql")
    public ResultDTO getExampleSql() {
        ResultDTO exampleSql = sqlService.getExampleSql();
        return exampleSql;
    }

    /**
     * 做题部分
     */
    @PostMapping("/getAllId")
    public ResultDTO getAllId(){
        return testService.getAllTestId();
    }

    @PostMapping("/getTestDiscribe")
    public ResultDTO getTestDiscribe(@RequestParam Integer id) throws Exception{
        return testService.getTestDiscribe(id);
    }

    @PostMapping("/runSQL")
    public ResultDTO runSQL(@RequestParam String token,
                            @RequestParam String userSQL,
                            @RequestParam Integer testId
                            ) throws Exception {
        //根据token查找id
        User user = sqlService.user(token);
        ResultDTO resultDTO = testService.runSQL(user.getId(), testId, userSQL , user);
        return resultDTO;
    }

}
