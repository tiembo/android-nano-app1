package org.songfamily.tiem.nanodegree.app1.helpers;

import java.util.List;

import kaaes.spotify.webapi.android.models.Image;

public class ImageHelper {

    /**
     * Returns the url String field of the specified list of Image objects.
     * If the list is empty, return null.
     *
     * @param images List of Image objects
     * @return null if the list is empty; otherwise the string URL of the first Image in the list
     */
    public static String getImageUrl(List<Image> images) {
        if (images.size() == 0)
            return null;
        else
            return images.get(0).url;
    }
}
