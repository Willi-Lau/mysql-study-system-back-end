package com.lwy.demo.utils;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.Arrays;

@Configuration
public class KafkaClientUtils {
    // 指定要监听的 topic
    @KafkaListener(topics = "canal")
    public void consumeCanal(String msg) throws JSONException { // 参数: 收到的 value
        System.out.println("收到的信息: " + msg);
        JSONObject jsonObject = new JSONObject(msg);
        String data = jsonObject.getString("data");

        System.out.println(data);
        String substring = data.substring(0, data.length() - 2);
        String substring1 = substring.substring(2, substring.length());
        String[] split = substring1.split(",");
        System.out.println(Arrays.asList(split));

        System.out.println(jsonObject.get("type"));
        System.out.println(jsonObject.get("table"));
        System.out.println(jsonObject.get("database"));
    }
}
