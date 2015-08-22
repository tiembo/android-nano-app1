package org.songfamily.tiem.nanodegree.app1;

import android.app.Activity;
import android.content.Context;
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

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    public static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    private SearchResultsAdapter mAdapter;
    private EditText mEditText;
    private ViewSwitcher mViewSwitcher;
    private List<Artist> mArtlistList;
    private ListView mListView;

    public MainActivityFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        // initialize Adapter and set to ListView
        mAdapter = new SearchResultsAdapter(getActivity(), new ArrayList<Artist>());
        mListView = (ListView) view.findViewById(R.id.lv_search_results);
        mListView.setAdapter(mAdapter);

        // set listener for when the user taps on a list item
        mListView.setOnItemClickListener(this);

        // set listener for when user submits their EditText query
        mEditText = (EditText) view.findViewById(R.id.et_search);
        mEditText.setOnEditorActionListener(this);

        // used to show / hide progress bar during network activity
        mViewSwitcher = (ViewSwitcher) view.findViewById(R.id.vs_search);

        // use artist data from Bundle, if available
        if (savedInstanceState != null) {
            mArtlistList = BundleHelper.getArtistList(savedInstanceState);

            if (mArtlistList != null) {
                populateAdapter();
            }
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
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
        if (mArtlistList != null)
            BundleHelper.putArtistList(outState, mArtlistList);

        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
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
        mCallbacks.onItemSelected(artist.id, artist.name);
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

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        mListView.setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            mListView.setItemChecked(mActivatedPosition, false);
        } else {
            mListView.setItemChecked(position, true);
        }

        mActivatedPosition = position;
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
        void onItemSelected(String artistId, String artistName);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String artistId, String artistName) {}
    };
}
