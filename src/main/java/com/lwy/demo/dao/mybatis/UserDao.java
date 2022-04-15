package com.lwy.demo.dao.mybatis;


import com.lwy.demo.entity.User;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public interface UserDao {
    /**
     * 查询所有用户的信息
     * @return
     */
    @Select("select * from user")
    List<User> getUserList();

    /**
     *  查询指定用户名
     * @return password
     */
    @Select("select * from user where studentNumber = #{username} or identityCardNumber = #{username}")

    User getUser(String username);
    /**
     * 修改指定用户的封禁状态
     */
    @Update("update user set state = #{state} where id = #{id}")
    void changeState(HashMap map);

    /**
     * 修改密码
     * @param map
     */
    @Update("update user set password = #{password} where phone = #{phone}")
    void changePassword( HashMap<String,String> map);
    /**
     * 根据条件查询用户
     */
    List<User> getUserByCondition(HashMap<String,Object> map);
    /**
     * 查询用户数量
     */
    @Select("select max(id) from user")
    Integer getMaxId();
}
