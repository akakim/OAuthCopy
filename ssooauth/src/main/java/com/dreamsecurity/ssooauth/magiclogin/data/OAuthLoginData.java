package com.dreamsecurity.ssooauth.magiclogin.data;

import android.text.TextUtils;
import com.dreamsecurity.ssooauth.common.logger.Logger;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.SecureRandom;

public class OAuthLoginData {

    private static final String TAG = "OAuthLoginData";

    private String mInOAuthClientId;
    private String mInOAuthClientSecret;
    private String mInOAuthCallback;

    private String mInOAuthState;

    private String 			mOAuthState;
    private String 			mOAuthCode;
    private OAuthErrorCode 	mOAuthErrorCode;
    private String 			mOAuthErrorDesc;

    public OAuthLoginData(String clientId, String clientSecret, String callbackUrl) {
        init(clientId, clientSecret, callbackUrl, null);
    }

    public OAuthLoginData(String clientId, String clientSecret, String callbackUrl, String state) {
        init(clientId, clientSecret, callbackUrl, state);
    }

    private void init(String clientId, String clientSecret, String callbackUrl, String state) {
        mInOAuthClientId = clientId;
        mInOAuthClientSecret = clientSecret;
        mInOAuthCallback = callbackUrl;

        if (TextUtils.isEmpty(state)) {
            mInOAuthState = generateState();
        } else {
            mInOAuthState = state;
        }
    }


    private String generateState() {
        SecureRandom random = new SecureRandom();
        String state = new BigInteger(130, random).toString(32);

        try {
            return URLEncoder.encode(state, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return state;
        }
    }

    public void setMiddleResult(String code, String state, String errorCode, String errorDesc) {
        mOAuthCode = code;
        mOAuthState = state;
        mOAuthErrorCode = OAuthErrorCode.fromString(errorCode);
        mOAuthErrorDesc = errorDesc;
    }


    public boolean isSuccess() {
        if (TextUtils.isEmpty(mOAuthErrorCode.getCode())) {
            if (isStateOk()) {
                if (TextUtils.isEmpty(mOAuthCode)) {
                    return false;
                } else {
                    return true;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public String getInitState() {
        return mInOAuthState;
    }

    public String getState() {
        return mOAuthState;
    }

    public String getClientId() {
        return mInOAuthClientId;
    }

    public String getClientSecret() {
        return mInOAuthClientSecret;
    }

    /**
     * code 값이 문제가 있는 경우 null 을 리턴함
     * @return code (인증 생성 중간 값)
     */
    public String getCode() {
        if (isStateOk()) {
            return mOAuthCode;
        } else {
            // code 가 null 인 조건은 2가지임 --> code 가 없거나, state 가 없거나
            return null;
        }
    }

    public OAuthErrorCode getErrorCode() {
        return mOAuthErrorCode;
    }

    public String getErrorDesc() {
        return mOAuthErrorDesc;
    }

    private boolean isStateOk() {
        if (mInOAuthState.equalsIgnoreCase(mOAuthState)) {
            return true;
        }
        if (!Logger.isRealVersion()) {
            Logger.d(TAG, "state is not valid. init:" + mInOAuthState + ", check:" + mOAuthState);
        }
        return false;
    }

    public String getCallbackUrl() {
        return mInOAuthCallback;
    }
}
