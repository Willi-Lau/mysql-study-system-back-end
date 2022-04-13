package com.lwy.demo.kafka;

import com.lwy.demo.config.InfoConfig;
import com.lwy.demo.controller.MessageController;
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

    @Autowired
    private MessageController messageController;

    Logger logger = LoggerFactory.getLogger(getClass());


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

        JSONObject jsonObject = new JSONObject(msg);
        //如果说User表发生改变就修改user缓存信息
        if("mysqlstudy".equals(jsonObject.get("database"))
                && "user".equals(jsonObject.get("table"))
                && "UPDATE".equals(jsonObject.get("type"))
        ){

            logger.info(msg);
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
            //删除学号密码部分
            redisUtil.del(InfoConfig.REDIS_USER_STUDENT_NUMBER+userMap.get("studentNumber"));
            //删除身份证密码部分
            redisUtil.del(InfoConfig.REDIS_USER_IDENTITY_CARD_NUMBER+userMap.get("identityCardNumber"));
        }
        //禁用用户 上面删除了学号和身份证号的缓存 下面的修改黑名单List
//        if("mysqlstudy".equals(jsonObject.get("database"))
//                && "user".equals(jsonObject.get("table"))
//                && "UPDATE".equals(jsonObject.get("type"))
//                && jsonObject.getString("old").contains("state")
//        ){
//            logger.info(msg);
//            //获取修改的数据
//            String data = jsonObject.getString("data");
//            //拆分 因为这个data 是一个json
//            String substringCutEnd = data.substring(0, data.length() - 2);
//            String substringCutHead = substringCutEnd.substring(2, substringCutEnd.length());
//            //数组中每一个元素是 "studentNumber":"9876543211"这种格式
//            String[] dataList = substringCutHead.split(",");
//            HashMap<String,String> userMap = new HashMap<>(16);
//            for (String jsonValue : dataList){
//                String[] info = jsonValue.split(":");
//                String key = info[0].substring(1,info[0].length()-1);
//                String value = info[1].substring(1,info[1].length()-1);
//                userMap.put(key,value);
//            }
//            //这里不能删除再查询获取 名单需要一直存在
//            //移除名单
//            if(userMap.get("state").equals("1")){
//                redisUtil.setRemove(InfoConfig.USER_BLACK_LIST,Integer.parseInt(userMap.get("id")));
//            }
//            //如果改为false要加入redis黑名单里
//            else {
//                redisUtil.sSet(InfoConfig.USER_BLACK_LIST,Integer.parseInt(userMap.get("id")));
//            }
//        }

    }

    /**
     * kafka 消费 管理员发送全体通知
     * @param msg
     */
    @KafkaListener(topics = "managerSendMessage")
    public void consumeManagerSendMagess(String msg) {
       //调用SSE
        messageController.push(msg);

    }
}
