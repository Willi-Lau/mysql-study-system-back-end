package com.lwy.demo.dao.mybatis;

import com.lwy.demo.entity.School;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchoolDao {
    /**
     * 查询所有的学校
     */
    @Select("select * from school")
    List<School> getSchoolList();
    /**
     * 指定学校人数+1
     */
    @Update("update school set studentNum = studentNum + 1 where name = #{name}")
    void addSchoolNum(String name);
}
