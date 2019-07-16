package com.dreamsecurity.ssooauth.magiclogin.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import com.dreamsecurity.ssooauth.R;
import com.dreamsecurity.ssooauth.common.logger.Logger;
import com.dreamsecurity.ssooauth.magiclogin.OAuthLogin;
import com.dreamsecurity.ssooauth.magiclogin.OAuthLoginConnection;
import com.dreamsecurity.ssooauth.magiclogin.OAuthLoginDefine;
import com.dreamsecurity.ssooauth.magiclogin.data.*;
import com.dreamsecurity.ssooauth.util.DeviceAppInfo;

public class OAuthLoginActivity extends Activity {

    private static final String TAG = "OAuthLoginActivity";
    private static int REQUESTCODE_LOGIN = 100;
    private static int CUSTOMTAB_LOGIN = -1;

    // dialog
    private OAuthDialog oAuthDialog = new OAuthDialog();

    private Context mContext;
    private OAuthLoginData oAuthLoginData;
    private String clientName;

    private boolean isForceDestroyed = true;

    private boolean isLoginActivityStarted = false;
    /*private CustomTabsListener mCustomTabListener = new CustomTabsListener() {
        @Override
        public void onReceive(Intent intent) {
            if (intent == null) {
                intent = new Intent();
                intent.putExtra(OAuthIntent.EXTRA_OAUTH_ERROR_CODE
                        , OAuthErrorCode.CLIENT_USER_CANCEL.getCode());
                intent.putExtra(OAuthIntent.EXTRA_OAUTH_ERROR_DESCRIPTION
                        , OAuthErrorCode.CLIENT_USER_CANCEL.getDesc());
            }
            onActivityResult(CUSTOMTAB_LOGIN, RESULT_OK, intent);
        }
    };*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Logger.isRealVersion()) {
            Logger.d(TAG, "onCreate()");
        }

        if (!initData(savedInstanceState)) {
            return;
        }

        if (null != savedInstanceState) {
            isLoginActivityStarted = savedInstanceState.getBoolean("IsLoginActivityStarted");
        }


        if (!isLoginActivityStarted) {
            isLoginActivityStarted = true;

            if (!Logger.isRealVersion()) {
                Logger.d(TAG, "onCreate() first");
            }
            runOnlyOnce();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (!Logger.isRealVersion()) {
            Logger.d(TAG, "onRestoreInstanceState()");
        }

        if (null != savedInstanceState) {
            isLoginActivityStarted = savedInstanceState.getBoolean("IsLoginActivityStarted");
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (!Logger.isRealVersion()) {
            Logger.d(TAG, "Login onSaveInstanceState()");
        }

        outState.putBoolean("IsLoginActivityStarted", isLoginActivityStarted);
        outState.putString("OAuthLoginData_state", oAuthLoginData.getInitState());
    }


    /**
     * 각 상황별로 네이버 로그인을 진행할 액티비티를 실행
     * @param mOAuthLoginData 네아로 메타 정보
     */
    private void startLoginActivity(OAuthLoginData mOAuthLoginData) {
        if (!Logger.isRealVersion()) {
            Logger.d(TAG, "startLoginActivity()");
        }

        // https://oss.navercorp.com/id-mobilesdk/naverid-3rdparty-sdk-android/issues/39
        // 에 의해 비공개로 로직을 남겨둔다
        if (OAuthLoginDefine.LOGIN_BY_NAVERAPP_ONLY) {
            // Only Naver Login
            tryOAuthByNaverapp(oAuthLoginData);
            return;
        }
       /* else if (OAuthLoginDefine.LOGIN_BY_CUSTOM_TAB_ONLY) {

            // Only Custom Tab Login
            tryOAuthByCustomTab(mOAuthLoginData);

            return;
        }*/
        /*else if (OAuthLoginDefine.LOGIN_BY_WEBVIEW_ONLY) {
            // Only WebView Login
            startLoginWebviewActivity(mOAuthLoginData);
            return;

        }*/
        // Test 용이 아닌 기본 로직은 아래의 else를 타게 된다
        if (!OAuthLoginDefine.LOGIN_BY_WEBVIEW_ONLY) {
            if (tryOAuthByNaverapp(mOAuthLoginData)) return;
           // if (tryOAuthByCustomTab(mOAuthLoginData)) return;
        }
        // inapp browser 를 통해 기존 로그인 방식으로 로그인한다.
//        startLoginWebviewActivity(mOAuthLoginData);
    }

    /**
     * 커스텀 탭으로 로그인 시도
     * @param loginData 네아로 메타 정보
     * @return 실행 여부
     */
    /*private boolean tryOAuthByCustomTab(OAuthLoginData loginData) {

        if (isEnableDoNotKeepActivity())  {
            return false;
        }

        if (!CustomTabsManager.isCustomTabAvailable(this)) return false;
        CustomTabsManager manager = new CustomTabsManager(this);
        manager.setCustomTabListener(mCustomTabListener);

        Intent intent = newParamIntent(OAuthCustomTabActivity.class, loginData.getClientId(), loginData.getInitState(), loginData.getCallbackUrl());
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, CUSTOMTAB_LOGIN);
        return true;
    }*/

