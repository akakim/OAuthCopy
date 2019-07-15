package com.dreamsecurity.ssooauth.magiclogin.data;

import android.text.TextUtils;

import java.util.Map;

public class OAuthResponse {
    // deleteToken 의 경우엔  {"result":"success",...} 이런 형태로 리턴됨
    private String 		mResultValue;

    private String 		mAccessToken;
    private long 		mExpiresIn;

    // 이녀석은 한번 받아오면 계속 쓸 수 있음
    private String 		mRefreshToken;
    // [Bearer|MAC]
    private String 		mTokenType;

    private OAuthErrorCode 	mErrorCode;
    private String 			mErrorDescription;

    // ret 에서 값을 추출하여 위 값들을 정해줌
    public OAuthResponse(Map<String, String> ret) {

        mAccessToken = ret.get("access_token");
        mRefreshToken = ret.get("refresh_token");
        mTokenType = ret.get("token_type");
        try {
            mExpiresIn = Long.parseLong(ret.get("expires_in"));
        } catch (Exception e) {
            mExpiresIn = 3600;		// default value
        }

        mErrorCode = OAuthErrorCode.fromString(ret.get("error"));
        mErrorDescription = ret.get("error_description");

        mResultValue = ret.get("result");
    }


    public OAuthResponse(OAuthErrorCode errorCode) {
        mErrorCode = errorCode;
        mErrorDescription = errorCode.getDesc();

    }

    public OAuthResponse(OAuthErrorCode errorCode, String errorDescription) {

        mErrorCode = errorCode;
        mErrorDescription = errorDescription;

    }


    public String getAccessToken() {
        return mAccessToken;
    }

    public String getRefreshToken() {
        return mRefreshToken;
    }

    public long getExpiresIn() {
        return mExpiresIn;
    }

    public String getTokenType() {
        return mTokenType;
    }

    public OAuthErrorCode getErrorCode() {
        return mErrorCode;
    }

    public String getErrorDesc() {
        return mErrorDescription;
    }

    public String getResultValue() {
        return mResultValue;
    }

    public boolean isSuccess() {

        if (TextUtils.isEmpty(mErrorCode.getCode())) {
            if (TextUtils.isEmpty(mAccessToken)) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }


}
