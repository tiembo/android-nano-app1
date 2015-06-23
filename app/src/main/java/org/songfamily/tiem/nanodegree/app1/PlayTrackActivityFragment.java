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

import org.songfamily.tiem.nanodegree.app1.helpers.BundleHelper;
import org.songfamily.tiem.nanodegree.app1.helpers.ImageHelper;
import org.songfamily.tiem.nanodegree.app1.helpers.StringHelper;

import java.io.IOException;

import kaaes.spotify.webapi.android.models.Track;


public class PlayTrackActivityFragment extends BaseFragment
    implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener,
        View.OnClickListener {

    enum PlayButtonIcon {
        PLAY,
        PAUSE
    }

    public static final String TRACK_LIST_BUNDLE = "trackListBundle";
    public static final String TRACK_SELECTED = "trackSelect";

    private MediaPlayer mMediaPlayer = null;
    private boolean mIsTrackPrepared = false;
    private String mTrackPreviewUrl;

    private ImageView mPlayPauseButton;
    private TextView mTrackLength;

    public PlayTrackActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play_track, container, false);
        mTrackLength = (TextView) view.findViewById(R.id.pt_tv_track_length);

        // fetch track information from bundle...
        Intent intent = getActivity().getIntent();
        Bundle bundle = intent.getBundleExtra(TRACK_LIST_BUNDLE);
        int trackSelected = intent.getIntExtra(TRACK_SELECTED, 0);
        Track track = BundleHelper.getTrackList(bundle).get(trackSelected);

        String albumImageUrl = ImageHelper.getImageUrl(track.album.images);
        String albumName = track.album.name;
        String artistName = track.artists.get(0).name;
        String trackName = track.name;
        mTrackPreviewUrl = track.preview_url;

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

        // start playing track
        playButtonClicked();

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mMediaPlayer != null)
            mMediaPlayer.release();
    }

    @Override
    public void onClick(View view) {
        playButtonClicked();
    }

    // *** begin helper methods ***********************************************
    private void playButtonClicked() {
        if (mMediaPlayer == null)
            initMediaPlayer();

        if (mMediaPlayer.isPlaying()) {
            setPlayIcon(PlayButtonIcon.PLAY);
            mMediaPlayer.pause();
        } else {
            playTrack();
        }
    }

    private void playTrack() {
        if (mIsTrackPrepared) {
            setPlayIcon(PlayButtonIcon.PAUSE);
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

    private void setPlayIcon(PlayButtonIcon icon) {
        switch (icon) {
            case PLAY:
                mPlayPauseButton.setImageResource(android.R.drawable.ic_media_play);
                break;
            case PAUSE:
                mPlayPauseButton.setImageResource(android.R.drawable.ic_media_pause);
                break;
        }
    }
    // *** end helper methods *************************************************

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
        mTrackLength.setText(StringHelper.getPlayTime(mediaPlayer.getDuration()));
        playTrack();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mIsTrackPrepared = false;
        setPlayIcon(PlayButtonIcon.PLAY);
        mMediaPlayer.release();
        mMediaPlayer = null;
    }
    // *** end media player helper methods and callbacks section **************
}