    /**
     * 네이버 앱으로 로그인 시도
     * @param loginData 네아로 메타 정보
     * @return 실행 여부
     */
    /*private boolean tryOAuthByNaverapp(OAuthLoginData loginData) {
        try {

            Intent intent = newParamIntent(loginData.getClientId(), loginData.getInitState(), loginData.getCallbackUrl());
            intent.putExtra(BrowserIntentKey.APP_NAME, clientName);

            if (DeviceAppInfo.isIntentFilterExist(mContext, OAuthLoginDefine.NAVER_PACKAGE_NAME, OAuthLoginDefine.ACTION_OAUTH_LOGIN)) {

                if (!Logger.isRealVersion()) {
                    Logger.d(TAG, "startLoginActivity() with naapp");
                }

                // 네이버앱에서 처리 가능한 intent
//                intent.setPackage(OAuthLoginDefine.NAVER_PACKAGE_NAME);
                intent.setAction(OAuthLoginDefine.ACTION_OAUTH_LOGIN);
                startActivityForResult(intent, REQUESTCODE_LOGIN);
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isForceDestroyed) {
            OAuthLoginPreferenceManager prefMng = new OAuthLoginPreferenceManager(mContext);
            prefMng.setLastErrorCode(OAuthErrorCode.ACTIVITY_IS_SINGLE_TASK);
            prefMng.setLastErrorDesc("OAuthLoginActivity is destroyed.");

            OAuthLogin.oAuthLoginHandler.run(false);
        }
    }



    private void runOnlyOnce() {
        if (oAuthLoginData == null) {
            finishWithErrorResult(OAuthErrorCode.CLIENT_ERROR_NO_CLIENTID);
            return;
        }

        startLoginActivity(oAuthLoginData);


    }

    private void finishWithErrorResult(OAuthErrorCode errCode) {
        if (!Logger.isRealVersion()) {
            Logger.d("GILSUB", "Login finishWithErrorResult()");
        }

        Intent intent = new Intent();
        OAuthLoginPreferenceManager prefMng = new OAuthLoginPreferenceManager(mContext);

        prefMng.setLastErrorCode(errCode);
        prefMng.setLastErrorDesc(errCode.getDesc());

        intent.putExtra(OAuthIntent.EXTRA_OAUTH_ERROR_CODE, errCode.getCode());
        intent.putExtra(OAuthIntent.EXTRA_OAUTH_ERROR_DESCRIPTION, errCode.getDesc());

        setResult(RESULT_CANCELED, intent);
        finish();

        propagationResult(false);
    }


    private boolean initData(Bundle savedInstanceState) {

        mContext = OAuthLoginActivity.this;

        OAuthLoginPreferenceManager pref = new OAuthLoginPreferenceManager(mContext);
        String clientId = pref.getClientId();
        String clientSecret = pref.getClientSecret();
        String callbackUrl = pref.getCallbackUrl();
        String state = (null == savedInstanceState) ? null : savedInstanceState.getString("OAuthLoginData_state");

        clientName = pref.getClientName();

        if (TextUtils.isEmpty(clientId)) {
            finishWithErrorResult(OAuthErrorCode.CLIENT_ERROR_NO_CLIENTID);
            return false;
        }
        if (TextUtils.isEmpty(clientSecret)) {
            finishWithErrorResult(OAuthErrorCode.CLIENT_ERROR_NO_CLIENTSECRET);
            return false;
        }
        if (TextUtils.isEmpty(clientName)) {
            finishWithErrorResult(OAuthErrorCode.CLIENT_ERROR_NO_CLIENTNAME);
            return false;
        }
        if (TextUtils.isEmpty(callbackUrl)) {
            finishWithErrorResult(OAuthErrorCode.CLIENT_ERROR_NO_CALLBACKURL);
            return false;
        }

        oAuthLoginData = new OAuthLoginData(clientId, clientSecret, callbackUrl, state);

        return true;
    }

    private void propagationResult(boolean b) {
        if (OAuthLogin.oAuthLoginHandler != null) {
            Message msg = new Message();
            msg.what = b ? 1 : 0;
            OAuthLogin.oAuthLoginHandler.sendMessage(msg);
        }


    }

    /**
     * 네아로 로그인을 위한 파라메터 인텐트를 생성
     * @param nextActivity 이동할 액티비티 클래스
     * @param clientId 클라이언트 ID
     * @param initState 요청 state
     * @param callbackUrl 콜백 url 패키지명
     * @return 생성된 인텐트
     */
    @NonNull
    private Intent newParamIntent(Class<? extends Activity> nextActivity, String clientId, String initState, String callbackUrl) {
        Intent intent;
        if(nextActivity == null) {
            intent = new Intent();
        } else {
            intent = new Intent(this, nextActivity);
        }
        intent.putExtra(BrowserIntentKey.CLIENT_ID, clientId);
        intent.putExtra(BrowserIntentKey.CALLBACK_URL, callbackUrl);
        intent.putExtra(BrowserIntentKey.STATE, initState);
        intent.putExtra(BrowserIntentKey.SDK_VERSION, OAuthLoginDefine.VERSION);

        return intent;
    }

