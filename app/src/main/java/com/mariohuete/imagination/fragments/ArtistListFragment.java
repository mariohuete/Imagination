package com.mariohuete.imagination.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.mariohuete.imagination.R;
import com.mariohuete.imagination.adapters.CustomListAdapter;
import com.mariohuete.imagination.models.Album;
import com.mariohuete.imagination.models.Artist;
import com.mariohuete.imagination.models.Data;
import com.mariohuete.imagination.utils.Common;
import com.mariohuete.imagination.utils.ImageLoader;
import com.mariohuete.imagination.utils.RestApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A list fragment representing a list of Artists. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link ArtistDetailFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class ArtistListFragment extends ListFragment {
    private Context context;
    private ProgressDialog pd;
    private CustomListAdapter customAdapter;
    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sArtistCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        void onItemSelected(Artist id);
    }

    /**
     * A implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sArtistCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(Artist id) {
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArtistListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        pd = new ProgressDialog(context);
        pd.setMessage(getString(R.string.loading));
        pd.show();
        dataRequest(Common.thirdPartyLibs);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // Reset the active callbacks interface to the implementation.
        mCallbacks = sArtistCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        mCallbacks.onItemSelected(Common.artistList.get(position));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }
        mActivatedPosition = position;
    }

    public void dataRequest(boolean thirdPartyLibs) {
        if(thirdPartyLibs) {
            // If use Retrofit clear directory and files
            ImageLoader.clearCache();
            // Use retrofit to get json data
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(getString(R.string.end_point)).build();
            RestApi api = restAdapter.create(RestApi.class);
            api.getData(new Callback<Data>() {
                @Override
                public void success(Data model, Response response) {
                    Common.artistList = model.getArtists();
                    Common.albumList = model.getAlbums();
                    customAdapter = new CustomListAdapter(getActivity(),
                            Common.artistList);
                    setListAdapter(customAdapter);
                    if (pd.isShowing())
                        pd.dismiss();
                    Toast.makeText(context, getString(R.string.success), Toast.LENGTH_LONG).show();
                }

                @Override
                public void failure(RetrofitError error) {
                    if (pd.isShowing())
                        pd.dismiss();
                    Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
        else {
            //TODO change for AsyncTask
            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {
                    JSONObject obj = getJSON(getString(R.string.end_point) + "/data.json", 20000);
                    try {
                        Common.artistList = parseJSONArrayArtists(obj.getJSONArray(
                                getString(R.string.artists)));
                        Common.albumList = parseJSONArrayAlbums(obj.getJSONArray(
                                getString(R.string.albums)));
                        /*Toast.makeText(context, getString(R.string.success),
                                Toast.LENGTH_LONG).show();*/
                    } catch (JSONException e) {
                        /*Toast.makeText(context, getString(R.string.error),
                                Toast.LENGTH_LONG).show();*/
                        e.printStackTrace();
                    }
                }
            });
            th.start();
            try {
                th.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            customAdapter = new CustomListAdapter(getActivity(), Common.artistList);
            setListAdapter(customAdapter);
            if (pd.isShowing())
                pd.dismiss();
        }
        // Retrieve preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        // Store preferences when data is retrieved to not show splash screen again
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(getString(R.string.show_splash), false);
        editor.commit();
    }

    public JSONObject getJSON(String url, int timeout) {
        HttpURLConnection c = null;
        try {
            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(timeout);
            c.setReadTimeout(timeout);
            c.connect();
            int status = c.getResponseCode();
            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    br.close();
                    try {
                        return new JSONObject(sb.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;
    }

    public List<Artist> parseJSONArrayArtists(JSONArray arr) {
        JSONObject obj;
        if (arr != null) {
            int len = arr.length();
            for (int i = 0; i < len; i++){
                try {
                    obj = arr.getJSONObject(i);
                Common.artistList.add(new Artist(obj.getDouble(getString(R.string.id)),
                        obj.getString(getString(R.string.genres)),
                        obj.getString(getString(R.string.picture)),
                        obj.getString(getString(R.string.name)),
                        obj.getString(getString(R.string.description))));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return Common.artistList;
    }

    public List<Album> parseJSONArrayAlbums(JSONArray arr) {
        JSONObject obj;
        if (arr != null) {
            int len = arr.length();
            for (int i = 0; i < len; i++){
                try {
                    obj = arr.getJSONObject(i);
                    Common.albumList.add(new Album(obj.getDouble(getString(R.string.id)),
                            obj.getDouble(getString(R.string.artistId)),
                            obj.getString(getString(R.string.title)),
                            obj.getString(getString(R.string.type)),
                            obj.getString(getString(R.string.picture))));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return Common.albumList;
    }

}
