package org.songfamily.tiem.nanodegree.app1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class ArtistTracksActivity extends AppCompatActivity {

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
}
