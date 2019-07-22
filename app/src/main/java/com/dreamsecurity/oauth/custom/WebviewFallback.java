package com.dreamsecurity.oauth.custom;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import com.dreamsecurity.oauth.CustomTabActivityHelper;

public class WebviewFallback implements CustomTabActivityHelper.CustomTabFallback {
    @Override
    public void openUri(Activity activity, Uri uri) {
        Intent intent = new Intent( activity, WebviewActivity.class);
        intent.putExtra( WebviewActivity.EXTRA_URL,uri.toString());
        activity.startActivity(intent);
    }
}
