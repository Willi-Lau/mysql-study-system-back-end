package com.lwy.demo.service.impl;

import com.lwy.demo.TO.ResultDTO;

import com.lwy.demo.config.InfoConfig;
import com.lwy.demo.dao.mybatis.ManagerBroadcastDao;
import com.lwy.demo.entity.ManagerBroadcast;
import com.lwy.demo.kafka.KafkaProducer;
import com.lwy.demo.service.ManagerSendMessageService;
import com.lwy.demo.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutionException;

@Service
@Transactional
public class ManagerSendMessageServiceImpl implements ManagerSendMessageService {


    @Autowired
    private KafkaProducer kafkaProduct;

    @Autowired
    private ManagerBroadcastDao managerBroadcastDao;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public ResultDTO sentMeaasge(String content,String token) throws Exception {
        kafkaProduct.data("managerSendMessage", content);
        ResultDTO resultDTO = new ResultDTO();
        //存入广播记录表中
        Integer managerId = (Integer)redisUtil.get(InfoConfig.REDIS_TOKEN_MANAGER_LOGIN_INFO + token);
        ManagerBroadcast managerBroadcast = ManagerBroadcast.builder().context(content).managerId(managerId).time(String.valueOf(System.currentTimeMillis())).build();
       managerBroadcastDao.insertManagerBroadcast(managerBroadcast);
        return resultDTO;
    }
}
