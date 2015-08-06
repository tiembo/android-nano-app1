package org.songfamily.tiem.nanodegree.app1;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.songfamily.tiem.nanodegree.app1.helpers.BundleHelper;
import org.songfamily.tiem.nanodegree.app1.helpers.ImageHelper;
import org.songfamily.tiem.nanodegree.app1.helpers.PlaybackService;
import org.songfamily.tiem.nanodegree.app1.helpers.StringHelper;

import java.util.concurrent.TimeUnit;

import kaaes.spotify.webapi.android.models.Track;


public class PlayTrackActivityFragment extends DialogFragment
    implements View.OnClickListener {

    public static final String TRACK_LIST_BUNDLE = "trackListBundle";
    public static final String TRACK_SELECTED = "trackSelect";
    public static final String TAG = "PlayTrackActivityFragment";

    private PlaybackService mService;
    private boolean mServiceBound = false;
    private BroadcastReceiver mBroadcastReceiver;

    private Bundle mTrackListBundle;
    private int mTrackSelected;
    private TextView tvAlbumName;
    private TextView tvArtistName;
    private TextView tvTrackName;
    private ImageView ivAlbumImage;
    private SeekBar seekBar;
    private TextView tvElapsedTime;
    private TextView tvTrackLength;

    public PlayTrackActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mBroadcastReceiver = initBroadcastReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play_track, container, false);

        // fetch track information from arguments
        mTrackListBundle = getArguments().getBundle(TRACK_LIST_BUNDLE);
        mTrackSelected = getArguments().getInt(TRACK_SELECTED);

        tvAlbumName = (TextView) view.findViewById(R.id.pt_tv_album_name);
        tvArtistName = (TextView) view.findViewById(R.id.pt_tv_artist_name);
        tvTrackName = (TextView) view.findViewById(R.id.pt_tv_track_name);
        ivAlbumImage = (ImageView) view.findViewById(R.id.pt_iv_album_image);
        seekBar = (SeekBar) view.findViewById(R.id.pt_seek_bar);
        tvElapsedTime = (TextView) view.findViewById(R.id.pt_tv_elapsed_time);
        tvTrackLength = (TextView) view.findViewById(R.id.pt_tv_track_length);
        updateViewsWithTrackInfo();

        // set up play / pause, next, and previous button
        view.findViewById(R.id.pt_iv_play_pause_track).setOnClickListener(this);
        view.findViewById(R.id.pt_iv_next_track).setOnClickListener(this);
        view.findViewById(R.id.pt_iv_previous_track).setOnClickListener(this);

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        bindToService();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadcastReceiver,
                new IntentFilter(PlaybackService.SERVICE_FILTER));
    }

    @Override
    public void onStop() {
        super.onStop();

        // Unbind from the service
        if (mServiceBound) {
            getActivity().unbindService(mConnection);
            mServiceBound = false;
        }
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_share, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, StringHelper.getShareText(getSelectedTrack()));
            shareIntent.setType("text/plain");
            startActivity(Intent.createChooser(shareIntent, getActivity().getString(R.string.share_window_title)));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (!mServiceBound) {
            bindToService();
        }

        switch (view.getId()) {
            case R.id.pt_iv_play_pause_track:
                playButtonClicked();
                break;
            case R.id.pt_iv_next_track:
                nextButtonClicked();
                break;
            case R.id.pt_iv_previous_track:
                previousButtonClicked();
                break;
        }
    }

    // *** begin playback helper methods **************************************
    private void playButtonClicked() {
        mService.onPlayPauseAction();
    }

    private void nextButtonClicked() {
        mService.onNextAction();
        mTrackSelected = mService.getmTrackSelected();
        updateViewsWithTrackInfo();
    }

    private void previousButtonClicked() {
        mService.onPreviousAction();
        mTrackSelected = mService.getmTrackSelected();
        updateViewsWithTrackInfo();
    }
    // *** end playback helper methods ****************************************

    // *** begin service helper methods ***************************************
    private void bindToService() {
        Intent intent = new Intent(getActivity(), PlaybackService.class);
        intent.putExtra(TRACK_LIST_BUNDLE, mTrackListBundle);
        intent.putExtra(TRACK_SELECTED, mTrackSelected);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            PlaybackService.LocalBinder binder = (PlaybackService.LocalBinder) service;
            mService = binder.getService();
            mServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mServiceBound = false;
        }
    };

    private BroadcastReceiver initBroadcastReceiver() {
        return new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra(PlaybackService.SERVICE_MESSAGE);

                switch (message) {
                    case (PlaybackService.TRACK_PREPARED):
                        int trackLength = mService.getTrackLength();
                        seekBar.setMax(trackLength);
                        tvTrackLength.setText(getTime(trackLength));
                        break;
                    case (PlaybackService.ELAPSED_TIME):
                        int elapsedTime = intent.getIntExtra(PlaybackService.SERVICE_DATA, 0);
                        seekBar.setProgress(elapsedTime);
                        tvElapsedTime.setText(getTime(elapsedTime));
                        break;
                }
            }
        };
    }
    // *** end service helper methods *****************************************

    // *** begin misc helper methods ******************************************
    private void updateViewsWithTrackInfo() {
        Track track = getSelectedTrack();
        String albumImageUrl = ImageHelper.getImageUrl(track.album.images);
        String albumName = track.album.name;
        String artistName = track.artists.get(0).name;
        String trackName = track.name;

        tvAlbumName.setText(albumName);
        tvArtistName.setText(artistName);
        tvTrackName.setText(trackName);
        Picasso.with(getActivity())
                .load(albumImageUrl)
                .into(ivAlbumImage);
    }

    private Track getSelectedTrack() {
        return BundleHelper.getTrackList(mTrackListBundle).get(mTrackSelected);
    }

    private String getTime(int duration) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        long seconds = (TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(minutes));
        return String.format("%02d:%02d", minutes, seconds);
    }
    // *** end misc helper methods ********************************************
}
