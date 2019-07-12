package com.dreamsecurity.ssooauth.common;

import android.content.Context;
import android.util.Log;

/**
 *
 */
public class Logger {

    public interface ILoggerStrategy{
        void setTagPrefix(String tagPrefix);
        void i(String tag,String msg);
        void w(String tag,String msg);

        void d(String tag,String msg);
        void v(String tag,String msg);
        void write(int level,String tag,String msg);

    }

    private static ILoggerStrategy logger = LoggerStrategyLog.getInstance();
    private static boolean realVersion = true;
    private static String tagPrefix     = "";

    public static void setTagPrefix(String tagPrefix) {
        Logger.tagPrefix = tagPrefix;
        logger.setTagPrefix( tagPrefix );
    }

    public static void setRealVersion(boolean realVersion) {
        Logger.realVersion = realVersion;
    }

    public static void setLogger(ILoggerStrategy customLogger){
        logger = customLogger;
    }

    public static void switchingToFile(Context context){
        logger = LoggerStrategyFileLog.getInstance(context);
    }
    public static void switchingToLogcat(){
        logger = LoggerStrategyLog.getInstance( tagPrefix );
    }

    public static void switchingToNoLogging(){
//        logger = LoggerStrategyLog.getInstance()
    }

    public void i(String tag, String msg) {
        logger.i(tag,msg);
    }

    public void w(String tag, String msg) {
        logger.w( tag , msg);
    }

    public void d(String tag, String msg) {
        logger.d( tag, msg );
    }

    public void v(String tag, String msg) {
        logger.v(tag,msg);
    }

    public void write(int level, String tag, String msg) {
       logger.write( level, tag, msg);
    }
}
