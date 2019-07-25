package com.dreamsecurity.oauth.data;

import com.dreamsecurity.oauth.custom.common.OAuthErrorCode;

import java.util.Map;

public class OAuthorizedResponse {

    private String result;

    private String accessToken;
    private Long expiredIn;

    private String refreshToken;
    private String tokenType;

    private OAuthErrorCode errorCode;
    private String errorDescription;


    public OAuthorizedResponse(){

    }
    // ret 에서 값을 추출하여 위 값들을 정해줌
    public OAuthorizedResponse(Map<String, String> ret) {

        accessToken = ret.get("access_token");
        refreshToken = ret.get("refresh_token");
        tokenType = ret.get("token_type");
        try {
            expiredIn = Long.parseLong(ret.get("expires_in"));
        } catch (Exception e) {
            expiredIn = 3600L;		// default value
        }

        errorCode = OAuthErrorCode.fromString(ret.get("error"));
        errorDescription = ret.get("error_description");

        result   = ret.get("result");
    }

    public String getResult() {
        return result;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Long getExpiredIn() {
        return expiredIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public OAuthErrorCode getErrorCode() {
        return errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setExpiredIn(Long expiredIn) {
        this.expiredIn = expiredIn;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public void setErrorCode(OAuthErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }
}
