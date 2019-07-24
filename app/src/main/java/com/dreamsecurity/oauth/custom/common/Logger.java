package com.dreamsecurity.oauth.custom.common;

import android.content.Context;

/**
 *
 */
public class Logger {

    public interface ILoggerStrategy{
        void setTagPrefix(String tagPrefix);
        void i(String tag, String msg);
        void w(String tag, String msg);
        void e(String tag, String msg);
        void d(String tag, String msg);
        void v(String tag, String msg);
        void write(int level, String tag, String msg);

    }

    private static ILoggerStrategy logger = LoggerStrategyLog.getInstance();
    private static boolean realVersion = true;
    private static String tagPrefix     = "";

    public static void setTagPrefix(String tagPrefix) {
        Logger.tagPrefix = tagPrefix;
        logger.setTagPrefix( tagPrefix );
    }

    public static boolean isRealVersion() {
        return realVersion;
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

    public static void i(String tag, String msg) {
        logger.i(tag,msg);
    }

    public static void w(String tag, String msg) {
        logger.w( tag , msg);
    }

    public static void d(String tag, String msg) {
        logger.d( tag, msg );
    }

    public static void v(String tag, String msg) {
        logger.v(tag,msg);
    }

    public static void e(String tag, String msg) {
        logger.e(tag,msg);
    }

    public static void write(int level, String tag, String msg) {
       logger.write( level, tag, msg);
    }

    public static void write(Exception exception) {
        if (exception == null) {
            return;
        }
        e("Exception", exception.toString());
        StackTraceElement[] elem = exception.getStackTrace();
        for (int i = 0; i < elem.length; ++i) {
            e("Exception", elem[i].toString());
        }
    }

}
