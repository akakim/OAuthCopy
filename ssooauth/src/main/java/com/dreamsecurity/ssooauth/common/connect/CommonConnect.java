package com.dreamsecurity.ssooauth.common.connect;


import android.content.Context;
import com.dreamsecurity.ssooauth.common.logger.Logger;
import com.dreamsecurity.ssooauth.magiclogin.OAuthLoginDefine;
import com.dreamsecurity.ssooauth.util.HttpConnectionUtil;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 쿠키 부분은 기본적으로 Minsdk가 21이 넘기 때문에 삭제.
 *
 * 서버에서의 request 시 사용할 수 있는 class
 */
public class CommonConnect {

    private static final String TAG = "CommonConnection";
    protected static HttpURLConnection httpURLConnection = null;



    protected static boolean cancel =  false;

    public CommonConnect() {
    }

    /**
     * URL 로 request 후 reponse로 ResponseData를 리턴
     * @param context context
     * @param strRequestUrl request url
     * @param cookies cookie string
     * @param userAgent useragent
     * @return : url request 로 얻은 response data를 리턴. response data는 content 와 status-code, cookie 로 구성됨
     */
    public static ResponseData request(Context context, String strRequestUrl, String cookies, String userAgent) {
        return request(context, strRequestUrl, cookies, userAgent, false);
    }


    public static ResponseData request(Context context, String strRequestUrl, String cookies, String userAgent, String authHeader) {
        return request(context, strRequestUrl, cookies, userAgent, authHeader, false, OAuthLoginDefine.TIMEOUT);
    }

    public static ResponseData request(Context context, String strRequestUrl, String cookies, String userAgent, boolean httpClientIsolated) {
        return request(context, strRequestUrl, cookies, userAgent, null, httpClientIsolated, OAuthLoginDefine.TIMEOUT);
    }

    public static ResponseData request(Context context, String strRequestUrl, String cookies, String userAgent, String authHeader, boolean httpClientIsolated) {
        return request(context, strRequestUrl, cookies, userAgent, authHeader, httpClientIsolated, OAuthLoginDefine.TIMEOUT);
    }

