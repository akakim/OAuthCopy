package com.dreamsecurity.oauth;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.dreamsecurity.oauth.activity.OAuthCustomTabActivity;
import com.dreamsecurity.oauth.activity.OAuthLoginActivity;
import com.dreamsecurity.oauth.custom.OAuthPresenter;


public class OAuthSampleActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "OAuthSampleActivity";

    /**
     * client 정보를 넣어준다.
     */
    private static String OAUTH_CLIENT_ID = "jyvqXeaVOVmV";
    private static String OAUTH_CLIENT_SECRET = "527300A0_COq1_XV33cf";
    private static String OAUTH_CLIENT_NAME = "네이버 아이디로 로그인";

//    private static OAuthLogin OAuthLoginInstance;
    private static Context mContext;

    private TextView tvAccessToken;
    private static TextView tvRefreshToken;
    private static TextView tvExpired;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth_sample);


        findViewById( R.id.btnAuthentication).setOnClickListener( this );
        findViewById( R.id.btnLogout).setOnClickListener( this );
        findViewById( R.id.btnRefresh).setOnClickListener( this );
        findViewById( R.id.btnCustomChromeTab).setOnClickListener( this );
        findViewById( R.id.btnAuthorizationAcv).setOnClickListener( this );

        tvAccessToken = findViewById( R.id.tvAccessToken);
        tvRefreshToken = findViewById( R.id.tvRefreshToken);
        tvExpired = findViewById( R.id.tvExpires );

        Uri.Builder builder = new Uri.Builder();
        builder.scheme( "naver3rdpartylogin");
        builder.authority("authorize");
        builder.appendPath("/");

        Log.d(TAG,"get Uri " + builder.toString());
/*btnCustomChromeTab
        OAuthLoginInstance = OAuthLogin.getInstance();
        OAuthLoginInstance.showDevLog( true );
        OAuthLoginInstance.init( this, OAUTH_CLIENT_ID,OAUTH_CLIENT_SECRET,OAUTH_CLIENT_NAME);
*/


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onResume() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onResume();

    }

    @Override
    public void onClick(View view) {
        switch ( view.getId()){
            case R.id.btnAuthentication:
                break;
            case R.id.btnLogout:
                break;
            case R.id.btnRefresh:
                break;
            case R.id.btnCustomChromeTab:
              //  startActivity( new Intent( this , OAuthLoginActivity.class ));
                break;
            case R.id.btnAuthorizationAcv:


                Intent oauthIntent = new Intent(this,OAuthLoginActivity.class );

                oauthIntent.putExtra(OAuthPresenter.INTENT_KEY_CLIENT_ID, "f3b1c70e-6c3d-4344-8a4c-743c67a928e6");
                oauthIntent.putExtra(OAuthPresenter.INTENT_KEY_CLIENT_SECRET, "ALnxTUqecvZkmBhTQTPOOzr4W4cTlL4k-1TSLrvm4sNgxeN1SYHWakmODgouraM6BnJrj9LT0as6g6cjlSzClyM");
                oauthIntent.putExtra(OAuthPresenter.INTENT_KEY_REDIRECT_URI, "dreamtestlogin://authorize/");
                oauthIntent.putExtra(OAuthPresenter.INTENT_KEY_STATE, "111");
                oauthIntent.putExtra(OAuthPresenter.INTENT_KEY_OAUTH_SDK_VERSION, BuildConfig.VERSION_NAME);
//                oauthIntent.putExtra(OAuthPresenter.INTENT_KEY_RESPONSE_TYPE, BuildConfig.VERSION_NAME);

                startActivity( oauthIntent );
                break;
        }
    }

    private void updateView(){

    }

    /**
     * blocking / network method
     * background 에서(asynctask, thread 등에서) 실행해야함
     * shared preference에 저장된 갱신 토큰을 이용해 접근 토큰을 갱신하고 갱신된 접근 토큰을 반환한다
     * 실패시 null을 리턴함.
     * @param context context
     * @return access token string (실패시 null을 리턴)
     */
//    public String refreshAccessToken(Context context) {
//        OAuthLoginPreferenceManager prefMng = new OAuthLoginPreferenceManager(context);
//        String clientId = prefMng.getClientId();
//        String clientSecret = prefMng.getClientSecret();
//        String refreshToken = prefMng.getRefreshToken();
//
//        OAuthResponse res = OAuthLoginConnection.requestRefreshToken(context, clientId, clientSecret, refreshToken);
//
//        String at = res.getAccessToken();
//        if (TextUtils.isEmpty(at)) {
//            return null;
//        }
//
//        prefMng.setAccessToken(res.getAccessToken());
//        prefMng.setExpiresAt(System.currentTimeMillis() / 1000 + res.getExpiresIn());
//
//        return at;
//    }
//
//    /// 로그인 결과로 얻어온 Access Token 을 리턴함
//    public String getAccessToken(Context context) {
//        OAuthLoginPreferenceManager pref = new OAuthLoginPreferenceManager(context);
//        String at = pref.getAccessToken();
//
//        if (TextUtils.isEmpty(at)) {
//            return null;
//        }
//        return at;
//    }
//
//    /// 로그인 결과로 얻어온 Refresh Token 을 리턴함
//    public String getRefreshToken(Context context) {
//        OAuthLoginPreferenceManager pref = new OAuthLoginPreferenceManager(context);
//        String rt = pref.getRefreshToken();
//
//        if (TextUtils.isEmpty(rt)) {
//            return null;
//        }
//        return rt;
//    }
//
//    /// Access Token 의 만료 시간을 리턴함
//    public long getExpiresAt(Context context) {
//        OAuthLoginPreferenceManager pref = new OAuthLoginPreferenceManager(context);
//        long expiresAt = pref.getExpiresAt();
//
//        return expiresAt;
//    }
//
//    /// 지난 로그인 시도가 실패한 경우 Error code 를 리턴함
//    public OAuthErrorCode getLastErrorCode(Context context) {
//        OAuthLoginPreferenceManager pref = new OAuthLoginPreferenceManager(context);
//        return pref.getLastErrorCode();
//    }
//
//    /// 지난 로그인 시도가 실패한 경우 Error description 을 리턴함
//    public String getLastErrorDesc(Context context) {
//        OAuthLoginPreferenceManager pref = new OAuthLoginPreferenceManager(context);
//        return pref.getLastErrorDesc();
//    }

    /// API 를 호출하고 성공하는 경우 결과(content body)를 리턴함
    /**
     * blocking / network method
     * background 에서(asynctask, thread 등에서) 실행해야함
     * @param context context
     * @param accessToken access token
     * @param url url
     * @return api 호출 결과
     */
