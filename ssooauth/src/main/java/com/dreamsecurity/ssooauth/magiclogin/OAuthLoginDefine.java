package com.dreamsecurity.ssooauth.magiclogin;

import android.os.Build;
import com.dreamsecurity.ssooauth.BuildConfig;

public class OAuthLoginDefine {

    // Lib 버전
    public static final String VERSION = BuildConfig.VERSION_NAME;
    // 로그 태그
    public static final String LOG_TAG = "MagicOAuthLogin";
    // 패키지명

    public static final String ACTION_OAUTH_LOGIN  =     "com.dream.android.search.action.OAUTH_LOGIN";
    public static final String ACTION_OAUTH_2NDAPP =     "com.dream.android.action.OAUTH2_LOGIN";

    // 네이버앱이 없거나 업그레이드가 필요한 경우 네이버앱에 대한 market link 팝업을 띄울것인지 여부
    public static boolean		MARKET_LINK_WORKING = true;

    // 닫기 버튼 등이 들어가 있는 하단 탭의 노출 유무
    public static boolean		BOTTOM_TAB_WORKING = true;


    // 로그인을 webview 혹은 네이버앱 통해서 할 수 있는데 true 로 설정하면 webview 로만 로그인하게 됨.
    public static boolean		LOGIN_BY_NAVERAPP_ONLY = false;

    // 로그인을 webview 혹은 네이버앱 통해서 할 수 있는데 true 로 설정하면 webview 로만 로그인하게 됨.
    public static boolean		LOGIN_BY_CUSTOM_TAB_ONLY = false;

    // 로그인을 webview 혹은 네이버앱 통해서 할 수 있는데 true 로 설정하면 webview 로만 로그인하게 됨.
    public static boolean		LOGIN_BY_WEBVIEW_ONLY = false;
    // network timeout
    public static int 			TIMEOUT = 10000;

    public static boolean CUSTOM_TAB_REQUIRED_RE_AUTH = false;

    /**
     * 커스텀탭 사용가능 버전
     */
    public static final int		CUSTOMTAB_AVAILABLE_VER = Build.VERSION_CODES.JELLY_BEAN;


}
