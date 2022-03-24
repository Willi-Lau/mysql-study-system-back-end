package com.lwy.demo.controller;

import com.lwy.demo.TO.ResultDTO;
import com.lwy.demo.entity.User;
import com.lwy.demo.service.SqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Pattern;

@CrossOrigin
@RestController
@RequestMapping("/SqlController")
public class SqlController {

    @Autowired
    private SqlService sqlService;

    @PostMapping("/executeSql")
    public ResultDTO executeSql(@RequestParam String sql,
                                @RequestParam String token) throws Exception {
        ResultDTO resultDTO = new ResultDTO();
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
        } else if (Pattern.matches("^show.*tables.*", sql)) {
            resultDTO = sqlService.showTables(user);
        } else {
            resultDTO = sqlService.sqlUnSelect(user, sql);
        }
        return resultDTO;
    }

    @PostMapping("/getExampleSql")
    public ResultDTO getExampleSql() {
        ResultDTO exampleSql = sqlService.getExampleSql();
        return exampleSql;
    }


}
