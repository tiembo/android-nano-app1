package org.songfamily.tiem.nanodegree.app1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

public class ArtistTracksAdapter extends ArrayAdapter<Track> {
    private Context mContext;

    private class ViewHolder {
        public TextView trackName;
        public TextView albumName;
        public ImageView trackImage;
    }

    public ArtistTracksAdapter(Context context, List<Track> results) {
        super(context, R.layout.list_item_track, results);
        mContext = context;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.list_item_track, viewGroup, false);
            holder = new ViewHolder();
            holder.trackName = (TextView) view.findViewById(R.id.tv_track_name);
            holder.albumName = (TextView) view.findViewById(R.id.tv_album_name);
            holder.trackImage = (ImageView) view.findViewById(R.id.iv_track_image);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Track track = getItem(i);
        holder.trackName.setText(track.name);
        holder.albumName.setText(track.album.name);

        String trackImageUrl = getTrackImage(track);
        if (trackImageUrl != null) {
            Picasso.with(mContext)
                    .load(trackImageUrl)

                    // TODO: change this
                    .error(R.drawable.abc_btn_radio_material)
                    .into(holder.trackImage);
        } else {

            // TODO: change this to match default ImageView drawable
            holder.trackImage.setImageResource(R.drawable.abc_ab_share_pack_mtrl_alpha);
        }

        return view;
    }

    private String getTrackImage(Track track) {
        int imageCount = track.album.images.size();

        if (imageCount == 0)
            return null;
        else
            return track.album.images.get(0).url;
    }
}
