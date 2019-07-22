package com.dreamsecurity.oauth.custom;

public interface OAuthCallback {

    public void onResponseLogin();

    public void onResponseAccessToken();
}
