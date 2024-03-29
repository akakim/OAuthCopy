package com.dreamsecurity.oauth.custom;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class CustomTabsHelper {
    private static final String TAG = "CustomTabsHelper";
    static final String STABLE_PACKAGE = "com.android.chrome";
    static final String BETA_PACKAGE = "com.chrome.beta";
    static final String DEV_PACKAGE = "com.chrome.dev";
    static final String LOCAL_PACKAGE = "com.google.android.apps.chrome";
    private static final String EXTRA_CUSTOM_TABS_KEEP_ALIVE =
            "android.support.customtabs.extra.KEEP_ALIVE";
    private static final String ACTION_CUSTOM_TABS_CONNECTION =
            "android.support.customtabs.action.CustomTabsService";

    private static String packageNameToUse;
    private CustomTabsHelper(){}

    public static void addKeepAliveExtra(Context context, Intent intent){

    }

    /**
     * Goes through all apps that handler VIEW intents and have a warmup service.
     * Pick the one chosen by the user if there is one, otherwise makes a best effort to return a
     * valid package name
     *
     * @param context
     * @return
     */
    public static String getPackageNameTouse(Context context){
        if( packageNameToUse != null )
                return packageNameToUse;

        PackageManager pm       = context.getPackageManager();
        Intent activityIntent   = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.naver.com/"));
        ResolveInfo defaultViewHandlerInfo = pm.resolveActivity(activityIntent, 0);
        String defaultViewHandlerPackageName = null;

        if ( defaultViewHandlerPackageName != null ) {
            defaultViewHandlerPackageName  = defaultViewHandlerInfo.activityInfo.packageName;
        }


        // 모든 앱이 VIEW Intent를 다룰 수 있는지를 확인한다.
        List<ResolveInfo> resolvedActivityList = pm.queryIntentActivities( activityIntent, 0);
        List<String> packagesSupportingCustomTabs = new ArrayList<>();

        for (ResolveInfo info : resolvedActivityList){
            Intent serviceIntent = new Intent();
            serviceIntent.setAction(ACTION_CUSTOM_TABS_CONNECTION);
            serviceIntent.setPackage( info.activityInfo.packageName);
            if (pm.resolveService(serviceIntent, 0) != null) {

                packagesSupportingCustomTabs.add(info.activityInfo.packageName);

                Log.d( TAG, "package Custom Tab tax" + info.activityInfo.packageName);

            }
        }

        // Now packagesSupportingCustomTbs contains all apps that cna be handle both VIEW
        // intent and service call

        if( packagesSupportingCustomTabs.isEmpty()){
            packageNameToUse = null;
        } else if( packagesSupportingCustomTabs.size() == 1 ){
            packageNameToUse = packagesSupportingCustomTabs.get(0);

        } else if (!TextUtils.isEmpty(defaultViewHandlerPackageName)
                && !hasSpecializedHandlerIntents(context, activityIntent)
                && packagesSupportingCustomTabs.contains(defaultViewHandlerPackageName)) {
            packageNameToUse = defaultViewHandlerPackageName;
        } else if ( packagesSupportingCustomTabs.contains( STABLE_PACKAGE )){
            packageNameToUse = STABLE_PACKAGE;
        } else if ( packagesSupportingCustomTabs.contains( BETA_PACKAGE )){
            packageNameToUse = BETA_PACKAGE;
        } else if ( packagesSupportingCustomTabs.contains( DEV_PACKAGE )){
            packageNameToUse = DEV_PACKAGE;
        } else if ( packagesSupportingCustomTabs.contains( LOCAL_PACKAGE)){
            packageNameToUse = LOCAL_PACKAGE;
        }

        return packageNameToUse;
    }

    /**
     * 주어진 Intent 를 다루는데 특화된것이 있는지를 확인한다.
     * @param intent The intent to check with.
     * @return Whether there is a specialized handler for the given intent.
     */
    private static boolean hasSpecializedHandlerIntents( Context context, Intent intent){

        try{
            PackageManager pm = context.getPackageManager();
            List<ResolveInfo> handlers = pm.queryIntentActivities(
                    intent,
                    PackageManager.GET_RESOLVED_FILTER
            );

            if( handlers == null || handlers.size() == 0){
                return false;
            }

            for( ResolveInfo resolveInfo : handlers ) {
                IntentFilter filter = resolveInfo.filter;
                if( filter == null ) continue;
                if( filter.countDataAuthorities() == 0 || filter.countDataPaths() == 0 ) continue;;
                if( resolveInfo.activityInfo == null ) continue;

                return true;
            }

        } catch (RuntimeException e ){
            Log.e(TAG,"Runtime exception while getting specialized handlers ");
        }

        return false;
    }
    /**
     * @return All possible chrome package names that provide custom tabs feature.
     */
    public static String[] getPackages() {
        return new String[]{"", STABLE_PACKAGE, BETA_PACKAGE, DEV_PACKAGE, LOCAL_PACKAGE};
    }
}
