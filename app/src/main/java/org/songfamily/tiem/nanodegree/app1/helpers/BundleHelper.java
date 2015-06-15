package org.songfamily.tiem.nanodegree.app1.helpers;

import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

public class BundleHelper {
    private static final String KEY_TRACK_NAMES = "trackNames";
    private static final String KEY_ALBUM_NAMES = "albumNames";
    private static final String KEY_TRACK_IMAGE_URLS = "trackImageUrls";

    public static void putTrackList(Bundle bundle, @NonNull List<Track> trackList) {
        int numTracks = trackList.size();
        String[] trackNames = new String[numTracks];
        String[] albumNames = new String[numTracks];
        String[] trackImageUrls = new String[numTracks];

        for (int i = 0; i < numTracks; i++) {
            Track t = trackList.get(i);
            trackNames[i] = t.name;
            albumNames[i] = t.album.name;
            trackImageUrls[i] = ImageHelper.getImageUrl(t);
        }

        bundle.putStringArray(KEY_TRACK_NAMES, trackNames);
        bundle.putStringArray(KEY_ALBUM_NAMES, albumNames);
        bundle.putStringArray(KEY_TRACK_IMAGE_URLS, trackImageUrls);
    }

    public static List<Track> getTrackList(Bundle bundle) {
        String[] trackNames = bundle.getStringArray(KEY_TRACK_NAMES);
        String[] albumNames = bundle.getStringArray(KEY_ALBUM_NAMES);
        String[] trackImageUrls = bundle.getStringArray(KEY_TRACK_IMAGE_URLS);

        List<Track> trackList = new ArrayList<>();

        for (int i = 0; i < trackNames.length; i++) {
            List<Image> images = new ArrayList<>();
            String imageUrl = trackImageUrls[i];
            if (imageUrl != null) {
                Image image = new Image();
                image.url = imageUrl;
                images.add(image);
            }

            Album a = new Album();
            a.images = images;
            a.name = albumNames[i];
            Track t = new Track();
            t.album = a;
            t.name = trackNames[i];
            trackList.add(t);
        }

        return trackList;
    }
}
