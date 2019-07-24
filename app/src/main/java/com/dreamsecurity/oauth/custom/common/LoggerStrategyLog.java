package com.dreamsecurity.oauth.custom.common;

import android.util.Log;

public class LoggerStrategyLog implements Logger.ILoggerStrategy {

    public final static LoggerStrategyLog instance = new LoggerStrategyLog();

    public static String TAG_PREFIX = "dreamLogin";
    public static LoggerStrategyLog getInstance() { return instance; }

    public static LoggerStrategyLog getInstance(String tag){
        instance.setTagPrefix( tag );
        return instance;
    }

    private LoggerStrategyLog(){ }


    @Override
    public void setTagPrefix(String tagPrefix) {
        TAG_PREFIX = tagPrefix;
    }

    @Override
    public void i(String tag, String msg) {
        Log.i( TAG_PREFIX + tag , msg);
    }

    @Override
    public void w(String tag, String msg) {
        Log.w( TAG_PREFIX + tag , msg);
    }

    @Override
    public void e(String tag, String msg) {
        Log.e( TAG_PREFIX + tag,msg);
    }

    @Override
    public void d(String tag, String msg) {
        Log.d( TAG_PREFIX + tag , msg);
    }

    @Override
    public void v(String tag, String msg) {
        Log.v( TAG_PREFIX + tag , msg);
    }

    @Override
    public void write(int level, String tag, String msg) {
        Log.println( level, TAG_PREFIX + tag, msg);
    }
}
