package com.dreamsecurity.oauth.custom;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import com.dreamsecurity.oauth.custom.common.Constant;

public interface OAuthPresenter extends Constant {

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

    String EXTRA_KEY_CODE = "code";
    String EXTRA_KEY_STATE = INTENT_KEY_STATE;
    String EXTRA_KEY_ERROR = "error";
    String EXTRA_KEY_ERROR_DESCRIPTION = "error_des";
    String PARAM_LOCALE_CODE = "locale";

    String EXTRA_ACCESS_TOKEN = "access_token";
    /// OAuth 인증 성공 후 token_secret 값을 얻기 위한 Extra field 명.
    String EXTRA_REFRESH_TOKEN = "refresh_token";

    String EXTRA_EXPIRES_IN = "expires_in";
    String EXTRA_TOKEN_TYPE = "token_type";
    String EXTRA_OAUTH_STATE = "oauth_state";
    String EXTRA_OAUTH_CODE = "oauth_code";

    String EXTRA_ERROR_CODE = "oauth_error_code";
    String EXTRA_ERROR_DESCRIPTION = "oauth_error_desc";

    String PARAM_VALUE_AUTH = "authorization_code";
    String PARAM_VALUE_REFRESH = "authorization_code";

    String PARAM_KEY_STATE = "state";
    String PARAM_APP_TYPE = "app_type";
    String PARAM_NETWORK = "network";
    String PARAM_OS = "oauth_os";
    String PARAM_VERSION = "version";




    String makeLoginParameter(Intent getIntent);
    void requestLoginPage(String url);
    void requestLoginPage(Intent getIntent);

    void returnResult(@NonNull Intent data);

    void responseCodeAndRequest();
    void getAccessToken();



}
