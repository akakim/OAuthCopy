package com.dreamsecurity.oauth.custom;

import androidx.browser.customtabs.CustomTabsClient;

/**
 * Callback For the Service is connected
 */
public interface ServiceConnectionCallback {

    /**
     * Called when the service is connected
     * @param client a CustomTabsClient
     */
    void onServiceConnected(CustomTabsClient client );

    /**
     *
     */
    void onServiceDisconnected();
}
