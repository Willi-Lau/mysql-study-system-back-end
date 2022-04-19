package com.lwy.demo.dao.mybatis;

import com.lwy.demo.entity.ManagerBroadcast;
import org.apache.ibatis.annotations.Insert;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerBroadcastDao {

    @Insert("insert into managerbroadcast(managerId,Time,context) values(#{managerId},#{time},#{context}) ")
    void insertManagerBroadcast(ManagerBroadcast managerBroadcast);
}
