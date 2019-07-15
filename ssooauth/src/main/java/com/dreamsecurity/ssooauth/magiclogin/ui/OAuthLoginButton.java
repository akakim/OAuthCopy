package com.dreamsecurity.ssooauth.magiclogin.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.dreamsecurity.ssooauth.magiclogin.OAuthLogin;
import com.dreamsecurity.ssooauth.magiclogin.OAuthLoginHandler;

public class OAuthLoginButton extends ImageView {

    public static final String TAG = "OAuthLoginButton";
    private Context context;
    private static OAuthLoginHandler handler;

    public OAuthLoginButton(Context context) {
        this(context,null,-1,-1);

    }

    public OAuthLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs,-1,-1);
    }

    public OAuthLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,-1);
    }

    public OAuthLoginButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init( context );
    }

    private void init(Context context){
        this.context = context;

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                OAuthLogin.getInstance();getContext();
            }
        });

    }

    /**
     * 버튼을 클릭하여 로그인하는 경우, 로그인 결과를 받을 handler 를 지정해준다.
     * @param oauthLoginHandler 로그인 결과를 받을 handler
     */
    public void setOAuthLoginHandler(final OAuthLoginHandler oauthLoginHandler) {
        handler = oauthLoginHandler;
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if( isInEditMode() ) return;
        super.onLayout(changed, left, top, right, bottom);
    }
}
