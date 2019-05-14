package com.flowable.configuration;

import com.flowable.common.DateConvert;

import java.text.*;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: zhanghj
 * 开发时间: 2017/4/11<br>
 * <br>
 */
public class MySimpleDateFormat extends SimpleDateFormat {

    public MySimpleDateFormat(String pattern) {
        super(pattern);
    }

    public MySimpleDateFormat(String pattern, Locale locale) {
        super(pattern, locale);
    }

    public MySimpleDateFormat(String pattern, DateFormatSymbols formatSymbols) {
        super(pattern, formatSymbols);
    }

    @Override
    public Date parse(String text, ParsePosition pos) {
        /**
         * 设置处理长度,不设置处理长度会报解析报错
         */
        pos.setIndex(text.length());
        DateConvert dateConvert = new DateConvert();
        return dateConvert.convert(text);
    }
}
