package org.songfamily.tiem.nanodegree.app1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivityFragment extends BaseFragment
        implements Callback<ArtistsPager>, AdapterView.OnItemClickListener {

    private SearchResultsAdapter mAdapter;

    public MainActivityFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        // initialize Adapter and set to ListView
        mAdapter = new SearchResultsAdapter(getActivity(), new ArrayList<Artist>());
        ListView listView = (ListView) view.findViewById(R.id.lv_search_results);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);

        // placeholder - automatically search for now
        searchForArtist("coldplay");

        return view;
    }

    private void searchForArtist(String artist) {
        mService.searchArtists(artist, this);
    }

    @Override
    public void success(final ArtistsPager artistsPager, Response response) {

        // update adapter with data from server
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.clear();
                mAdapter.addAll(artistsPager.artists.items);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void failure(RetrofitError error) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Artist artist = mAdapter.getItem(i);
        Intent intent = new Intent(getActivity(), ArtistTracksActivity.class);
        intent.putExtra(ArtistTracksActivityFragment.ARTIST_ID_EXTRA, artist.id);
        intent.putExtra(ArtistTracksActivityFragment.ARTIST_NAME_EXTRA, artist.name);
        startActivity(intent);
    }
}