//    public String requestApi(Context context, String accessToken, String url) {
//        String authHeader = "bearer " + accessToken;
//        if (!Logger.isRealVersion()) {
//            Logger.d(TAG, "at:" + accessToken + ", url:" + url);
//            Logger.d(TAG, "header:" + authHeader);
//        }
//        ResponseData res = CommonConnect.request(context, url, null, null, authHeader);
//
//        if (!Logger.isRealVersion()) {
//            Logger.d(TAG, "res.statuscode" + res.getStatusCode());
//            Logger.d(TAG, "res.content" + res.getContent());
//        }
//
//        if (res == null) {
//            return null;
//        }
//
//        return res.getContent();
//    }


    /**
     * blocking / network method
     * background 에서(asynctask, thread 등에서) 실행해야함
     * 클라이언트 및 서버에 저장되어 있는 Access token 및 Refresh token 을 삭제
     * @param context context
     * @return boolean true 서버에서 token 삭제 성공, false 서버에서 token 삭제 실패
     */
//    public boolean logoutAndDeleteToken(Context context) {
//        OAuthLoginPreferenceManager prefMng = new OAuthLoginPreferenceManager(context);
//        String clientId = prefMng.getClientId();
//        String clientSecret = prefMng.getClientSecret();
//        String accessToken = prefMng.getAccessToken();
//
//        logout(context);
//
//        OAuthResponse res = null;
//        try {
//            res = OAuthLoginConnection.deleteToken(context, clientId, clientSecret, accessToken);
//
//            if ("success".equalsIgnoreCase(res.getResultValue())) {
//                return true;
//            }
//
//            prefMng.setLastErrorCode(res.getErrorCode());
//            prefMng.setLastErrorDesc(res.getErrorDesc());
//
//            return false;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//
//            prefMng.setLastErrorCode(OAuthErrorCode.ERROR_NO_CATAGORIZED);
//            prefMng.setLastErrorDesc(e.getMessage());
//
//            return false;
//        }
//    }


    /// 클라이언트에 저장되어 있는 Access token 및 Refresh token을 삭제함
/*    public void logout(Context context) {
        OAuthLoginPreferenceManager prefMng = new OAuthLoginPreferenceManager(context);

        prefMng.setAccessToken("");
        prefMng.setRefreshToken("");

        prefMng.setLastErrorCode(OAuthErrorCode.NONE);
        prefMng.setLastErrorDesc("");
    }

    private class OAuthLoginTask extends AsyncTask<Void, Void, String> {
        private Context _context;
        private OAuthDialog oauthDialog = new OAuthDialog();

        OAuthLoginTask(Context context) {
            _context = context;
        }
        @Override
        protected void onPreExecute() {
            oauthDialog.showProgressDlg(_context, _context.getString(R.string.magic_sso_string_getting_token), null);
        }
        @Override
        protected String doInBackground(Void... params) {
            return refreshAccessToken(_context);
        }
        protected void onPostExecute(String at) {
            try {
                oauthDialog.hideProgressDlg();
            } catch (Exception e) {
                // do nothing
            }

            if (TextUtils.isEmpty(at)) {
                Intent intent = new Intent(_context, OAuthLoginActivity.class);
                _context.startActivity(intent);
            } else {
                oAuthLoginHandler.run(true);
            }
        }
    }

    private OAuthLoginHandler oAuthLoginHandler = new OAuthLoginHandler() {

        @Override
        public void run(boolean success) {
            if (success) {
                String accessToken = OAuthLoginInstance.getAccessToken(mContext);
                String refreshToken = OAuthLoginInstance.getRefreshToken(mContext);
                long expiresAt = OAuthLoginInstance.getExpiresAt(mContext);

                tvAccessToken.setText(accessToken);
                tvRefreshToken.setText(refreshToken);
                tvExpired.setText(String.valueOf(expiresAt));

            } else {
                String errorCode = OAuthLoginInstance.getLastErrorCode(mContext).getCode();
                String errorDesc = OAuthLoginInstance.getLastErrorDesc(mContext);
                Toast.makeText(mContext, "errorCode:" + errorCode + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
            }
        }
    };*/
}
