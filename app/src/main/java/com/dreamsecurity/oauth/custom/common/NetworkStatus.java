package com.dreamsecurity.oauth.custom.common;

public enum NetworkStatus {
    /// 성공
    SUCCESS				("SUCCESS", 		null),
    BUSY				("BUSY", 			"BUSY"),			// http client 가 사용중
    CANCEL				("CANCEL", 			"CANCEL"),	  		// 그외의 Exception 발생으로 실패
    URL_ERROR			("URL_ERROR", 		"URL_ERROR"),			// url 이 이상한 경우
    CONNECTION_TIMEOUT	("CONNECTION_TIMEOUT", "CONNECTION_TIMEOUT"), 		// timeout 발생한 경우
    CONNECTION_FAIL		("CONNECTION_FAIL", "CONNECTION_FAIL"),  			// connection 실패
    EXCEPTION_FAIL		("EXCEPTION_FAIL", 	"EXCEPTION_FAIL"),	  		// 그외의 Exception 발생으로 실패
    NO_PEER_CERTIFICATE	("NO_PEER_CERTIFICATE", "NO_PEER_CERTIFICATE"),		// 인증서오류
    FAIL				("FAIL", 			"FAIL");	  		// 그외의 실패


    private String stat;
    //private String mErrMsg;

    NetworkStatus(String str, String errMsg) {
        stat = str;
        //mErrMsg = errMsg;
    }

    public String getValue() {
        return stat;
    }

}
