package org.songfamily.tiem.nanodegree.app1.helpers;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import org.songfamily.tiem.nanodegree.app1.MainActivity;
import org.songfamily.tiem.nanodegree.app1.PlayTrackActivityFragment;
import org.songfamily.tiem.nanodegree.app1.R;

import java.io.IOException;

import kaaes.spotify.webapi.android.models.Track;

public class PlaybackService extends Service
    implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener {

    public static final int SERVICE_ID = 8675309;
    public static final String PLAY_PAUSE_INTENT = "PLAY_PAUSE_INTENT";
    public static final String PREV_INTENT = "PREV_INTENT";
    public static final String NEXT_INTENT = "NEXT_INTENT";

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
    public int onStartCommand(Intent intent, int flags, int startId) {
        int superCommand = super.onStartCommand(intent, flags, startId);

        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case PLAY_PAUSE_INTENT:
                    onPlayPauseAction();
                    break;
                case NEXT_INTENT:
                    onNextAction();
                    break;
                case PREV_INTENT:
                    onPreviousAction();
                    break;
            }

            return superCommand;
        }

        mTrackListBundle = intent.getBundleExtra(PlayTrackActivityFragment.TRACK_LIST_BUNDLE);
        int trackDesired = intent.getIntExtra(PlayTrackActivityFragment.TRACK_SELECTED, 0);

        // either starting new or changed track
        if (mTrackSelected != trackDesired) {
            mTrackSelected = trackDesired;
            if (mMediaPlayer != null) {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                }
                onCompletion(mMediaPlayer);
            }
        }

        return superCommand;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        playTrack();
        broadcastTrackPrepared();
        startElapsedTimeRunnable();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        stopForeground(true);
        mMediaPlayer = null;
        mHandler.removeCallbacks(elapsedTimeRunnable);
        broadcastTrackCompleted();
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
            prepareTrack();
        } else {
            if (mMediaPlayer.isPlaying()) {
                pauseTrack();
            } else {
                playTrack();
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

        prepareTrack();
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

        prepareTrack();
    }

    private void prepareTrack() {
        broadcastTrackPreparing();
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

    private void playTrack() {
        mMediaPlayer.start();
        broadcastUpdatePlayPause();
    }

    private void pauseTrack() {
        mMediaPlayer.pause();
        broadcastUpdatePlayPause();
    }

    private Notification buildNotification(String trackName, String artistName) {
        PendingIntent pi = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                new Intent(getApplicationContext(), MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_library_music_black_48dp)
                .setContentTitle(trackName)
                .setContentText(artistName)
                .setContentIntent(pi);

        boolean showActionButtons = true;
        if (showActionButtons) {
            Intent prevIntent = new Intent(getApplicationContext(), PlaybackService.class);
            prevIntent.setAction(PREV_INTENT);
            PendingIntent prevPendingIntent = PendingIntent.getService(
                    getApplicationContext(),
                    1,
                    prevIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            Intent playPauseIntent = new Intent(getApplicationContext(), PlaybackService.class);
            playPauseIntent.setAction(PLAY_PAUSE_INTENT);
            PendingIntent playPausePendingIntent = PendingIntent.getService(
                    getApplicationContext(),
                    2,
                    playPauseIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            Intent nextIntent = new Intent(getApplicationContext(), PlaybackService.class);
            nextIntent.setAction(NEXT_INTENT);
            PendingIntent nextPendingIntent = PendingIntent.getService(
                    getApplicationContext(),
                    3,
                    nextIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            builder.addAction(android.R.drawable.ic_media_previous, "Previous", prevPendingIntent)
                    .addAction(android.R.drawable.ic_media_pause, "Pause", playPausePendingIntent)
                    .addAction(android.R.drawable.ic_media_next, "Next", nextPendingIntent);
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            builder.setVisibility(Notification.VISIBILITY_PUBLIC);
        }

        return builder.build();
    }

    // *** begin broadcast methods ********************************************
    // TODO: replace these with enum?
    static final public String SERVICE_FILTER = "PlaybackServiceFilter";
    static final public String SERVICE_MESSAGE = "PlaybackServiceMessage";
    static final public String SERVICE_DATA = "PlaybackServiceData";
    static final public String TRACK_PREPARING = "TrackPreparing";
    static final public String TRACK_PREPARED = "TrackPrepared";
    static final public String UPDATE_PLAY_PAUSE = "UpdatePlayPause";
    static final public String TRACK_COMPLETED = "TrackCompleted";
    static final public String ELAPSED_TIME = "ElapsedTime";

    private void broadcastTrackPreparing() {
        broadcastIntent(getBroadcastIntent().putExtra(SERVICE_MESSAGE, TRACK_PREPARING));
    }

    private void broadcastTrackPrepared() {
        broadcastIntent(getBroadcastIntent().putExtra(SERVICE_MESSAGE, TRACK_PREPARED));
    }

    private void broadcastUpdatePlayPause() {
        broadcastIntent(getBroadcastIntent().putExtra(SERVICE_MESSAGE, UPDATE_PLAY_PAUSE));
    }

    private void broadcastTrackCompleted() {
        broadcastIntent(getBroadcastIntent().putExtra(SERVICE_MESSAGE, TRACK_COMPLETED));
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

    public TrackState getTrackState() {
        if (mMediaPlayer == null) {
            return null;
        }

        return mMediaPlayer.isPlaying() ? TrackState.PLAYING : TrackState.PAUSED;
    }

    public boolean isMediaPlayerNull() { return mMediaPlayer == null; }

    public void setProgress(int msec) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(msec);
        }
    }
    // *** end helper methods *************************************************
}
