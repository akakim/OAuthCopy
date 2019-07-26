package com.dreamsecurity.oauth.custom.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.text.TextUtils;
import com.dreamsecurity.oauth.BuildConfig;
import com.dreamsecurity.oauth.custom.common.Logger;

import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

public class AppUtil {


    private static final String TAG = "AppUtil";

    /**
     * 현재 단말기의 언어 설정을 확인하여 한글인지 여부를 리턴함
     * @param context context
     * @return korean 여부
     */
    public boolean isKorean(Context context) {
        Locale systemLocale = context.getResources().getConfiguration().locale;
        String strLanguage = systemLocale.getLanguage();
        if (strLanguage.startsWith("ko"))
            return true;
        return false;
    }

    public static String getUserAgent(Context context) {
        String androidVersionInfo = "Android/" + (android.os.Build.VERSION.RELEASE);
        String modelInfo = "Model/" + (android.os.Build.MODEL);

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
            String loginModuleInfo = "OAuthLoginMod/" + BuildConfig.VERSION_NAME;

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


    /**
     * 현재 설치되어 있는 패키지가 있는 확인
     * @param context context
     * @param packageName 패키지명 (예: "com.nhn.android.search")
     * @return if 네이버앱 있음, true. else, false
     */
    public static boolean isAppExist(Context context, String packageName) {
        try {
            if (TextUtils.isEmpty(packageName)) {
                return false;
            }

            Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);

            // 네이버앱 미설치
            if (intent == null) {
                Logger.i(TAG, packageName + " is not installed.");
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
    /**
     * 현재 디바이스에 packageName에 IntentFilter가 설정 되어 있는 앱이 있는 지 검색
     *
     * @param context 앱 컨텍스트
     * @param packageName 검색할 패키지 명
     * @param intentName 검색할 필터 명
     * @return 존재 여부
     */
    public static boolean isIntentFilterExist(Context context, String packageName, String intentName) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(new Intent(intentName), PackageManager.GET_META_DATA);
        for (ResolveInfo resolveInfo : list) {
            if (!Logger.isRealVersion()) {
                Logger.d(TAG, "intent filter name:" + intentName);
                Logger.d(TAG, "package name:" + resolveInfo.activityInfo.packageName + ", " + resolveInfo.activityInfo.name);
            }
            if (packageName.equalsIgnoreCase(resolveInfo.activityInfo.packageName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isIntentFilterExist(Context context, String intentName) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(new Intent(intentName), PackageManager.GET_META_DATA);
        for (ResolveInfo resolveInfo : list) {
            if (!Logger.isRealVersion()) {
                Logger.d(TAG, "intent filter name:" + intentName);
                Logger.d(TAG, "package name:" + resolveInfo.activityInfo.packageName + ", " + resolveInfo.activityInfo.name);
            }
            return true;
        }
        return false;
    }

}
