package org.songfamily.tiem.nanodegree.app1;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ViewSwitcher;

import org.songfamily.tiem.nanodegree.app1.helpers.BundleHelper;
import org.songfamily.tiem.nanodegree.app1.helpers.MySharedPrefs;

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

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

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
            searchForTracks(artistId, MySharedPrefs.getCountryCode(getActivity()));
        } else {
            mTrackList = BundleHelper.getTrackList(savedInstanceState);
            populateAdapter();
        }

        return view;
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

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
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
        mCallbacks.onItemSelected(bundle, i);
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        void onItemSelected(Bundle trackList, int selectedTrack);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(Bundle trackList, int selectedTrack) {}
    };
}
