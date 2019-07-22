package com.dreamsecurity.oauth.custom;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.FragmentTransaction;
import com.dreamsecurity.oauth.custom.common.HttpUtil;
import com.dreamsecurity.ssooauth.common.logger.Logger;

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

    public CustomTabOAuthPresenter(Context context,OAuthCallback oAuthCallback) {
        this.context = context;
        this.oAuthCallback = oAuthCallback;
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


            return HttpUtil.generateRequestCustmTabAuthURL(
                    oauthParam.getStringExtra( INTENT_KEY_CLIENT_ID ),
                    "4G",
                    oauthParam.getStringExtra( INTENT_KEY_REDIRECT_URI),
                    "1_0_1"
            );
        }
    }



    @Override
    public void requestLoginPage(String url) {
        List<PackageInfo> customTabsPackages = getCustomTabsPackages( this.context );

        if( customTabsPackages.size() == 1 ){
            launchUrl( customTabsPackages.get(0).packageName,url);
            return;
        }

        //FragmentTransaction ft = getSupportFragmentManager().beginTransaction();


    }

    @Override
    public void requestLoginPage(Intent getIntent) {

        requestLoginPage( makeLoginParameter( getIntent ));
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

        customTabsIntent.launchUrl( context, Uri.parse(url) );

    }
}
