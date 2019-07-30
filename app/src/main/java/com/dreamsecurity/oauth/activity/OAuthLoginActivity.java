package com.dreamsecurity.oauth.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatEditText;
import com.dreamsecurity.oauth.BuildConfig;
import com.dreamsecurity.oauth.R;
import com.dreamsecurity.oauth.custom.CustomTabOAuthPresenter;
import com.dreamsecurity.oauth.custom.CustomTabsListener;
import com.dreamsecurity.oauth.custom.OAuthCallback;
import com.dreamsecurity.oauth.custom.OAuthPresenter;
import com.dreamsecurity.oauth.custom.common.*;
import com.dreamsecurity.oauth.data.OAuthorizedResponse;
import org.json.JSONException;
import org.json.JSONObject;


public class OAuthLoginActivity extends AppCompatActivity implements OAuthCallback {

    private static final String TAG = "OAuthLoginActivity";
    private static int REQUESTCODE_LOGIN = 100;
    private static int CUSTOMTAB_LOGIN = -1;

    // dialog TODO : Deprecate 된거 수정하기.
    //private OAuthLoginDialogMng mDialogMng = new OAuthLoginDialogMng();


//    private Context mContext;
    private String mClientName;

    private boolean isForceDestroyed = true;

    private boolean isLoginActivityStarted = false;

    private GetAccessTokenTask accessTokenTask;

    String clientId;
    String clientSecret;
    String callbackUrl;
    String state;

    String code;
    String stateFromServer;
    String errorCode;
    String errorMessage;

