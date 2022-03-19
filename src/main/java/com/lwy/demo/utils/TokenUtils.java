package com.lwy.demo.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.lwy.demo.config.InfoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Component
public class TokenUtils {
    //设置过期时间
    private static final long EXPIRE_DATE= 12 * 60 * 60 * 100000;

    @Autowired
    private RedisUtil redisUtil;

    public  String token (String username){
        String token = "";
        Integer i = username.hashCode();
        String[] split = UUID.randomUUID().toString().split("-");
        for (String s : split){
            token += s;
        }
        token += i;
        redisUtil.set(InfoConfig.REDIS_TOKEN_LOGIN_INFO +token,InfoConfig.REDIS_TOKEN_LOGIN_INFO +token,EXPIRE_DATE);
        return token;
    }

    public  boolean verify(String token){
        boolean b = redisUtil.hasKey(InfoConfig.REDIS_TOKEN_LOGIN_INFO +token);
        return b;
    }
}
