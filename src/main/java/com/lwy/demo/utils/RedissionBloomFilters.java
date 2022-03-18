package com.lwy.demo.utils;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.Funnels;
import org.redisson.Redisson;
import org.redisson.RedissonBloomFilter;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.redisson.command.CommandExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.redisson.config.Config;


@Component
public class RedissionBloomFilters {

    static RBloomFilter<String> bloomFilter;

    public RedissionBloomFilters(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        config.useSingleServer().setPassword("12345");
        //构造Redisson
        RedissonClient redisson = Redisson.create(config);

        bloomFilter = redisson.getBloomFilter("userLoginFilter");
        //初始化布隆过滤器：预计元素为100000000L,误差率为3%
        bloomFilter.tryInit(100000000L,0.03);
    }

    public void addValue(String s){
        bloomFilter.add(s);
    }

    public boolean containsValue(String s){
        return bloomFilter.contains(s);
    }

}
