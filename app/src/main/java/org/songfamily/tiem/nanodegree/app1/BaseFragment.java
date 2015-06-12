package org.songfamily.tiem.nanodegree.app1;

import android.app.Activity;
import android.app.Fragment;
import android.view.Gravity;
import android.widget.Toast;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;

public abstract class BaseFragment extends Fragment {
    protected SpotifyService mService;
    private Toast mToast;

    public BaseFragment() {

        // initialize Spotify API service
        if (mService == null) {
            SpotifyApi api = new SpotifyApi();
            mService = api.getService();
        }
    }

    protected void showToast(int stringId) {
        if (mToast != null)
            mToast.cancel();

        Activity activity = getActivity();
        mToast = Toast.makeText(activity, activity.getString(stringId), Toast.LENGTH_LONG);
        mToast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
        mToast.show();
    }
}
