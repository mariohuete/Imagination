package com.mariohuete.imagination.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.mariohuete.imagination.fragments.ArtistDetailFragment;
import com.mariohuete.imagination.fragments.ArtistListFragment;
import com.mariohuete.imagination.R;
import com.mariohuete.imagination.models.Artist;
import com.mariohuete.imagination.utils.Common;
import com.mariohuete.imagination.utils.Network;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;


/**
 * An activity representing a list of Artists. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ArtistDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ArtistListFragment} and the item details
 * (if present) is a {@link ArtistDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link ArtistListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class ArtistListActivity extends ActionBarActivity implements ArtistListFragment.Callbacks {
    @Optional @InjectView(R.id.toolbar) Toolbar toolbar;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private Network network;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_list);
        if (findViewById(R.id.artist_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
            Common.twoPane = true;
            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((ArtistListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.artist_list))
                    .setActivateOnItemClick(true);
        }
        else {
            // View injection
            ButterKnife.inject(this);
            setSupportActionBar(toolbar);
            // Set custom logo in the toolbar.
            getSupportActionBar().setLogo(R.drawable.logo_long);
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

    /**
     * Callback method from {@link ArtistListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(Artist id) {
        if(Network.isConnected(getApplicationContext())) {
            if (mTwoPane) {
                // In two-pane mode, show the detail view in this activity by
                // adding or replacing the detail fragment using a
                // fragment transaction.
                Bundle arguments = new Bundle();
                arguments.putSerializable(ArtistDetailFragment.ARG_ITEM_ID, id);
                ArtistDetailFragment fragment = new ArtistDetailFragment();
                fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.artist_detail_container, fragment)
                        .commit();
            } else {
                // In single-pane mode, simply start the detail activity
                // for the selected item ID.
                Intent detailIntent = new Intent(this, ArtistDetailActivity.class);
                detailIntent.putExtra(ArtistDetailFragment.ARG_ITEM_ID, id);
                startActivity(detailIntent);
            }
        }
        else if (!Network.isAlertShowing()) {
            Network.showAlert(getApplicationContext(), this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Retrieve preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Store preferences
        SharedPreferences.Editor editor = preferences.edit();
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                if(Network.isConnected(getApplicationContext())) {
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
                else if (!Network.isAlertShowing()){
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
    public void onBackPressed() {
        // Close completely the app if the background is interrupt by clicking back button
        System.exit(0);
    }

}
