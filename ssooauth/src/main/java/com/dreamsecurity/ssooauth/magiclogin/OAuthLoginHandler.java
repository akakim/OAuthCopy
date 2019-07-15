package com.dreamsecurity.ssooauth.magiclogin;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.NonNull;


public abstract class OAuthLoginHandler extends Handler {

    public OAuthLoginHandler(){ super(); }

    public OAuthLoginHandler(Looper mainLooper){ super(mainLooper);}

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        boolean success = ( msg.what == 1 );
        run(success);
    }

    public abstract void run(boolean success);
}
