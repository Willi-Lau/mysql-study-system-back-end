package com.lwy.demo;

import com.lwy.demo.utils.TimeUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.concurrent.*;

@SpringBootTest
class DemoApplicationTests {

    @Test
    void contextLoads() throws UnknownHostException {
        InetAddress addr = InetAddress.getLocalHost();
        System.out.println("Local HostAddress: "+addr.getHostAddress());
        String hostname = addr.getHostName();
        System.out.println("Local host name: "+hostname);
    }

    @Test
    void contextLoads2() throws Exception {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10,30,0, TimeUnit.SECONDS,new LinkedBlockingDeque<>());
       threadPoolExecutor.execute(()->{
           System.out.println("hahah");
       });

    }

}
