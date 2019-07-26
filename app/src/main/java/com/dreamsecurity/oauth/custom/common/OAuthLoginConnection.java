package com.dreamsecurity.oauth.custom.common;

import android.content.Context;
import com.dreamsecurity.oauth.BuildConfig;
import com.dreamsecurity.oauth.custom.CustomTabOAuthPresenter;
import com.dreamsecurity.oauth.custom.util.HttpUtil;
import com.dreamsecurity.oauth.data.HttpResponse;
import com.dreamsecurity.oauth.data.OAuthorizedResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class OAuthLoginConnection extends CommonConnection {

    private static final String TAG = "OAuthLoginConnection";
    /**
     * access token 요청
     * @param context context
     * @param clientId client id
     * @param clientSecret client secret
     * @param initState oauth2.0 에서의 state 값 (random seed)
     * @param code oauth2.0 에서의 code 값  (intermidate auth code)
     * @return oauth response
     */
    public static OAuthorizedResponse requestAccessToken(Context context, String clientId, String clientSecret, String initState, String code,String callback) {

        String requestUrl = generateRequestAccessTokenUrl(clientId, clientSecret, initState, code, BuildConfig.VERSION_NAME,callback);
        return request(context, requestUrl);
    }


    public static OAuthorizedResponse requestRefreshToken(Context context, String clientId, String clientSecret, String refreshToken) {

        String requestUrl = generateRequestRefreshAccessTokenUrl(clientId, clientSecret, refreshToken, BuildConfig.VERSION_NAME);
        return request(context, requestUrl);
    }

    public static OAuthorizedResponse deleteToken(Context context, String clientId, String clientSecret, String accessToken) {

        String requestUrl = generateRequestDeleteAccessTokenUrl(clientId, clientSecret, accessToken, BuildConfig.VERSION_NAME);
        return request(context, requestUrl);
    }


    private static OAuthorizedResponse request(Context context, String requestUrl) {
        HttpResponse data = CommonConnection.request(context, requestUrl, null,"Android_Native");


        if (!(data.getStat().equals(HttpResponse.ResponseDataStat.SUCCESS))) {
            if (data.getStatusCode() == 503) {
                return new OAuthorizedResponse(OAuthErrorCode.SERVER_ERROR_TEMPORARILY_UNAVAILABLE);
            } else if (data.getStatusCode() == 500) {
                return new OAuthorizedResponse(OAuthErrorCode.SERVER_ERROR_SERVER_ERROR);
            } else if (data.getStat().equals(HttpResponse.ResponseDataStat.CLIENT_ACTION_CONNECTION_TIMEOUT)
                    || data.getStat().equals(HttpResponse.ResponseDataStat.CLIENT_ACTION_CONNECTION_FAIL)){
                return new OAuthorizedResponse(OAuthErrorCode.CLIENT_ERROR_CONNECTION_ERROR);
            } else if (data.getStat().equals(HttpResponse.ResponseDataStat.CLIENT_ACTION_NO_PEER_CERTIFICATE)){
                return new OAuthorizedResponse(OAuthErrorCode.CLIENT_ERROR_CERTIFICATION_ERROR);
            } else {
                return new OAuthorizedResponse(OAuthErrorCode.ERROR_NO_CATAGORIZED);
            }
        }

        try {
            JSONObject jsonObj = new JSONObject(data.getContent());

            if (!Logger.isRealVersion()) {
                Logger.d(TAG, "len :" + jsonObj.length());
                Logger.d(TAG, "str :" + jsonObj.toString());
            }

            Iterator it = jsonObj.keys();
            Map<String, String> ret = new HashMap<String, String>();

            while (it.hasNext()) {
                String key = (String) it.next();
                String value = jsonObj.getString(key);
                ret.put(key, value);

                if (!Logger.isRealVersion()) {
                    Logger.d(TAG, "key:" + key + ",value:" + value);
                }
            }

            return new OAuthorizedResponse(ret);

        } catch (JSONException e) {
            if (!Logger.isRealVersion() && null != data) {
                Logger.d(TAG, "content:" + data.getContent());
            }
            e.printStackTrace();
        }

        return new OAuthorizedResponse(OAuthErrorCode.CLIENT_ERROR_PARSING_FAIL);
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
    public String generateRequestWebViewAuthorizationUrl(String clientId, String state, String callbackUrl, String locale, String network, String version) {
        return String.format("%s%s", Constant.OAUTH_REQUEST_AUTH_URL,
                HttpUtil.getQueryParameter(newAuthorizationParamMap(clientId, state, callbackUrl, locale, network, "true", version)));
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
    public String generateRequestCustomTabAuthorizationUrl(String clientId, String state, String appPackageName, String locale, String network, String version) {
        return String.format("%s%s", Constant.OAUTH_REQUEST_AUTH_URL,
                HttpUtil.getQueryParameter(newAuthorizationParamMap(clientId, state, appPackageName, locale, network, "custom_tab", version)));
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
    private Map<String, String> newAuthorizationParamMap(String clientId, String state, String callbackUrl, String locale, String network, String inAppType, String version) {
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


        if (CustomTabOAuthPresenter.CUSTOM_TAB_REQUIRED_RE_AUTH) {
            paramArray.put("auth_type", "reauthenticate");
        }

        return paramArray;
    }

    public static String generateRequestAccessTokenUrl(String clientId, String clientSecret, String state, String code, String version,String callback) {
        Map<String, String> paramArray = new HashMap<String, String>();

        paramArray.put("client_id", clientId);
        paramArray.put("client_secret", clientSecret);
        paramArray.put("grant_type", "authorization_code");
        paramArray.put("state", state);
        paramArray.put("code", code);
        paramArray.put("oauth_os", "android");
        paramArray.put("version", "android-"+ version);
        paramArray.put("redirect_uri", callback);



        return String.format("%s%s", Constant.OAUTH_REQUEST_ACCESS_TOKEN_URL, HttpUtil.getQueryParameter(paramArray));
    }

    public static String generateRequestRefreshAccessTokenUrl(String clientId, String clientSecret, String refreshToken, String version) {

        Map<String, String> paramArray = new HashMap<String, String>();

        paramArray.put("client_id", clientId);
        paramArray.put("client_secret", clientSecret);
        paramArray.put("grant_type", "refresh_token");
        paramArray.put("refresh_token", refreshToken);
        paramArray.put("oauth_os", "android");
        paramArray.put("version", "android-"+ version);


        return String.format("%s%s", Constant.OAUTH_REQUEST_ACCESS_TOKEN_URL, HttpUtil.getQueryParameter(paramArray));
    }

    public static String generateRequestDeleteAccessTokenUrl(String clientId, String clientSecret, String accessToken, String version) {

        Map<String, String> paramArray = new HashMap<String, String>();

        paramArray.put("client_id", clientId);
        paramArray.put("client_secret", clientSecret);
        paramArray.put("grant_type", "delete");
        paramArray.put("access_token", accessToken);
        paramArray.put("service_provider", "NAVER");
        paramArray.put("oauth_os", "android");
        paramArray.put("version", "android-"+ version);


        return String.format("%s%s", Constant.OAUTH_REQUEST_ACCESS_TOKEN_URL, HttpUtil.getQueryParameter(paramArray));
    }

}