    public static ResponseData request(Context context,String strRequestUrl,String cookies,String userAgent,String authHeader, boolean httpClientIsolated,int timeout){

        ResponseData res = new ResponseData();
        List<String> postCookies = new ArrayList<>();


        HttpsURLConnection httpClient = null;
        // get 방식은 output이 없음 .

        synchronized (CommonConnect.class){

            if( !httpClientIsolated ){

                if( httpURLConnection != null ){
                    res.setResultCode( ResponseData.ResponseDataStat.BUSY,"HttpClient already in use.");
                }
            }

            if( !Logger.isRealVersion()){
                Logger.d(TAG, "request url : " + strRequestUrl);
            }

            if (strRequestUrl == null || strRequestUrl.length() == 0) {
                res.setResultCode(ResponseData.ResponseDataStat.URL_ERROR, "strRequestUrl is null");
                return res;
            }

            try{
                if( httpClientIsolated){

                    if(userAgent != null && userAgent.length() > 0 ){
                        httpClient = getDefaultHttpsConnection("GET", strRequestUrl, userAgent, timeout);
                    }else {
                        httpClient = getDefaultHttpsConnection("GET", strRequestUrl, userAgent, timeout);
                    }
                }else {
                    if( userAgent !=null && userAgent.length() > 0){
                        httpURLConnection = getDefaultHttpsConnection("GET", strRequestUrl, userAgent, timeout);
                    }else {
                        httpURLConnection = getDefaultHttpsConnection("GET", strRequestUrl, context, timeout);
                    }
                }
            }catch ( MalformedURLException e ){
                res.setResultCode( ResponseData.ResponseDataStat.URL_ERROR,"malformed url : " + e.getMessage() );
                e.printStackTrace();
                return res;
            } catch (IOException e ){
                res.setResultCode(ResponseData.ResponseDataStat.CONNECTION_FAIL, "connection open fail : " + e.getMessage());
                e.printStackTrace();
                return res;

            }catch (Exception e ){
                res.setResultCode(ResponseData.ResponseDataStat.EXCEPTION_FAIL, "unknown fail : " + e.getMessage());
                Logger.e(TAG, "exception step : connection establishing");
                e.printStackTrace();
                return res;
            }

            cancel = false;
        }
        // Cookie 유틸 삭제 . WebView


        try{

            if(httpClientIsolated){
                if( null != cookies && cookies.length() >0 ){

                    httpClient.setRequestProperty( "Cookie",cookies);

                }

                if( null != authHeader && authHeader.length() > 0){
                    httpClient.setRequestProperty("Authorization", authHeader);
                }

                int responseCode = httpClient.getResponseCode();
                String contentType = HttpConnectionUtil.getCharsetFromContenttypeHeader(httpClient.getHeaderFields());

                InputStream in = null;
                try {
                    in = httpClient.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                    in = httpClient.getErrorStream();
                }

                res.setResponseData(responseCode, contentType, in, postCookies);


            }
        }catch (SSLPeerUnverifiedException e) {
            res.setResultCode(ResponseData.ResponseDataStat.NO_PEER_CERTIFICATE, "SSLPeerUnverifiedException : " + e.getMessage());
            e.printStackTrace();
        } catch (SSLProtocolException e) {
            res.setResultCode(ResponseData.ResponseDataStat.NO_PEER_CERTIFICATE, "SSLProtocolException : " + e.getMessage());
            e.printStackTrace();
        } catch (SSLKeyException e) {
            res.setResultCode(ResponseData.ResponseDataStat.NO_PEER_CERTIFICATE, "SSLKeyException : " + e.getMessage());
            e.printStackTrace();
        } catch (SSLHandshakeException e) {
            res.setResultCode(ResponseData.ResponseDataStat.NO_PEER_CERTIFICATE, "SSLHandshakeException : " + e.getMessage());
            e.printStackTrace();
        } catch (SSLException e) {
            res.setResultCode(ResponseData.ResponseDataStat.NO_PEER_CERTIFICATE, "SSLException : " + e.getMessage());
            e.printStackTrace();
        } catch (SocketTimeoutException e) {
            res.setResultCode(ResponseData.ResponseDataStat.CONNECTION_TIMEOUT, "SocketTimeoutException : " + e.getMessage());
            e.printStackTrace();
        } catch (SocketException e) {
            res.setResultCode(ResponseData.ResponseDataStat.CONNECTION_FAIL, "SocketException : " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            res.setResultCode(ResponseData.ResponseDataStat.EXCEPTION_FAIL, "IOException : " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            res.setResultCode(ResponseData.ResponseDataStat.EXCEPTION_FAIL, "Exception : " + e.getMessage());
            e.printStackTrace();
        }

        try {
            if (httpClientIsolated) {
                httpClient.disconnect();
            } else {
                httpURLConnection.disconnect();
            }
        } catch (Exception e) {
            Logger.write(e);
        } finally {
            if (httpClientIsolated) {
                httpClient = null;
            } else {
                httpURLConnection = null;
            }
        }

        if( cancel ){
            ResponseData cc = new ResponseData();
            cc.setResultCode( ResponseData.ResponseDataStat.CANCEL,"User Cancel");
            return cc;
        }

        return res;
    }

    /**
     * 로그인 모듈 user-agent 가 설정된 http client 를 리턴
     * @throws IOException
     * @throws MalformedURLException
     */
    public static HttpsURLConnection getDefaultHttpsConnection(String method, String url, Context context, int timeout) throws MalformedURLException, IOException {
        //  String useragent = DeviceAppInfo.getUserAgent(context);
        return getDefaultHttpsConnection(method, url, "", timeout);
    }

    private static HttpsURLConnection getDefaultHttpsConnection(String method, String url, String userAgent, int timeout) throws MalformedURLException, IOException {
        return (HttpsURLConnection) getDefaultHttpConnection(method, url, "", timeout);
    }

    public static HttpURLConnection getDefaultHttpConnection(String method, String url, Context context, int timeout) throws MalformedURLException, IOException {
//        String useragent = DeviceAppInfo.getUserAgent(context);
        return getDefaultHttpConnection(method, url, "", timeout);
    }

    private static HttpURLConnection getDefaultHttpConnection( String method,String url, String userAgent,int timeout) throws MalformedURLException, IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();

        // 보안상 Http 요청의 캐쉬는 사용하지 않는다.
        urlConnection.setDefaultUseCaches( false );
        urlConnection.setUseCaches( false );

        urlConnection.setRequestMethod( method );
        urlConnection.setRequestProperty("User-Agent",userAgent ); // 파일 다운로드시 다른걸 사용해야함.

        urlConnection.setReadTimeout( timeout );
        urlConnection.setConnectTimeout( timeout );

        urlConnection.setDoInput( true );

        if("GET".equalsIgnoreCase( method )){
            urlConnection.setDoOutput( false );
        } else {
            urlConnection.setDoOutput( true );
        }
        return urlConnection;

    }

    public static boolean isBusy(){
        if( httpURLConnection != null )
            return true;
        return false;
    }

    public static void cancel(){
        cancel = true;

        if( httpURLConnection != null){
            Logger.e(TAG,"cancel() https connection shutdown");
            httpURLConnection.disconnect();
            httpURLConnection = null;
        }
    }
}
