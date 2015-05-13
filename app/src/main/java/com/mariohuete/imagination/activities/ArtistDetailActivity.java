package com.mariohuete.imagination.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.mariohuete.imagination.fragments.ArtistDetailFragment;
import com.mariohuete.imagination.R;
import com.mariohuete.imagination.models.Artist;
import com.mariohuete.imagination.utils.Common;
import com.mariohuete.imagination.utils.Network;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;


/**
 * An activity representing a single Artist detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ArtistListActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link ArtistDetailFragment}.
 */
public class ArtistDetailActivity extends ActionBarActivity {
    private Network network;
    @Optional @InjectView(R.id.toolbar) Toolbar toolbar;
    @InjectView(R.id.title) TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_detail);
        // View injection
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        // Show the Up button in the toolbar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Set Artist's name in toolbar
        title.setText(((Artist)getIntent().getSerializableExtra(
                ArtistDetailFragment.ARG_ITEM_ID)).getName());
        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putSerializable(ArtistDetailFragment.ARG_ITEM_ID,
                    getIntent().getSerializableExtra(ArtistDetailFragment.ARG_ITEM_ID));
            ArtistDetailFragment fragment = new ArtistDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.artist_detail_container, fragment)
                    .commit();
        }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Retrieve preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Store preferences
        SharedPreferences.Editor editor = preferences.edit();
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                if(Network.isConnected(getApplicationContext())) {
                    NavUtils.navigateUpTo(this, new Intent(this, ArtistListActivity.class));
                }
                else if (!Network.isAlertShowing()) {
                    Network.showAlert(getApplicationContext(), this);
                }
                return true;
            case R.id.action_settings:
                if(Network.isConnected(getApplicationContext())) {
                    NavUtils.navigateUpTo(this, new Intent(this, ArtistListActivity.class));
                }
                else if (!Network.isAlertShowing()) {
                    Network.showAlert(getApplicationContext(), this);
                }
                return true;
            case R.id.action_with_third:
                Common.thirdPartyLibs = true;
                editor.putBoolean(getString(R.string.third), Common.thirdPartyLibs);
                editor.apply();
                return true;
            case R.id.action_no_third:
                Common.thirdPartyLibs = false;
                editor.putBoolean(getString(R.string.third), Common.thirdPartyLibs);
                editor.apply();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}
