package com.dreamsecurity.oauth.custom.common;

import android.content.Context;
import com.dreamsecurity.oauth.BuildConfig;
import com.dreamsecurity.oauth.custom.util.HttpUtil;
import com.dreamsecurity.oauth.data.HttpResponse;
import com.dreamsecurity.oauth.data.OAuthorizedResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DefaultOAuthConnector extends DefaultHttpConnector {

    private static final String TAG = "DefaultOAuthConnector";
//    public static HttpResponse get(Context context, String parameter, Map<String, String> parameters) {
//        String requestUrl = HttpUtil.generateRequestAccessTokenUrl()
//        return request(context, requestUrl,null);
//    }

    public static OAuthorizedResponse post(String url , JSONObject parameters) {
        return request("POST", url, parameters);
    }

    private static OAuthorizedResponse request(String method , String requestUrl ,JSONObject outputObj ){
        HttpResponse data = DefaultHttpConnector.httpRequest(method, requestUrl, null,false,TIMEOUT,outputObj);


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
            if (null != data && !Logger.isRealVersion()) {
                Logger.d(TAG, "content:" + data.getContent());
            }
            e.printStackTrace();
        }

        return new OAuthorizedResponse(OAuthErrorCode.CLIENT_ERROR_PARSING_FAIL);
    }




}
