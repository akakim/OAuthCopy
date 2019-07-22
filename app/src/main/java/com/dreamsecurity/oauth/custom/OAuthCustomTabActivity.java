package com.dreamsecurity.oauth.custom;

import android.content.Intent;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.os.Bundle;
import androidx.browser.customtabs.CustomTabsClient;
import androidx.fragment.app.FragmentActivity;
import com.dreamsecurity.oauth.R;
import com.dreamsecurity.ssooauth.common.logger.Logger;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class OAuthCustomTabActivity extends FragmentActivity implements ServiceConnectionCallback,OAuthCallback {

    public static final String TAG = OAuthCustomTabActivity.class.getSimpleName();
    public static final String SAVE_CUSTOM_TAB_OPEN = "isCustomTabOpen";
    private boolean isCustomTabOpen;

    private CustomTabOAuthPresenter customTabOauthPresenter;
    private CustomTabsHelper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth_custom_tab);

        Logger.setRealVersion( false );

        if( savedInstanceState == null ){
            Logger.d( TAG, " Custom Tab  지원화면 열림 ");
            customTabOauthPresenter = new CustomTabOAuthPresenter(this ,this);

            customTabOauthPresenter.requestLoginPage( getIntent() );

        }
//        CustomTabsHelper.getPackageNameTouse(this);

    }

    /**
     * OAuth 인증을 시작할 때 로그인 페이지를 요청한다.
     * 요청시
     */
    private void requestLoginPage(){

        Intent oauthParam = getIntent();
        if( oauthParam != null ){

        }
    }

    @Override
    public void onServiceConnected(CustomTabsClient client) {

    }

    @Override
    public void onServiceDisconnected() {

    }

    @Override
    public void onResponseLogin() {

    }

    @Override
    public void onResponseAccessToken() {

    }
}
