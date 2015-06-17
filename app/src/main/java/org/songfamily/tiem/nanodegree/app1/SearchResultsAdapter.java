package org.songfamily.tiem.nanodegree.app1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.songfamily.tiem.nanodegree.app1.helpers.ImageHelper;

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;

public class SearchResultsAdapter extends ArrayAdapter<Artist> {
    private Context mContext;

    private class ViewHolder {
        public TextView artistName;
        public ImageView artistImage;
    }

    public SearchResultsAdapter(Context context, List<Artist> results) {
        super(context, R.layout.list_item_search_result, results);
        mContext = context;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.list_item_search_result, viewGroup, false);
            holder = new ViewHolder();
            holder.artistName = (TextView) view.findViewById(R.id.tv_artist_name);
            holder.artistImage = (ImageView) view.findViewById(R.id.iv_artist_image);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Artist artist = getItem(i);
        holder.artistName.setText(artist.name);

        String artistImageUrl = ImageHelper.getArtistImage(artist);
        if (artistImageUrl != null) {
            Picasso.with(mContext)
                    .load(artistImageUrl)
                    .error(R.drawable.ic_cloud_off_black_48dp)
                    .into(holder.artistImage);
        } else {
            holder.artistImage.setImageResource(R.drawable.ic_library_music_black_48dp);
        }

        return view;
    }
}
