package com.lwy.demo.service.impl;

import com.lwy.demo.TO.ResultDTO;

import com.lwy.demo.kafka.KafkaProducer;
import com.lwy.demo.service.ManagerSendMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutionException;

@Service
@Transactional
public class ManagerSendMessageServiceImpl implements ManagerSendMessageService {


    @Autowired
    private KafkaProducer kafkaProduct;

    @Override
    public ResultDTO sentMeaasge(String content) throws ExecutionException, InterruptedException {
        kafkaProduct.data("managerSendMessage", content);
        ResultDTO resultDTO = new ResultDTO();
        return resultDTO;
    }
}
