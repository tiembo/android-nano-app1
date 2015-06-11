package org.songfamily.tiem.nanodegree.app1;

import android.app.Fragment;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;

public abstract class BaseFragment extends Fragment {
    protected SpotifyService mService;

    public BaseFragment() {

        // initialize Spotify API service
        if (mService == null) {
            SpotifyApi api = new SpotifyApi();
            mService = api.getService();
        }
    }
}
