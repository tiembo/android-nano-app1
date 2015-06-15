package org.songfamily.tiem.nanodegree.app1.helpers;

import kaaes.spotify.webapi.android.models.Track;

public class ImageHelper {

    public static String getImageUrl(Track track) {
        int imageCount = track.album.images.size();

        if (imageCount == 0)
            return null;
        else
            return track.album.images.get(0).url;
    }
}
