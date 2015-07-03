package org.songfamily.tiem.nanodegree.app1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ViewSwitcher;

import org.songfamily.tiem.nanodegree.app1.helpers.BundleHelper;

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
    implements Callback<Tracks>, ListView.OnItemClickListener {

    public static final String ARTIST_ID_EXTRA = "artistId";
    public static final String ARTIST_NAME_EXTRA = "artistName";
    public static final String COUNTRY_ID = "US";
    private ArtistTracksAdapter mAdapter;
    private List<Track> mTrackList;
    private ViewSwitcher mViewSwitcher;

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
        listView.setOnItemClickListener(this);

        // set action bar subtitle with artists's name
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        String artistName = getArguments().getString(ARTIST_NAME_EXTRA);
        android.support.v7.app.ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null)
            actionBar.setSubtitle(artistName);

        // used to show / hide progress bar during network activity
        mViewSwitcher = (ViewSwitcher) view.findViewById(R.id.vs_tracks);

        // fetch track list from Spotify if needed; otherwise use Bundle data
        if (savedInstanceState == null) {
            String artistId = getArguments().getString(ARTIST_ID_EXTRA);
            searchForTracks(artistId, COUNTRY_ID);
        } else {
            mTrackList = BundleHelper.getTrackList(savedInstanceState);
            populateAdapter();
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mTrackList != null)
            BundleHelper.putTrackList(outState, mTrackList);
    }

    private void searchForTracks(String artistId, String countryCode) {
        mViewSwitcher.showNext();

        Map<String, Object> params = new HashMap<>(1);
        params.put("country", countryCode);

        mService.getArtistTopTrack(artistId, params, this);
    }

    private void populateAdapter() {
        mAdapter.clear();
        mAdapter.addAll(mTrackList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void success(final Tracks tracks, Response response) {

        // update adapter with data from server
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mViewSwitcher.showNext();
                mTrackList = tracks.tracks;

                if (mTrackList.size() == 0)
                    showToast(R.string.no_tracks);
                else
                    populateAdapter();
            }
        });
    }

    @Override
    public void failure(RetrofitError error) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mViewSwitcher.showNext();
                showToast(R.string.network_error);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Bundle bundle = new Bundle();
        BundleHelper.putTrackList(bundle, mTrackList);

        Intent intent = new Intent(getActivity(), PlayTrackActivity.class);
        intent.putExtra(PlayTrackActivityFragment.TRACK_LIST_BUNDLE, bundle);
        intent.putExtra(PlayTrackActivityFragment.TRACK_SELECTED, i);
        startActivity(intent);
    }
}
