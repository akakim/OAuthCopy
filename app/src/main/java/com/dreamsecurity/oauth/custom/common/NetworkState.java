package com.dreamsecurity.oauth.custom.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.Toast;
import com.dreamsecurity.oauth.R;

public class NetworkState {
    private static boolean shown = false;

    /**
     * Check network connect
     * @param context Application context
     * @return True if network connect or occurred error. otherwise false.
     *
     */
    public static boolean isDataConnected(Context context) {
        try {
            ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();

            return info != null && manager.getActiveNetworkInfo().isConnected();
        } catch (Exception err) {
            err.printStackTrace();
        }
        return true;
    }

    private static boolean isConnected(Context context, int connectType) {
        try {
            ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (manager != null) {

                if (Build.VERSION.SDK_INT < 23) {
                    @SuppressWarnings("deprecation")
                    NetworkInfo ni = manager.getNetworkInfo(connectType);
                    if (ni.isConnected()) {
                        return (true);
                    }

                } else {
                    Network[] allNetwork = manager.getAllNetworks();

                    for (Network network : allNetwork) {
                        NetworkInfo info = manager.getNetworkInfo(network);
                        if (null != info) {
                            if (connectType == info.getType()
                                    && info.isConnected()) {
                                return true;
                            }
                        }
                    }

                }

            }
        } catch (Exception err) {
            err.printStackTrace();
        }
        return (false);
    }

    public static boolean is3GConnected(Context context) {
        return isConnected(context, ConnectivityManager.TYPE_MOBILE);
    }

    public static boolean isWifiConnected(Context context) {
        return isConnected(context, ConnectivityManager.TYPE_WIFI);
    }

    public static void showRetry(final Context context, final RetryListener retryListener) {
        if (shown || context == null) {
            return;
        }
        if (context instanceof Activity) {
            if (((Activity)context).isFinishing()) {
                return;
            }
        }
        shown = true;
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setIcon(android.R.drawable.ic_dialog_alert);
        dialog.setMessage(context.getString(R.string.magic_sso_string_network_state_not_available));
        dialog.setPositiveButton(context.getString(R.string.retry), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                shown = false;
                if (context instanceof Activity) {
                    if (((Activity)context).isFinishing()) {
                        return;
                    }
                }
                retryListener.onResult(true);
            }

        });
        dialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                shown = false;
                if (context instanceof Activity) {
                    if (((Activity)context).isFinishing()) {
                        return;
                    }
                }
                retryListener.onResult(false);
            }

        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface arg0) {
                shown = false;
                if (context instanceof Activity) {
                    if (((Activity)context).isFinishing()) {
                        return;
                    }
                }
                retryListener.onResult(false);
            }

        });

        try {
            dialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static boolean checkConnectivity(Context context, boolean showDialog, final RetryListener retryListener) {
        if (isDataConnected(context)) {
            return (true);
        }

        if (showDialog) {
            if (retryListener == null) {
                String msg = context.getString(R.string.magic_sso_string_network_state_not_available);
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                return false;
            }

            showRetry(context, retryListener);
        }
        return (false);
    }


    /**
     * RetryListener
     */
    public interface RetryListener {
        public void onResult(boolean retry);
    }


    public static String getNetworkState(Context context) {
        String network = "other";

        if (NetworkState.is3GConnected(context)) {
            network = "cell";
        } else if (NetworkState.isWifiConnected(context)) {
            network = "wifi";
        }

        return network;
    }
}
