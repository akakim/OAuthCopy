package com.dreamsecurity.ssooauth.magiclogin;

import android.content.Context;
import com.dreamsecurity.ssooauth.common.connect.CommonConnect;
import com.dreamsecurity.ssooauth.common.connect.ResponseData;
import com.dreamsecurity.ssooauth.common.logger.Logger;
import com.dreamsecurity.ssooauth.magiclogin.data.OAuthErrorCode;
import com.dreamsecurity.ssooauth.magiclogin.data.OAuthResponse;
import com.dreamsecurity.ssooauth.util.DeviceAppInfo;
import com.dreamsecurity.ssooauth.util.OAuthQueryUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class OAuthLoginConnection extends CommonConnect {

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
    public static OAuthResponse requestAccessToken(Context context, String clientId, String clientSecret, String initState, String code) {
        String locale = DeviceAppInfo.getInstance().getLocaleString(context);
        String requestUrl = OAuthQueryUtil.generateRequestAccessTokenUrl(clientId, clientSecret, initState, code, locale, OAuthLoginDefine.VERSION);
        return request(context, requestUrl);
    }


    public static OAuthResponse requestRefreshToken(Context context, String clientId, String clientSecret, String refreshToken) {
        String locale = DeviceAppInfo.getInstance().getLocaleString(context);
        String requestUrl =OAuthQueryUtil.generateRequestRefreshAccessTokenUrl(clientId, clientSecret, refreshToken, locale, OAuthLoginDefine.VERSION);
        return request(context, requestUrl);
    }

    public static OAuthResponse deleteToken(Context context, String clientId, String clientSecret, String accessToken) {
        String locale = DeviceAppInfo.getInstance().getLocaleString(context);
        String requestUrl = OAuthQueryUtil.generateRequestDeleteAccessTokenUrl(clientId, clientSecret, accessToken, locale, OAuthLoginDefine.VERSION);
        return request(context, requestUrl);
    }

    private static OAuthResponse request(Context context, String requestUrl) {
        ResponseData data = CommonConnect.request(context, requestUrl, null, null);

        if (!(data.getStat().equals(ResponseData.ResponseDataStat.SUCCESS))) {

            if (data.getStatusCode() == 503) {
                return new OAuthResponse(OAuthErrorCode.SERVER_ERROR_TEMPORARILY_UNAVAILABLE);
            } else if (data.getStatusCode() == 500) {
                return new OAuthResponse(OAuthErrorCode.SERVER_ERROR_SERVER_ERROR);
            } else if (data.getStat().equals(ResponseData.ResponseDataStat.CONNECTION_TIMEOUT)
                    || data.getStat().equals(ResponseData.ResponseDataStat.CONNECTION_FAIL)){
                return new OAuthResponse(OAuthErrorCode.CLIENT_ERROR_CONNECTION_ERROR);
            } else if (data.getStat().equals(ResponseData.ResponseDataStat.NO_PEER_CERTIFICATE)){
                return new OAuthResponse(OAuthErrorCode.CLIENT_ERROR_CERTIFICATION_ERROR);
            } else {
                return new OAuthResponse(OAuthErrorCode.ERROR_NO_CATAGORIZED);
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

            return new OAuthResponse(ret);

        } catch (JSONException e) {
            if (!Logger.isRealVersion() && null != data) {
                Logger.d(TAG, "content:" + data.getContent());
            }
            e.printStackTrace();
        }

        return new OAuthResponse(OAuthErrorCode.CLIENT_ERROR_PARSING_FAIL);

    }

    /**
     * naver id 에 해당하는 token 과 token secret 을 얻어와 OAuth1.0a 의 인증을 위한 헤더를 포함하여 요청한다.
     * @param context context
     * @param requestUrl request url
     * @param cookie cookie
     * @param userAgent user-agent string
     * @param authHeader Authorization Header string
     * @return response data
     */
    public static ResponseData requestWithOAuthHeader(Context context, String requestUrl, String cookie, String userAgent, String authHeader) {
        ResponseData data = CommonConnect.request(context, requestUrl, null, null, authHeader, false);
        return data;
    }
}
