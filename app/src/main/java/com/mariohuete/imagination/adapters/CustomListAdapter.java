package com.mariohuete.imagination.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.mariohuete.imagination.R;
import com.mariohuete.imagination.models.Artist;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 *
 * Created by Mario Huete Jiménez on 08/05/15.
 */
public class CustomListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Artist> artistItems;

    public CustomListAdapter(Activity activity, List<Artist> movieItems) {
        this.activity = activity;
        this.artistItems = movieItems;
    }

    static class ViewHolder{
        @InjectView(R.id.thumbnail)
        ImageView thumbNail;
        @InjectView(R.id.name)
        TextView name;
        @InjectView(R.id.genre)
        TextView genre;

        public ViewHolder(View view){
            ButterKnife.inject(this, view);
        }
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
            convertView = inflater.inflate(R.layout.list_row, parent, false);
        ViewHolder holder = new ViewHolder(convertView);
        // Getting artist data for the row
        Artist a = artistItems.get(position);
        // Thumbnail image
        Ion.with(activity)
                .load(a.getPicture())
                .withBitmap()
                .placeholder(R.drawable.holder)
                .error(R.drawable.holder)
                .intoImageView(holder.thumbNail);
        // Artist's name
        holder.name.setText(a.getName());
        // Genres
        holder.genre.setText(a.getGenres());
        return convertView;
    }

}