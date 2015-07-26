package org.songfamily.tiem.nanodegree.app1.helpers;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

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

    private Bundle mTrackListBundle;
    private int mTrackSelected;
    private MediaPlayer mMediaPlayer = null;

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
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        stopForeground(true);
        mMediaPlayer = null;
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    public void onPlayPauseAction() {
        if (mMediaPlayer == null) {
            Track track = BundleHelper.getTrackList(mTrackListBundle).get(mTrackSelected);

            String trackName = track.name;
            String artistName = track.artists.get(0).name;

            PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                    new Intent(getApplicationContext(), MainActivity.class),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notification = new NotificationCompat.Builder(getApplicationContext())
                    .setOngoing(true)
                    .setSmallIcon(R.drawable.ic_library_music_black_48dp)
                    .setContentTitle(trackName)
                    .setContentText(artistName)
                    .setContentIntent(pi)
                    .build();

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
            startForeground(SERVICE_ID, notification);
        } else {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            } else {
                mMediaPlayer.start();
            }
        }
    }
}
