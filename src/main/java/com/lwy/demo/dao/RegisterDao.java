package com.lwy.demo.dao;


import com.lwy.demo.config.InfoConfig;
import com.lwy.demo.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.springframework.stereotype.Repository;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.net.URL;
import java.sql.*;
import java.util.Map;

@Repository
public class RegisterDao {
    /**
     * 注册
     * @param user
     */
    public boolean register(User user) throws Exception {
        Connection conn = null;
        Statement stat = null;
        PreparedStatement preparedStatement = null;
        try {
            //数据库操作
            Class.forName("com.mysql.cj.jdbc.Driver");
            //一开始必须填一个已经存在的数据库
            String url = InfoConfig.JDBC_DRIVER;
            conn = DriverManager.getConnection(url, InfoConfig.JDBC_USERNAME, InfoConfig.JDBC_PASSWORD);
            conn.setAutoCommit(false);
            stat = conn.createStatement();
            preparedStatement = conn.prepareStatement("insert into user (name,studentNumber,identityCardNumber,password,phone,university,className)values (?,?,?,?,?,?,?)");
            //存入 user表
            preparedStatement.setString(1,user.getName());
            preparedStatement.setString(2,user.getStudentNumber());
            preparedStatement.setString(3,user.getIdentityCardNumber());
            preparedStatement.setString(4,user.getPassword());
            preparedStatement.setString(5,user.getPhone());
            preparedStatement.setString(6,user.getUniversity());
            preparedStatement.setString(7,user.getClassName());
            boolean userResult = preparedStatement.execute();
            //为该用户新建一个数据库 库名字为 stu+学号
            StringBuilder databasesName = new StringBuilder();
            databasesName.append("stu");
            databasesName.append(user.getStudentNumber());
            stat.executeUpdate("create database " + databasesName);
            //为该用户创建一个有操作自己库权限的mysql账号 账户：usr+学号 密码：身份证号
            StringBuilder createInformation = new StringBuilder();
            createInformation.append("grant all privileges on ");
            createInformation.append(databasesName);
            createInformation.append(".* to usr");
            createInformation.append(user.getStudentNumber());
            createInformation.append("@'%' identified by '");
            createInformation.append(user.getIdentityCardNumber());
            createInformation.append("';");
            boolean createInformationResult = stat.execute(createInformation.toString());
            if(createInformationResult && userResult){
                conn.commit();
            }
            else {
                conn.rollback();
                return false;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            conn.rollback();
            return false;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        } finally {
            preparedStatement.close();
            stat.close();
            conn.close();
            return true;
        }
    }
}