    AppCompatEditText edCompat;
    private CustomTabsListener customTabListener = new CustomTabsListener() {
        @Override
        public void onReceive(Intent intent) {

            Logger.d( OAuthLoginActivity.class.getSimpleName(),"CustomTab Receive... ");
            if (intent == null) {
                intent = new Intent();
                intent.putExtra(OAuthPresenter.EXTRA_ERROR_CODE
                        , OAuthErrorCode.CLIENT_USER_CANCEL.getCode());
                intent.putExtra(OAuthPresenter.EXTRA_ERROR_DESCRIPTION
                        , OAuthErrorCode.CLIENT_USER_CANCEL.getDesc());
            }
            onActivityResult(CUSTOMTAB_LOGIN, RESULT_OK, intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth_login);

        edCompat = findViewById( R.id.edCompat );
        if (!Logger.isRealVersion()) {
            Logger.d(TAG, "onCreate()");
        }

        boolean initResult = initData(savedInstanceState);

        Logger.d(TAG, " initResult  " + initResult);
        if ( initResult ) {
            return;
        }

        if (null != savedInstanceState) {
           // Logger.d(TAG, " isLoginActivityStarted  " + isLoginActivityStarted);
            isLoginActivityStarted = savedInstanceState.getBoolean("IsLoginActivityStarted");
        }



        Logger.d(TAG, " isLoginActivityStarted  " + isLoginActivityStarted);
        if (!isLoginActivityStarted) {
            isLoginActivityStarted = true;

            if (!Logger.isRealVersion()) {
                Logger.d(TAG, "onCreate() first");
            }
            runOnlyOnce(initResult);
        }

        Logger.d(TAG, " End  ");
    }
    private boolean initData( Bundle savedInstanceState){
      //  OAuthLoginPreferManager opm = new OAuthLoginPreferManager( this );


        clientId = getIntent().getStringExtra(OAuthPresenter.INTENT_KEY_CLIENT_ID);
        clientSecret = getIntent().getStringExtra(OAuthPresenter.INTENT_KEY_CLIENT_SECRET);
        callbackUrl =getIntent().getStringExtra(OAuthPresenter.INTENT_KEY_REDIRECT_URI);
        state = (null == savedInstanceState) ? null : savedInstanceState.getString("OAuthLoginData_state");

        Logger.d(TAG, "inti OAuth LoginActivity ");
        Logger.d( TAG,OAuthPresenter.INTENT_KEY_CLIENT_ID + clientId );
        Logger.d( TAG,OAuthPresenter.INTENT_KEY_CLIENT_SECRET + clientSecret );
        Logger.d( TAG,OAuthPresenter.INTENT_KEY_REDIRECT_URI+ callbackUrl );
        Logger.d( TAG,"state" + state );


//        Logger.d( TAG,"client_id" + clientId );

       // mClientName = opm.getClientName();

        if( TextUtils.isEmpty( clientId )) {
            finishWithErrorResult(OAuthErrorCode.CLIENT_ERROR_NO_CLIENTID);
            return false;
        }

        if (TextUtils.isEmpty(clientSecret)) {
            finishWithErrorResult(OAuthErrorCode.CLIENT_ERROR_NO_CLIENTSECRET);
            return false;
        }
        if (TextUtils.isEmpty(mClientName)) {
            finishWithErrorResult(OAuthErrorCode.CLIENT_ERROR_NO_CLIENTNAME);
            return false;
        }
        if (TextUtils.isEmpty(callbackUrl)) {
            finishWithErrorResult(OAuthErrorCode.CLIENT_ERROR_NO_CALLBACKURL);
            return false;
        }

      //  accessTokenTask = new GetAccessTokenTask(clientId, clientSecret, callbackUrl, state,callbackUrl);

        return true;
    }
//    private boolean initData( Bundle savedInstanceState){
//        OAuthLoginPreferManager opm = new OAuthLoginPreferManager( this );
//        clientId = opm.getClientId();
//        clientSecret = opm.getClientSecret();
//        callbackUrl = opm.getCallbackUrl();
//        state = (null == savedInstanceState) ? null : savedInstanceState.getString("OAuthLoginData_state");
//
//        Logger.d(TAG, "inti OAuth LoginActivity ");
//        Logger.d( TAG,OAuthPresenter.INTENT_KEY_CLIENT_ID + clientId );
//        Logger.d( TAG,OAuthPresenter.INTENT_KEY_CLIENT_SECRET + clientSecret );
//        Logger.d( TAG,OAuthPresenter.INTENT_KEY_REDIRECT_URI+ callbackUrl );
//        Logger.d( TAG,"state" + state );
//
//
////        Logger.d( TAG,"client_id" + clientId );
//
//        mClientName = opm.getClientName();
//
//        if( TextUtils.isEmpty( clientId )) {
//            finishWithErrorResult(OAuthErrorCode.CLIENT_ERROR_NO_CLIENTID);
//            return false;
//        }
//
//        if (TextUtils.isEmpty(clientSecret)) {
//            finishWithErrorResult(OAuthErrorCode.CLIENT_ERROR_NO_CLIENTSECRET);
//            return false;
//        }
//        if (TextUtils.isEmpty(mClientName)) {
//            finishWithErrorResult(OAuthErrorCode.CLIENT_ERROR_NO_CLIENTNAME);
//            return false;
//        }
//        if (TextUtils.isEmpty(callbackUrl)) {
//            finishWithErrorResult(OAuthErrorCode.CLIENT_ERROR_NO_CALLBACKURL);
//            return false;
//        }
//
//      //  accessTokenTask = new GetAccessTokenTask(clientId, clientSecret, callbackUrl, state,callbackUrl);
//
//        return true;
//    }

    private void runOnlyOnce(boolean result ) {
/*        if ( !result) {
            finishWithErrorResult(OAuthErrorCode.CLIENT_ERROR_NO_CLIENTID);
            return;
        }*/

        startLoginActivity();

    }

    private void startLoginActivity() {
        if (!Logger.isRealVersion()) {
            Logger.d(TAG, "startLoginActivity()");
        }

        // https://oss.navercorp.com/id-mobilesdk/naverid-3rdparty-sdk-android/issues/39
        // 에 의해 비공개로 로직을 남겨둔다
        if (Constant.LOGIN_BY_NAVERAPP_ONLY) {
            // Only Naver Login
            Logger.d(TAG, "OnlyNaverLogin()");

            //tryOAuthByNaverapp(mOAuthLoginData);
            return;
        }
        else if (Constant.LOGIN_BY_CUSTOM_TAB_ONLY) {

            Logger.d(TAG, "OnlyCustomTab()");
            // Only Custom Tab Login
            tryOAuthByCustomTab();

            return;
        }
        else if (Constant.LOGIN_BY_WEBVIEW_ONLY) {
            // Only WebView Login
            Logger.d(TAG, "OnlyWebView ()");
            startLoginWebviewActivity();
            return;

        }

        // Test 용이 아닌 기본 로직은 아래의 else를 타게 된다
        if (!Constant.LOGIN_BY_WEBVIEW_ONLY) {
         //   if (tryOAuthByNaverapp(mOAuthLoginData)) return;
            if (tryOAuthByCustomTab()) return;
        }
        // inapp browser 를 통해 기존 로그인 방식으로 로그인한다.

        Logger.d(TAG, " StartWebViewActivity()");
        startLoginWebviewActivity();
    }


    private void finishWithErrorResult( OAuthErrorCode code) {
        if (!Logger.isRealVersion()) {
            Logger.d(TAG, "Login finishWithErrorResult()");
        }

        Intent intent = new Intent();
        OAuthLoginPreferManager prefMng = new OAuthLoginPreferManager( this );

        prefMng.setLastErrorCode(code);
        prefMng.setLastErrorDesc(code.getDesc());

        intent.putExtra(OAuthPresenter.EXTRA_ERROR_CODE, code.getCode());
        intent.putExtra(OAuthPresenter.EXTRA_ERROR_DESCRIPTION, code.getDesc());

    }

    /*/// 로그인 결과로 얻어온 Access Token 을 리턴함
    public String getAccessToken(Context context) {
        OAuthLoginPreferenceManager pref = new OAuthLoginPreferenceManager(context);
        String at = pref.getAccessToken();

        if (TextUtils.isEmpty(at)) {
            return null;
        }
        return at;
    }

    /// 로그인 결과로 얻어온 Refresh Token 을 리턴함
    public String getRefreshToken(Context context) {
        OAuthLoginPreferenceManager pref = new OAuthLoginPreferenceManager(context);
        String rt = pref.getRefreshToken();

        if (TextUtils.isEmpty(rt)) {
            return null;
        }
        return rt;
    }

    /// Access Token 의 만료 시간을 리턴함
    public long getExpiresAt(Context context) {
        OAuthLoginPreferenceManager pref = new OAuthLoginPreferenceManager(context);
        long expiresAt = pref.getExpiresAt();

        return expiresAt;
    }

    /// 로그인 결과로 얻어온 Token의 Type을 리턴함
    public String getTokenType(Context context) {
        OAuthLoginPreferenceManager pref = new OAuthLoginPreferenceManager(context);
        String tokenType = pref.getTokenType();

        if (TextUtils.isEmpty(tokenType)) {
            return null;
        }
        return tokenType;
    }*/



    /**
     * 커스텀 탭으로 로그인 시도
     * @return 실행 여부
     */
    private boolean tryOAuthByCustomTab( ) {

        if (isEnableDoNotKeepActivity())  {
            return false;
        }

        if (!CustomTabOAuthPresenter.isCustomTabAvailable(this))
            return false;

        CustomTabOAuthPresenter manager = new CustomTabOAuthPresenter(this,this);
        manager.setCustomTabListener(customTabListener);
        /*oauthIntent.putExtra(OAuthPresenter.INTENT_KEY_CLIENT_ID, "f3b1c70e-6c3d-4344-8a4c-743c67a928e6");
        oauthIntent.putExtra(OAuthPresenter.INTENT_KEY_CLIENT_SECRET, "ALnxTUqecvZkmBhTQTPOOzr4W4cTlL4k-1TSLrvm4sNgxeN1SYHWakmODgouraM6BnJrj9LT0as6g6cjlSzClyM");
        oauthIntent.putExtra(OAuthPresenter.INTENT_KEY_REDIRECT_URI, "dreamtestlogin://authorize/");
        oauthIntent.putExtra(OAuthPresenter.INTENT_KEY_STATE, "111");
        oauthIntent.putExtra(OAuthPresenter.INTENT_KEY_OAUTH_SDK_VERSION, BuildConfig.VERSION_NAME);*/


        //// TEST /////
        Intent intent = newParamIntent(OAuthCustomTabActivity.class, TestConstant.CLIENT_ID, TestConstant.STATE, TestConstant.REDIRECT_URI);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, CUSTOMTAB_LOGIN);
        return true;
    }

