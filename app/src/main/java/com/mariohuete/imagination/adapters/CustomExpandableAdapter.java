package com.mariohuete.imagination.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.mariohuete.imagination.R;
import com.mariohuete.imagination.models.Album;
import com.mariohuete.imagination.utils.Common;
import com.mariohuete.imagination.utils.ImageLoader;

import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 *
 * Created by Mario Huete Jiménez on 08/05/15.
 */
public class CustomExpandableAdapter extends BaseExpandableListAdapter {
    private Activity activity;
    private String listDataHeader;
    private HashMap<String, List<Album>> albumItems;

    static class ViewHolder{
        @InjectView(R.id.thumbnail)
        ImageView thumbNail;
        @InjectView(R.id.name)
        TextView txtListChild;

        public ViewHolder(View view){
            // View injection in parent view
            ButterKnife.inject(this, view);
        }
    }

    public CustomExpandableAdapter(Activity activity, String listDataHeader,
                                   HashMap<String, List<Album>> albumItems) {
        this.activity = activity;
        this.listDataHeader = listDataHeader;
        this.albumItems = albumItems;
        // If thirdPartyLibs == false, use custom image cache
        if(!Common.thirdPartyLibs) {
            // Create ImageLoader object to download and show image in expandable list
            // Call ImageLoader constructor to initialize FileCache
            new ImageLoader(activity.getApplicationContext());
        }
    }

    @Override
    public int getGroupCount() {
        return 1;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return albumItems.get(listDataHeader).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listDataHeader;
    }

    @Override
    public Object getChild(int groupPosition, int childPositition) {
        return albumItems.get(listDataHeader).get(childPositition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, parent, false);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expandable_row, parent, false);
        }
        ViewHolder holder = new ViewHolder(convertView);
        holder.txtListChild.setText(albumItems.get(listDataHeader).get(childPosition).getTitle());
        // Thumbnail image: if thirdPartyLibs == true -> load with Ion, else -> custom image cache
        if(Common.thirdPartyLibs) {
            Ion.with(activity)
                    .load(albumItems.get(listDataHeader).get(childPosition).getPicture())
                    .withBitmap()
                    .placeholder(R.drawable.holder)
                    .error(R.drawable.holder)
                    .intoImageView(holder.thumbNail);
        }
        else {
            //DisplayImage function from ImageLoader Class
            ImageLoader.displayImage(albumItems.get(listDataHeader).get(childPosition).getPicture(),
                    holder.thumbNail);
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

}