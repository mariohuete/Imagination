package com.mariohuete.imagination.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.mariohuete.imagination.R;
import com.mariohuete.imagination.activities.ArtistDetailActivity;
import com.mariohuete.imagination.activities.ArtistListActivity;
import com.mariohuete.imagination.adapters.CustomExpandableAdapter;
import com.mariohuete.imagination.models.Album;
import com.mariohuete.imagination.models.Artist;
import com.mariohuete.imagination.utils.Common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * A fragment representing a single Artist detail screen.
 * This fragment is either contained in a {@link ArtistListActivity}
 * in two-pane mode (on tablets) or a {@link ArtistDetailActivity}
 * on handsets.
 */
public class ArtistDetailFragment extends Fragment {
    @InjectView(R.id.cover) ImageView image;
    @InjectView(R.id.genres) TextView genres;
    @InjectView(R.id.description) TextView desc;
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The content this fragment is presenting.
     */
    private Artist mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArtistDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = (Artist)getArguments().getSerializable(ARG_ITEM_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artist_detail, container, false);
        ButterKnife.inject(this, rootView);
        // Show the content.
        if (mItem != null) {
            Ion.with(getActivity())
                    .load(mItem.getPicture())
                    .withBitmap()
                    .placeholder(R.drawable.cover)
                    .error(R.drawable.cover)
                    .intoImageView(image);
            genres.setText(mItem.getGenres());
            desc.setText(Html.fromHtml(mItem.getDescription()));
            desc.setMovementMethod(LinkMovementMethod.getInstance());
            int i = 0;
            boolean stop = false;
            List<Album> albums = new ArrayList<>();
            // Albums are ordered by artist, so 'stop' variable is used to stop the loop when
            // reach all artist's albums for efficiency.
            while(i < Common.albumList.size()) {
                if(Common.albumList.get(i).getArtistId() == mItem.getId()) {
                    albums.add(Common.albumList.get(i));
                    stop = true;
                }
                else {
                    if(stop)
                        i = Common.albumList.size();
                }
                i++;
            }
            ExpandableListView list = ((ExpandableListView) rootView.findViewById(R.id.album_list));
            HashMap<String, List<Album>> map = new HashMap<>();
            map.put(getString(R.string.albums), albums);
            CustomExpandableAdapter customAdapter = new CustomExpandableAdapter(getActivity(), 
                    getString(R.string.albums), map);
            list.setAdapter(customAdapter);
        }
        return rootView;
    }
}
