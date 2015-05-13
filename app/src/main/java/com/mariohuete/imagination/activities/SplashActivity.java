package com.mariohuete.imagination.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;

import com.mariohuete.imagination.R;
import com.mariohuete.imagination.utils.Common;
import com.mariohuete.imagination.utils.Network;


/**
 *
 * Created by Mario Huete Jiménez on 09/05/15.
 */
public class SplashActivity extends Activity {
    private int splashTime = 0;
    // Define the Handler that receives messages from the thread and update the progress
    private Network network;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final Context context = this;
        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                String response = msg.getData().getString(getString(R.string.message));
                if((response != null) && (response.equals(getString(R.string.message)))) {
                    Network.showAlert(context, (Activity)context);
                }
                else {
                    if(Common.reload ) { //((!Common.reload) && (response.equals(getString(R.string.reload)))
                        // Retrieve preferences
                        SharedPreferences preferences = PreferenceManager.
                                getDefaultSharedPreferences(context);
                        boolean showSplash = preferences.getBoolean(getString(R.string.show_splash),
                                true);
                        preferences = PreferenceManager.getDefaultSharedPreferences(context);
                        Common.thirdPartyLibs = preferences.getBoolean(getString(R.string.third), true);
                        if (showSplash)
                            splashTime = 3000;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // This method will be executed once the timer is over
                                Intent i = new Intent(SplashActivity.this, ArtistListActivity.class);
                                startActivity(i);
                                // Close this activity
                                finish();
                            }
                        }, splashTime);
                    }
                }
            }
        };
        Network.isConnected(context, handler);
        // Retrieve preferences
        /*SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean showSplash = preferences.getBoolean(getString(R.string.show_splash), true);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Common.thirdPartyLibs = preferences.getBoolean(getString(R.string.third), true);
        if(showSplash)
            splashTime = 3000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                Intent i = new Intent(SplashActivity.this, ArtistListActivity.class);
                startActivity(i);
                // Close this activity
                finish();
            }
        }, splashTime);*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Register receiver for connectivity changes
        network = new Network(this);
        registerReceiver(network, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(!Network.isConnected(getApplicationContext()) && !Network.isAlertShowing()) {
            Network.showAlert(getApplicationContext(), this);
        }
        else if(Network.isConnected(getApplicationContext()) && Network.isAlertShowing()) {
            Network.dismissAlert();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(network);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
    }

}
