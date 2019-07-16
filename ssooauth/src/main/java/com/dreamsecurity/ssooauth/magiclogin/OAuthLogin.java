package com.dreamsecurity.ssooauth.magiclogin;

import android.content.Context;
import android.text.TextUtils;
import com.dreamsecurity.ssooauth.common.logger.Logger;
import com.dreamsecurity.ssooauth.magiclogin.data.OAuthErrorCode;
import com.dreamsecurity.ssooauth.magiclogin.data.OAuthLoginPreferenceManager;
import com.dreamsecurity.ssooauth.magiclogin.data.OAuthLoginState;
import com.dreamsecurity.ssooauth.magiclogin.data.OAuthResponse;
import com.dreamsecurity.ssooauth.util.DeviceAppInfo;

public class OAuthLogin {

    private static final String TAG = "OAuthLogin";

    private static OAuthLogin instance;

    public static OAuthLoginHandler oAuthLoginHandler;

    public static OAuthLogin getInstance(){

        if( instance == null ){
            instance = new OAuthLogin();
        }

        return instance;
    }

    private OAuthLogin(){
        // do nothing
    }

    /**
     *  OAuth 인증시 필요한 값들을 preference에 저장함.
     * @param context
     * @param clientId
     * @param clientSecret
     * @param clientName
     */
    public void init(Context context,String clientId , String clientSecret, String clientName){
        String packageName = context.getPackageName();

        OAuthLoginPreferenceManager manager = new OAuthLoginPreferenceManager( context );
        manager.setClientId( clientId );
        manager.setClientSecret( clientSecret );
        manager.setClientName( clientName );
        manager.setCallbackUrl(packageName );

        manager.setLastErrorCode(OAuthErrorCode.NONE);
        manager.setLastErrorDesc("");
    }


    public static String getVersion() {
        return OAuthLoginDefine.VERSION;
    }

    public void showDevLog(boolean isDev){
        Logger.setRealVersion( !isDev );
    }

    private boolean valid(Context context) {
        if (null == context) {
            Logger.i(TAG, "context is null");
            return false;
        }
        OAuthLoginPreferenceManager prefMng = new OAuthLoginPreferenceManager(context);
        if (TextUtils.isEmpty(prefMng.getClientId())) {
            Logger.i(TAG, "CliendId is null");
            return false;
        }
        if (TextUtils.isEmpty(prefMng.getClientSecret())) {
            Logger.i(TAG, "CliendSecret is null");
            return false;
        }
        return true;
    }


    /// 네아로 인스턴스의 로그인 상태를 리턴해줌
    /**
     * @param context 저장된 access token 및 refresh token 을 얻어오기 위해 sharedPreference를 얻어올 때 쓰임
     * @return {@link OAuthLoginState} ref
     */
    public OAuthLoginState getState(Context context) {
        if (!valid(context)) {
            return OAuthLoginState.NEED_INIT;
        }
        OAuthLoginPreferenceManager prefMng = new OAuthLoginPreferenceManager(context);
        String at = prefMng.getAccessToken();
        String rt = prefMng.getRefreshToken();

        if (TextUtils.isEmpty(at)) {
            if (TextUtils.isEmpty(rt)) {
                return OAuthLoginState.NEED_LOGIN;
            } else {
                return OAuthLoginState.NEED_REFRESH_TOKEN;
            }
        }
        return OAuthLoginState.OK;
    }


    /**
     * blocking / network method
     * background 에서(asynctask, thread 등에서) 실행해야함
     * 클라이언트 및 서버에 저장되어 있는 Access token 및 Refresh token 을 삭제
     * @param context context
     * @return boolean true 서버에서 token 삭제 성공, false 서버에서 token 삭제 실패
     */
    public boolean logoutAndDeleteToken(Context context) {
        OAuthLoginPreferenceManager prefMng = new OAuthLoginPreferenceManager(context);
        String clientId = prefMng.getClientId();
        String clientSecret = prefMng.getClientSecret();
        String accessToken = prefMng.getAccessToken();

        logout(context);

        OAuthResponse res = null;
        try {
            res = OAuthLoginConnection.deleteToken(context, clientId, clientSecret, accessToken);

            if ("success".equalsIgnoreCase(res.getResultValue())) {
                return true;
            }

            prefMng.setLastErrorCode(res.getErrorCode());
            prefMng.setLastErrorDesc(res.getErrorDesc());

            return false;

        } catch (Exception e) {
            e.printStackTrace();

            prefMng.setLastErrorCode(OAuthErrorCode.ERROR_NO_CATAGORIZED);
            prefMng.setLastErrorDesc(e.getMessage());

            return false;
        }
    }
    /// 클라이언트에 저장되어 있는 Access token 및 Refresh token을 삭제함
    public void logout(Context context) {
        OAuthLoginPreferenceManager prefMng = new OAuthLoginPreferenceManager(context);

        prefMng.setAccessToken("");
        prefMng.setRefreshToken("");

        prefMng.setLastErrorCode(OAuthErrorCode.NONE);
        prefMng.setLastErrorDesc("");
    }

    /// 로그인 결과로 얻어온 Access Token 을 리턴함
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

        return  pref.getExpiresAt();
    }

    /// 지난 로그인 시도가 실패한 경우 Error code 를 리턴함
    public OAuthErrorCode getLastErrorCode(Context context) {
        OAuthLoginPreferenceManager pref = new OAuthLoginPreferenceManager(context);
        return pref.getLastErrorCode();
    }

    /// 지난 로그인 시도가 실패한 경우 Error description 을 리턴함
    public String getLastErrorDesc(Context context) {
        OAuthLoginPreferenceManager pref = new OAuthLoginPreferenceManager(context);
        return pref.getLastErrorDesc();
    }

}
