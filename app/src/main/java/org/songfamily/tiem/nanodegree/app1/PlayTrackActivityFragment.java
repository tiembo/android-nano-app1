package org.songfamily.tiem.nanodegree.app1;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
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
import org.songfamily.tiem.nanodegree.app1.helpers.PlaybackService;
import org.songfamily.tiem.nanodegree.app1.helpers.StringHelper;

import kaaes.spotify.webapi.android.models.Track;


public class PlayTrackActivityFragment extends DialogFragment
    implements View.OnClickListener {

    public static final String TRACK_LIST_BUNDLE = "trackListBundle";
    public static final String TRACK_SELECTED = "trackSelect";
    public static final String TAG = "PlayTrackActivityFragment";

    private MediaPlayer mMediaPlayer = null;
    private Track mTrack;
    private Bundle mTrackListBundle;
    private int mTrackSelected;

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

        // fetch track information from arguments...
        mTrackListBundle = getArguments().getBundle(TRACK_LIST_BUNDLE);
        mTrackSelected = getArguments().getInt(TRACK_SELECTED);
        mTrack = BundleHelper.getTrackList(mTrackListBundle).get(mTrackSelected);

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
        ImageView mPlayPauseButton = (ImageView) view.findViewById(R.id.pt_iv_play_pause_track);
        mPlayPauseButton.setOnClickListener(this);

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
        switch (view.getId()) {
            case R.id.pt_iv_play_pause_track:
                playButtonClicked();
                break;
        }
    }

    // *** begin helper methods ***********************************************
    private void playButtonClicked() {
        Intent intent = new Intent(getActivity(), PlaybackService.class);
        intent.putExtra(TRACK_LIST_BUNDLE, mTrackListBundle);
        intent.putExtra(TRACK_SELECTED, mTrackSelected);
        intent.setAction(PlaybackService.ACTION_PLAY_PAUSE);
        getActivity().startService(intent);
    }
}
