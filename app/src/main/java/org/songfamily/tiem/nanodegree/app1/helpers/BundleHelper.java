package org.songfamily.tiem.nanodegree.app1.helpers;

import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Helper methods to store and fetch bundles
 */
public class BundleHelper {
    private static final String KEY_ARTIST_NAMES = "artistNames";
    private static final String KEY_ARTIST_IDS = "artistIds";
    private static final String KEY_ARTIST_IMAGE_URLS = "artistImageUrls";
    private static final String KEY_TRACK_NAMES = "trackNames";
    private static final String KEY_ALBUM_NAMES = "albumNames";
    private static final String KEY_TRACK_IMAGE_URLS = "trackImageUrls";

    public static void putArtistList(Bundle bundle, @NonNull List<Artist> artistList) {
        int numArtists = artistList.size();
        String[] artistNames = new String[numArtists];
        String[] artistImageUrls = new String[numArtists];
        String[] artistIds = new String[numArtists];

        for (int i = 0; i < numArtists; i++) {
            Artist a =  artistList.get(i);
            artistNames[i] = a.name;
            artistIds[i] = a.id;
            artistImageUrls[i] = ImageHelper.getImageUrl(a.images);
        }

        bundle.putStringArray(KEY_ARTIST_NAMES, artistNames);
        bundle.putStringArray(KEY_ARTIST_IMAGE_URLS, artistImageUrls);
        bundle.putStringArray(KEY_ARTIST_IDS, artistIds);
    }

    public static List<Artist> getArtistList(Bundle bundle) {
        String[] artistNames = bundle.getStringArray(KEY_ARTIST_NAMES);
        String[] artistImageUrls = bundle.getStringArray(KEY_ARTIST_IMAGE_URLS);
        String[] artistIds = bundle.getStringArray(KEY_ARTIST_IDS);

        List<Artist> artistList = new ArrayList<>();

        for (int i = 0; i < artistNames.length; i++) {
            Artist a = new Artist();
            a.images = createImageList(artistImageUrls[i]);
            a.name = artistNames[i];
            a.id = artistIds[i];
            artistList.add(a);
        }

        return artistList;
    }

    public static void putTrackList(Bundle bundle, @NonNull List<Track> trackList) {
        int numTracks = trackList.size();
        String[] trackNames = new String[numTracks];
        String[] albumNames = new String[numTracks];
        String[] trackImageUrls = new String[numTracks];

        for (int i = 0; i < numTracks; i++) {
            Track t = trackList.get(i);
            trackNames[i] = t.name;
            albumNames[i] = t.album.name;
            trackImageUrls[i] = ImageHelper.getImageUrl(t.album.images);
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
            Album a = new Album();
            a.images = createImageList(trackImageUrls[i]);
            a.name = albumNames[i];
            Track t = new Track();
            t.album = a;
            t.name = trackNames[i];
            trackList.add(t);
        }

        return trackList;
    }

    /**
     * Creates a List containing a single Image object with its url field set to the specified
     * string.
     *
     * @param imageUrl URL of Image
     * @return List containing a single Image with its url field set to imageUrl
     */
    private static List<Image> createImageList(String imageUrl) {
        List<Image> images = new ArrayList<>();
        if (imageUrl != null) {
            Image image = new Image();
            image.url = imageUrl;
            images.add(image);
        }

        return images;
    }
}
