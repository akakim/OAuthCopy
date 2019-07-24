package com.dreamsecurity.oauth.custom.common;

public enum OAuthLoginState {
    /// 초기화가 필요한 상태
    NEET_INIT,
    // 로그인 필요한 상태 ( ACCESS_TOKEN, REFRESH TOKEN 이 없음 )
    NEED_LOGIN,
    NEED_REFRESH_TOKEN,
    OK;
}
