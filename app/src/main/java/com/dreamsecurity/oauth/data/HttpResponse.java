package com.dreamsecurity.oauth.data;

import android.text.TextUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HttpResponse {

    private final String TAG = "ResponseData";

    ResponseDataStat stat;
    int statusCode;
    String content;
    String errorDetail;
    List<String> cookieList;
    String xmlEncoding;

    public HttpResponse() {
        stat = ResponseDataStat.SUCCESS;
        statusCode = -1;
        content = "";
        errorDetail = "";
        cookieList = new ArrayList<String>();
        xmlEncoding = "utf-8";
    }

    public enum ResponseDataStat {
        /// 성공
        SUCCESS				("SUCCESS", 		null),
        CLIENT_ACTION_BUSY				("BUSY", 			"BUSY"),			// http client 가 사용중
        CLIENT_ACTION_CANCEL				("CANCEL", 			"유저의 취소"),	  		// 그외의 Exception 발생으로 실패
        CLIENT_ACTION_URL_ERROR			("URL_ERROR", 		"URL_ERROR"),			// url 이 이상한 경우
        CLIENT_ACTION_CONNECTION_TIMEOUT	("CONNECTION_TIMEOUT", "CONNECTION_TIMEOUT"), 		// timeout 발생한 경우
        CLIENT_ACTION_CONNECTION_FAIL		("CONNECTION_FAIL", "CONNECTION_FAIL"),  			// connection 실패
        CLIENT_ACTION_IO_FAIL ("IO_EXCEPTION","IO_EXCEPTION"),
        CLIENT_ACTION_EXCEPTION_FAIL		("EXCEPTION_FAIL", 	"EXCEPTION_FAIL"),	  		// 그외의 Exception 발생으로 실패
        CLIENT_ACTION_NO_PEER_CERTIFICATE	("NO_PEER_CERTIFICATE", "NO_PEER_CERTIFICATE"),		// 인증서오류
        FAIL				("FAIL", 			"FAIL");	  		// 그외의 실패

        private String stat;
        //private String mErrMsg;

        ResponseDataStat(String str, String errMsg) {
            stat = str;
            //mErrMsg = errMsg;
        }

        public String getValue() {
            return stat;
        }
    }



    public void setResultCode(ResponseDataStat stat) {
        this.stat = stat;
    }



    /*
     * httpUrlConnection 을 통해 얻어온 데이터를 넣어준다
     * @param statusCode
     * @param contentType
     * @param responseContent
     * @param cookieList
     */
    public void setResponseData(int statusCode, String contentType, InputStream responseContent, List<String> cookieList) {
        this.statusCode = statusCode;

        if (cookieList != null) {
            this.cookieList = cookieList;
        }

        try {
            xmlEncoding = contentType;
            content = getContent(responseContent, xmlEncoding);
        } catch (IllegalStateException e) {
            setResultCode(ResponseDataStat.FAIL);
        } catch (Exception e) {
            setResultCode(ResponseDataStat.FAIL);
        }
    }

    /**
     * inputStream 에서 string 을 얻어냄
     * @param is
     * @param encodeType
     * @return
     */
    private String getContent(InputStream is, String encodeType) {
        final int bufferSize = 1024;
        char[] readString = new char[(int)bufferSize];
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(is, encodeType));
        } catch (UnsupportedEncodingException e1) {
            reader = new BufferedReader(new InputStreamReader(is));
        }

        int read;
        StringBuilder buf = new StringBuilder();
        try {
            while ((read = reader.read(readString, 0, bufferSize)) > 0) {
                buf.append(readString, 0, read);
            }
            reader.close();
        } catch (IOException e) {
            setResultCode(ResponseDataStat.FAIL);
        } catch (RuntimeException e) {
            setResultCode(ResponseDataStat.FAIL);
        }
        return buf.toString();
    }

    @Override
    public String toString() {
        return "Statuscode:" + statusCode + ", Content:" + content + ", Cookie:" + TextUtils.join("|", cookieList) + ", ErrorDetail:" + errorDetail;
    }

    public OAuthorizedResponse convertToErrorMessage(){
        OAuthorizedResponse res = new OAuthorizedResponse();

        //res.setErrorCode( this.m);
        return res;

    }

    public OAuthorizedResponse convertToOAuthResponse() throws Exception{
        OAuthorizedResponse res = new OAuthorizedResponse();

        //parse Response

        return res;
    }

    public ResponseDataStat getStat() {
        return stat;
    }

    public void setStat(ResponseDataStat stat) {
        this.stat = stat;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getErrorDetail() {
        return errorDetail;
    }

    public void setErrorDetail(String errorDetail) {
        this.errorDetail = errorDetail;
    }

    public List<String> getCookieList() {
        return cookieList;
    }

    public void setCookieList(List<String> cookieList) {
        this.cookieList = cookieList;
    }

    public String getXmlEncoding() {
        return xmlEncoding;
    }

    public void setXmlEncoding(String xmlEncoding) {
        this.xmlEncoding = xmlEncoding;
    }
}
