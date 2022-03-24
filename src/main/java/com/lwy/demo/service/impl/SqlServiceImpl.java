package com.lwy.demo.service.impl;


import com.lwy.demo.TO.ResultDTO;
import com.lwy.demo.config.InfoConfig;
import com.lwy.demo.dao.SqlDao;
import com.lwy.demo.entity.User;
import com.lwy.demo.service.SqlService;
import com.lwy.demo.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class SqlServiceImpl implements SqlService {

    @Autowired
    private SqlDao sqlDao;

    @Autowired
    private RedisUtil redisUtil;

    List<String> selectColumnList = null;

    @Override
    public ResultDTO sqlSelect(User user, String sql) throws SQLException {
        //查看sql语句是否有分号结尾 如果有去掉
        String[] s = sql.split(" ");
        StringBuilder sql1 = new StringBuilder();
        for (int i = 0; i < s.length; i++) {
            if (i == s.length - 1 && (s[i].equals(";") || s[i].endsWith(";"))) {
                s[i] = s[i].substring(0, s[i].length() - 1);
            }
            sql1.append(s[i]);
            sql1.append(" ");
        }
        sql = sql1.toString();
        ResultDTO resultDTO = new ResultDTO();
        //解析出表的名字
        List<String> tableNameList = analysisTableName(sql);
        //获取表的字段
        List<String> columnNameList = new ArrayList<>();
        //解析sql 查看是 * 还是指定了特定的字段 0 = * | 1 = id | 2 = table1.id , table2.*
        String sql2 = analysisColumn(sql, tableNameList, user);
        System.out.println(sql2);
        columnNameList = this.selectColumnList;
        //执行sql 执行查询返回查询结果
        List<Map<String, String>> selectExecuteList = sqlDao.selectExecute(user, sql2, columnNameList);
        //根据字段进行封装 封装结果到Map 结构为List<Map<String,String>>
        String errorLog = selectExecuteList.get(0).get("errorLog");
        if (StringUtils.isEmpty(errorLog)) {
            resultDTO.setObject("sql success");
        } else {
            resultDTO.setObject(errorLog);
        }

        resultDTO.setMap(new HashMap<>(16));
        resultDTO.getMap().put("column", columnNameList);
        resultDTO.getMap().put("result", selectExecuteList);
        resultDTO.getMap().put("table", tableNameList);
        resultDTO.getMap().put("sql", sql2);

        return resultDTO;
    }

    @Override
    public ResultDTO sqlUnSelect(User user, String sql) throws SQLException {
        return sqlDao.unSelectExecute(user, sql);
    }

    @Override
    public User user(String token) throws Exception {
        // RedisUtil redisUtil = new RedisUtil();
        //判断redis里是否有这个key
        boolean hasKey = redisUtil.hasKey(InfoConfig.REDIS_TOKEN_LOGIN_INFO + token);
        if (!hasKey) {
            return new User();
        }
        //从redis 获取用户id
        Integer id = (Integer) redisUtil.get(InfoConfig.REDIS_TOKEN_LOGIN_INFO + token);
        //查询账户其他信息
        User user = sqlDao.user(id);
        return user;
    }

    @Override
    public ResultDTO showTables(User user) throws SQLException {
        ResultDTO resultDTO = new ResultDTO();
        List<Map<String, String>> list = sqlDao.showTables(user);
        List<String> columnNameList = new ArrayList<>();
        columnNameList.add("tableName");
        List<String> tableNameList = new ArrayList<>();
        for (Map<String, String> map : list) {
            tableNameList.add(map.get("tableName"));
        }

        resultDTO.setMap(new HashMap<>(16));
        resultDTO.getMap().put("column", columnNameList);
        resultDTO.getMap().put("result", list);
        resultDTO.getMap().put("table", tableNameList);
        resultDTO.getMap().put("sql", "show tables");
        resultDTO.setObject("sql success");
        return resultDTO;
    }

    @Override
    public ResultDTO getExampleSql() {
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setMap(new HashMap<>(16));
        List<HashMap<String, String>> resultList = new ArrayList<>();

        HashMap<String, String> map0 = new HashMap<>(16);
        map0.put("example", "sql例句可以直接使用");
        map0.put("type", "sql例句可以直接使用");
        resultList.add(map0);
        //查询select
        String selectExample = (String) redisUtil.get(InfoConfig.REDIS_SQL_EXAMPLE_ + "SELECT");
        HashMap<String, String> map = new HashMap<>(16);
        map.put("example", selectExample);
        map.put("type", "查询");
        resultList.add(map);
        //查询insert
        String insertExample = (String) redisUtil.get(InfoConfig.REDIS_SQL_EXAMPLE_ + "INSERT");
        HashMap<String, String> map1 = new HashMap<>(16);
        map1.put("example", insertExample);
        map1.put("type", "增加");
        resultList.add(map1);
        //查询delete
        String deleteExample = (String) redisUtil.get(InfoConfig.REDIS_SQL_EXAMPLE_ + "DELETE");
        HashMap<String, String> map2 = new HashMap<>(16);
        map2.put("example", deleteExample);
        map2.put("type", "删除 谨慎使用");
        resultList.add(map2);
        //查询update
        String updateExample = (String) redisUtil.get(InfoConfig.REDIS_SQL_EXAMPLE_ + "UPDATE");
        HashMap<String, String> map3 = new HashMap<>(16);
        map3.put("example", updateExample);
        map3.put("type", "修改 谨慎使用");
        resultList.add(map3);
        //查询show tables
        String showTablesExample = (String) redisUtil.get(InfoConfig.REDIS_SQL_EXAMPLE_ + "SHOWTABLES");
        System.out.println(showTablesExample);
        HashMap<String, String> map4 = new HashMap<>(16);
        map4.put("example", showTablesExample);
        map4.put("type", "获取所有的表的名字");
        resultList.add(map4);

        resultDTO.getMap().put("result", resultList);
        List<String> columnNameList = new ArrayList<>();
        Collections.addAll(columnNameList, "type","example");
        resultDTO.getMap().put("column", columnNameList);
        resultDTO.setObject("sql success");

        return resultDTO;
    }

    private List<String> analysisTableName(String sql) {
        List<String> tableNameList = new ArrayList<>();
        String[] sqlArr = sql.split(" ");
        for (int i = 1; i < sqlArr.length; i++) {
            if (sqlArr[i - 1].equals("from")) {
                tableNameList.add(sqlArr[i]);
            }
            if (sqlArr[i - 1].equals("join")) {
                tableNameList.add(sqlArr[i]);
            }
        }
        return tableNameList;
    }

    /**
     * 根据sql语句查询要返回的字段 *为所有
     * <p>
     * select table1.* , table2.id from table1 left join table2 on tab1....
     * select * from ...
     *
     * @param sql
     * @return
     */
    private String analysisColumn(String sql, List<String> tableNameList, User user) throws SQLException {
        selectColumnList = new ArrayList<>();
        String resultSql = "select ";
        //from 之后得sql语句
        String fromSQL = " from ";
        String[] arr1 = sql.split(" ");
        boolean isBetweenSelectAndFrom = false;
        boolean isFromOver = false;
        String selectSql = "";
        for (String s : arr1) {
            //到达select
            if (s.equals("select")) {
                isBetweenSelectAndFrom = true;
                continue;
            }
            if (s.equals("from")) {
                isBetweenSelectAndFrom = false;
                isFromOver = true;
                continue;
            }
            //添加select选项到STRING
            if (isBetweenSelectAndFrom) {
                selectSql += s;
            }
            //from后的条件拼接
            if (isFromOver) {
                fromSQL += " ";
                fromSQL += s;
            }
        }
        //再次对select过滤
        String[] arr2 = selectSql.split(",");
        //如果条件里包含单个 * 则拼接from里所有的部分
        boolean isIncludeStar = false;
        for (String s : arr2) {
            if (s.equals("*")) {
                isIncludeStar = true;
            }
        }
        if (isIncludeStar) {
            //处理字段
            for (String table : tableNameList) {
                List<String> column = sqlDao.getColumn(user, table);
                for (String cloumnName : column) {
                    resultSql += " " + table + "." + cloumnName + " " + table + cloumnName + " ,";
                    selectColumnList.add(table + cloumnName);
                }
                resultSql = resultSql.substring(0, resultSql.length() - 2);
                resultSql += fromSQL;
                return resultSql;
            }

        }
        //如果不包含 分别筛选内容进行拼接
        for (String s : arr2) {
            String[] split = s.split("\\.");
            //有条件的多表查询
            if (split.length > 1) {
                //针对每一个select 再次split
                //如果.*那么久获取所有
                if (split[1].equals("*")) {
                    //获取此表的所有字段
                    String table1 = split[0];
                    List<String> column = sqlDao.getColumn(user, table1);
                    //拼接到sql
                    //拼接到表字段list
                    for (String cloumnName : column) {
                        resultSql += " " + table1 + "." + cloumnName + " " + table1 + cloumnName + " ,";
                        selectColumnList.add(table1 + cloumnName);
                    }
                }
                //如果是.id则单独获取
                else {
                    String table1 = split[0];
                    resultSql += " " + table1 + "." + split[1] + " " + table1 + split[1] + " ,";
                    selectColumnList.add(table1 + split[1]);
                }
            }
            //单表查询
            else {
                String cloumn1 = split[0];
                resultSql += " " + tableNameList.get(0) + "." + cloumn1 + " " + tableNameList.get(0) + cloumn1 + " ,";
                selectColumnList.add(tableNameList.get(0) + cloumn1);
            }

        }
        resultSql += " ";
        //去掉最后一个 ,
        resultSql = resultSql.trim().substring(0, resultSql.length() - 2);
        resultSql += fromSQL;
        return resultSql;
    }

    /**
     * 分析名字 stduent.id 获取表名student
     */
    private String analysisTable(String sql) {
        //获取点的位置
        int index = -1;
        char[] chars = sql.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '.') {
                index = i;
                break;
            }
        }
        return index > 0 ? sql.substring(0, index - 1) : sql;

    }
}