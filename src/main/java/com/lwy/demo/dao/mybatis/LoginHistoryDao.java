package com.lwy.demo.dao.mybatis;

import com.lwy.demo.entity.ManagerLoginHistory;
import com.lwy.demo.entity.UserLoginHistory;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * @author Administrator
 */
@Repository
public interface LoginHistoryDao {
    /**
     * 用户历史
     */
    @Insert("insert into userloginhistory(userid,logintime,localhostaddress) values(#{userId},#{loginTime},#{localHostAddress})")
    void addUserLoginHistory(UserLoginHistory userLoginHistory);

    /**
     * 管理员
     */
    @Insert("insert into managerloginhistory(userid,logintime,localhostaddress) values(#{userId},#{loginTime},#{localHostAddress})")
    void addManagerLoginHistory(ManagerLoginHistory managerLoginHistory);

    /**
     *根据id获取用户登录历史信息
     */
    @Select("select * from userloginhistory where userId = #{id}")
    List<UserLoginHistory> getUserLoginHistory(Integer id);

    /**
     * 查询所有登录历史记录
     */
    @Select("select * from userloginhistory")
    List<UserLoginHistory> getAllUserLoginHistory();

    /**
     * 查询指定日期的历史记录
     */
    @Select("select * from userloginhistory where loginTime >= #{startTime} and loginTime <= #{endTime}")
    List<UserLoginHistory> getAllUserLoginHistoryByDate(HashMap<String,String> map);

    /**
     * 查询指定用户指定日期历史记录
     */
    @Select("select * from userloginhistory where loginTime >= #{startTime} and loginTime <= #{endTime} and userId = #{id}")
    List<UserLoginHistory> getUserLoginHistoryByDate(HashMap<String,Object> map);

}
