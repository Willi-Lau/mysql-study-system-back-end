package com.lwy.demo.kafka;

import com.lwy.demo.TO.ResultDTO;
import org.apache.kafka.common.protocol.types.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SuccessCallback;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class KafkaProducer {

    // Kafka 模板用来向 kafka 发送数据
    @Autowired
    KafkaTemplate<String, String> kafka;

    Logger logger = LoggerFactory.getLogger(getClass());


    public ResultDTO data(String topic,String message) throws ExecutionException, InterruptedException {
        ResultDTO resultDTO = new ResultDTO();
        //发送message
        ListenableFuture<SendResult<String, String>> listenableFuture = kafka.send(topic, message);
        //发送成功回调
        SuccessCallback<SendResult<String, String>> successCallback = new SuccessCallback<SendResult<String, String>>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                //成功业务逻辑
                logger.info("success send kafka" + result);
                resultDTO.setType(true);
                resultDTO.setObject(result);
            }
        };
        //发送失败回调
        FailureCallback failureCallback = new FailureCallback() {
            @Override
            public void onFailure(Throwable ex) {
                //失败业务逻辑
                logger.error("error send kafka" + ex);
                resultDTO.setType(false);
                resultDTO.setObject(ex);
            }
        };
        //添加回调函数
        listenableFuture.addCallback(successCallback, failureCallback);
        return resultDTO;
    }
}