    /**
     * login webview 를 실행함
     * 로그인의중심이 될 앱이 없거나, Custom ChromeTab 도 없는경우 웹뷰로함
     */
    private void startLoginWebviewActivity() {
        if (!Logger.isRealVersion()) {
            Logger.d(TAG, "startLoginActivity() with webview");
        }

        /*startActivityForResult(newParamIntent(OAuthLoginInAppBrowserActivity.class
                , loginData.getClientId(), loginData.getInitState(), loginData.getCallbackUrl())
                , REQUESTCODE_LOGIN);*/
    }

    private void propagationResult(boolean b) {
       if ( OAuthLogin.getInstance().getOAuthLoginHandler()!= null ) {
            Message msg = new Message();
            msg.what = b ? 1 : 0;
            OAuthLogin.getInstance().getOAuthLoginHandler().sendMessage(msg);
        }else {

           Log.e(TAG, "ffooo Handler is null ");
       }
    }

    private boolean isEnableDoNotKeepActivity() {
        int flag;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            flag = Settings.System.getInt(getContentResolver(), Settings.System.ALWAYS_FINISH_ACTIVITIES, 0);
        } else {
            flag = Settings.Global.getInt(getContentResolver(), Settings.Global.ALWAYS_FINISH_ACTIVITIES, 0);
        }
        return flag == 1;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        isForceDestroyed = false;

