package com.mariohuete.imagination.activities;

import android.content.Intent;
import android.content.SharedPreferences;
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

import butterknife.ButterKnife;
import butterknife.InjectView;


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
    @InjectView(R.id.toolbar) Toolbar toolbar;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_list);
        // View injection
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        // Set custom logo in the toolbar.
        getSupportActionBar().setLogo(R.drawable.logo_long);
        if (findViewById(R.id.artist_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((ArtistListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.artist_list))
                    .setActivateOnItemClick(true);
        }
    }

    /**
     * Callback method from {@link ArtistListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(Artist id) {
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
        }
        else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, ArtistDetailActivity.class);
            detailIntent.putExtra(ArtistDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
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
        int id = item.getItemId();
        // noinspection SimplifiableIfStatement
        // Refresh content
        if(id == R.id.action_settings) {
            return true;
        }
        // App works with third party libraries(Retrofit, ButterKnife and Ion)
        if(id == R.id.action_with_third) {
            Common.thirdPartyLibs = true;
            editor.putBoolean(getString(R.string.third), Common.thirdPartyLibs);
            editor.commit();
            return true;
        }
        // App works without third party libraries
        if(id == R.id.action_no_third) {
            Common.thirdPartyLibs = false;
            editor.putBoolean(getString(R.string.third), Common.thirdPartyLibs);
            editor.commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
