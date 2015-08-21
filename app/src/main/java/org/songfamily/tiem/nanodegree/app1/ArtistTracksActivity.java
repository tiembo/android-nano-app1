package org.songfamily.tiem.nanodegree.app1;

import android.content.Intent;
import android.os.Bundle;

public class ArtistTracksActivity extends MenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_tracks);

        if (savedInstanceState == null) {
            String artistId = getIntent().getStringExtra(ArtistTracksActivityFragment.ARTIST_ID_EXTRA);
            String artistName = getIntent().getStringExtra(ArtistTracksActivityFragment.ARTIST_NAME_EXTRA);

            Bundle bundle = new Bundle();
            bundle.putString(ArtistTracksActivityFragment.ARTIST_ID_EXTRA, artistId);
            bundle.putString(ArtistTracksActivityFragment.ARTIST_NAME_EXTRA, artistName);
            ArtistTracksActivityFragment fragment = new ArtistTracksActivityFragment();
            fragment.setArguments(bundle);
            getFragmentManager().beginTransaction()
                    .replace(R.id.artist_tracks_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onItemSelected(Bundle trackList, int selectedTrack) {
        Intent intent = new Intent(this, PlayTrackActivity.class);
        intent.putExtra(PlayTrackActivityFragment.TRACK_LIST_BUNDLE, trackList);
        intent.putExtra(PlayTrackActivityFragment.TRACK_SELECTED, selectedTrack);
        startActivity(intent);
    }
}
