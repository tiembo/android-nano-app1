package org.songfamily.tiem.nanodegree.app1;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;


public class PlayTrackActivityFragment extends BaseFragment
    implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener,
        View.OnClickListener {
    public static final String ALBUM_IMAGE_URL = "album_image_url";
    public static final String ALBUM_NAME = "album_name";
    public static final String ARTIST_NAME = "artist_name";
    public static final String TRACK_NAME = "track_name";
    public static final String TRACk_PREVIEW_URL = "track_preview_url";

    private MediaPlayer mMediaPlayer = null;
    private boolean mIsTrackPrepared = false;
    private String mTrackPreviewUrl;
    private ImageView mPlayPauseButton;

    public PlayTrackActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play_track, container, false);

        // fetch track information from bundle...
        Intent intent = getActivity().getIntent();
        String albumImageUrl = intent.getStringExtra(ALBUM_IMAGE_URL);
        String albumName = intent.getStringExtra(ALBUM_NAME);
        String artistName = intent.getStringExtra(ARTIST_NAME);
        String trackName = intent.getStringExtra(TRACK_NAME);
        mTrackPreviewUrl = intent.getStringExtra(TRACk_PREVIEW_URL);

        // ...and write to views...
        TextView tvAlbumName = (TextView) view.findViewById(R.id.pt_tv_album_name);
        TextView tvArtistName = (TextView) view.findViewById(R.id.pt_tv_artist_name);
        TextView tvTrackName = (TextView) view.findViewById(R.id.pt_tv_track_name);
        tvAlbumName.setText(albumName);
        tvArtistName.setText(artistName);
        tvTrackName.setText(trackName);

        //...and load album image
        ImageView ivAlbumImage = (ImageView) view.findViewById(R.id.pt_iv_album_image);
        Picasso.with(getActivity())
                .load(albumImageUrl)
                .into(ivAlbumImage);

        // set up play / pause button
        mPlayPauseButton = (ImageView) view.findViewById(R.id.pt_iv_play_pause_track);
        mPlayPauseButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mMediaPlayer != null)
            mMediaPlayer.release();
    }

    private void playTrack() {
        if (mIsTrackPrepared) {
            mPlayPauseButton.setImageResource(android.R.drawable.ic_media_pause);
            mMediaPlayer.start();
        } else {
            try {
                mMediaPlayer.setDataSource(mTrackPreviewUrl);
                mMediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (mMediaPlayer == null)
            initMediaPlayer();

        if (mMediaPlayer.isPlaying()) {
            mPlayPauseButton.setImageResource(android.R.drawable.ic_media_play);
            mMediaPlayer.pause();
        } else {
            playTrack();
        }
    }

    // *** begin media player helper methods and callbacks section ************
    private void initMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mIsTrackPrepared = true;
        playTrack();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mIsTrackPrepared = false;
        mMediaPlayer.release();
        mMediaPlayer = null;
    }
    // *** end media player helper methods and callbacks section **************
}
