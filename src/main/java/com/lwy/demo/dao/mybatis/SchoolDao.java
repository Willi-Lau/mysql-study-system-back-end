package com.lwy.demo.dao.mybatis;

import com.lwy.demo.entity.School;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
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
    /**
     * 指定学校人数-1
     */
    @Update("update school set studentNum = studentNum - 1 where name = #{name}")
    void cutSchoolNum(String name);
    /**
     * 给学校进行续期
     */
    @Update("update school set deadline = #{renewDuration} where id = #{id}")
    void renewSchool(HashMap<String,Object> map);
    /**
     * 新建学校
     */
    @Insert("insert into school(name,createTime,deadline,studentNum) values(#{name},#{createTime},#{deadline},0)")
    void insertSchool(School school);
    /**
     * 根据id查询指定学校
     */
    @Select("select * from school where id = #{id}")
    School getSchool(Integer id);
}
