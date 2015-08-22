package org.songfamily.tiem.nanodegree.app1.helpers;

import android.os.Bundle;

// POJO to store global application state
public class GlobalData {
    private static GlobalData instance = null;
    public boolean isTablet = false;
    public boolean isPlaybackActive = false;
    public Bundle currentTrackList;
    public int currentSelectedTrack;

    private GlobalData() {
        // no-op
    }

    public static GlobalData getInstance() {
        if (instance == null) {
            instance = new GlobalData();
        }
        return instance;
    }
}
