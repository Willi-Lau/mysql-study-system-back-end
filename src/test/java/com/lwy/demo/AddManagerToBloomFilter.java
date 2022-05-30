package com.lwy.demo;


import com.lwy.demo.config.InfoConfig;
import com.lwy.demo.dao.elasticsearch.ESSchoolDao;
import com.lwy.demo.dao.mybatis.ManagerDao;
import com.lwy.demo.dao.mybatis.SchoolDao;
import com.lwy.demo.dao.mybatis.UserDao;
import com.lwy.demo.entity.Manager;
import com.lwy.demo.entity.School;
import com.lwy.demo.entity.User;
import com.lwy.demo.utils.ElasticSearchSchoolUtils;
import com.lwy.demo.utils.RedisUtil;
import com.lwy.demo.utils.TimeUtils;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@SpringBootTest
public class AddManagerToBloomFilter {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ESSchoolDao esSchoolDao;

    @Autowired
    private ManagerDao managerDao;

    @Autowired
    private SchoolDao schoolDao;


    @Test
    public void Test() throws JSONException {

//        List<User> userList = userDao.getUserList();
//        for (User user : userList){
//            redisUtil.set(InfoConfig.REDIS_USER_STUDENT_NUMBER+user.getStudentNumber(),user);
//            redisUtil.set(InfoConfig.REDIS_USER_IDENTITY_CARD_NUMBER+user.getIdentityCardNumber(),user);
//        }
//
//        List<Manager> managerList = managerDao.getManagerList();
//        for (Manager manager : managerList){
//            redisUtil.set(InfoConfig.REDIS_MANAGER_NUMBER + manager.getManagerNumber() , manager);
//        }
//
//        List<School> schoolList = schoolDao.getSchoolList();
//        esSchoolDao.saveAll(schoolList);

//        redisUtil.set(InfoConfig.REDIS_SQL_EXAMPLE_+"SELECT_MUTIPLY","select * from student left join studentinnfo on student.id = studentinnfo.id left join user on user.id = student.id");
//        redisUtil.set(InfoConfig.REDIS_SQL_EXAMPLE_+"SELECT_SON","select * from student where id in (select id from user)");
//    }

        List<User> userList = userDao.getUserList();
        for (User user : userList){
            redisUtil.sSet("USER_ID_NUMBER_SET",user.getIdentityCardNumber());
            redisUtil.sSet("USER_STUDENT_NUMBER_SET",user.getStudentNumber());
            redisUtil.sSet("USER_PHONE_NUMBER_SET",user.getPhone());
        }
    }

    //模板
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    /**
     * 创建 index
     */
    @Test
    public void createIndex(){

        Iterable<School> all = esSchoolDao.findAll();
        List<School> schoolList = new ArrayList<>();
        all.forEach((x)->{schoolList.add(x);});
        System.out.println("-------------------------------------------------"+schoolList);

        School school = esSchoolDao.findById("2").get();
        System.out.println("----------------------------------------------------"+school);
    }

    @Autowired
    private ElasticSearchSchoolUtils elasticSearchSchoolUtils;
        @Test
    public void save(){
            List<School> all = elasticSearchSchoolUtils.getAll();
            all.forEach(System.out::println);

            System.out.println("----------------------------------------");

            List<School> list = elasticSearchSchoolUtils.getByName("北京理工");
            list.forEach(System.out::println);
        }

        @Test
        public void sb() throws ParseException {
//            2023-04-21T11:42:52.699Z
            String time = "2023-04-21T11:42:52.699Z";
            System.out.println(time.substring(0,10) + " "+time.substring(11,19));
            Long aLong = TimeUtils.changeTimeStringToLong(time.substring(0,10) + " "+time.substring(11,19));
            System.out.println(aLong);
        }
    @Test
    public void sb2() throws ParseException {
            List<Integer> list1 = new ArrayList<>();
            List<Integer> list2 = new ArrayList<>();

        System.out.println(list1.equals(list2));

    }

}
