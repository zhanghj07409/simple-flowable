package com.flowable.common;

import org.springframework.core.convert.converter.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: zhanghj
 * 开发时间: 2017/4/11<br>
 * <br>
 */
public class DateConvert implements Converter<String, Date> {

    private static final String sdf1reg = "^\\d{2,4}\\-\\d{1,2}\\-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$";

    private static final String sdf2reg = "^\\d{2,4}\\-\\d{1,2}\\-\\d{1,2}$";

    private static final String sdf3reg = "^\\d{2,4}\\/\\d{1,2}\\/\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$";

    private static final String sdf4reg = "^\\d{2,4}\\/\\d{1,2}\\/\\d{1,2}$";

    private static final String sdf5reg = "^\\d{8}$";

    private static final String sdf6reg = "^\\d{2,4}\\-\\d{1,2}\\-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}.\\d{1,3}$";

    private static final String sdf7reg = "^\\d{2,4}\\-\\d{1,2}\\-\\d{1,2}T\\d{1,2}:\\d{1,2}:\\d{1,2}.\\d{1,3}Z$";

    @Override
    public Date convert(String str) {
        Date date = null;
        Pattern p1 = Pattern.compile(sdf1reg);
        Matcher m1 = p1.matcher(str);
        Pattern p2 = Pattern.compile(sdf2reg);
        Matcher m2 = p2.matcher(str);
        Pattern p3 = Pattern.compile(sdf3reg);
        Matcher m3 = p3.matcher(str);
        Pattern p4 = Pattern.compile(sdf4reg);
        Matcher m4 = p4.matcher(str);

        Pattern p5 = Pattern.compile(sdf5reg);
        Matcher m5 = p5.matcher(str);
        Pattern p6 = Pattern.compile(sdf6reg);
        Matcher m6 = p6.matcher(str);
        Pattern p7 = Pattern.compile(sdf7reg);
        Matcher m7 = p7.matcher(str);
        try {
            if (m1.matches()) {
                date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str);
            }
            else if (m2.matches()) {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(str);
            }
            else if (m3.matches()) {
                date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(str);
            }
            else if (m4.matches()) {
                date = new SimpleDateFormat("yyyy/MM/dd").parse(str);
            }
            else if (m5.matches()) {
                date = new SimpleDateFormat("yyyyMMdd").parse(str);
            }
            else if (m6.matches()){
                date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(str);
            }
            else if (m7.matches()){
                date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(str);
            }
        }catch (ParseException e) {
            throw new RuntimeException("非法日期字符串，解析失败：" + str, e);
        }

        return date;
    }
}
