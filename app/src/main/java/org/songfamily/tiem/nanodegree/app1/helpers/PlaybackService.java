package org.songfamily.tiem.nanodegree.app1.helpers;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import org.songfamily.tiem.nanodegree.app1.MainActivity;
import org.songfamily.tiem.nanodegree.app1.PlayTrackActivityFragment;
import org.songfamily.tiem.nanodegree.app1.R;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import kaaes.spotify.webapi.android.models.Track;

public class PlaybackService extends Service
    implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener {

    public static final int SERVICE_ID = 8675309;

    private Bundle mTrackListBundle;
    private int mTrackSelected;
    private MediaPlayer mMediaPlayer = null;
    final Handler mHandler = new Handler();

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public PlaybackService getService() {
            // Return this instance of PlaybackService so clients can call public methods
            return PlaybackService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        mTrackListBundle = intent.getBundleExtra(PlayTrackActivityFragment.TRACK_LIST_BUNDLE);
        mTrackSelected = intent.getIntExtra(PlayTrackActivityFragment.TRACK_SELECTED, 0);
        return mBinder;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        broadcastTrackPrepared();
        startElapsedTimeRunnable();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        stopForeground(true);
        mMediaPlayer = null;
        mHandler.removeCallbacks(elapsedTimeRunnable);
        broadcastElapsedTime(0);
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    public int getmTrackSelected() {
        return mTrackSelected;
    }

    public void onPlayPauseAction() {
        if (mMediaPlayer == null) {
            startTrack();
        } else {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            } else {
                mMediaPlayer.start();
            }
        }
    }

    public void onNextAction() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer = null;
        }

        mTrackSelected++;
        if (mTrackSelected > BundleHelper.getTrackList(mTrackListBundle).size() - 1) {
            mTrackSelected = 0;
        }

        startTrack();
    }

    // TODO: go to beginning of track if x seconds of the track has already been played
    public void onPreviousAction() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer = null;
        }

        mTrackSelected--;
        if (mTrackSelected < 0) {
            mTrackSelected = BundleHelper.getTrackList(mTrackListBundle).size() - 1;
        }

        startTrack();
    }

    private void startTrack() {
        Track track = BundleHelper.getTrackList(mTrackListBundle).get(mTrackSelected);

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);

        try {
            mMediaPlayer.setDataSource(track.preview_url);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String trackName = track.name;
        String artistName = track.artists.get(0).name;
        Notification notification = buildNotification(trackName, artistName);
        startForeground(SERVICE_ID, notification);
    }

    private Notification buildNotification(String trackName, String artistName) {
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Builder(getApplicationContext())
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_library_music_black_48dp)
                .setContentTitle(trackName)
                .setContentText(artistName)
                .setContentIntent(pi)
                .build();
    }

    // *** begin broadcast methods ********************************************
    static final public String SERVICE_FILTER = "PlaybackServiceFilter";
    static final public String SERVICE_MESSAGE = "PlaybackServiceMessage";
    static final public String SERVICE_DATA = "PlaybackServiceData";
    static final public String TRACK_PREPARED = "TrackPrepared";
    static final public String ELAPSED_TIME = "ElapsedTime";

    private void broadcastTrackPrepared() {
        broadcastIntent(getBroadcastIntent().putExtra(SERVICE_MESSAGE, TRACK_PREPARED));
    }

    private void broadcastElapsedTime(int time) {
        broadcastIntent(getBroadcastIntent()
                .putExtra(SERVICE_MESSAGE, ELAPSED_TIME)
                .putExtra(SERVICE_DATA, time));
    }

    private Intent getBroadcastIntent() {
        return new Intent(SERVICE_FILTER);
    }

    private void broadcastIntent(Intent intent) {
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }
    // *** end broadcast methods **********************************************

    // *** begin handler and runnables methods ********************************
    private void startElapsedTimeRunnable() {
        mHandler.post(elapsedTimeRunnable);
    }

    Runnable elapsedTimeRunnable = new Runnable() {
        @Override
        public void run() {
            broadcastElapsedTime(mMediaPlayer.getCurrentPosition());
            mHandler.postDelayed(this, 100);
        }
    };
    // *** end handler and runnables methods **********************************

    // *** begin helper methods ***********************************************
    public int getTrackLength() {
        return mMediaPlayer.getDuration();
    }
    // *** end helper methods *************************************************
}
