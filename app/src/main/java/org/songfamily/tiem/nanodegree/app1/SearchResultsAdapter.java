package org.songfamily.tiem.nanodegree.app1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;

public class SearchResultsAdapter extends ArrayAdapter<Artist> {
    private LayoutInflater mInflater;

    private class ViewHolder {
        public TextView artistName;
    }

    public SearchResultsAdapter(Context context, List<Artist> results) {
        super(context, R.layout.list_item_search_result, results);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if (view == null) {
            view = mInflater.inflate(R.layout.list_item_search_result, viewGroup, false);
            holder = new ViewHolder();
            holder.artistName = (TextView) view.findViewById(R.id.tv_artist_name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Artist artist = getItem(i);
        holder.artistName.setText(artist.name);

        return view;
    }
}
