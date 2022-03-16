package com.lwy.demo.dao;


import org.apache.ibatis.annotations.Insert;
import org.springframework.stereotype.Repository;

@Repository
public interface RegisterDao {
    @Insert("insert into user ")
    void register();
}
