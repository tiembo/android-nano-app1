package org.songfamily.tiem.nanodegree.app1;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import kaaes.spotify.webapi.android.models.Artist;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        SearchResultsAdapter adapter = new SearchResultsAdapter(getActivity(), getArtists());
        ListView listView = (ListView) view.findViewById(R.id.lv_search_results);
        listView.setAdapter(adapter);

        return view;
    }

    // temporary method to generate list of Artists
    private Artist[] getArtists() {
        int size = 20;
        Artist[] results = new Artist[20];
        for (int i = 0; i < size; i++) {
            Artist artist = new Artist();
            artist.name = "Artist " + (i + 1);
            results[i] = artist;
        }

        return results;
    }
}
