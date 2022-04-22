package com.lwy.demo.kafka;

import com.lwy.demo.config.InfoConfig;
import com.lwy.demo.controller.MessageController;
import com.lwy.demo.entity.School;
import com.lwy.demo.entity.User;
import com.lwy.demo.utils.ElasticSearchSchoolUtils;
import com.lwy.demo.utils.RedisUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumer {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private MessageController messageController;

    @Autowired
    private ElasticSearchSchoolUtils elasticSearchSchoolUtils;

    Logger logger = LoggerFactory.getLogger(getClass());


    @KafkaListener(topics = "manager")
    public void consumeManager(String msg) {
        logger.info("kafka topic-manager: " + msg);
    }

    /**
     * 修改user下的密码信息
     * @param msg 从canal 传递过来的参数
     * @throws JSONException
     */
    @KafkaListener(topics = "canal")
    public void consumeCanal(String msg) throws JSONException {

        JSONObject jsonObject = new JSONObject(msg);
        //如果说User表发生改变就修改user缓存信息
        if("mysqlstudy".equals(jsonObject.get("database"))
                && "user".equals(jsonObject.get("table"))
                && "UPDATE".equals(jsonObject.get("type"))
        ){
            //输出日志
            logger.info("kafka topic-canal: " + msg);
            //获取修改的数据
            HashMap<String,String> userMap = this.analysisKafkaResultData(jsonObject);
            //删除学号密码部分
            redisUtil.del(InfoConfig.REDIS_USER_STUDENT_NUMBER+userMap.get("studentNumber"));
            //删除身份证密码部分
            redisUtil.del(InfoConfig.REDIS_USER_IDENTITY_CARD_NUMBER+userMap.get("identityCardNumber"));
        }
        //修改 School 到 ElasticSearch
        if("mysqlstudy".equals(jsonObject.get("database"))
                && "school".equals(jsonObject.get("table"))

        ){
            //输出日志
            logger.info("kafka topic-canal: " + msg);
            //获取修改的数据
            HashMap<String,String> schoolMap = this.analysisKafkaResultData(jsonObject);
            //拼接School
            School school = School.builder().name(schoolMap.get("name"))
                    .id(Integer.parseInt(schoolMap.get("id")))
                    .createTime(schoolMap.get("createTime"))
                    .deadline(schoolMap.get("deadline"))
                    .studentNum(Integer.parseInt(schoolMap.get("studentNum")))
                    .build();
            //存入 ElasticSearch
            elasticSearchSchoolUtils.update(school);
        }

    }

    /**
     * kafka 消费 管理员发送全体通知
     * @param msg
     */
    @KafkaListener(topics = "managerSendMessage")
    public void consumeManagerSendMagess(String msg) {
       //调用SSE
        messageController.push(msg);
        logger.info("kafka topic-managerSendMessage: " + msg);
    }

    /**
     * 解析 Kafka 从 Canal 传递进来的数据并返回结果
     * @param jsonObject
     * @return HashMap<String,String> key : mysql-column    value : 对应的值
     * @throws JSONException
     */
    private HashMap<String,String> analysisKafkaResultData(JSONObject jsonObject) throws JSONException {
        //获取修改的数据
        String data = jsonObject.getString("data");
        //拆分 因为这个data 是一个json
        String substringCutEnd = data.substring(0, data.length() - 2);
        String substringCutHead = substringCutEnd.substring(2, substringCutEnd.length());
        //数组中每一个元素是 "studentNumber":"9876543211"这种格式
        String[] dataList = substringCutHead.split(",");
        HashMap<String,String> resultMap = new HashMap<>(16);
        for (String jsonValue : dataList){
            String[] info = jsonValue.split(":");
            String key = info[0].substring(1,info[0].length()-1);
            String value = info[1].substring(1,info[1].length()-1);
            resultMap.put(key,value);
        }
        return resultMap;
    }
}
