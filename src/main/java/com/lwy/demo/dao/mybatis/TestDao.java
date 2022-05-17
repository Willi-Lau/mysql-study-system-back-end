package com.lwy.demo.dao.mybatis;

import com.lwy.demo.entity.Test1;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Repository
public interface TestDao {
    /**
     * 公共sql执行
     * @param sql
     */
    @Select("select * from test1 , (select id,englishResult from test1 ) e1 , (select id,mathResult from test1 ) m1 where test1.id = e1.id and e1.id = m1.id and e1.englishResult > m1.mathResult;")
    List<Test1> test1(String sql);

    @Select("#{sql}")
    List<Test1> test2(String sql);
}
