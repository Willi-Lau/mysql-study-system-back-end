package com.lwy.demo.utils;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Administrator
 */
@Component
public class LocalHostUtil {

    public String getLocalHost() throws UnknownHostException {
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        if(StringUtils.isEmpty(hostAddress)){
            return "null";
        }
        return hostAddress;
    }

    public String getHostName() throws Exception{
        String hostName = InetAddress.getLocalHost().getHostName();
        if(StringUtils.isEmpty(hostName)){
            return "null";
        }
        return hostName;
    }

}
