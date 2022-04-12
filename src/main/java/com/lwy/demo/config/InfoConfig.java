package com.lwy.demo.config;


import org.springframework.stereotype.Component;

/**
 * @author Administrator
 */
public class InfoConfig {
    //redis 用户学号密码前置头
    public static String REDIS_USER_STUDENT_NUMBER = "REDIS_USER_STUDENT_NUMBER";
    //redis 用户身份证号密码前置头
    public static String REDIS_USER_IDENTITY_CARD_NUMBER = "REDIS_USER_IDENTITY_CARD_NUMBER";
    //mysql主库信息
    public static String JDBC_DRIVER = "jdbc:mysql://localhost:3306/mysqlstudy?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf8&useSSL=false";
    public static String JDBC_USERNAME = "dsp";
    public static String JDBC_PASSWORD = "dsp";
    //token前置头 普通用户
    public static String REDIS_TOKEN_LOGIN_INFO = "REDIS_TOKEN_LOGIN_INFO";
    //token前置头 管理员
    public static String REDIS_TOKEN_MANAGER_LOGIN_INFO = "REDIS_TOKEN_MANAGER_LOGIN_INFO";
    //redis sql例句前置头
    public static String REDIS_SQL_EXAMPLE_ = "REDIS_SQL_EXAMPLE_";
    //bloom filter 管理员前置头
    public static String BLOOM_FILTER_MANAGER = "manager";
    //redis 管理员账户密码前置头
    public static String REDIS_MANAGER_NUMBER = "REDIS_MANAGER_NUMBER";
    public static String REDIS_MANAGER_LEVEL = "REDIS_MANAGER_LEVEL";
    //redis 用户权限禁用黑名单
    public static String USER_BLACK_LIST = "USER_BLACK_LIST";
    //redis 管理员权限禁用黑名单
    public static String MANAGER_BLACK_LIST = "MANAGER_BLACK_LIST";
















}
