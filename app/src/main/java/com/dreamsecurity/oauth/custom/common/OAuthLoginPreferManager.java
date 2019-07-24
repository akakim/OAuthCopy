package com.dreamsecurity.oauth.custom.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.Map;

public class OAuthLoginPreferManager {

    public static final String TAG = "OAuthLoginPreferManager";

    private final String PREF_NAME_PER_APP  = "DreamOAuthData";
    private  final String  ACCESS_TOKEN = "ACCESS_TOKEN";
    private  final String  REFRESH_TOKEN = "REFRESH_TOKEN";
    private  final String  EXPIRES_AT = "EXPIRES_AT";
    private  final String  TOKEN_TYPE =	"TOKEN_TYPE";
    private  final String  CLIENT_ID = "CLIENT_ID";
    private  final String  CLIENT_SECRET = "CLIENT_SECRET";

    private  final String  CLIENT_NAME = "CLIENT_NAME";
    private  final String  CALLBACK_URL	= "CALLBACK_URL";
    private  final String  LAST_ERROR_CODE	= "LAST_ERROR_CODE";
    private  final String  LAST_ERROR_DESC = "LAST_ERROR_DESC";
    private Context context = null;
    private SharedPreferences pref = null;

    public OAuthLoginPreferManager( Context context)  {
        this.context = context;

        if( context != null){
            if( pref == null ){
                pref = context.getSharedPreferences( PREF_NAME_PER_APP , Context.MODE_PRIVATE );
            }
        } else {
            Logger.e(TAG, " Context 가 널.");
        }

    }



    public void setAccessToken(String aToken) {
        set( ACCESS_TOKEN,aToken );
    }

    public String getAccessToken() {
        String token = (String) get(ACCESS_TOKEN);

        if (TextUtils.isEmpty(token)) {
            return null;
        }

        // expires time 검증 후 return 해줌
        if (System.currentTimeMillis() / 1000 - getExpiresAt() < 0)
            return token;

        // 만료로 인해 값은 있으나 리턴안해줌
        Logger.i(TAG, "access token is expired.");

        return null;
    }

    public void setRefreshToken(String rToken) {
        set(REFRESH_TOKEN,rToken);
    }

    public String getRefreshToken() {
        return (String) get(REFRESH_TOKEN);
    }

    public void setExpiresAt(Long expiresTimeStamp) {
        set(EXPIRES_AT,expiresTimeStamp);
    }

    public long getExpiresAt() {
        Long expires = (Long) get(EXPIRES_AT);
        if (expires == null)
            return 0;

        return expires;
    }

    public void setClientId(String clientId) {
        set(CLIENT_ID,clientId);
    }

    public String getClientId() {
        return (String) get(CLIENT_ID);
    }


    public void setClientSecret(String clientSecret) {
        set(CLIENT_SECRET,clientSecret);
    }

    public String getClientSecret() {
        return (String) get(CLIENT_SECRET);
    }

    public void setClientName(String clientName) {
        set(CLIENT_NAME,clientName);
    }

    public String getClientName() {
        return (String) get(CLIENT_NAME);
    }


    public void setCallbackUrl(String callbackUrl) {
       set(CALLBACK_URL,callbackUrl);
    }

    public String getCallbackUrl() {
        return (String)get(CALLBACK_URL);
    }


    public void setTokenType(String tokenType) {
        set(TOKEN_TYPE,tokenType);
    }

    public String getTokenType() {
        return (String) get(TOKEN_TYPE);
    }


    public void setLastErrorCode(OAuthErrorCode errorCode) {
       set( LAST_ERROR_CODE , errorCode );
    }

    public OAuthErrorCode getLastErrorCode() {
        String code = (String)get(LAST_ERROR_CODE);
        return OAuthErrorCode.fromString(code);
    }

    public void setLastErrorDesc(String errorDesc) {
        set(LAST_ERROR_DESC,errorDesc);
    }

    public String getLastErrorDesc() {
        return (String)get( LAST_ERROR_DESC );
    }

    private boolean set ( String key , Object data ){
        boolean result = false;
        int cnt = 0;
        // preference 기록이 실패하는 경우 3회까지 재실행함
        while (result == false && cnt < 3) {
            if (cnt > 0) {
                Logger.e(TAG, "preference set() fail (cnt:" + cnt + ")");
                try {
                    Thread.sleep(50);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            result = setSub(key , data);
            cnt ++;
        }

        return result;
    }

    private boolean setSub(String key, Object data ){
        if( pref == null )
            return false;

        SharedPreferences.Editor editor = pref.edit();
        editor.apply();
        if (editor == null)
            return false;

        try{
            if ( data instanceof String) {
                editor.putString( key , (String)data );
            }else if ( data instanceof Integer ) {
                editor.putInt( key , (Integer) data);
            } else if ( data instanceof  Long){
                editor.putLong( key , (Long) data);
            }


            return editor.commit();
        }catch ( Exception e ){
            if (!Logger.isRealVersion()) {
                Logger.e(TAG, "Prefernce Set() fail, key:" + key + ", data : " + data + "e:" + e.getMessage());
            }
        }
        return false;
    }

    public Object get(String key){
        try {
            return pref.getAll().get( key );

        } catch (Exception e) {
            if (!Logger.isRealVersion()) {
                Logger.e(TAG, "get() fail, e:" + e.getMessage());
            }
        }
        return null;
    }

}
