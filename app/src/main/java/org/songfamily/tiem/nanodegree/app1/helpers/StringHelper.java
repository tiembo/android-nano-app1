package org.songfamily.tiem.nanodegree.app1.helpers;

import java.util.concurrent.TimeUnit;

import kaaes.spotify.webapi.android.models.Track;

public class StringHelper {

    public static String getPlayTime(long milliseconds) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);

        return String.format("%02d:%02d", minutes, seconds);
    }

    public static String getShareText(Track track) {
        return track.artists.get(0).name +
                " - " +
                track.name +
                " (" +
                track.preview_url +
                ")";
    }
}
