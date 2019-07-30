package com.dreamsecurity.oauth.custom.common;

import com.dreamsecurity.oauth.BuildConfig;


/**
 * 조금 더 편하게 프로젝트를 응용한다면, SourceSet을 응용하면 될거같다.
 * 목적 : Constant.java 파일에서  요청하는 url을 모두 볼 수있고 Build.gradle 파일에서 변형을 하면 상수값을 바꾸는
 * 것이 목적이다.
 */
public interface Constant {
    String OAUTH_REQUEST_AUTH_URL = "http://10.10.30.196:50001/authorize?";
    String OAUTH_REQUEST_ACCESS_TOKEN_URL = "http://10.10.30.196:50001/token?";



    // 로그인 페이지를 요청시 사용함 .
    String PARAM_KEY_CLIENT_ID = "client_id";
    String PARAM_KEY_CLIENT_SECRET = "client_secret";
    String PARAM_KEY_REDIRECT_URI = "redirect_uri";
    String PARAM_KEY_GRANT_TYPE = "grant_type";
    String PARAM_KEY_RESPONSE_TYPE = "response_type";
    String PARAM_KEY_SCOPE = "scope";
    String PARAM_KEY_VALUE = "address name";

    String PARAM_KEY_CODE = "authorization_code";
    String PARAM_STATE_CODE = "state";
    String PARAM_LOCALE_CODE = "locale";


    String PARAM_VALUE_AUTH = "authorization_code";
    String PARAM_VALUE_RESPONSE_CODE = "code";


    String PARAM_KEY_STATE = "state";
    String PARAM_APP_TYPE = "app_type";
    String PARAM_NETWORK = "network";
    String PARAM_OS = "oauth_os";
    String PARAM_VERSION = "version";

    /**
     * 사용자가 수정 가능한 값들
     */
    // 네이버앱이 없거나 업그레이드가 필요한 경우 네이버앱에 대한 market link 팝업을 띄울것인지 여부
    public static boolean		MARKET_LINK_WORKING = true;

    // 닫기 버튼 등이 들어가 있는 하단 탭의 노출 유무
    public static boolean		BOTTOM_TAB_WORKING = true;


    // 로그인을 webview 혹은 네이버앱 통해서 할 수 있는데 true 로 설정하면 webview 로만 로그인하게 됨.
    boolean		LOGIN_BY_NAVERAPP_ONLY = false;

    // 로그인을 webview 혹은 네이버앱 통해서 할 수 있는데 true 로 설정하면 webview 로만 로그인하게 됨.
    boolean		LOGIN_BY_CUSTOM_TAB_ONLY = true;

    // 로그인을 webview 혹은 네이버앱 통해서 할 수 있는데 true 로 설정하면 webview 로만 로그인하게 됨.
    boolean		LOGIN_BY_WEBVIEW_ONLY = false;
    // network timeout
    int 			TIMEOUT = 10000;

    boolean CUSTOM_TAB_REQUIRED_RE_AUTH = false;
}