    /**
     * 네아로 로그인을 위한 파라메터 인텐트를 생성
     * @param clientId 클라이언트 ID
     * @param initState 요청 state값
     * @param callbackUrl 콜백 url (패키지명)
     * @return 생성된 인텐트
     */
    @NonNull
    private Intent newParamIntent(String clientId, String initState, String callbackUrl) {
        return newParamIntent(null, clientId, initState, callbackUrl);
    }


    /**
     * 네이버 앱으로 로그인 시도
     * @param loginData 네아로 메타 정보
     * @return 실행 여부
     */
    private boolean tryOAuthByNaverapp(OAuthLoginData loginData) {
 /*       try {

            Intent intent = newParamIntent(loginData.getClientId(), loginData.getInitState(), loginData.getCallbackUrl());
            //intent.putExtra(OAuthLoginBrowserIntentParam.INTENT_PARAM_KEY_APP_NAME, clientName);

            if (DeviceAppInfo.isIntentFilterExist(mContext, OAuthLoginDefine.NAVER_PACKAGE_NAME, OAuthLoginDefine.ACTION_OAUTH_LOGIN)) {

                if (!Logger.isRealVersion()) {
                    Logger.d(TAG, "startLoginActivity() with naapp");
                }

                intent.setAction(OAuthLoginDefine.ACTION_OAUTH_LOGIN);
                startActivityForResult(intent, REQUESTCODE_LOGIN);
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }*/
        return false;
    }


    /**
     * 커스텀 탭으로 로그인 시도
     * @param loginData 네아로 메타 정보
     * @return 실행 여부
     */
   /* private boolean tryOAuthByCustomTab(OAuthLoginData loginData) {

        if (isEnableDoNotKeepActivity())  {
            return false;
        }

        if (!CustomTabsManager.isCustomTabAvailable(this)) return false;
        CustomTabsManager manager = new CustomTabsManager(this);
        manager.setCustomTabListener(mCustomTabListener);

        Intent intent = newParamIntent(OAuthCustomTabActivity.class, loginData.getClientId(), loginData.getInitState(), loginData.getCallbackUrl());
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, CUSTOMTAB_LOGIN);
        return true;
    }*/

    private class GetAccessTokenTask extends AsyncTask<Void, Void, OAuthResponse> {
        @Override
        protected void onPreExecute() {
            try {
                oAuthDialog.showProgressDlg(mContext, mContext.getString(R.string.magic_sso_string_getting_token), null);
            } catch (Exception e) {
                // do nothing
            }
        }

        @Override
        protected OAuthResponse doInBackground(Void... params) {
            try {
                return OAuthLoginConnection.requestAccessToken(mContext,
                        oAuthLoginData.getClientId(),
                        oAuthLoginData.getClientSecret(),
                        oAuthLoginData.getState(), oAuthLoginData.getCode());
            } catch (Exception e) {
                return new OAuthResponse(OAuthErrorCode.CLIENT_ERROR_CONNECTION_ERROR);
            }
        }

        protected void onPostExecute(OAuthResponse res) {
            try {
                oAuthDialog.hideProgressDlg();
            } catch (Exception e) {
                // do nothing
            }

            try {
                Intent intent = new Intent();
                OAuthLoginPreferenceManager prefMng = new OAuthLoginPreferenceManager(mContext);

                if (res.isSuccess()) {
                    prefMng.setAccessToken(res.getAccessToken());
                    prefMng.setRefreshToken(res.getRefreshToken());
                    prefMng.setExpiresAt(System.currentTimeMillis() / 1000 + res.getExpiresIn());
                    prefMng.setTokenType(res.getTokenType());
                    prefMng.setLastErrorCode(OAuthErrorCode.NONE);
                    prefMng.setLastErrorDesc(OAuthErrorCode.NONE.getDesc());

                    intent.putExtra(OAuthIntent.EXTRA_OAUTH_ACCESS_TOKEN, res.getAccessToken());
                    intent.putExtra(OAuthIntent.EXTRA_OAUTH_REFRESH_TOKEN, res.getRefreshToken());
                    intent.putExtra(OAuthIntent.EXTRA_OAUTH_EXPIRES_IN, res.getExpiresIn());
                    intent.putExtra(OAuthIntent.EXTRA_OAUTH_TOKEN_TYPE, res.getTokenType());
                    setResult(RESULT_OK, intent);
                } else {
                    if (res.getErrorCode() == OAuthErrorCode.NONE) {
                        finishWithErrorResult(OAuthErrorCode.CLIENT_USER_CANCEL);
                        return;
                    } else {
                        prefMng.setLastErrorCode(res.getErrorCode());
                        prefMng.setLastErrorDesc(res.getErrorDesc());
                        intent.putExtra(OAuthIntent.EXTRA_OAUTH_ERROR_CODE, res.getErrorCode().getCode());
                        intent.putExtra(OAuthIntent.EXTRA_OAUTH_ERROR_DESCRIPTION, res.getErrorDesc());
                        setResult(RESULT_CANCELED, intent);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            finish();

            try {
                propagationResult(res.isSuccess());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}
