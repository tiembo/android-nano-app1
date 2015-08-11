package org.songfamily.tiem.nanodegree.app1;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class PlayTrackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_track);

        FragmentManager fragmentManager = getFragmentManager();
        PlayTrackActivityFragment fragment = new PlayTrackActivityFragment();

        // copy intent extras to argument extras
        Bundle trackList = getIntent().getBundleExtra(PlayTrackActivityFragment.TRACK_LIST_BUNDLE);
        int selectedTrack = getIntent().getIntExtra(PlayTrackActivityFragment.TRACK_SELECTED, 0);
        Bundle newBundle = new Bundle();
        newBundle.putBundle(PlayTrackActivityFragment.TRACK_LIST_BUNDLE, trackList);
        newBundle.putInt(PlayTrackActivityFragment.TRACK_SELECTED, selectedTrack);
        fragment.setArguments(newBundle);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // For a little polish, specify a transition animation
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // To make it fullscreen, use the 'content' root view as the container
        // for the fragment, which is always the root view for the activity
        transaction.add(android.R.id.content, fragment)
                .addToBackStack(null).commit();
    }
}
