package com.dreamsecurity.oauth.custom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.dreamsecurity.oauth.custom.common.HttpUtil;
import com.dreamsecurity.oauth.custom.common.Logger;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import static androidx.browser.customtabs.CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION;

public class CustomTabOAuthPresenter implements OAuthPresenter {

    public static final String ACTION_DREAM_CUSTOM_TAB = "ACTION_DREAM_3RDPARTY_CUSTOM_TAB";

    private Context context;
    private OAuthCallback oAuthCallback;

    private List<PackageInfo> packageInfos;
    public CustomTabOAuthPresenter(Context context,OAuthCallback oAuthCallback) {
        this.context = context;
        this.oAuthCallback = oAuthCallback;
        this.packageInfos = getCustomTabsPackages( context);
    }

    public static  List<PackageInfo> getCustomTabsPackages(Context context){

        PackageManager pm = context.getPackageManager();

        // 기본 VIEW intent handler를 가져온다.
        Intent activityIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com"));

        // Get all apps that can handle VIEW intents.
        List<ResolveInfo> resolvedActivityList = pm.queryIntentActivities(activityIntent, 0);
        List<PackageInfo> packagesSupportingCustomTabs = new ArrayList<>();

        for (ResolveInfo info : resolvedActivityList) {
            Intent serviceIntent = new Intent();
            serviceIntent.setAction(ACTION_CUSTOM_TABS_CONNECTION);
            serviceIntent.setPackage(info.activityInfo.packageName);
            Log.d("custom tab util", info.activityInfo.packageName);
            // Check if this package also resolves the Custom Tabs service.
            if (pm.resolveService(serviceIntent, 0) != null) {
                try {
                    ApplicationInfo ai = pm.getApplicationInfo(info.activityInfo.packageName, 0);
                    if (ai.enabled) {
                        packagesSupportingCustomTabs.add(pm.getPackageInfo(info.activityInfo.packageName, 0));
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return packagesSupportingCustomTabs;
    }

    @Override
    public String makeLoginParameter(Intent oauthParam) {

        if( oauthParam == null ){
            Logger.e(context.getClass().getSimpleName()," Login 인자값 설정 실패했습니다. ");
            return "";
        } else {

            oauthParam.getStringExtra( INTENT_KEY_REDIRECT_URI);
            oauthParam.getStringExtra( INTENT_KEY_STATE );


            return HttpUtil.generateRequestCustomTabAuthURL(
                    oauthParam.getStringExtra( INTENT_KEY_CLIENT_ID ),
                    "4G",
                    oauthParam.getStringExtra( INTENT_KEY_REDIRECT_URI),
                    "1_0_1"
            );
        }
    }



    @Override
    public void requestLoginPage(String url) {
        //List<PackageInfo> customTabsPackages = getCustomTabsPackages( this.context );

        Logger.d( getClass().getSimpleName(), "request Login Page : " + url );
        if( packageInfos.size() == 1 ){
            launchUrl( packageInfos.get(0).packageName,url);

        }

        if( oAuthCallback != null ) {
            oAuthCallback.onProgress(Uri.parse( url ));
        }else {
            Log.e(getClass().getSimpleName(), " requestLoginPage 시점에 oauthCallback이 널입니다. ");
        }


        //FragmentTransaction ft = getSupportFragmentManager().beginTransaction();


    }

    @Override
    public void requestLoginPage(Intent getIntent) {

        requestLoginPage( makeLoginParameter( getIntent ) );
    }

    @Override
    public void returnResult(@NonNull Intent data) {

    }

    @Override
    public void responseCodeAndRequest() {

    }

    @Override
    public void getAccessToken() {

    }

    @Override
    public String generateState() {
        SecureRandom random = new SecureRandom();

        String state = new BigInteger(130,random).toString(32);

        try{
            return URLEncoder.encode(state,"UTF-8");
        } catch (UnsupportedEncodingException e){
            return state;
        }
    }

    /**
     * 커스텀 탭을 이용해 url을 호출한다.
     * 커스텀 탭 사용가능 앱이 여러개 일경우 다이얼 로그를
     * @param packageName 커스텀 탭 패키지 명
     * @param url 실행할 url
     */
    public void launchUrl(String packageName, String url) {
        final CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                .setShowTitle(true)
                .build();

        customTabsIntent.intent.setPackage(packageName);

        Logger.d( getClass().getSimpleName(), " launch URL "  + Uri.parse(url).toString() );
        customTabsIntent.launchUrl( context, Uri.parse(url) );

    }

    /**
     *  커스텀 탭의 결과를 리턴한다.
     *  코드를 호출한다. ( 1회성임 )
     *
     * @param intent
     */
    public void sendResult( Intent intent ){
        intent.setAction(ACTION_DREAM_CUSTOM_TAB);
        intent.setClass(this.context, CustomTabOAuthPresenter.class);
        LocalBroadcastManager instance = LocalBroadcastManager.getInstance( this.context );
        instance.sendBroadcast(intent);
    }

    /**
     * 커스텀 탭 리스너를 지정합니다.<br />
     * 리스너는 <b>일회용</b>입니다. 사용에 주의하세요.
     *
     * @param listener 실행될 리스너 결과 값은 {@link Intent}로 주고 받습니다
     */
    public void setCustomTabListener(final CustomTabsListener listener) {
        final LocalBroadcastManager instance = LocalBroadcastManager.getInstance(context);

        instance.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                listener.onReceive(intent);
                instance.unregisterReceiver(this);
            }
        }, new IntentFilter(ACTION_DREAM_CUSTOM_TAB));
    }

    /**
     * 커스텀 탭의 결과 값을 리턴
     *
     * @param intent 리턴 값
     */
    public void sendCustomTabResult(Intent intent) {
        intent.setAction(ACTION_DREAM_CUSTOM_TAB);
        intent.setClass(context, CustomTabOAuthPresenter.class);
        LocalBroadcastManager instance = LocalBroadcastManager.getInstance(context);
        instance.sendBroadcast(intent);
    }

    public List<PackageInfo> getPackageInfos() {
        return packageInfos;
    }
}
