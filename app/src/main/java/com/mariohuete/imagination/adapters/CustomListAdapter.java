package com.mariohuete.imagination.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.mariohuete.imagination.R;
import com.mariohuete.imagination.models.Artist;

import java.io.IOException;
import java.net.URL;
import java.util.List;


/**
 *
 * Created by Mario Huete Jiménez on 03/05/15.
 */
public class CustomListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Artist> artistItems;
    //ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomListAdapter(Activity activity, List<Artist> movieItems) {
        this.activity = activity;
        this.artistItems = movieItems;
    }

    @Override
    public int getCount() {
        return artistItems.size();
    }

    @Override
    public Object getItem(int location) {
        return artistItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row, null);

        /*if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();*/
        ImageView thumbNail = (ImageView) convertView
                .findViewById(R.id.thumbnail);
        TextView title = (TextView) convertView.findViewById(R.id.name);
        //TextView rating = (TextView) convertView.findViewById(R.id.rating);
        TextView genre = (TextView) convertView.findViewById(R.id.genre);
        //TextView year = (TextView) convertView.findViewById(R.id.releaseYear);

        // getting movie data for the row
        Artist a = artistItems.get(position);

        // thumbnail image
        /*this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                URL newurl;
                Bitmap mIcon_val = null;
                try {
                    newurl = new URL(a.getPicture());
                    if(android.os.Build.VERSION.SDK_INT > 8) {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                .permitAll().build();
                        StrictMode.setThreadPolicy(policy);

                    }
                    mIcon_val = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                thumbNail.setImageBitmap(mIcon_val);
            }
        });*/
        Ion.with(activity)
                .load(a.getPicture())
                .withBitmap()
                .placeholder(R.drawable.holder)
                .error(R.drawable.holder)
                .intoImageView(thumbNail);

        // title
        title.setText(a.getName());

        // rating
        //rating.setText("Rating: " + "3");//String.valueOf(a.getRating()));

        // genre
        genre.setText(a.getGenres());
        /*String genreStr = "";
        for (String str : m.getGenre()) {
            genreStr += str + ", ";
        }
        genreStr = genreStr.length() > 0 ? genreStr.substring(0,
                genreStr.length() - 2) : genreStr;
        genre.setText(genreStr);*/

        // release year
        //year.setText("1985");//String.valueOf(m.getYear()));

        return convertView;
    }
}