package com.dreamsecurity.oauth.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import android.os.Bundle;
import androidx.browser.customtabs.CustomTabsClient;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import com.dreamsecurity.oauth.R;
import com.dreamsecurity.oauth.custom.*;
import com.dreamsecurity.oauth.custom.common.CustomTabDialogFragment;
import com.dreamsecurity.oauth.custom.util.HttpUtil;
import com.dreamsecurity.oauth.custom.common.OAuthErrorCode;
import com.dreamsecurity.oauth.custom.common.Logger;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class OAuthCustomTabActivity extends FragmentActivity implements ServiceConnectionCallback, OAuthCallback {

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

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * lounchMode에서 singleTask로 설정한 후
     * intent url을 통해 호출 할 경우 onCreate가 아닌 이 메서드가 호출 된다.
     *
     * @param intent intent url을 통해 넘어온 파라메터를 파싱하여 생성된 {@link Intent}
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Logger.d(TAG,"open by intent url ");

        String data = intent.getDataString();

        Uri resultUri = Uri.parse(data);

        resultUri.getQueryParameter("code");

        String code = resultUri.getQueryParameter("code");
        String state = "";

        String error = "";
        /*String code = intent.getStringExtra("code");
        String state = intent.getStringExtra("state");
        String error = intent.getStringExtra("error");*/

        Logger.d(TAG,"open by intent url " +
                 "code : " + code +
                 "state : " + state +
                "error : " + error );
        String errorDescription = HttpUtil.getDecodedString(intent.getStringExtra("error_description"));

        if (customTabOauthPresenter == null) {
            customTabOauthPresenter = new CustomTabOAuthPresenter(this ,this);
        }
        Intent resultIntent = new Intent();

        if( code != null || error != null ) {
            resultIntent.putExtra(OAuthPresenter.EXTRA_KEY_CODE, code);
            resultIntent.putExtra(OAuthPresenter.EXTRA_KEY_STATE, state);
            resultIntent.putExtra(OAuthPresenter.EXTRA_KEY_ERROR, error);
            resultIntent.putExtra(OAuthPresenter.EXTRA_KEY_ERROR_DESCRIPTION, errorDescription);
        } else {
            resultIntent.putExtra(OAuthPresenter.EXTRA_KEY_STATE, state);
            resultIntent.putExtra(OAuthPresenter.EXTRA_KEY_ERROR, code);
            resultIntent.putExtra(OAuthPresenter.EXTRA_KEY_ERROR_DESCRIPTION, errorDescription);
        }

        customTabOauthPresenter.sendCustomTabResult(resultIntent);


        setResult(RESULT_CANCELED);
        finish();
    }

    /**
     * 요청 후 Intent 파라메터로 넘어온 oauth결과를 저장한 Intent를 생성한다.
     *
     * @param code             결과 코드
     * @param state            요청시 전달한 csrf 코드
     * @param error            에러 코드
     * @param errorDescription 에러 메시지
     * @return 파라메터를 저장한 인텐트
     */
    private void responseResult(String code, String state, String error, String errorDescription) {

        Intent resultIntent = new Intent();
        resultIntent.putExtra(OAuthPresenter.EXTRA_KEY_CODE, code);
        resultIntent.putExtra(OAuthPresenter.EXTRA_KEY_STATE, state);
        resultIntent.putExtra(OAuthPresenter.EXTRA_KEY_ERROR, error);
        resultIntent.putExtra(OAuthPresenter.EXTRA_KEY_ERROR_DESCRIPTION, errorDescription);

        returnResult(resultIntent);
    }

    /**
     * 내부 앱 문제로 인해 에러가 발생할 때 리턴할 인텐트
     *
     * @param state state 값
     * @param code 에러 코드
     * @param desc 에러 메시지
     * @return 에러 내용이 저장된 인텐트
     */
    private void responseError(String state, String code, String desc) {

        Intent intent = new Intent();


        intent.putExtra(OAuthPresenter.EXTRA_KEY_STATE, state);
        intent.putExtra(OAuthPresenter.EXTRA_KEY_ERROR, code);

        intent.putExtra(OAuthPresenter.EXTRA_KEY_ERROR_DESCRIPTION, desc);

        returnResult(intent);
    }


    private void returnResult(@NonNull Intent data) {

        //customTabOauthPresenter = new CustomTabOAuthPresenter(this ,this);
        if (customTabOauthPresenter == null) {
            customTabOauthPresenter = new CustomTabOAuthPresenter(this ,this);
        }

        customTabOauthPresenter.sendCustomTabResult(data);
        setResult(RESULT_CANCELED);
        finish();
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
    public void onProgress(final Uri uri) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        Fragment prev = getSupportFragmentManager().findFragmentByTag( CustomTabDialogFragment.DIALOG_TAG);

        if( prev != null ){
            ft.remove(prev);

        }

        ft.addToBackStack( null );

        CustomTabDialogFragment fragment =
                CustomTabDialogFragment.newInstance( customTabOauthPresenter.getPackageInfos() );
        fragment.setPackageSelectListener(new CustomTabDialogFragment.OnPackageSelectListener() {
            @Override
            public void onPackageSelect(PackageInfo packageInfo) {
                if(packageInfo == null) {
                    responseError(null, OAuthErrorCode.CLIENT_USER_CANCEL.getCode()
                            , OAuthErrorCode.CLIENT_USER_CANCEL.getDesc());
                    return ;
                }
                customTabOauthPresenter.launchUrl(packageInfo.packageName, uri.toString());
            }
        });

        fragment.show( ft, CustomTabDialogFragment.DIALOG_TAG);
    }

    @Override
    public void onResponseLogin() {

    }

    @Override
    public void onResponseAccessToken() {

    }
}
