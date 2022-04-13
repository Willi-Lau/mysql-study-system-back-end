package com.lwy.demo.dao.mybatis;

import com.lwy.demo.entity.Manager;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManagerDao {
    /**
     * 查询指定管理员
     */
    @Select("select * from manager where managerNumber = #{managerNumber}")
    Manager getManager(String managerNumber);
    /**
     * 查询所有管理员
     */
    @Select("select * from manager")
    List<Manager> getManagerList();
}
