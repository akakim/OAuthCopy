package com.dreamsecurity.ssooauth.magiclogin.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

public class OAuthDialog {

    public Object progressDialogSync = new Object();
    public Object alertDialogSync = new Object();
    private ProgressDialog loginGlobalDefaultProgressDialog = null;

    /**
     * progress dialog를 보여줌
     *
     * @param context context
     * @param msg
     *            dialog에 출력할 메시지
     * @param onCancelListener
     *            back-key 등으로 cancel 될 경우 실행될 listener. 주로 백그라운드로 처리되던 작업의 중지를 하는 로직이 들어감
     * @return 생성실패하는 경우 false 리턴, 정상적인 경우 true 리턴
     */
    public boolean showProgressDlg(Context context, String msg, DialogInterface.OnCancelListener onCancelListener) {

        synchronized( progressDialogSync ) {
            try {
                if (loginGlobalDefaultProgressDialog != null) {
                    loginGlobalDefaultProgressDialog.hide();
                    loginGlobalDefaultProgressDialog.dismiss();
                }

                loginGlobalDefaultProgressDialog = new ProgressDialog( context, androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog );
                loginGlobalDefaultProgressDialog.setIndeterminate( true );
                loginGlobalDefaultProgressDialog.setMessage(msg);
                loginGlobalDefaultProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

                if (onCancelListener != null) {
                    loginGlobalDefaultProgressDialog.setOnCancelListener(onCancelListener);
                }
                loginGlobalDefaultProgressDialog.setCanceledOnTouchOutside(false);
                loginGlobalDefaultProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        // dismiss 되는 경우 null로 처리하여 다음 show Progress 하는데 문제
                        // 없도록 함
                        loginGlobalDefaultProgressDialog = null;
                    }
                });

                loginGlobalDefaultProgressDialog.show();

                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    /**
     * showPregressDlg()로 만든 progress dialog를 없앰
     *
     * @return 없거나 실패한경우 false 리턴, 정상적으로 없어진 경우 true 리턴
     */
    public synchronized boolean hideProgressDlg() {
        synchronized(progressDialogSync) {
            if (loginGlobalDefaultProgressDialog == null) {
                return false;
            }
            try {
                loginGlobalDefaultProgressDialog.hide();
                loginGlobalDefaultProgressDialog.dismiss();
                loginGlobalDefaultProgressDialog = null;
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }


}
