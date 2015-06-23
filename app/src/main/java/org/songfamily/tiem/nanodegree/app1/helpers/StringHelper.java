package org.songfamily.tiem.nanodegree.app1.helpers;

import java.util.concurrent.TimeUnit;

public class StringHelper {

    public static String getPlayTime(long milliseconds) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);

        return String.format("%02d:%02d", minutes, seconds);
    }
}
