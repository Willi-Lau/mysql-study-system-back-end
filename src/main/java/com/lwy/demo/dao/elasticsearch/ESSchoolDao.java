package com.lwy.demo.dao.elasticsearch;

import com.lwy.demo.entity.School;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ESSchoolDao extends ElasticsearchRepository<School,String> {
}
