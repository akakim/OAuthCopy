package com.dreamsecurity.ssooauth.magiclogin;

import android.content.Context;
import com.dreamsecurity.ssooauth.magiclogin.data.OAuthErrorCode;
import com.dreamsecurity.ssooauth.magiclogin.data.OAuthLoginPreferenceManager;
import com.dreamsecurity.ssooauth.magiclogin.data.OAuthResponse;
import com.dreamsecurity.ssooauth.util.DeviceAppInfo;

public class OAuthLogin {

    private static final String TAG = "OAuthLogin";

    private static OAuthLogin instance;

    public static OAuthLoginHandler mOAuthLoginHandler;

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
    }

    /// 네아로 SDK의 버전을 리턴한다
    public static String getVersion() {
        return OAuthLoginDefine.VERSION;
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


}
