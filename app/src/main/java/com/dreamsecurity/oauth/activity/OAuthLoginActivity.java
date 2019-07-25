package com.dreamsecurity.oauth.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.dreamsecurity.oauth.R;
import com.dreamsecurity.oauth.custom.CustomTabsListener;
import com.dreamsecurity.oauth.custom.OAuthPresenter;
import com.dreamsecurity.oauth.custom.common.Logger;
import com.dreamsecurity.oauth.custom.common.OAuthErrorCode;
import com.dreamsecurity.oauth.custom.common.OAuthLoginPreferManager;
import com.dreamsecurity.oauth.data.OAuthorizedResponse;


public class OAuthLoginActivity extends AppCompatActivity {

    private static final String TAG = "OAuthLoginActivity";
    private static int REQUESTCODE_LOGIN = 100;
    private static int CUSTOMTAB_LOGIN = -1;

    // dialog TODO : Deprecate 된거 수정하기.
    //private OAuthLoginDialogMng mDialogMng = new OAuthLoginDialogMng();


    private Context mContext;
    //private OAuthLoginData mOAuthLoginData;
    private String mClientName;

    private boolean isForceDestroyed = true;

    private boolean mIsLoginActivityStarted = false;


    private CustomTabsListener mCustomTabListener = new CustomTabsListener() {
        @Override
        public void onReceive(Intent intent) {
            if (intent == null) {
                intent = new Intent();
                intent.putExtra(OAuthPresenter.EXTRA_ERROR_CODE
                        , OAuthErrorCode.CLIENT_USER_CANCEL.getCode());
                intent.putExtra(OAuthPresenter.EXTRA_ERROR_DESCRIPTION
                        , OAuthErrorCode.CLIENT_USER_CANCEL.getDesc());
            }
            onActivityResult(CUSTOMTAB_LOGIN, RESULT_OK, intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth_login);
        if (!Logger.isRealVersion()) {
            Logger.d(TAG, "onCreate()");
        }

    }

    private boolean initData( Bundle savedInstanceState){
        OAuthLoginPreferManager opm = new OAuthLoginPreferManager( this );
        String clientId = opm.getClientId();
        String clientSecret = opm.getClientSecret();
        String callbackUrl = opm.getCallbackUrl();
        String state = (null == savedInstanceState) ? null : savedInstanceState.getString("OAuthLoginData_state");


        Logger.d( TAG,"client_id" + clientId );
        Logger.d( TAG,"clientSecret" + clientSecret );
        Logger.d( TAG,"callbackUrl" + callbackUrl );
        Logger.d( TAG,"state" + state );
//        Logger.d( TAG,"client_id" + clientId );

        mClientName = opm.getClientName();

        if( TextUtils.isEmpty( clientId )) {
            finishWithErrorResult(OAuthErrorCode.CLIENT_ERROR_NO_CLIENTID);
            return false;
        }

        if (TextUtils.isEmpty(clientSecret)) {
            finishWithErrorResult(OAuthErrorCode.CLIENT_ERROR_NO_CLIENTSECRET);
            return false;
        }
        if (TextUtils.isEmpty(mClientName)) {
            finishWithErrorResult(OAuthErrorCode.CLIENT_ERROR_NO_CLIENTNAME);
            return false;
        }
        if (TextUtils.isEmpty(callbackUrl)) {
            finishWithErrorResult(OAuthErrorCode.CLIENT_ERROR_NO_CALLBACKURL);
            return false;
        }

        //mOAuthLoginData = new OAuthLoginData(clientId, clientSecret, callbackUrl, state);

        return true;
    }

    private void finishWithErrorResult( OAuthErrorCode code) {
        if (!Logger.isRealVersion()) {
            Logger.d(TAG, "Login finishWithErrorResult()");
        }

        Intent intent = new Intent();
        OAuthLoginPreferManager prefMng = new OAuthLoginPreferManager(mContext);

        prefMng.setLastErrorCode(code);
        prefMng.setLastErrorDesc(code.getDesc());

        intent.putExtra(OAuthPresenter.EXTRA_ERROR_CODE, code.getCode());
        intent.putExtra(OAuthPresenter.EXTRA_ERROR_DESCRIPTION, code.getDesc());
    }

    private class GetAccessTokenTask extends AsyncTask<Void,Void, OAuthorizedResponse> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Logger.d(getClass().getSimpleName(), " onPreExecuted"); // TODO: UI 효과 추가.
        }

        @Override
        protected OAuthorizedResponse doInBackground(Void... voids) {
            try {
                return OAuthLoginConnection.requestAccessToken(mContext,
                        mOAuthLoginData.getClientId(),
                        mOAuthLoginData.getClientSecret(),
                        mOAuthLoginData.getState(), mOAuthLoginData.getCode());
            } catch (Exception e) {
                return new OAuthResponse(OAuthErrorCode.CLIENT_ERROR_CONNECTION_ERROR);
            }
        }

        @Override
        protected void onPostExecute(OAuthorizedResponse oAuthorizedResponse) {
            super.onPostExecute(oAuthorizedResponse);

            Logger.d(getClass().getSimpleName(), " onPostExecuted");
        }
    }
}
