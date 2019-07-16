package com.dreamsecurity.ssooauth.magiclogin.data;

public class BrowserIntentKey {

    // 아래 3개는 로그인 페이지 불러올때 쓸 데이터
    public static final String CLIENT_ID = "ClientId";
    public static final String CALLBACK_URL = "ClientCallbackUrl";
    public static final String STATE = "state";
    // 아래 2개는 네이버앱을 통한 로그인시 전달됨
    public static final String APP_NAME = "app_name";
    public static final String SDK_VERSION = "oauth_sdk_version";
    // 아래 2개는 동의 창의 내용을 직접 받아와서 보여주는 경우 사용됨
    public static final String FORM_URL = "agreeFormUrl";
    public static final String FORM_CONTENT = "agreeFormContent";
    public static final String OAUTH_URL = "OAuthUrl";

}
