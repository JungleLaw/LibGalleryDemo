package com.zte.android.lib.media.gallery.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zte.android.lib.media.R;
import com.zte.android.lib.media.gallery.photoloader.entity.Album;

import java.util.List;

/**
 * 图册浏览器
 * Created by Jungle on 2017/7/28.
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private Context context;
    private List<Album> albumList;
    private ClickCallback callback;
    private int index = 0;

    public void setCallback(ClickCallback callback) {
        this.callback = callback;
    }

    public AlbumAdapter(Context context, List<Album> albumList) {
        this.context = context;
        this.albumList = albumList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_album_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Album album = albumList.get(position);
        holder.vTvAlbumName.setText(album.getName());
        holder.vTvSize.setText(String.valueOf(album.getMedias().size()));
//        ImageDisplayUtils.display(context, album.getAlbumCover().getPath(), holder.vIvCover);
        if (position == index) {
            holder.vIvSelect.setVisibility(View.VISIBLE);
        } else {
            holder.vIvSelect.setVisibility(View.INVISIBLE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callback != null) {
                    callback.callback(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumList == null ? 0 : albumList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView vTvAlbumName;
        ImageView vIvCover;
        TextView vTvSize;
        ImageView vIvSelect;

        public ViewHolder(View itemView) {
            super(itemView);
//            ButterKnife.bind(this, itemView);
            vTvAlbumName = itemView.findViewById(R.id.tv_album_name);
            vIvCover = itemView.findViewById(R.id.iv_cover);
            vTvSize = itemView.findViewById(R.id.tv_size);
            vIvSelect = itemView.findViewById(R.id.iv_item_select);
        }
    }

    public void setSelectedAlbum(int index) {
        this.index = index;
        notifyDataSetChanged();
    }

    public interface ClickCallback {
        void callback(int position);
    }
}
