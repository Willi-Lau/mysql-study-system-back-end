package com.lwy.demo.utils;

import com.lwy.demo.dao.elasticsearch.ESSchoolDao;
import com.lwy.demo.entity.School;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ElasticSearchSchoolUtils {

    @Autowired
    private ESSchoolDao esSchoolDao;

    public void addAll(Iterable iterable){
        esSchoolDao.saveAll(iterable);
    }

    public void add(School school){
        esSchoolDao.save(school);
    }

    public void delete(School school){
        esSchoolDao.delete(school);
    }

    public void update(School school){
        this.add(school);
    }

    public List<School> getByName(String name){
        MatchPhraseQueryBuilder matchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery("name", name);
        Iterable<School> schoolIterable = esSchoolDao.search(matchPhraseQueryBuilder);
        List<School> schoolList = new ArrayList<>();
        schoolIterable.forEach(schoolList::add);
        return schoolList;
    }

    public List<School> getAll(){
        Iterable<School> allSchool = esSchoolDao.findAll();
        List<School> schoolList = new ArrayList<>();
        allSchool.forEach(schoolList::add);
        return schoolList;
    }
}
