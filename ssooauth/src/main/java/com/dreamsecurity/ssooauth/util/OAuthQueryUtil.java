package com.dreamsecurity.ssooauth.util;

import com.dreamsecurity.ssooauth.magiclogin.OAuthLoginDefine;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class OAuthQueryUtil {
    private static final String TAG = "OAuthQueryGenerator";

    private final static String OAUTH_REQUEST_AUTH_URL = "https://nid.naver.com/oauth2.0/authorize?";
    private final static String OAUTH_REQUEST_ACCESS_TOKEN_URL = "https://nid.naver.com/oauth2.0/token?";

    private static String percentEncode(String s) throws UnsupportedEncodingException {
        if (s == null) {
            return "";
        }
        return URLEncoder.encode(s, "UTF-8")
                // OAuth encodes some characters differently:
                .replace("+", "%20").replace("*", "%2A")
                .replace("%7E", "~");
    }

    protected static String getQueryParameter(Map<String, String> paramArray) {
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

    /**
     * webview에서 인증 시작할때 쓸 url 을 만듬
     *
     * @param clientId    client id
     * @param state       OAuth2.0 에서 쓰이는 state string (random seed)
     * @param callbackUrl 완료 후 돌아갈 url
     * @param locale      언어값
     * @return generated url
     */
    public static String generateRequestWebViewAuthorizationUrl(String clientId, String state, String callbackUrl, String locale) {
        return generateRequestWebViewAuthorizationUrl(clientId, state, callbackUrl, locale, null, OAuthLoginDefine.VERSION);
    }

    /**
     * webview에서 인증 시작할때 쓸 url 을 만듬
     *
     * @param clientId    client id
     * @param state       OAuth2.0 에서 쓰이는 state string (random seed)
     * @param callbackUrl 완료 후 돌아갈 url
     * @param locale      언어값
     * @param network     네트워크 상태(wifi, 3g ...)
     * @return generated url
     */
    public static String generateRequestWebViewAuthorizationUrl(String clientId, String state, String callbackUrl, String locale, String network, String version) {
        return String.format("%s%s", OAUTH_REQUEST_AUTH_URL,
                getQueryParameter(newAuthorizationParamMap(clientId, state, callbackUrl, locale, network, "true", version)));
    }

    /**
     * 커스텀 탭에서 인증 시작할때 쓸 url 을 만듬
     *
     * @param clientId    client id
     * @param state       OAuth2.0 에서 쓰이는 state string (random seed)
     * @param appPackageName 앱의 패키지 명
     * @param locale      언어값
     * @param network     네트워크 상태(wifi, 3g ...)
     * @return generated url
     */
    public static String generateRequestCustomTabAuthorizationUrl(String clientId, String state, String appPackageName, String locale, String network, String version) {
        return String.format("%s%s", OAUTH_REQUEST_AUTH_URL,
                getQueryParameter(newAuthorizationParamMap(clientId, state, appPackageName, locale, network, "custom_tab", version)));
    }

    /**
     * 파라메터 맵을 생성한다.
     * @param clientId    client id
     * @param state       OAuth2.0 에서 쓰이는 state string (random seed)
     * @param callbackUrl 완료 후 돌아갈 url
     * @param locale      언어값
     * @param network     네트워크 상태(wifi, 3g ...)
     * @param inAppType   요청의 주체 (웹뷰 "true", 커스텀 탭 "custom_tab")
     * @param version     현재 sdk 버전
     * @return 해당 정보들이 포함된 해시맵 객체
     */
    private static Map<String, String> newAuthorizationParamMap(String clientId, String state, String callbackUrl, String locale, String network, String inAppType, String version) {
        Map<String, String> paramArray = new HashMap<>();
        paramArray.put("client_id", clientId);
        paramArray.put("inapp_view", inAppType);
        paramArray.put("response_type", "code");
        paramArray.put("oauth_os", "android");
        paramArray.put("version", "android-"+ version);
        if (null != network) {
            paramArray.put("network", network);
        }
        paramArray.put("locale", locale);
        paramArray.put("redirect_uri", callbackUrl);        // getQueryParameter 에서 encoding 됨. 2014.11.27일 강병국님 메일로 수정됨
        paramArray.put("state", state);


        if (OAuthLoginDefine.CUSTOM_TAB_REQUIRED_RE_AUTH) {
            paramArray.put("auth_type", "reauthenticate");
        }

        return paramArray;
    }

    public static String generateRequestAccessTokenUrl(String clientId, String clientSecret, String state, String code, String locale, String version) {
        Map<String, String> paramArray = new HashMap<String, String>();

        paramArray.put("client_id", clientId);
        paramArray.put("client_secret", clientSecret);
        paramArray.put("grant_type", "authorization_code");
        paramArray.put("state", state);
        paramArray.put("code", code);
        paramArray.put("oauth_os", "android");
        paramArray.put("version", "android-"+ version);
        paramArray.put("locale", locale);


        return String.format("%s%s", OAUTH_REQUEST_ACCESS_TOKEN_URL, getQueryParameter(paramArray));
    }

    public static String generateRequestRefreshAccessTokenUrl(String clientId, String clientSecret, String refreshToken, String locale, String version) {

        Map<String, String> paramArray = new HashMap<String, String>();

        paramArray.put("client_id", clientId);
        paramArray.put("client_secret", clientSecret);
        paramArray.put("grant_type", "refresh_token");
        paramArray.put("refresh_token", refreshToken);
        paramArray.put("oauth_os", "android");
        paramArray.put("version", "android-"+ version);
        paramArray.put("locale", locale);

        return String.format("%s%s", OAUTH_REQUEST_ACCESS_TOKEN_URL, getQueryParameter(paramArray));
    }

    public static String generateRequestDeleteAccessTokenUrl(String clientId, String clientSecret, String accessToken, String locale, String version) {

        Map<String, String> paramArray = new HashMap<String, String>();

        paramArray.put("client_id", clientId);
        paramArray.put("client_secret", clientSecret);
        paramArray.put("grant_type", "delete");
        paramArray.put("access_token", accessToken);
        paramArray.put("service_provider", "NAVER");
        paramArray.put("oauth_os", "android");
        paramArray.put("version", "android-"+ version);
        paramArray.put("locale", locale);

        return String.format("%s%s", OAUTH_REQUEST_ACCESS_TOKEN_URL, getQueryParameter(paramArray));
    }

}
