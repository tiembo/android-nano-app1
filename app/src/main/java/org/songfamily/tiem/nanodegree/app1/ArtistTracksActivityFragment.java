package org.songfamily.tiem.nanodegree.app1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ArtistTracksActivityFragment extends BaseFragment
    implements Callback<Tracks> {

    public static final String ARTIST_ID_EXTRA = "artistId";
    public static final String ARTIST_NAME_EXTRA = "artistName";
    public static final String COUNTRY_ID = "US";
    private ArtistTracksAdapter mAdapter;

    public ArtistTracksActivityFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist_tracks, container, false);

        // initialize Adapter and set to ListView
        mAdapter = new ArtistTracksAdapter(getActivity(), new ArrayList<Track>());
        ListView listView = (ListView) view.findViewById(R.id.lv_tracks);
        listView.setAdapter(mAdapter);

        // set action bar subtitle with artists's name
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        String artistName = activity.getIntent().getStringExtra(ARTIST_NAME_EXTRA);
        android.support.v7.app.ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null)
            actionBar.setSubtitle(artistName);

        // search Spotify for the artist's top tracks
        String artistId = activity.getIntent().getStringExtra(ARTIST_ID_EXTRA);
        searchForTracks(artistId, COUNTRY_ID);

        return view;
    }

    private void searchForTracks(String artistId, String countryCode) {
        Map<String, Object> params = new HashMap<>(1);
        params.put("country", countryCode);

        mService.getArtistTopTrack(artistId, params, this);
    }

    @Override
    public void success(final Tracks tracks, Response response) {

        // update adapter with data from server
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                List<Track> trackList = tracks.tracks;

                if (trackList.size() == 0) {
                    showToast(R.string.no_tracks);
                } else {
                    mAdapter.clear();
                    mAdapter.addAll(trackList); // server returns a maximum of 10 results
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void failure(RetrofitError error) {

    }
}
