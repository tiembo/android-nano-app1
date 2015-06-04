package org.songfamily.tiem.nanodegree.app1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import kaaes.spotify.webapi.android.models.Artist;

public class SearchResultsAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Artist[] mSearchResults;

    private class ViewHolder {
        public TextView artistName;
    }

    public SearchResultsAdapter(Context context, Artist[] searchResults) {
        mInflater = LayoutInflater.from(context);
        mSearchResults = searchResults;
    }

    @Override
    public int getCount() {
        return mSearchResults.length;
    }

    @Override
    public Object getItem(int i) {
        return mSearchResults[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
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

        Artist artist = (Artist) getItem(i);
        holder.artistName.setText(artist.name);

        return view;
    }
}
