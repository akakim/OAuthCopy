package com.dreamsecurity.ssooauth.common.util;

import android.content.Context;
import android.os.Build;

import java.util.Locale;

public class DeviceAppInfo {
    private static final String TAG = "DeviceAppInfo";

    // Singleton
    private static final DeviceAppInfo instance = new DeviceAppInfo();

    public static DeviceAppInfo getInstance() {
        return instance;
    }

    /**
     * 현재 단말기의 언어 설정을 확인하여 한글인지를 확인함.
     * @param context
     * @return
     */
    public boolean isKorean(Context context){
        Locale systemLocale = context.getResources().getConfiguration().locale;

        String strLanguage = systemLocale.getLanguage();
        if (strLanguage.startsWith("ko"))
            return true;
        return false;
    }

    /**
     * 미완 .
     * @param context
     * @return
     */
    public static String getUserAgent( Context context ){
        String androidVersionInfo = "Android/" + Build.VERSION.RELEASE;
        String modelInfo = "Model/" + Build.MODEL;
        String useragent = androidVersionInfo.replaceAll("\\s", "") + " " + modelInfo.replaceAll("\\s", "");

        return useragent;
    }
}
