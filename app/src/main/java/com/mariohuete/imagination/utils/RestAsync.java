package com.mariohuete.imagination.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.ListFragment;

import com.mariohuete.imagination.R;
import com.mariohuete.imagination.adapters.CustomListAdapter;
import com.mariohuete.imagination.models.Album;
import com.mariohuete.imagination.models.Artist;

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


/**
 *
 * Created by Mario Huete Jiménez on 09/05/15.
 */
public class RestAsync extends AsyncTask<Void, Void, Boolean> {
    private ProgressDialog pd;
    private Context context;
    private ListFragment fragment;
    private CustomListAdapter adapter;

    public RestAsync(Context ctx, ListFragment frg, CustomListAdapter adp) {
        context = ctx;
        fragment = frg;
        adapter = adp;
    }

    @Override
    protected void onPreExecute() {
        pd = new ProgressDialog(context);
        pd.setMessage(context.getString(R.string.loading_async));
        pd.show();
    }

    @Override
    protected Boolean doInBackground(Void... strings) {
        boolean success;
        JSONObject obj = getJSON(context.getString(R.string.end_point) + "/data.json", 20000);
        try {
            Common.artistList = parseJSONArrayArtists(obj.getJSONArray(
                    context.getString(R.string.artists)));
            Common.albumList = parseJSONArrayAlbums(obj.getJSONArray(
                    context.getString(R.string.albums)));
            success = true;
        } catch (JSONException e) {
            success = false;
            e.printStackTrace();
        }
        return success;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        adapter = new CustomListAdapter(fragment.getActivity(), Common.artistList);
        fragment.setListAdapter(adapter);
        // Show by default de first item if app is in two pane model
        if(Common.twoPane)
            fragment.getListView().performItemClick(fragment.getListView().getChildAt(0), 0, 0);
        if (pd.isShowing())
            pd.dismiss();
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
                    Common.artistList.add(new Artist(obj.getDouble(context.getString(R.string.id)),
                            obj.getString(context.getString(R.string.genres)),
                            obj.getString(context.getString(R.string.picture)),
                            obj.getString(context.getString(R.string.name)),
                            obj.getString(context.getString(R.string.description))));
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
                    Common.albumList.add(new Album(obj.getDouble(context.getString(R.string.id)),
                            obj.getDouble(context.getString(R.string.artistId)),
                            obj.getString(context.getString(R.string.title)),
                            //obj.getString(context.getString(R.string.type)),
                            obj.getString(context.getString(R.string.picture))));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return Common.albumList;
    }

}
