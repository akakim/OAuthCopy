package com.dreamsecurity.ssooauth.common.connect;

import android.text.TextUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 서버의 Response로부터 Login Resutl를 생성한다.
 */
public class ResponseData {
    private final String TAG = "ResponseData";

    private ResponseDataStat 	stat;
    private int 					statusCode;
    private String 				content;
    private String 				errorDetail;
    private List<String>         cookieList;
    private String				xmlEncoding;
    ResponseData() {
        stat = ResponseDataStat.SUCCESS;
        statusCode = -1;
        content = "";
        errorDetail = "";
        cookieList = new ArrayList<String>();
        xmlEncoding = "utf-8";
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

    public enum ResponseDataStat{
        // 성공
        SUCCESS ( "SUCCESS",  null),
        BUSY				("BUSY", 			"BUSY"),			// http client 가 사용중
        CANCEL				("CANCEL", 			"CANCEL"),	  		// 그외의 Exception 발생으로 실패
        URL_ERROR			("URL_ERROR", 		"URL_ERROR"),			// url 이 이상한 경우
        CONNECTION_TIMEOUT	("CONNECTION_TIMEOUT", "CONNECTION_TIMEOUT"), 		// timeout 발생한 경우
        CONNECTION_FAIL		("CONNECTION_FAIL", "CONNECTION_FAIL"),  			// connection 실패
        EXCEPTION_FAIL		("EXCEPTION_FAIL", 	"EXCEPTION_FAIL"),	  		// 그외의 Exception 발생으로 실패
        NO_PEER_CERTIFICATE	("NO_PEER_CERTIFICATE", "NO_PEER_CERTIFICATE"),		// 인증서오류
        FAIL				("FAIL", 			"FAIL");	  		// 그외의 실패


        private String stat;
        private String errorMessage;
        ResponseDataStat(String str, String errorMessage){
            stat = str;
            this.errorMessage = errorMessage;
        }

        public String getStat(){
            return stat;
        }
    }

    public void setResponseData(int statusCode, String contentType, InputStream responseContent,List<String> cookieList){
        this.statusCode = statusCode;

        if( cookieList != null){
            this.cookieList.addAll( cookieList );
        }

        try {
            xmlEncoding = contentType;
            content = getContent(responseContent, xmlEncoding);
        } catch (IllegalStateException e) {
            setResultCode(ResponseDataStat.EXCEPTION_FAIL, "setResponseData()-IllegalStateException:" + e.getMessage());
        } catch (Exception e) {
            setResultCode(ResponseDataStat.EXCEPTION_FAIL, "setResponseData()-Exception:" + e.getMessage());
        }
    }


    public void setResultCode(ResponseDataStat stat, String msg) {
        this.stat = stat;
        this.errorDetail = msg;
    }

    /**
     * inputStream 에서 string 을 얻어냄
     * @param is
     * @param encodeType
     * @return
     */
    private String getContent(InputStream is, String encodeType){
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
            setResultCode(ResponseDataStat.EXCEPTION_FAIL, "getContent()-IOException:" + e.getMessage());
        } catch (RuntimeException e) {
            setResultCode(ResponseDataStat.EXCEPTION_FAIL, "getContent()-RuntimeException:" + e.getMessage());
        }
        return buf.toString();
    }

    @Override
    public String toString() {
        return "ResponseData{" +
                "statusCode=" + statusCode +
                ", content='" + content + '\'' +
                ", errorDetail='" + errorDetail + '\'' +
                ", " + TextUtils.join("|", cookieList) +
                ", xmlEncoding='" + xmlEncoding + '\'' +
                '}';
    }
}
