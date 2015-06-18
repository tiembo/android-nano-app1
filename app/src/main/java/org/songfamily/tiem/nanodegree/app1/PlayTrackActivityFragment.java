package org.songfamily.tiem.nanodegree.app1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class PlayTrackActivityFragment extends BaseFragment {
    public static final String ALBUM_IMAGE_URL = "album_image_url";
    public static final String ALBUM_NAME = "album_name";
    public static final String ARTIST_NAME = "artist_name";
    public static final String TRACK_NAME = "track_name";
    public static final String TRACk_PREVIEW_URL = "track_preview_url";

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
        String trackPreviewUrl = intent.getStringExtra(TRACk_PREVIEW_URL);

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

        return view;
    }
}
