package com.lwy.demo;

import com.lwy.demo.utils.TimeUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.InetAddress;
import java.net.UnknownHostException;

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
        System.out.println(1649779200000L - 1649692800000L);
        System.out.println((long) 365 * 1000 * 60 * 60 * 24);

    }

}
