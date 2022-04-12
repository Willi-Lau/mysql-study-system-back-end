package com.lwy.demo.dao;

import com.lwy.demo.entity.ManagerLoginHistory;
import com.lwy.demo.entity.UserLoginHistory;
import org.apache.ibatis.annotations.Insert;
import org.springframework.stereotype.Repository;

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
}
