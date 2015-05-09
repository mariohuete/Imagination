package com.mariohuete.imagination.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.mariohuete.imagination.R;
import com.mariohuete.imagination.utils.Common;


/**
 *
 * Created by Mario Huete Jiménez on 09/05/15.
 */
public class SplashActivity extends Activity {
    private int splashTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Context context = this;
        // Retrieve preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
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
        }, splashTime);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {}

}
