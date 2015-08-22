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
import android.support.annotation.NonNull;
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
import org.songfamily.tiem.nanodegree.app1.helpers.GlobalData;
import org.songfamily.tiem.nanodegree.app1.helpers.ImageHelper;
import org.songfamily.tiem.nanodegree.app1.helpers.PlaybackService;
import org.songfamily.tiem.nanodegree.app1.helpers.StringHelper;
import org.songfamily.tiem.nanodegree.app1.helpers.TrackState;

import java.util.concurrent.TimeUnit;

import kaaes.spotify.webapi.android.models.Track;


public class PlayTrackActivityFragment extends DialogFragment
    implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    public static final String TRACK_LIST_BUNDLE = "trackListBundle";
    public static final String TRACK_SELECTED = "trackSelect";
    public static final String TAG = "PlayTrackActivityFragment";
    public static final String RESUME_CONTROL = "resumeControl";

    private PlaybackService mService;
    private boolean mServiceBound = false;
    private BroadcastReceiver mBroadcastReceiver;

    private Bundle mTrackListBundle;
    private int mTrackSelected;
    private TextView tvAlbumName;
    private TextView tvArtistName;
    private TextView tvTrackName;
    private ImageView ivAlbumImage;
    private View progressBar;
    private SeekBar seekBar;
    private TextView tvElapsedTime;
    private TextView tvTrackLength;
    private ImageView ivPrev;
    private ImageView ivPlayPause;
    private ImageView ivNext;

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

        // fetch track information from arguments or savedInstanceState
        boolean resumeControl = getArguments().getBoolean(RESUME_CONTROL);
        if (savedInstanceState == null) {
            if (!resumeControl) {
                mTrackListBundle = getArguments().getBundle(TRACK_LIST_BUNDLE);
                mTrackSelected = getArguments().getInt(TRACK_SELECTED);
            }
        } else {
            mTrackListBundle = savedInstanceState.getBundle(TRACK_LIST_BUNDLE);
            mTrackSelected = savedInstanceState.getInt(TRACK_SELECTED);
        }

        if (resumeControl) {
            GlobalData globalData = GlobalData.getInstance();
            mTrackListBundle = globalData.currentTrackList;
            mTrackSelected = globalData.currentSelectedTrack;
        } else {
            GlobalData globalData = GlobalData.getInstance();
            globalData.currentTrackList = mTrackListBundle;
            globalData.currentSelectedTrack = mTrackSelected;
        }

        tvAlbumName = (TextView) view.findViewById(R.id.pt_tv_album_name);
        tvArtistName = (TextView) view.findViewById(R.id.pt_tv_artist_name);
        tvTrackName = (TextView) view.findViewById(R.id.pt_tv_track_name);
        ivAlbumImage = (ImageView) view.findViewById(R.id.pt_iv_album_image);
        progressBar = view.findViewById(R.id.pt_progress_bar);
        seekBar = (SeekBar) view.findViewById(R.id.pt_seek_bar);
        tvElapsedTime = (TextView) view.findViewById(R.id.pt_tv_elapsed_time);
        tvTrackLength = (TextView) view.findViewById(R.id.pt_tv_track_length);
        ivPrev = (ImageView) view.findViewById(R.id.pt_iv_previous_track);
        ivPlayPause = (ImageView) view.findViewById(R.id.pt_iv_play_pause_track);
        ivNext = (ImageView) view.findViewById(R.id.pt_iv_next_track);
        updateViewsWithTrackInfo();

        // set up play / pause, next, previous buttons and seek bar
        view.findViewById(R.id.pt_iv_play_pause_track).setOnClickListener(this);
        view.findViewById(R.id.pt_iv_next_track).setOnClickListener(this);
        view.findViewById(R.id.pt_iv_previous_track).setOnClickListener(this);
        seekBar.setEnabled(false);
        seekBar.setOnSeekBarChangeListener(this);

        if (GlobalData.getInstance().isTablet) {
            View shareView = view.findViewById(R.id.pt_iv_share_track);
            shareView.setVisibility(View.VISIBLE);
            shareView.setOnClickListener(this);
        }

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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBundle(TRACK_LIST_BUNDLE, mTrackListBundle);
        outState.putInt(TRACK_SELECTED, mTrackSelected);
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

        if (!GlobalData.getInstance().isTablet) {
            menu.clear(); // to prevent duplicates on device rotation
            inflater.inflate(R.menu.menu_share, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            shareTrackIntent();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void shareTrackIntent() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, StringHelper.getShareText(getSelectedTrack()));
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, getActivity().getString(R.string.share_track_via)));
    }

    @Override
    public void onClick(View view) {
        if (!mServiceBound) {
            bindToService();
        }

        // TODO: inline these?
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
            case R.id.pt_iv_share_track:
                shareTrackIntent();
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // no-op
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // no-op
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mService.setProgress(seekBar.getProgress());
    }
    // *** begin playback helper methods **************************************
    private void playButtonClicked() {
        mService.onPlayPauseAction();
    }

    private void nextButtonClicked() {
        mService.onNextAction();
    }

    private void previousButtonClicked() {
        mService.onPreviousAction();
    }
    // *** end playback helper methods ****************************************

    // *** begin service helper methods ***************************************
    private void bindToService() {
        Intent intent = new Intent(getActivity(), PlaybackService.class);
        intent.putExtra(TRACK_LIST_BUNDLE, mTrackListBundle);
        intent.putExtra(TRACK_SELECTED, mTrackSelected);
        getActivity().startService(intent);
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

            if (mService.isMediaPlayerNull()) {
                playButtonClicked();
            } else {
                updateSeekBar();
                updatePlayPauseButton();
            }
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
                    case (PlaybackService.TRACK_PREPARING):
                        setViewsClickability(false);
                        progressBar.setVisibility(View.VISIBLE);
                        break;
                    case (PlaybackService.TRACK_PREPARED):
                        setViewsClickability(true);
                        progressBar.setVisibility(View.GONE);
                        updateSeekBar();
                        break;
                    case (PlaybackService.TRACK_CHANGED):
                        mTrackSelected = mService.getTrackSelected();
                        updateViewsWithTrackInfo();
                        break;
                    case (PlaybackService.UPDATE_PLAY_PAUSE):
                        updatePlayPauseButton();
                        break;
                    case (PlaybackService.TRACK_COMPLETED):
                        seekBar.setProgress(0);
                        seekBar.setEnabled(false);
                        tvElapsedTime.setText(getTime(0));
                        ivPlayPause.setImageResource(android.R.drawable.ic_media_play);
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
    private void setViewsClickability(boolean clickable) {
        ivPrev.setClickable(clickable);
        ivPlayPause.setClickable(clickable);
        ivNext.setClickable(clickable);
    }

    private void updateViewsWithTrackInfo() {
        Track track = getSelectedTrack();
        String albumImageUrl = ImageHelper.getImageUrl(track.album.images);
        String albumName = track.album.name;
        String artistName = track.artists.get(0).name;
        String trackName = track.name;

        tvAlbumName.setText("Placeholder album name");
        tvArtistName.setText(artistName);
        tvTrackName.setText(trackName);
        Picasso.with(getActivity())
                .load(albumImageUrl)
                .into(ivAlbumImage);
    }

    private void updateSeekBar() {
        int trackLength = mService.getTrackLength();
        seekBar.setEnabled(true);
        seekBar.setMax(trackLength);
        tvTrackLength.setText(getTime(trackLength));
    }

    private void updatePlayPauseButton() {
        TrackState trackState = mService.getTrackState();

        if (trackState == TrackState.PLAYING) {
            ivPlayPause.setImageResource(android.R.drawable.ic_media_pause);
        } else if (trackState == TrackState.PAUSED) {
            ivPlayPause.setImageResource(android.R.drawable.ic_media_play);
        } // no-op otherwise
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
