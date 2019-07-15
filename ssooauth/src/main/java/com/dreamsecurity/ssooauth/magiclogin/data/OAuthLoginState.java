package com.dreamsecurity.ssooauth.magiclogin.data;

public enum OAuthLoginState {

    /// 초기화가 필요한 상태
    NEED_INIT,
    /// 로그인이 필요한 상태 (access token, refresh token 이 없음)
    NEED_LOGIN,
    /// 토큰 refresh 가 필요한 상태 (access token 은 없고, refresh token 은 있음)
    NEED_REFRESH_TOKEN,
    /// access token 이 있는 상태 (사용자가 외부사이트 연결관리 페이지에서 연결 끊기 한 경우 서버에선 유효하지 않을 수 있음)
    OK;
}
