package com.lwy.demo.utils;

import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Component
public class TimeUtils {

    /**
     * 获取当前时间
     * @return
     */
    public static Long getNowTime(){
        return System.currentTimeMillis();
    }

    /**
     * 时间戳转日期
     * @param time
     * @return
     * @throws ParseException
     */
    public static String changeTimeLongToString(Long time) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse(sdf.format(time));
        return sdf.format(date);
    }

    /**
     * 日期转时间戳
     * @param time
     * @return
     * @throws ParseException
     */
    public static Long changeTimeStringToLong(String time) throws ParseException {
        SimpleDateFormat myFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        Date parse = myFormat.parse(time);
        long time1 = parse.getTime();
        return time1;
    }
}
