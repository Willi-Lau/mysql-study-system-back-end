package com.lwy.demo.dao;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginDao {
    /**
     *  查询指定用户名的密码
     * @return password
     */
    @Select("select password from user where username = #{username}")
    String login(String username);
}
