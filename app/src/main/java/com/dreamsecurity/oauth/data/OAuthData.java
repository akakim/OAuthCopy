package com.dreamsecurity.oauth.data;

import com.dreamsecurity.oauth.custom.common.OAuthErrorCode;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.SecureRandom;

public class OAuthData {
    private static final String TAG = "OAuthData";

    private String inClientId;
    private String inClientSecret;
    private String inRedirectURIk;
    private String inState;

    private String 			currentState;
    private String 			code;
    private OAuthErrorCode  errorCode;
    private String 			errorDesc;


    public OAuthData(String inClientId, String inClientSecret, String inRedirectURIk) {

        this ( inClientId, inClientSecret,inRedirectURIk, null);
    }

    public OAuthData(String inClientId, String inClientSecret, String inRedirectURIk, String inState) {
        this.inClientId = inClientId;
        this.inClientSecret = inClientSecret;
        this.inRedirectURIk = inRedirectURIk;
        this.inState = inState;
    }

    public String generateState() {
        SecureRandom random = new SecureRandom();

        String state = new BigInteger(130,random).toString(32);

        try{
            return URLEncoder.encode(state,"UTF-8");
        } catch (UnsupportedEncodingException e){
            return state;
        }
    }
}
