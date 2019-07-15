package com.dreamsecurity.ssooauth.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import com.dreamsecurity.ssooauth.common.logger.Logger;
import com.dreamsecurity.ssooauth.magiclogin.OAuthLogin;

import java.net.URLEncoder;
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
        String packageName = "";

        try {
            PackageManager pm = context.getPackageManager();
            packageName = context.getPackageName();
            PackageInfo info = pm.getPackageInfo(packageName, PackageManager.GET_GIDS | PackageManager.GET_SIGNATURES | PackageManager.GET_META_DATA);

            String appId = "";
            if (info.applicationInfo.loadDescription(pm) != null) {
                appId = ",appId:" + info.applicationInfo.loadDescription(pm);
            }
            String appInfo = String.format("%s/%s(%d,uid:%d%s)", packageName, info.versionName, info.versionCode, info.applicationInfo.uid, appId);
            String loginModuleInfo = "OAuthLoginMod/" + OAuthLogin.getVersion();

            useragent += " " + appInfo.replaceAll("\\s", "") + " " + loginModuleInfo.replaceAll("\\s", "");
        } catch (PackageManager.NameNotFoundException e) {
            Logger.e(TAG, "not installed app : " + packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return useragent;
    }

    public String getLocaleString(Context context) {
        String strLanguage = "ko_KR";
        try {
            Locale systemLocale;
            if(Build.VERSION_CODES.N > Build.VERSION.SDK_INT) {
                systemLocale = context.getResources().getConfiguration().locale;
            } else {
                systemLocale = context.getResources().getConfiguration().getLocales().get(0);
            }
            strLanguage = systemLocale.toString();

            if (TextUtils.isEmpty(strLanguage)) {
                return "ko_KR";
            }

            // 일부 toString 값에 #로 추가 정보가 붙는 경우가 있는데 이럴 경우 {language code}_{country code}로 처리한다.
            if (!strLanguage.equalsIgnoreCase(URLEncoder.encode(strLanguage, "utf-8"))) {
                strLanguage = systemLocale.getLanguage() + "_" + systemLocale.getCountry();
            }
        } catch (Exception e) {
            // do nothing
        }

        return strLanguage;
    }
}