        if (requestCode == CUSTOMTAB_LOGIN && resultCode == RESULT_CANCELED) {
            Logger.d(TAG, "activity call by customtab");
            return;
        }

        if (data == null) {
            finishWithErrorResult(OAuthErrorCode.CLIENT_USER_CANCEL);
            return;
        }
//        data.getExtras().getString()
        String state = data.getStringExtra(OAuthPresenter.EXTRA_OAUTH_STATE);
        String code = data.getStringExtra(OAuthPresenter.EXTRA_OAUTH_CODE);
        String errorCode = data.getStringExtra(OAuthPresenter.EXTRA_ERROR_CODE);
        String errorDesc = data.getStringExtra(OAuthPresenter.EXTRA_ERROR_DESCRIPTION);

        this.stateFromServer = state;
        this.code = code;
        this.errorCode = errorCode;
        this.errorMessage = errorDesc;
//        mOAuthLoginData.setMiddleResult(code, state, errorCode, errorDesc);

        if (TextUtils.isEmpty(code)) {
            OAuthLoginPreferManager prefMng = new OAuthLoginPreferManager( this );

            prefMng.setLastErrorCode(OAuthErrorCode.fromString(errorCode));
            prefMng.setLastErrorDesc(errorDesc);

            setResult(RESULT_CANCELED, data );
            finish();

            propagationResult(false);
        } else {
            try {
                accessTokenTask = new GetAccessTokenTask(TestConstant.CLIENT_ID,TestConstant.STATE,code,TestConstant.REDIRECT_URI);
                accessTokenTask.execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    }

    @Override
    public void onProgress(Uri uri) {

    }

    @Override
    public void onResponseLogin() {

    }

    @Override
    public void onResponseAccessToken() {

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
        intent.putExtra(OAuthPresenter.INTENT_KEY_CLIENT_ID, clientId);
        intent.putExtra(OAuthPresenter.INTENT_KEY_REDIRECT_URI, callbackUrl);
        intent.putExtra(OAuthPresenter.INTENT_KEY_STATE, initState);
        intent.putExtra(OAuthPresenter.INTENT_KEY_SCOPE, TestConstant.SCOPE);
//        intent.putExtra(OAuthPresenter.INTENT_KEY_OAUTH_SDK_VERSION, BuildConfig.VERSION_NAME);

        return intent;
    }

    private class GetAccessTokenTask extends AsyncTask<Void,Void, OAuthorizedResponse> {

        String clientId;
//        String clientSecret;
        String state;
        String code;
        String callbackUrl;

        JSONObject jsonObject;
        public GetAccessTokenTask( String clientId,String state,String code ,String callbackUrl) throws JSONException {

            this.clientId = clientId;
//            this.clientSecret = clientSecret;
            this.state = state;
            this.code = code;
            this.callbackUrl = callbackUrl;

            jsonObject = new JSONObject();

            jsonObject.put( Constant.PARAM_KEY_CLIENT_ID,clientId );
            jsonObject.put( Constant.PARAM_KEY_STATE, state );
            jsonObject.put( Constant.PARAM_KEY_CODE,code );
            jsonObject.put( Constant.PARAM_KEY_REDIRECT_URI, callbackUrl );

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Logger.d(getClass().getSimpleName(), " onPreExecuted"); // TODO: UI 효과 추가.
        }

        @Override
        protected OAuthorizedResponse doInBackground(Void... voids) {
            try {

                return DefaultOAuthConnector.post(
                        TestConstant.OAUTH_REQUEST_ACCESS_TOKEN_URL,
                        jsonObject
                );
                /*return OAuthLoginConnection.requestAccessToken(OAuthLoginActivity.this,
                        clientId,
                        clientSecret,
                        state,
                        code,
                        callbackUrl
                );*/
            } catch (Exception e) {
                return new OAuthorizedResponse(OAuthErrorCode.CLIENT_ERROR_CONNECTION_ERROR);
            }
        }

        @Override
        protected void onPostExecute(OAuthorizedResponse oAuthorizedResponse) {
            super.onPostExecute(oAuthorizedResponse);

            Logger.d(getClass().getSimpleName(), " onPostExecuted");// TODO : UI 효과 추가.
            Logger.d(getClass().getSimpleName(), oAuthorizedResponse.toString());// TODO : UI 효과 추가.



            try{

                Intent i = new Intent();
                OAuthLoginPreferManager loginPreferManager = new OAuthLoginPreferManager(OAuthLoginActivity.this);

              //  edCompat.setText( oAuthorizedResponse.isSuccess()+"");
               /* if( oAuthorizedResponse.isSuccess()) {
                    loginPreferManager.setAccessToken(oAuthorizedResponse.getAccessToken());
                    loginPreferManager.setRefreshToken(oAuthorizedResponse.getRefreshToken());
                    loginPreferManager.setExpiresAt(System.currentTimeMillis() / 1000 + oAuthorizedResponse.getExpiresIn());
                    loginPreferManager.setTokenType(oAuthorizedResponse.getTokenType());
                    loginPreferManager.setLastErrorCode(OAuthErrorCode.NONE);
                    loginPreferManager.setLastErrorDesc(OAuthErrorCode.NONE.getDesc());
                    setResult(RESULT_OK, i);
                } else {
                    if (oAuthorizedResponse.getErrorCode() == OAuthErrorCode.NONE) {
                        finishWithErrorResult(OAuthErrorCode.CLIENT_USER_CANCEL);
                        return;
                    } else {
                        loginPreferManager.setLastErrorCode(oAuthorizedResponse.getErrorCode());
                        loginPreferManager.setLastErrorDesc(oAuthorizedResponse.getErrorDescription());
                        i.putExtra(OAuthPresenter.EXTRA_ERROR_CODE, oAuthorizedResponse.getErrorCode().getCode());
                        i.putExtra(OAuthPresenter.EXTRA_ERROR_DESCRIPTION, oAuthorizedResponse.getErrorDescription());
                        setResult(RESULT_CANCELED, i);
                    }
                }*/
            } catch (Exception e) {
                e.printStackTrace();
            }
            finish();

            try {
                propagationResult(oAuthorizedResponse.isSuccess());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
