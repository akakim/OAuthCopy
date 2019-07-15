package com.dreamsecurity.ssooauth.common.logger;

import android.content.Context;

/**
 * 미완성 .
 * file을 권한을 피해서 로그를 편하게 조회할수 있을까?
 *
 */
public class LoggerStrategyFileLog implements Logger.ILoggerStrategy {

    public final static LoggerStrategyFileLog instance = new LoggerStrategyFileLog();

    public static String TAG_PREFIX = "dreamLogin";

    public static Context context;
    public static String fileName = "dreamLogger";


    public static LoggerStrategyFileLog getInstance(Context context) { return instance; }

    public static LoggerStrategyFileLog getInstance(Context context ,String tag){
        LoggerStrategyFileLog.context = context;
        instance.setTagPrefix( tag );
        return instance;
    }

    @Override
    public void setTagPrefix(String tagPrefix) {

    }

    @Override
    public void i(String tag, String msg) {

    }

    @Override
    public void w(String tag, String msg) {

    }

    @Override
    public void e(String tag, String msg) {

    }

    @Override
    public void d(String tag, String msg) {

    }

    @Override
    public void v(String tag, String msg) {

    }

    @Override
    public void write(int level, String tag, String msg) {

    }
}
