package org.songfamily.tiem.nanodegree.app1;

import android.app.Activity;
import android.os.Bundle;

public class PrefActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
