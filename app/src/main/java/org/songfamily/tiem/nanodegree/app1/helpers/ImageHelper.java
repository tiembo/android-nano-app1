package org.songfamily.tiem.nanodegree.app1.helpers;

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

public class ImageHelper {

    /**
     * Fetch image URL relevant to the specified track
     *
     * @param track Track
     * @return URL of image
     */
    public static String getImageUrl(Track track) {
        return getImageCommon(track.album.images);
    }

    /**
     * Fetch image URL relevant to the specified artist
     *
     * @param artist Artist
     * @return URL of image
     */
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
