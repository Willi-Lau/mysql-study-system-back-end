package com.lwy.demo.dao.mybatis;

import com.lwy.demo.entity.AllTestDiscribe;
import org.apache.http.annotation.Obsolete;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public interface AllTestDiscribeDao {

    /**
     * 获取所有的题号
     */
    @Select("select id from allTestDiscribe")
    List<Integer> getAllId();
    /**
     * 根据id获取题目全部信息
     */
    @Select("select * from allTestDiscribe where id = #{id}")
    AllTestDiscribe getAllTestDiscribe(Integer id);
    /**
     * 根据id修改信息
     */
    @Update("update allTestDiscribe set tableColumn = #{tableColumn} where id = #{id}")
    void hahah(HashMap<String,String> map);
    /**
     * 获取所有的题目信息
     */
    @Select("select * from allTestDiscribe")
    List<AllTestDiscribe> getAll();

    /**
     * 添加题目信息
     */
    @Insert("insert into allTestDiscribe(titleDiscribe,tableName,tableColumn,trueSQL) values (#{titleDiscribe},#{tableName},#{tableColumn},#{trueSQL})")
    void insert(AllTestDiscribe allTestDiscribe);

    /**
     * 根据id删除题目信息
     */
    @Delete("delete from allTestDiscribe where id = #{id}")
    void delete(Integer id);

    /**
     * 根据id修改题目信息
     */
    @Update("update allTestDiscribe set titleDiscribe = #{titleDiscribe} , tableName = #{tableName}, tableColumn = #{tableColumn},trueSQL = #{trueSQL} where id = #{id}")
    void update(AllTestDiscribe allTestDiscribe);

    /**
     * 条件查询
     */
    List<AllTestDiscribe> selectByCondition(HashMap hashMap);

}
