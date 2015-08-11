package org.songfamily.tiem.nanodegree.app1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity
    implements MainActivityFragment.Callbacks,
        ArtistTracksActivityFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((MainActivityFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_main))
                .setActivateOnItemClick(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Toast.makeText(this, "TBD: Settings", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    /**
     * Callback method from {@link MainActivityFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String artistId, String artistName) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle bundle = new Bundle();
            bundle.putString(ArtistTracksActivityFragment.ARTIST_ID_EXTRA, artistId);
            bundle.putString(ArtistTracksActivityFragment.ARTIST_NAME_EXTRA, artistName);
            ArtistTracksActivityFragment fragment = new ArtistTracksActivityFragment();
            fragment.setArguments(bundle);
            getFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit();
        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent intent = new Intent(this, ArtistTracksActivity.class);
            intent.putExtra(ArtistTracksActivityFragment.ARTIST_ID_EXTRA, artistId);
            intent.putExtra(ArtistTracksActivityFragment.ARTIST_NAME_EXTRA, artistName);
            startActivity(intent);
        }
    }

    /**
     * Callback method from {@link ArtistTracksActivityFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(Bundle trackList, int selectedTrack) {
        PlayTrackActivityFragment fragment = new PlayTrackActivityFragment();
        Bundle newBundle = new Bundle();
        newBundle.putBundle(PlayTrackActivityFragment.TRACK_LIST_BUNDLE, trackList);
        newBundle.putInt(PlayTrackActivityFragment.TRACK_SELECTED, selectedTrack);
        fragment.setArguments(newBundle);
        fragment.show(getFragmentManager(), PlayTrackActivityFragment.TAG);
    }
}
