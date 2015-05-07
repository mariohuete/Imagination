package com.mariohuete.imagination.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.mariohuete.imagination.R;
import com.mariohuete.imagination.models.Album;

import java.util.HashMap;
import java.util.List;

/**
 *
 * Created by Mario Huete Jiménez on 05/05/15.
 */
public class CustomExpandableAdapter extends BaseExpandableListAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private String listDataHeader;
    private HashMap<String, List<Album>> albumItems;
    //ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomExpandableAdapter(Activity activity, String listDataHeader,
                                   HashMap<String, List<Album>> albumItems) {
        this.activity = activity;
        this.listDataHeader = listDataHeader;
        this.albumItems = albumItems;
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
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, null);
        }
        /*ImageView header = (ImageView) convertView
                .findViewById(R.id.albums);
        //lblListHeader.setTypeface(null, Typeface.BOLD);
        header.setText(headerTitle);*/

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        //final String childText = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expandable_row, null);
        }
        TextView txtListChild = (TextView) convertView.findViewById(R.id.name);
        txtListChild.setText(albumItems.get(listDataHeader).get(childPosition).getTitle());
        ImageView thumbNail = (ImageView) convertView.findViewById(R.id.thumbnail);
        Ion.with(activity)
                .load(albumItems.get(listDataHeader).get(childPosition).getPicture())
                .withBitmap()
                .placeholder(R.drawable.holder)
                .error(R.drawable.holder)
                .intoImageView(thumbNail);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}