package com.lwy.demo.controller;

import com.lwy.demo.TO.ResultDTO;
import com.lwy.demo.service.ForgetPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.xml.transform.Result;

/**
 * @author Administrator
 */
@CrossOrigin
@RestController
@RequestMapping("/FrogetPasswordController")
public class FrogetPasswordController {


    @Autowired
    private ForgetPasswordService forgetPasswordService;


    @PostMapping("/changePassword")
    public ResultDTO changePassword(@RequestParam String phone,
                                    @RequestParam String password){
        ResultDTO resultDTO = new ResultDTO();
        //验证手机号是否为空 密码是否为空
        if(StringUtils.isEmpty(phone) || StringUtils.isEmpty(password)){
            resultDTO.setType(false);
            resultDTO.setObject("NPException");
            return resultDTO;
        }
        //执行修改
        try {
            forgetPasswordService.changePassword(phone,password);
        } catch (Exception e) {
            e.printStackTrace();
            resultDTO.setType(false);
            return resultDTO;
        }
        resultDTO.setType(true);
        return resultDTO;
    }
}
