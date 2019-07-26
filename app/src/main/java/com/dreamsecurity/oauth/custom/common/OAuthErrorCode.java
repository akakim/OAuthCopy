package com.dreamsecurity.oauth.custom.common;

import android.text.TextUtils;

public enum OAuthErrorCode {

    NONE ("", ""),
    // 서버에서 내려주는 에러의 종류는 : http://tools.ietf.org/html/rfc6749 참고
    SERVER_ERROR_INVALID_REQUEST ("invalid_request", "invalid_request"),
    SERVER_ERROR_UNAUTHORIZED_CLIENT ("unauthorized_client", "unauthorized_client"),
    SERVER_ERROR_ACCESS_DENIED ("access_denied", "access_denied"),
    SERVER_ERROR_UNSUPPORTED_RESPONSE_TYPE ("unsupported_response_type", "unsupported_response_type"),
    SERVER_ERROR_INVALID_SCOPE ("invalid_scope", "invalid_scope"),
    SERVER_ERROR_SERVER_ERROR ("server_error", "server_error"),		// STATUS CODE == 500
    SERVER_ERROR_TEMPORARILY_UNAVAILABLE ("temporarily_unavailable", "temporarily_unavailable"),		// STATUS CODE == 503
    ERROR_NO_CATAGORIZED ("no_catagorized_error", "no_catagorized_error"),
    CLIENT_ERROR_PARSING_FAIL ("parsing_fail", "parsing_fail"),
    CLIENT_ERROR_NO_CLIENTID ("invalid_request", "no_clientid"),
    CLIENT_ERROR_NO_CLIENTSECRET ("invalid_request", "no_clientsecret"),
    CLIENT_ERROR_NO_CLIENTNAME ("invalid_request", "no_clientname"),
    CLIENT_ERROR_NO_CALLBACKURL ("invalid_request", "no_callbackurl"),
    CLIENT_ERROR_CONNECTION_ERROR ("server_error", "connection_error"),
    CLIENT_ERROR_CERTIFICATION_ERROR ("server_error", "certification_error"),
    CLIENT_USER_CANCEL ("user_cancel", "user_cancel"),
    ACTIVITY_IS_SINGLE_TASK("activity_is_single_task", "activity_is_single_task");
    /*SUCCESS				("SUCCESS", 		null),
    CLIENT_ACTION_BUSY				("BUSY", 			"BUSY"),			// http client 가 사용중
    CLIENT_ACTION_CANCEL				("CANCEL", 			"CANCEL"),	  		// 그외의 Exception 발생으로 실패
    CLIENT_ACTION_URL_ERROR			("URL_ERROR", 		"URL_ERROR"),			// url 이 이상한 경우
    CLIENT_ACTION_CONNECTION_TIMEOUT	("CONNECTION_TIMEOUT", "CONNECTION_TIMEOUT"), 		// timeout 발생한 경우
    CLIENT_ACTION_CONNECTION_FAIL		("CONNECTION_FAIL", "CONNECTION_FAIL"),  			// connection 실패
    CLIENT_ACTION_IO_FAIL ("IO_EXCEPTION","IO_EXCEPTION"),
    CLIENT_ACTION_EXCEPTION_FAIL		("EXCEPTION_FAIL", 	"EXCEPTION_FAIL"),	  		// 그외의 Exception 발생으로 실패
    CLIENT_ACTION_NO_PEER_CERTIFICATE	("NO_PEER_CERTIFICATE", "NO_PEER_CERTIFICATE"),		// 인증서오류
    FAIL				("FAIL", 			"FAIL");	  		// 그외의 실패*/

    private String mCode;
    private String mDesc;

    private OAuthErrorCode(String code, String desc) {
        mCode = code;
        mDesc = desc;
    }

    public String getCode() {
        return mCode;
    }

    public String getDesc() {
        return mDesc;
    }

    public void setDesc(String desc) {
        mDesc = desc;
    }

    public static OAuthErrorCode fromString(String str)  {
        if (TextUtils.isEmpty(str)) {
            return OAuthErrorCode.NONE;
        } else {
            for (OAuthErrorCode st : OAuthErrorCode.values()) {
                if (str.equalsIgnoreCase(st.mCode)) {
                    return st;
                }
                if (str.equalsIgnoreCase(st.name())) {
                    return st;
                }
            }
            return OAuthErrorCode.ERROR_NO_CATAGORIZED;
        }
    }


    public String toString() {
        return mCode;
    }
}
