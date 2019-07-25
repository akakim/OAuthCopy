package com.dreamsecurity.oauth.custom;

import android.content.Context;
import com.dreamsecurity.oauth.custom.common.Logger;
import com.dreamsecurity.oauth.custom.common.NetworkStatus;
import com.dreamsecurity.oauth.custom.common.OAuthErrorCode;
import com.dreamsecurity.oauth.data.OAuthorizedResponse;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class CommonConnection {

    private static final String TAG = "CommonConnection";

    protected static HttpURLConnection mHttpUrlConnection = null;

    // when user-cancel, this is set.
    protected static boolean mCancel;

    // network timeout
    public static int 			TIMEOUT = 10000;

    /**
     * URL 로 request 후 reponse로 ResponseData를 리턴
     * @param context context
     * @param strRequestUrl request url
     * @param cookies cookie string
     * @param userAgent useragent
     * @return : url request 로 얻은 response data를 리턴. response data는 content 와 status-code, cookie 로 구성됨
     */
    public static OAuthorizedResponse request(Context context, String strRequestUrl, String cookies, String userAgent) {
        return request(context, strRequestUrl, cookies, userAgent, false);
    }


    public static OAuthorizedResponse request(Context context, String strRequestUrl, String cookies, String userAgent, String authHeader) {
        return request(context, strRequestUrl, cookies, userAgent, authHeader, false, TIMEOUT);
    }

    public static OAuthorizedResponse request(Context context, String strRequestUrl, String cookies, String userAgent, boolean httpClientIsolated) {
        return request(context, strRequestUrl, cookies, userAgent, null, httpClientIsolated, TIMEOUT);
    }

    public static OAuthorizedResponse request(Context context, String strRequestUrl, String cookies, String userAgent, String authHeader, boolean httpClientIsolated) {
        return request(context, strRequestUrl, cookies, userAgent, authHeader, httpClientIsolated, TIMEOUT);
    }

    /*
     * get 방식의 login 관련 request 를 해주는 메쏘드
     */
    public static OAuthorizedResponse request(Context context, String strRequestUrl, String cookies, String userAgent, String authHeader, boolean httpClientIsolated, int timeout) {

        OAuthorizedResponse res = new OAuthorizedResponse();
        List<String> postCookies = new ArrayList<String>();

        HttpsURLConnection httpClient = null;
        // get 방식은 output 없음
        //OutputStream outputStream = null;

        synchronized(CommonConnection.class) {

            if (httpClientIsolated) {

            } else {
                if (mHttpUrlConnection != null) {
                    res.setErrorCode(OAuthErrorCode.CLIENT_ACTION_BUSY);
                    return res;
                }
            }

            if (!Logger.isRealVersion()) {
                Logger.d(TAG, "request url : " + strRequestUrl);
            }

            if (strRequestUrl == null || strRequestUrl.length() == 0) {
                res.setErrorCode( , "strRequestUrl is null");
                return res;
            }

            try {

                // HttpClient 설정
                if (httpClientIsolated) {
                    if (userAgent != null && userAgent.length() > 0) {
                        httpClient = getDefaultHttpsConnection("GET", strRequestUrl, userAgent, timeout);
                    } else {
                        httpClient = getDefaultHttpsConnection("GET", strRequestUrl, context, timeout);
                    }
                } else {
                    if (userAgent != null && userAgent.length() > 0) {
                        mHttpUrlConnection = getDefaultHttpsConnection("GET", strRequestUrl, userAgent, timeout);
                    } else {
                        mHttpUrlConnection = getDefaultHttpsConnection("GET", strRequestUrl, context, timeout);
                    }
                }
            } catch (MalformedURLException e) {
                res.setErrorCode(OAuthErrorCode.CLIENT_ACTION_URL_ERROR);
                e.printStackTrace();
                return res;
            } catch (IOException e) {
                res.setErrorCode(OAuthErrorCode.CLIENT_ACTION_CONNECTION_FAIL);
                e.printStackTrace();
                return res;
            } catch (Exception e) {
                res.setErrorCode(OAuthErrorCode.CLIENT_ACTION_EXCEPTION_FAIL);
                Logger.e(TAG, "exception step : connection establishing");
                e.printStackTrace();
                return res;
            }

            mCancel = false;
        }

        // cookie 값 설정
        /*if (cookies == null || cookies.length() == 0) {
            cookies = CookieUtil.getAllNidCookie();
            Logger.d(TAG, "request() --- request with naverCookie!");
            if (!Logger.isRealVersion()) {
                Logger.d(TAG, "request() --- " + cookies);
            }
        } else {
            Logger.d(TAG, "request() --- request with user Cookie!");
            if (!Logger.isRealVersion()) {
                Logger.d(TAG, "request() --- " + cookies);
            }
        }*/

        try {
            if (httpClientIsolated) {
                if (null != cookies && cookies.length() > 0) {
                    httpClient.setRequestProperty("Cookie", cookies);
                }
                if (null != authHeader && authHeader.length() > 0) {
                    httpClient.setRequestProperty("Authorization", authHeader);
                }

                int responseCode = httpClient.getResponseCode();
                Logger.i(TAG, "response status code:" + responseCode);

                postCookies = CookieUtil.getCookieUpperSDK23(httpClient.getHeaderFields());
                String contentType = HttpConnectionUtil.getCharsetFromContentTypeHeader(httpClient.getHeaderFields());

                InputStream in = null;
                try {
                    in = httpClient.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                    in = httpClient.getErrorStream();
                }

                res.setResponseData(responseCode, contentType, in, postCookies);

            } else {
                if (null != cookies && cookies.length() > 0) {
                    mHttpUrlConnection.setRequestProperty("Cookie", cookies);
                }
                if (null != authHeader && authHeader.length() > 0) {
                    mHttpUrlConnection.setRequestProperty("Authorization", authHeader);
                }

                int responseCode = mHttpUrlConnection.getResponseCode();
                Logger.i(TAG, "response status code:" + responseCode);

                postCookies = CookieUtil.getCookieUpperSDK23(mHttpUrlConnection.getHeaderFields());
                String contentType = HttpConnectionUtil.getCharsetFromContentTypeHeader(mHttpUrlConnection.getHeaderFields());

                InputStream in = null;
                try {
                    in = mHttpUrlConnection.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                    in = mHttpUrlConnection.getErrorStream();
                }

                res.setResponseData(responseCode, contentType, in, postCookies);
            }
        } catch (SSLPeerUnverifiedException e) {
            res.setErrorCode( OAuthErrorCode.CLIENT_ACTION_NO_PEER_CERTIFICATE );
            e.printStackTrace();
        } catch (SSLProtocolException e) {
            res.setErrorCode( OAuthErrorCode.CLIENT_ACTION_NO_PEER_CERTIFICATE);
            e.printStackTrace();
        } catch (SSLKeyException e) {
            res.setErrorCode( OAuthErrorCode.CLIENT_ACTION_NO_PEER_CERTIFICATE);
            e.printStackTrace();
        } catch (SSLHandshakeException e) {
            res.setErrorCode( OAuthErrorCode.CLIENT_ACTION_NO_PEER_CERTIFICATE);
            e.printStackTrace();
        } catch (SSLException e) {
            res.setErrorCode( OAuthErrorCode.CLIENT_ACTION_NO_PEER_CERTIFICATE );
            e.printStackTrace();
        } catch (SocketTimeoutException e) {
            res.setErrorCode( OAuthErrorCode.CLIENT_ACTION_CONNECTION_TIMEOUT);
            e.printStackTrace();
        } catch (SocketException e) {
            res.setErrorCode( OAuthErrorCode.CLIENT_ACTION_CONNECTION_FAIL );
            e.printStackTrace();
        } catch (IOException e) {
            res.setErrorCode( OAuthErrorCode.CLIENT_ACTION_IO_FAIL);
            e.printStackTrace();
        } catch (Exception e) {
            res.setErrorCode( OAuthErrorCode.CLIENT_ACTION_EXCEPTION_FAIL );
            e.printStackTrace();
        }

        try {
            if (httpClientIsolated) {
                httpClient.disconnect();
            } else {
                mHttpUrlConnection.disconnect();
            }
        } catch (Exception e) {
            Logger.write(e);
        } finally {
            if (httpClientIsolated) {
                httpClient = null;
            } else {
                mHttpUrlConnection = null;
            }
        }

        if (mCancel) {
            ResponseData cc = new ResponseData();
            cc.setResultCode(ResponseData.ResponseDataStat.CANCEL, "User cancel");
            return cc;
        }

        try {
            CookieUtil.setCookie(strRequestUrl, postCookies);
        } catch (Exception e) {
            res.setResultCode(ResponseData.ResponseDataStat.FAIL, "setCookie() failed :" + e.getMessage());
            Logger.write(e);
        }
        return res;
    }

    /**
     * 로그인 모듈 user-agent 가 설정된 http client 를 리턴
     * @throws IOException
     * @throws MalformedURLException
     */
    public static HttpsURLConnection getDefaultHttpsConnection(String method, String url, Context context, int timeout) throws MalformedURLException, IOException {
        String useragent = DeviceAppInfo.getUserAgent(context);
        return getDefaultHttpsConnection(method, url, useragent, timeout);
    }

    private static HttpsURLConnection getDefaultHttpsConnection(String method, String url, String userAgent, int timeout) throws MalformedURLException, IOException {
        return (HttpsURLConnection) getDefaultHttpConnection(method, url, userAgent, timeout);
    }

    public static HttpURLConnection getDefaultHttpConnection(String method, String url, Context context, int timeout) throws MalformedURLException, IOException {
        String useragent = DeviceAppInfo.getUserAgent(context);
        return getDefaultHttpConnection(method, url, useragent, timeout);
    }

    private static HttpURLConnection getDefaultHttpConnection(String method, String url, String userAgent, int timeout) throws MalformedURLException, IOException {
        HttpURLConnection urlConn = (HttpURLConnection) (new URL(url)).openConnection();

        urlConn.setDefaultUseCaches(false);
        urlConn.setUseCaches(false);

        urlConn.setRequestMethod(method);
        urlConn.setRequestProperty("User-Agent", userAgent);
        // TODO 파일 다운로드시엔 다른거 사용해야함. urlConn.setRequestProperty("Content-Type", "Application/xml");

        urlConn.setReadTimeout(timeout);
        urlConn.setConnectTimeout(timeout);

        urlConn.setDoInput(true);

        if ("GET".equalsIgnoreCase(method)) {
            urlConn.setDoOutput(false);
        } else {
            urlConn.setDoOutput(true);
        }

        return urlConn;
    }
    public static boolean isBusy() {
        if (mHttpUrlConnection != null)
            return true;
        return false;
    }

    public static void cancel() {
        mCancel = true;
        if (mHttpUrlConnection != null) {
            Logger.e(TAG, "cancel() https-connection shutdown");
            mHttpUrlConnection.disconnect();
            mHttpUrlConnection = null;
        }
        // executor 는 cancel 안해줌 -> 동작이 복잡해짐
    }
}
