package com.dreamsecurity.oauth.custom;

import android.content.Intent;

public interface OAuthPresenter {

    // 로그인 페이지를 요청시 사용함 .
    String INTENT_KEY_CLIENT_ID = "client_id";
    String INTENT_KEY_CLIENT_SECRET = "client_secret";
    String INTENT_KEY_REDIRECT_URI = "redirect_uri";
    String INTENT_KEY_STATE = "state";

    // 네이버앱 혹은 회사 앱을 사용을 이용하여 전달된 Parameter
    String INTENT_KEY_APP_NAME = "app_name";
    String INTENT_KEY_OAUTH_SDK_VERSION= "oauth_sdk_version";


    // 동의 창의 내용을 직접 받아와서 보여주는 화면
    String INTENT_KEY_AGREE_FORM_URL = "agree_form_url";
    String INTENT_KEY_AGREE_FORM_CONTENT= "agree_form_content";
    String INTENT_KEY_OAUTH_URL= "oauth";


    String makeLoginParameter(Intent getIntent);
    void requestLoginPage(String url);
    void requestLoginPage(Intent getIntent);

    void responseCodeAndRequest();
    void getAccessToken();

    String generateState();


}
