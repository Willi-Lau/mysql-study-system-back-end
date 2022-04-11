package com.lwy.demo.controller;

import com.lwy.demo.TO.ResultDTO;
import com.lwy.demo.service.ManagerSendMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@CrossOrigin
@RestController
@RequestMapping("/ManagerSendMessageController")
public class ManagerSendMessageController {

    @Autowired
    private ManagerSendMessageService managerSendMessageService;

    //发送群体通知 kafka -> SSE
    @PostMapping("/sendMeaasge")
    public ResultDTO sendMeaasge(@RequestParam String content) throws ExecutionException, InterruptedException {
        ResultDTO resultDTO = new ResultDTO();
        if(StringUtils.isEmpty(content)){
            resultDTO.setType(false);
            return resultDTO;
        }
        return managerSendMessageService.sentMeaasge(content);
    }
}
