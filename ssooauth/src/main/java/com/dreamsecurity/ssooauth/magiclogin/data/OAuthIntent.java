package com.dreamsecurity.ssooauth.magiclogin.data;

public class OAuthIntent {

    /// OAuth 인증 성공 후 token 값을 얻기 위한 Extra field 명.
    public static final String EXTRA_OAUTH_ACCESS_TOKEN = "oauth_access_token";
    /// OAuth 인증 성공 후 token_secret 값을 얻기 위한 Extra field 명.
    public static final String EXTRA_OAUTH_REFRESH_TOKEN = "oauth_refresh_token";

    public static final String EXTRA_OAUTH_EXPIRES_IN = "oauth_expires_in";
    public static final String EXTRA_OAUTH_TOKEN_TYPE = "oauth_token_type";

    public static final String EXTRA_OAUTH_STATE = "oauth_state";
    public static final String EXTRA_OAUTH_CODE = "oauth_code";

    public static final String EXTRA_OAUTH_ERROR_CODE = "oauth_error_code";
    public static final String EXTRA_OAUTH_ERROR_DESCRIPTION = "oauth_error_desc";

}
