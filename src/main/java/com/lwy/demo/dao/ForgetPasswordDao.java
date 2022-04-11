package com.lwy.demo.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Repository
public interface ForgetPasswordDao {

    @Update("update user set password = #{password} where phone = #{phone}")
    void changePassword(  HashMap<String,String> map );
}
