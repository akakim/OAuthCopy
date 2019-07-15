package com.dreamsecurity.ssooauth.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import com.dreamsecurity.ssooauth.common.logger.Logger;

public class OAuthUIUtil {


    private static final String TAG = "OAuthLoginUiUtil";

    /**
     * 현재 acitivty 를 portrait 로 orientation 을 고정해서 보여줄 것인지 여부 (3rd-party 앱에 포함된 oauth sdk version 으로 결정됨)
     * 4.1.3 미만 버젼은 가로/세로 전환이 불가능하거나, 전환시 버그가 있으므로 4.1.3 버젼부터 가로 / 세로 전환이 가능하도록 그 이전 버젼은 portrait 로 고정한다.
     * @param oauthSdkVersion
     * @return boolean 고정해서 보여줘야하면 true
     */
    static public boolean isFixActivityPortrait(String oauthSdkVersion) {

        try {
            if (null != oauthSdkVersion && oauthSdkVersion.length() > 4) {
                String[] versionNum = oauthSdkVersion.split("\\.");

                // 4.1.3 버젼 부터 version 을 넘겨주며 그 버젼 부턴 로테이션에 문제없음 (4.1.2 부터 문제없지만 4.1.2 인지 판별할 방법이 없음)
                if (2 < versionNum.length) {
                    int majorVer = Integer.valueOf(versionNum[0]);
                    int minorVer = Integer.valueOf(versionNum[1]);
                    int revisionNo = Integer.valueOf(versionNum[2]);
                    if (4 < majorVer) {
                        return false;
                    } else if (4 == majorVer) {
                        if (1 < minorVer) {
                            return false;
                        } else if (1 == minorVer){
                            if (2 < revisionNo) {
                                return false;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }


    /**
     * get drawable instance from context
     * @param context activity context
     * @param id instance id
     * @return the drawable instance if exist, null if drawable instance called id not exist
     */
    public static Drawable getDrawableById(Context context, int id) {
        Drawable drawable;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawable = context.getDrawable(id);
            } else {
                drawable = context.getResources().getDrawable(id);
            }
        } catch(Resources.NotFoundException nfe) {
            drawable = null;
            Logger.d(TAG, "drawable not found");
        }

        return drawable;
    }
}
