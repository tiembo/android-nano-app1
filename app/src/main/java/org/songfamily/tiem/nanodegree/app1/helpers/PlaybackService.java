package org.songfamily.tiem.nanodegree.app1.helpers;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
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
    public static final String ACTION_PLAY_PAUSE = "actionPlayPause";

    MediaPlayer mMediaPlayer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();

        if (action.equals(ACTION_PLAY_PAUSE)) {
            Bundle mTrackListBundle = intent.getBundleExtra(PlayTrackActivityFragment.TRACK_LIST_BUNDLE);
            int mTrackSelected = intent.getIntExtra(PlayTrackActivityFragment.TRACK_SELECTED, 0);
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
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        stopForeground(true);
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }
}
