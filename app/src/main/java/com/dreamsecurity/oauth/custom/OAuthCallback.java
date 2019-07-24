package com.dreamsecurity.oauth.custom;

import android.content.pm.PackageInfo;
import android.net.Uri;

public interface OAuthCallback {

    void onProgress( Uri uri );

    public void onResponseLogin();

    public void onResponseAccessToken();
}
