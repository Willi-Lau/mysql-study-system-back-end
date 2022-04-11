package com.lwy.demo.kafka;

import com.lwy.demo.config.InfoConfig;
import com.lwy.demo.entity.User;
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

@Configuration
public class KafkaConsumer {

    @Autowired
    private RedisUtil redisUtil;



    @KafkaListener(topics = "manager")
    public void consumeManager(String msg) {
        System.out.println("收到的信息: " + msg);
    }

    /**
     * 修改user下的密码信息
     * @param msg
     * @throws JSONException
     */
    @KafkaListener(topics = "canal")
    public void consumeCanal(String msg) throws JSONException { // 参数: 收到的 value
        //日志部分
        Logger logger = LoggerFactory.getLogger(getClass());
        logger.info(msg);
        JSONObject jsonObject = new JSONObject(msg);
        //符合修改 user表操作在进行更改
        if("mysqlstudy".equals(jsonObject.get("database"))
                && "user".equals(jsonObject.get("table"))
                && "UPDATE".equals(jsonObject.get("type"))){
            //获取修改的数据
            String data = jsonObject.getString("data");
            //拆分 因为这个data 是一个json
            String substringCutEnd = data.substring(0, data.length() - 2);
            String substringCutHead = substringCutEnd.substring(2, substringCutEnd.length());
            //数组中每一个元素是 "studentNumber":"9876543211"这种格式
            String[] dataList = substringCutHead.split(",");
            HashMap<String,String> userMap = new HashMap<>(16);
            for (String jsonValue : dataList){
                String[] info = jsonValue.split(":");
                String key = info[0].substring(1,info[0].length()-1);
                String value = info[1].substring(1,info[1].length()-1);
                userMap.put(key,value);
            }
            //修改学号密码部分
            redisUtil.set(InfoConfig.REDIS_USER_STUDENT_NUMBER+userMap.get("studentNumber"),userMap.get("password"));
            //修改身份证密码部分
            redisUtil.set(InfoConfig.REDIS_USER_IDENTITY_CARD_NUMBER+userMap.get("identityCardNumber"),userMap.get("password"));
        }

    }
}
