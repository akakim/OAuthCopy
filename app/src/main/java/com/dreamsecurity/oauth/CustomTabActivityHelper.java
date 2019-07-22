package com.dreamsecurity.oauth;


import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import androidx.browser.customtabs.CustomTabsClient; // android.support.customtabs.CustomTabsClient;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.browser.customtabs.CustomTabsServiceConnection; // android.support.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession;         //android.support.customtabs.CustomTabsSession
import com.dreamsecurity.oauth.custom.CustomTabsHelper;
import com.dreamsecurity.oauth.custom.ServiceConnection;
import com.dreamsecurity.oauth.custom.ServiceConnectionCallback;

import java.util.List;

/**
 * import android.support.customtabs.CustomTabsIntent;
 */
public class CustomTabActivityHelper implements ServiceConnectionCallback {

    final String TAG = getClass().getSimpleName();
    private CustomTabsSession session;
    private CustomTabsClient client;
    private CustomTabsServiceConnection connection;
    private ConnectionCallback callback;

    public static void openCustomTab(Activity activity,
                                     CustomTabsIntent customTabsIntent,
                                     Uri uri,
                                     CustomTabFallback fallback){
        String packageName = CustomTabsHelper.getPackageNameTouse( activity );


        // 패키지를 찾지 못한경우. 그것은 Chrome Custom Tabs이 지원되는 브라우저가
        // 없다는 이야기이다. 그래서 우리는 웹뷰를 반환 할 것이다.

         if( packageName == null ){
             if( fallback != null){
                  fallback.openUri( activity, uri );
             }
         } else {
             customTabsIntent.intent.setPackage( packageName );
             customTabsIntent.launchUrl( activity , uri );
         }
    }

    /**
     * activity에서부터 자원할당을 해체함.
     * @param activity
     */
    public void unbindCustomTabService( Activity activity ){
        if( this.connection == null ) return;
        activity.unbindService( this.connection);
        this.client     = null;
        this.session    = null;
        this.connection = null;
    }

    /**
     * 생성하거나 원래 가지고있는 customTabSession을 가져온다.
     * @return
     */
    public CustomTabsClient getClient() {

        if( this.client == null ){
            this.session =null;
        } else if ( this.session == null){
            this.session = this.client.newSession( null );

        }
        return client;
    }


    /**
     * Callback을 등록한다. 이것은 커스텀 탭 서비스가 연결 되거나 해체가 될때 호출된다.
     * @param callback
     */
    public void setCallback(ConnectionCallback callback) {
        this.callback = callback;
    }

    /**
     *
     * @param activity
     */
    public void bindCustomTabsService(Activity activity){
       if( client != null)
           return;

       String packagename = CustomTabsHelper.getPackageNameTouse( activity );
       if( packagename == null ){
           Log.e(TAG,"Custom Tab not supported ");

       }

       connection = new ServiceConnection(this);
       CustomTabsClient.bindCustomTabsService( activity, packagename,connection);
    }

    @Override
    public void onServiceConnected(CustomTabsClient client) {
        this.client = client;
        this.client.warmup( 0L);
        if( callback != null ) {
            callback.onCustomTabsConnected();
        }
    }

    @Override
    public void onServiceDisconnected() {
        client = null;
        session = null;

        if (callback != null)
            callback.onCustomTabsDisconnected();
    }

    /**
     *
     * @param uri
     * @param extras
     * @param otherLikelyBundles
     * @return mayLaunchUrl 이 적용되면 참을 반환함.
     */
    public boolean mayLaunchUrl(Uri uri, Bundle extras, List<Bundle> otherLikelyBundles){
        if( client == null )
            return false;

        CustomTabsSession session  = getSession();

        if ( session == null )
            return false;

        return session.mayLaunchUrl( uri , extras, otherLikelyBundles );
    }

    public CustomTabsSession getSession(){
        if ( client == null ){
            session  =  null;

        }else if ( session == null ){
            session = client.newSession( null );

        }
        return session;

    }

    public void setConnectionCallback(ConnectionCallback connectionCallback){
        this.callback = connectionCallback;
    }
    /**
     * A Callback for when the service is connected or disconnected. Use those callbacks to
     * handle UI changes when the service is connected or disconnected.
     */
    public interface ConnectionCallback {
        /**
         * Called when the service is connected.
         */
        void onCustomTabsConnected();

        /**
         * Called when the service is disconnected.
         */
        void onCustomTabsDisconnected();
    }

    /**
     * To be used as a fallback to open the Uri when Custom Tabs is not available.
     */
    public interface CustomTabFallback {
        /**
         *
         * @param activity The Activity that wants to open the Uri.
         * @param uri The uri to be opened by the fallback.
         */
        void openUri(Activity activity, Uri uri);
    }

}
