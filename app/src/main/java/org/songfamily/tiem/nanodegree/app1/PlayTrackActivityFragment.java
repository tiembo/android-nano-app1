package org.songfamily.tiem.nanodegree.app1;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.songfamily.tiem.nanodegree.app1.helpers.BundleHelper;
import org.songfamily.tiem.nanodegree.app1.helpers.ImageHelper;
import org.songfamily.tiem.nanodegree.app1.helpers.StringHelper;

import java.io.IOException;

import kaaes.spotify.webapi.android.models.Track;


public class PlayTrackActivityFragment extends DialogFragment
    implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener,
        View.OnClickListener {

    enum PlayButtonIcon {
        PLAY,
        PAUSE
    }

    public static final String TRACK_LIST_BUNDLE = "trackListBundle";
    public static final String TRACK_SELECTED = "trackSelect";
    public static final String TAG = "PlayTrackActivityFragment";

    private MediaPlayer mMediaPlayer = null;
    private boolean mIsTrackPrepared = false;
    private Track mTrack;

    private ImageView mPlayPauseButton;
    private TextView mTrackLength;

    public PlayTrackActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play_track, container, false);
        mTrackLength = (TextView) view.findViewById(R.id.pt_tv_track_length);

        // fetch track information from arguments...
        Bundle bundle = getArguments().getBundle(TRACK_LIST_BUNDLE);
        int selectedTrack = getArguments().getInt(TRACK_SELECTED);
        mTrack = BundleHelper.getTrackList(bundle).get(selectedTrack);

        String albumImageUrl = ImageHelper.getImageUrl(mTrack.album.images);
        String albumName = mTrack.album.name;
        String artistName = mTrack.artists.get(0).name;
        String trackName = mTrack.name;

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
    public void onPause() {
        super.onPause();

        if (mMediaPlayer != null)
            mMediaPlayer.release();
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
            shareIntent.putExtra(Intent.EXTRA_TEXT, StringHelper.getShareText(mTrack));
            shareIntent.setType("text/plain");
            startActivity(Intent.createChooser(shareIntent, getActivity().getString(R.string.share_window_title)));
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                mMediaPlayer.setDataSource(mTrack.preview_url);
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
