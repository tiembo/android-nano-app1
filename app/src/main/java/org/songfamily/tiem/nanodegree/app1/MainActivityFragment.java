package org.songfamily.tiem.nanodegree.app1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.songfamily.tiem.nanodegree.app1.helpers.BundleHelper;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivityFragment extends BaseFragment
        implements Callback<ArtistsPager>, AdapterView.OnItemClickListener, TextView.OnEditorActionListener {

    private SearchResultsAdapter mAdapter;
    private EditText mEditText;
    private ViewSwitcher mViewSwitcher;
    private List<Artist> mArtlistList;

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

        // set listener for when the user taps on a list item
        listView.setOnItemClickListener(this);

        // set listener for when user submits their EditText query
        mEditText = (EditText) view.findViewById(R.id.et_search);
        mEditText.setOnEditorActionListener(this);

        // used to show / hide progress bar during network activity
        mViewSwitcher = (ViewSwitcher) view.findViewById(R.id.vs_search);

        // use artist data from Bundle, if available
        if (savedInstanceState != null) {
            mArtlistList = BundleHelper.getArtistList(savedInstanceState);
            populateAdapter();
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mArtlistList != null)
            BundleHelper.putArtistList(outState, mArtlistList);
    }

    private void searchForArtist(String artist) {
        mViewSwitcher.showNext();
        mService.searchArtists(artist, this);
    }

    private void populateAdapter() {
        mAdapter.clear();
        mAdapter.addAll(mArtlistList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void success(final ArtistsPager artistsPager, Response response) {

        // update adapter with data from server
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mViewSwitcher.showNext();
                mArtlistList = artistsPager.artists.items;
                if (mArtlistList.size() > 0) {
                    populateAdapter();
                    hideSoftKeyboard();
                } else {
                    showToast(R.string.no_search_results);
                }
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

    // Navigates user to view an artist's track list
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Artist artist = mAdapter.getItem(i);
        Intent intent = new Intent(getActivity(), ArtistTracksActivity.class);
        intent.putExtra(ArtistTracksActivityFragment.ARTIST_ID_EXTRA, artist.id);
        intent.putExtra(ArtistTracksActivityFragment.ARTIST_NAME_EXTRA, artist.name);
        startActivity(intent);
    }

    // Handles keyboard request to start search
    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_SEARCH || hardwareEnterKeyPressed(keyEvent)) {
            String searchTerm = textView.getText().toString();

            if (searchTerm.isEmpty())
                showToast(R.string.empty_search_field);
            else
                searchForArtist(searchTerm);
            return true;
        }
        return false;
    }

    private boolean hardwareEnterKeyPressed(KeyEvent keyEvent) {
        return (keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER);
    }

    // from http://stackoverflow.com/a/1109108/887198
    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }
}
