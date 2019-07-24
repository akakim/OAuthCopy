package com.dreamsecurity.oauth.custom.common;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * TODO : generateRequestRefreshAccessTokenUrl ,generateRequestDeleteAccessTokenUrl 하드코딩내용 수정하기
 */
public class HttpUtil {

    private static String percentEncode(String s) throws UnsupportedEncodingException {
        if (s == null) {
            return "";
        }
        return URLEncoder.encode(s, "UTF-8")
                // OAuth encodes some characters differently:
                .replace("+", "%20").replace("*", "%2A")
                .replace("%7E", "~");
    }

    public static String getQueryParameter(Map<String, String> paramArray) {
        Set<String> keys = paramArray.keySet();
        StringBuilder query = new StringBuilder("");
        String value;

        for (String key : keys) {
            value = paramArray.get(key);
            if (key == null || value == null) {
                continue;
            }
            if (query.length() > 0) {
                query.append("&");
            }

            query.append(key);
            query.append("=");
            try {
                query.append(percentEncode(value));
            } catch (UnsupportedEncodingException e) {
                //e.printStackTrace();
                query.append(value);
            }

        }

        return query.toString();
    }

    public static String getDecodedString(String oriStr) {

        if (TextUtils.isEmpty(oriStr)) {
            return oriStr;
        }

        String decodedStr = "";
        try {
            decodedStr = URLDecoder.decode(oriStr, "UTF-8");
        } catch (Exception e) {
            // do nothing
        }
        if (!TextUtils.isEmpty(decodedStr)
                && !decodedStr.equalsIgnoreCase(oriStr)) {
            return decodedStr;
        }
        return oriStr;
    }


    public static String generateRequestWebVIewAuthURL(String clientID ,String network,String callbackURL,String version){

        return generateRequestAuthorizationURL(clientID,"web_view",network,callbackURL,version);

    }

    public static String generateRequestCustomTabAuthURL(String clientID , String network, String callbackURL, String version){
        return generateRequestAuthorizationURL(clientID,"custom_tab",network,callbackURL,version);
    }

    public static String generateRequestAuthorizationURL(String clientID ,String inAppType,String network,String callbackURL,String version){

        Map<String,String> resultMap = new HashMap<>();

        resultMap.put( Constant.PARAM_KEY_CLIENT_ID , clientID);
        resultMap.put( Constant.PARAM_KEY_REDIRECT_URI , callbackURL );
        resultMap.put( Constant.PARAM_KEY_GRANT_TYPE , Constant.PARAM_KEY_CODE );

        /* 아래의 3 값은 oauth 규격 외 인자값들 . */
        resultMap.put( Constant.PARAM_NETWORK , network);
        resultMap.put( Constant.PARAM_APP_TYPE , inAppType );
        resultMap.put(Constant.PARAM_OS, "android");
        resultMap.put(Constant.PARAM_VERSION, "android-"+ version);

        return String.format("%s%s", Constant.OAUTH_REQUEST_AUTH_URL , getQueryParameter( resultMap ));
    }

    public static String generateRequestAccessTokenUrl(String clientId, String clientSecret, String state, String code, String locale, String version) {
        Map<String, String> paramArray = new HashMap<String, String>();

        paramArray.put( Constant.PARAM_KEY_CLIENT_ID , clientId );
        paramArray.put( Constant.PARAM_KEY_CLIENT_SECRET, clientSecret );
        paramArray.put( Constant.PARAM_KEY_GRANT_TYPE , Constant.PARAM_VALUE_AUTH);
        paramArray.put( Constant.PARAM_STATE_CODE, state);
        paramArray.put( Constant.PARAM_KEY_CODE, code);
        paramArray.put( Constant.PARAM_OS, "android");
        paramArray.put( Constant.PARAM_VERSION, "android-"+ version);
        paramArray.put( Constant.PARAM_LOCALE_CODE, locale);


        return String.format("%s%s", Constant.OAUTH_REQUEST_ACCESS_TOKEN_URL, getQueryParameter(paramArray));
    }

    public String generateRequestRefreshAccessTokenUrl(String clientId, String clientSecret, String refreshToken, String locale, String version) {

        Map<String, String> paramArray = new HashMap<String, String>();

        paramArray.put( Constant.PARAM_KEY_CLIENT_ID , clientId );
        paramArray.put( Constant.PARAM_KEY_CLIENT_SECRET, clientSecret );
        paramArray.put("grant_type", "refresh_token");
        paramArray.put("refresh_token", refreshToken);
        paramArray.put("oauth_os", "android");
        paramArray.put("version", "android-"+ version);
        paramArray.put("locale", locale);

        return String.format("%s%s", Constant.OAUTH_REQUEST_ACCESS_TOKEN_URL, getQueryParameter(paramArray));
    }




    public String generateRequestDeleteAccessTokenUrl(String clientId, String clientSecret, String accessToken, String locale, String version) {

        Map<String, String> paramArray = new HashMap<String, String>();

        paramArray.put( Constant.PARAM_KEY_CLIENT_ID , clientId );
        paramArray.put( Constant.PARAM_KEY_CLIENT_SECRET, clientSecret );
        paramArray.put("grant_type", "delete");
        paramArray.put("access_token", accessToken);
        paramArray.put("service_provider", "NAVER");
        paramArray.put("oauth_os", "android");
        paramArray.put("version", "android-"+ version);
        paramArray.put("locale", locale);

        return String.format("%s%s", Constant.OAUTH_REQUEST_ACCESS_TOKEN_URL, getQueryParameter(paramArray));
    }



}
