package com.mariohuete.imagination.utils;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;

import com.mariohuete.imagination.R;


/**
 *
 * Created by Mario Huete Jiménez on 12/05/15.
 */
public class Network extends BroadcastReceiver {
    private static AlertDialog alertDialog;
    private Activity activity;
    private static Handler handler = null;

    public Network(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(!isConnected(context) && !isAlertShowing())
            showAlert(context, activity);
        else if(isConnected(context) && isAlertShowing()) {
            dismissAlert();
            if(handler != null) {
                Bundle b = new Bundle();
                if(activity!= null &&
                        activity.getTitle().equals(context.getString(R.string.splash))) {
                    b.putString(context.getString(R.string.message),
                            context.getString(R.string.reload));
                    Common.reload = false;
                }
                Message msgObj = handler.obtainMessage();
                msgObj.setData(b);
                handler.sendMessage(msgObj);
                handler = null;
            }
        }
    }

    public static void showAlert(final Context context, final Activity activity) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        // Set title
        alertDialogBuilder.setTitle(context.getString(R.string.no_connection));
        // Set dialog message
        alertDialogBuilder
                    .setMessage(context.getString(R.string.click))
                    .setCancelable(false)
                    .setPositiveButton(context.getString(R.string.yes_button),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // If this button is clicked, open network settings
                                    Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                }
                            })
                    .setNegativeButton(context.getString(R.string.no_button),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // If this button is clicked, close alertDialog
                                    if (dialog != null)
                                        dialog.dismiss();
                                    if(activity.getTitle().equals(
                                            context.getString(R.string.app_name))) {
                                        activity.finish();
                                    }
                                }
                            });
        // Create alert dialog
        alertDialog = alertDialogBuilder.create();
        // Show
        alertDialog.show();
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobi = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return (wifi.isConnectedOrConnecting() || mobi.isConnectedOrConnecting());
    }

    public static void isConnected(Context context, Handler hlr) {
        handler = hlr;
        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobi = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        Bundle b = new Bundle();
        if(wifi.isConnectedOrConnecting() || mobi.isConnectedOrConnecting()) {
            b.putString(context.getString(R.string.message), null);
        }
        else {
            b.putString(context.getString(R.string.message), context.getString(R.string.message));
        }
        Message msgObj = handler.obtainMessage();
        msgObj.setData(b);
        handler.sendMessage(msgObj);
    }

    public static void dismissAlert() {
        if(alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
            alertDialog = null;
        }
    }

    public static boolean isAlertShowing() {
        return ((alertDialog != null) && (alertDialog.isShowing()));
    }

}
