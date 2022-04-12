package com.lwy.demo.dao;

import com.lwy.demo.entity.Manager;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginDao {
    /**
     *  查询指定用户名的密码
     * @return password
     */
    @Select("select password from user where studentNumber = #{username} or identityCardNumber = #{username}")
    String login(String username);

    /**
     *  查询指定用户名的id
     * @return password
     */
    @Select("select id from user where studentNumber = #{username} or identityCardNumber = #{username}")
    Integer id(String username);

    /**
     * 查询管理员用户密码
     */
    @Select("select * from manager where managerNumber = #{managerNumber}")
    Manager loginManager(String managerNumber);

    /**
     * 查询管理员id
     */
    @Select("select id from manager where managerNumber = #{managerNumber}")
    Integer managerId(String managerNumber);


}
