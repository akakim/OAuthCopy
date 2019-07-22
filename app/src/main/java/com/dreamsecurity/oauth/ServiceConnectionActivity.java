package com.dreamsecurity.oauth;

import android.net.Uri;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.browser.customtabs.CustomTabsIntent;
import com.dreamsecurity.oauth.custom.CustomTabsHelper;
import com.dreamsecurity.oauth.custom.WebviewFallback;

public class ServiceConnectionActivity extends AppCompatActivity
            implements View.OnClickListener,CustomTabActivityHelper.ConnectionCallback {

    private EditText edUri;
    private View btnMaylautnUri;
    private CustomTabActivityHelper customTabActivityHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_connectoin);

        this.edUri = findViewById(R.id.edUrl);
        btnMaylautnUri = findViewById(R.id.btnMayLaunchUri );
        btnMaylautnUri.setEnabled( false );
        btnMaylautnUri.setOnClickListener( this );

        findViewById( R.id.btnStartCustomTab).setOnClickListener( this );

        customTabActivityHelper = new CustomTabActivityHelper();
        customTabActivityHelper.setConnectionCallback(this);

    }


    @Override
    protected void onStart() {
        super.onStart();
        customTabActivityHelper.bindCustomTabsService( this );
    }

    @Override
    protected void onStop() {
        super.onStop();
        customTabActivityHelper.unbindCustomTabService( this );
        btnMaylautnUri.setEnabled( false );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        customTabActivityHelper.setCallback( null );
    }

    @Override
    public void onClick(View view) {

        int viewId = view.getId();
        Uri uri  = Uri.parse(edUri.getText().toString());
        switch (viewId){
            case R.id.edUrl:
                customTabActivityHelper.mayLaunchUrl(uri, null, null);
                break;
            case R.id.btnStartCustomTab:

                CustomTabsIntent intent =
                        new CustomTabsIntent.Builder( customTabActivityHelper.getSession())
                        .build();

                CustomTabActivityHelper.openCustomTab( this , intent ,uri, new WebviewFallback());

                break;
        }
    }

    @Override
    public void onCustomTabsConnected() {
        btnMaylautnUri.setEnabled( true );
        Toast.makeText(this,"CustomTab 서비스가 연결되었습니다.",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCustomTabsDisconnected() {
        btnMaylautnUri.setEnabled( false );
        Toast.makeText(this,"CustomTab 서비스가 연결 해제 되었습니다.",Toast.LENGTH_SHORT).show();
    }
}
