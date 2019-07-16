package com.dreamsecurity.ssooauth.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.text.TextUtils;
import com.dreamsecurity.ssooauth.common.logger.Logger;
import com.dreamsecurity.ssooauth.magiclogin.OAuthLogin;
import com.dreamsecurity.ssooauth.magiclogin.OAuthLoginDefine;

import java.net.URLEncoder;
import java.util.List;
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


    /**
     * 네이버앱을 제외한 앱들 중 oauth2 master 앱인 것 중 appstore 가 있으면 appstore 를 리턴하고 없으면 나머지 중 하나를 리턴한다.
     * @param context activity context
     * @return oauth2 master 앱 중 하나의 package name
     */
    public static String getPrimaryNaverOAuth2ndAppPackageName(Context context) {
        try {
            // TODO task permission 설정 안한 경우 동작 어떻게 할지 고려하기
            String[] appList = {"com.nhn.android.appstore"};
            List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(
                    new Intent(OAuthLoginDefine.ACTION_OAUTH_2NDAPP),
                    PackageManager.GET_META_DATA);

            for (String appName : appList) {
                for (ResolveInfo resolveInfo : list) {
                    if (!Logger.isRealVersion()) {
                        Logger.d(TAG, "package name:" + resolveInfo.activityInfo.packageName + ", " + resolveInfo.activityInfo.name);
                    }
                    if (resolveInfo.activityInfo.packageName.equals(appName)) {
                        return resolveInfo.activityInfo.packageName;
                    }
                }
            }

            if (!Logger.isRealVersion()) {
                Logger.d(TAG, "no app assinged in order-list. package name:" + list.get(0).activityInfo.packageName + ", " + list.get(0).activityInfo.name);
            }

            // TODO phishing 의 우려가 있으므로 제거 추후 서버에서 app list 받아올 수 있도록 개발하기
            //return list.get(0).activityInfo.packageName;

        } catch (Exception e) {

        }

        return null;
    }

    /**
     * 앱 이름 알려줌
     * @param context activity context
     * @return 앱 label 명
     */
    public static String getApplicationName(Context context) {
        PackageManager lPackageManager = context.getPackageManager();
        ApplicationInfo lApplicationInfo;
        String appName = "NAVER";

        try {
            lApplicationInfo = lPackageManager.getApplicationInfo(context.getApplicationInfo().packageName, 0);
            appName = (String)lPackageManager.getApplicationLabel(lApplicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return appName;
    }

}
