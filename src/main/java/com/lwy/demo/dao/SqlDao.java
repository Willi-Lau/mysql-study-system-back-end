package com.lwy.demo.dao;

import com.lwy.demo.TO.ResultDTO;
import com.lwy.demo.config.InfoConfig;
import com.lwy.demo.entity.User;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SqlDao {
    Connection conn = null;
    Statement stat = null;
    PreparedStatement preparedStatement = null;
    User user = null;
    public User user(Integer id) throws Exception {
        try {
            //数据库操作
            Class.forName("com.mysql.cj.jdbc.Driver");
            //一开始必须填一个已经存在的数据库
            String url = InfoConfig.JDBC_DRIVER;
            conn = DriverManager.getConnection(url, InfoConfig.JDBC_USERNAME, InfoConfig.JDBC_PASSWORD);
            conn.setAutoCommit(false);
            stat = conn.createStatement();
            preparedStatement = conn.prepareStatement("select * from user where id = ?");
            preparedStatement.setInt(1,id);
            ResultSet resultSet = preparedStatement.executeQuery();

            user= new User();
            while (resultSet.next()){
                user.setName(resultSet.getString("name"));
                user.setStudentNumber(resultSet.getString("studentNumber"));
                user.setIdentityCardNumber(resultSet.getString("identityCardNumber"));
            }
            conn.commit();
        }
          catch (ClassNotFoundException e) {
            e.printStackTrace();
            conn.rollback();
            return user;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return user;
        } finally {
            preparedStatement.close();
            stat.close();
            conn.close();
            return user;
        }
    }

    public List<String> getColumn(User user,String tableName) throws SQLException {
        List<String> columnList = new ArrayList<>();
        try {
            //数据库操作
            Class.forName("com.mysql.cj.jdbc.Driver");
            //连接自己的账户 数据库名： stu+学号 账户 usr+学号 密码 身份证号
            String url = "jdbc:mysql://localhost:3306/"+"stu"+user.getStudentNumber()+"?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf8&useSSL=false";
            conn = DriverManager.getConnection(url,"usr" + user.getStudentNumber() , user.getIdentityCardNumber());
            conn.setAutoCommit(false);
            stat = conn.createStatement();
            //获取字段
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet colRet = metaData.getColumns("stu"+user.getStudentNumber(),"%", tableName,"%");
            while(colRet.next()) {
                String columnName = colRet.getString("COLUMN_NAME");
                columnList.add(columnName);
            }
        } catch (Exception throwables) {
            throwables.printStackTrace();
        } finally {
            //preparedStatement.close();
            stat.close();
            conn.close();
            return columnList;
        }
    }


    public List<Map<String,String>> selectExecute(User user, String sql, List<String> columnList) throws SQLException {
        List<Map<String,String>> resultList = new ArrayList<>();
        try {
            //数据库操作
            Class.forName("com.mysql.cj.jdbc.Driver");
            //连接自己的账户 数据库名： stu+学号 账户 usr+学号 密码 身份证号
            String url = "jdbc:mysql://localhost:3306/"+"stu"+user.getStudentNumber()+"?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf8&useSSL=false";
            conn = DriverManager.getConnection(url,"usr" + user.getStudentNumber() , user.getIdentityCardNumber());
            conn.setAutoCommit(false);
            stat = conn.createStatement();
            //执行传进来的sql语句
            preparedStatement = conn.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            //结果集 一个map是一条sql记录

            while (resultSet.next()){
                HashMap<String,String> map = new HashMap<>(16);
                //循环sql表字段list 然后赋值到list
                int i = 1;
                for (String column : columnList){
                    map.put(column,resultSet.getString(i));
                    //这里是防止多表查询时候 两个表有相同字段会出现异常
                    i++;
                }
                //把一行记录存到list中
                resultList.add(map);
            }
            conn.commit();

        } catch (Exception throwables) {
            throwables.printStackTrace();
            HashMap<String,String> map = new HashMap<>(16);
            map.put("errorLog",throwables.getMessage());
            resultList = new ArrayList<>();
            resultList.add(map);
        } finally {
            preparedStatement.close();
            stat.close();
            conn.close();
            return resultList;
        }
    }
    public ResultDTO unSelectExecute(User user,String sql) throws SQLException {
        ResultDTO resultDTO = new ResultDTO();
        List<Map<String,String>> list = new ArrayList<>();
        try {
            //数据库操作
            Class.forName("com.mysql.cj.jdbc.Driver");
            //连接自己的账户 数据库名： stu+学号 账户 usr+学号 密码 身份证号
            String url = "jdbc:mysql://localhost:3306/"+"stu"+user.getStudentNumber()+"?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf8&useSSL=false";
            conn = DriverManager.getConnection(url,"usr" + user.getStudentNumber() , user.getIdentityCardNumber());
            conn.setAutoCommit(false);
            stat = conn.createStatement();
            //执行传进来的sql语句
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.executeUpdate();
            conn.commit();
            resultDTO.setObject("sql success");
            HashMap<String,String> map = new HashMap<>(16);
            map.put("sqlResult","执行成功");
            list.add(map);
            List<String> columnNameList = new ArrayList<>();
            columnNameList.add("sqlResult");
            resultDTO.setMap(new HashMap<>());
            resultDTO.getMap().put("column", columnNameList);
            resultDTO.getMap().put("result",list);
        } catch (Exception throwables) {
            throwables.printStackTrace();
            HashMap<String,String> map = new HashMap<>(16);
            map.put("errorLog",throwables.getMessage());
            list.add(map);

            resultDTO.setMap(new HashMap<>());
            resultDTO.getMap().put("result",list);
        } finally {
            preparedStatement.close();
            stat.close();
            conn.close();

        }
        return resultDTO;
    }

    public List<Map<String,String>> showTables(User user) throws SQLException {
        List<Map<String,String>> resultList = new ArrayList<>();
        try {
            //数据库操作
            Class.forName("com.mysql.cj.jdbc.Driver");
            //连接自己的账户 数据库名： stu+学号 账户 usr+学号 密码 身份证号
            String databasesName = "stu" + user.getStudentNumber();
            String url = "jdbc:mysql://localhost:3306/"+databasesName+"?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf8&useSSL=false";
            conn = DriverManager.getConnection(url,"usr" + user.getStudentNumber() , user.getIdentityCardNumber());
            conn.setAutoCommit(false);
            stat = conn.createStatement();
            //执行传进来的sql语句
            preparedStatement = conn.prepareStatement("show tables");
            ResultSet resultSet = preparedStatement.executeQuery();
            //结果集 一个map是一条sql记录
            while (resultSet.next()){
                HashMap<String,String> map = new HashMap<>(16);
                String string = resultSet.getString("Tables_in_"+databasesName);
                map.put("tableName",string);
                resultList.add(map);
            }

            conn.commit();
        } catch (Exception throwables) {
            throwables.printStackTrace();
            HashMap<String,String> map = new HashMap<>(16);
            map.put("errorLog",throwables.getMessage());
            resultList = new ArrayList<>();
            resultList.add(map);
        } finally {
            preparedStatement.close();
            stat.close();
            conn.close();
            return resultList;
        }
    }
}
