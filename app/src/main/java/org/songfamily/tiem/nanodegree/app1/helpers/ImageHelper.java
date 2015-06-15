package org.songfamily.tiem.nanodegree.app1.helpers;

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

public class ImageHelper {

    public static String getImageUrl(Track track) {
        return getImageCommon(track.album.images);
    }

    public static String getArtistImage(Artist artist) {
        return getImageCommon(artist.images);
    }

    private static String getImageCommon(List<Image> images) {
        if (images.size() == 0)
            return null;
        else
            return images.get(0).url;
    }
}
