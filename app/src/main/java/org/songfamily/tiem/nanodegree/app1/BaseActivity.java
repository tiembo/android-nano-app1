package org.songfamily.tiem.nanodegree.app1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.songfamily.tiem.nanodegree.app1.helpers.GlobalData;
import org.songfamily.tiem.nanodegree.app1.helpers.PlaybackService;

abstract class MenuActivity extends AppCompatActivity
    implements ArtistTracksActivityFragment.Callbacks {

    protected BroadcastReceiver mBroadcastReceiver;

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        mBroadcastReceiver = initBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter(PlaybackService.SERVICE_FILTER));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent(this, PrefActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_currently_playing:
                // TODO: implement this
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!GlobalData.getInstance().isPlaybackActive) {
            MenuItem navigateToPlayTrack = menu.getItem(0);
            navigateToPlayTrack.setEnabled(false);
            navigateToPlayTrack.setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private BroadcastReceiver initBroadcastReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra(PlaybackService.SERVICE_MESSAGE);

                switch (message) {
                    case PlaybackService.TRACK_PREPARED:
                    case PlaybackService.TRACK_COMPLETED:
                        invalidateOptionsMenu();
                        break;
                }
            }
        };
    }

}
