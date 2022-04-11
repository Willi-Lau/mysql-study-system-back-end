package com.lwy.demo.service;

import com.lwy.demo.TO.ResultDTO;

import java.util.concurrent.ExecutionException;

public interface ManagerSendMessageService {

    public ResultDTO sentMeaasge(String content) throws ExecutionException, InterruptedException;
}
