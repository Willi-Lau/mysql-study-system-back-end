package com.lwy.demo.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.RequestMapping;

@Component
public class KafkaProduct {

    @Qualifier
    KafkaTemplate<String, String> kafka;

    public String sendManagerData(String msg) {
        //发送
        kafka.send("first", msg).addCallback(success ->{
            //成功业务逻辑
            if(success != null){
                System.out.println("success send kafka" + success.getRecordMetadata().toString());
            }

        },failure->{
            //失败业务逻辑
            System.out.println("error send kafka" + failure.getMessage());
        });
        return "ok";
    }
}
