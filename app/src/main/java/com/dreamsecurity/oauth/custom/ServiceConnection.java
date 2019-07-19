package com.dreamsecurity.oauth.custom;

import android.content.ComponentName;
import androidx.browser.customtabs.CustomTabsClient;
import androidx.browser.customtabs.CustomTabsServiceConnection;

import java.lang.ref.WeakReference;

/**
 * CustomTabsServiceConnection 구현체 . callback이 누수되는걸 회피하기 위한 것이다.
 */
public class ServiceConnection extends CustomTabsServiceConnection {

    // weak reference to the serviceConnection Callback
    private WeakReference<ServiceConnectionCallback> connectionCallback;

    public ServiceConnection(ServiceConnectionCallback connectionCallback) {
        this.connectionCallback = new WeakReference<>(connectionCallback);
    }

    @Override
    public void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client) {
       ServiceConnectionCallback callback = connectionCallback.get();
       if( callback != null ){
           callback.onServiceConnected(client);
       }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        ServiceConnectionCallback callback = connectionCallback.get();
        if( callback != null ){
            callback.onServiceDisconnected();
        }
    }

    @Override
    public void onBindingDied(ComponentName name) {

    }

    @Override
    public void onNullBinding(ComponentName name) {

    }
}
