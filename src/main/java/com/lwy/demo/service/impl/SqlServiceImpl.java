package com.lwy.demo.service.impl;


import com.lwy.demo.TO.ResultDTO;
import com.lwy.demo.config.InfoConfig;
import com.lwy.demo.dao.jdbc.SqlDao;
import com.lwy.demo.entity.User;
import com.lwy.demo.service.SqlService;
import com.lwy.demo.utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.SQLException;
import java.util.*;

@Service
public class SqlServiceImpl implements SqlService {

    @Autowired
    private SqlDao sqlDao;

    @Autowired
    private RedisUtil redisUtil;

    List<String> selectColumnList = null;

    Logger logger = LoggerFactory.getLogger(getClass());

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
        logger.info("拼接SQL结果:"+sql2);
        columnNameList = this.selectColumnList;
        //执行sql 执行查询返回查询结果
        List<Map<String, String>> selectExecuteList = sqlDao.selectExecute(user, sql2, columnNameList);
        //根据字段进行封装 封装结果到Map 结构为List<Map<String,String>>
        if(selectExecuteList.size() != 0){
            String errorLog = selectExecuteList.get(0).get("errorLog");
            if (StringUtils.isEmpty(errorLog)) {
                resultDTO.setObject("sql success");
            } else {
                resultDTO.setObject(errorLog);
            }
        }
        else {
            resultDTO.setObject("sql success");
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
        HashMap<String, String> map4 = new HashMap<>(16);
        map4.put("example", showTablesExample);
        map4.put("type", "获取所有的表的名字");
        resultList.add(map4);
        //查询多表查询
        String selectMutiplyExample = (String) redisUtil.get(InfoConfig.REDIS_SQL_EXAMPLE_ + "SELECT_MUTIPLY");
        HashMap<String, String> map5 = new HashMap<>(16);
        map5.put("example", selectMutiplyExample);
        map5.put("type", "多表查询");
        resultList.add(map5);
        //查询子查询
        String selectSonExample = (String) redisUtil.get(InfoConfig.REDIS_SQL_EXAMPLE_ + "SELECT_SON");
        HashMap<String, String> map6 = new HashMap<>(16);
        map6.put("example", selectSonExample);
        map6.put("type", "子查询");
        resultList.add(map6);

        resultDTO.getMap().put("result", resultList);
        List<String> columnNameList = new ArrayList<>();
        Collections.addAll(columnNameList, "type","example");
        resultDTO.getMap().put("column", columnNameList);
        resultDTO.setObject("sql success");

        return resultDTO;
    }

    @Override
    public ResultDTO sqlExplain(User user, String sql) throws SQLException {
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

        List<String> columnNameList = new ArrayList<>();
        Collections.addAll(columnNameList, "id","select_type","table","type","possible_keys","key","ref","rows","Extra");
        //执行sql 执行查询返回查询结果
        List<Map<String, String>> selectExecuteList = sqlDao.selectExecute(user, sql, columnNameList);
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
        resultDTO.getMap().put("table", "explain");
        resultDTO.getMap().put("sql", sql);
        return resultDTO;
    }

    @Override
    public ResultDTO sqlShowIndex(User user, String sql) throws SQLException {
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

        List<String> columnNameList = new ArrayList<>();
        Collections.addAll(columnNameList, "Table","Non_unique","Key_name","Seq_in_index","Column_name","Collation","Cardinality","Sub_part","Packed","Null"
        ,"Index_type","Comment","Index_comment");
        //执行sql 执行查询返回查询结果
        List<Map<String, String>> selectExecuteList = sqlDao.selectExecute(user, sql, columnNameList);
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
        resultDTO.getMap().put("table", "explain");
        resultDTO.getMap().put("sql", sql);
        return resultDTO;
    }

    private List<String> analysisTableName(String sql) {
        List<String> tableNameList = new ArrayList<>();
        String[] sqlArr = sql.split(" ");
        int from = 0;
        for (int i = 1; i < sqlArr.length; i++) {
            if (sqlArr[i - 1].equals("from") && from == 0) {
                tableNameList.add(sqlArr[i]);
                from ++;
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
        int select = 0;
        int from = 0;
        for (String s : arr1) {
            //到达select
            if (s.equals("select") && select == 0) {
                isBetweenSelectAndFrom = true;
                select++;
                continue;
            }
            if (s.equals("from") && from == 0) {
                isBetweenSelectAndFrom = false;
                isFromOver = true;
                from++;
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
                    resultSql += " " + table + "." + this.captureString(cloumnName) + " " + table + this.captureString(cloumnName) + " ,";
                    selectColumnList.add(table + this.captureString(cloumnName));
                }
                resultSql = resultSql.substring(0, resultSql.length() - 2) + ",";

            }
            resultSql = resultSql.substring(0,resultSql.length() - 1);
            resultSql += fromSQL;
            return resultSql;
        }
        //如果不包含 分别筛选内容进行拼接 arr2 是 [a.id , b.id , b.name]
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
                        resultSql += " " + table1 + "." + this.captureString(cloumnName) + " " + table1 + this.captureString(cloumnName) + " ,";
                        selectColumnList.add(table1 + this.captureString(cloumnName));

                    }
                }
                //如果是.id则单独获取
                else {
                    String table1 = split[0];
                    resultSql += " " + table1 + "." + this.captureString(split[1]) + " " + table1 + this.captureString(split[1]) + " ,";
                    selectColumnList.add(table1 + this.captureString(split[1]));
                }
            }
            //单表查询
            else {
                //判断是否是聚合函数 如果是不需要处理
                String column1 = split[0];
                HashSet<String> set = new HashSet<>();
                Collections.addAll(set,"count(","sum(","max(","min(");
                boolean isPolymerization = false;
                for (String s1 : set){
                    if(column1.startsWith(s1)){
                        isPolymerization = true;
                    }
                }
                if(isPolymerization){
                    resultSql += " " + column1 + " ,";
                    selectColumnList.add(column1);
                }
                else{
                    resultSql += " " + tableNameList.get(0) + "." + column1 + " " + tableNameList.get(0) + column1 + " ,";
                    selectColumnList.add(tableNameList.get(0) + column1);
                }

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

    /**
     * 首字母小写转大写
     * @param s
     * @return
     */
    private String captureString(String s){
        char[] chars = s.toCharArray();
        chars[0] -= 32;
        return String.valueOf(chars);
    }

}
