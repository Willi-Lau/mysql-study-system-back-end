package com.lwy.demo.dao.mybatis;

import com.lwy.demo.entity.TableColumnAttribute;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TableColumnAttributeDao {
    /**
     * 获取表列表
     */
    @Select("select * from tableColumnAttribute")
    List<TableColumnAttribute> getAll();

    /**
     * 指定表名获取表信息
     */
    @Select("select * from tableColumnAttribute where tableName = #{name}")
    TableColumnAttribute get(String name);
}
