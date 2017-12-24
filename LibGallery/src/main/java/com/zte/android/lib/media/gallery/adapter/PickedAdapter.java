package com.zte.android.lib.media.gallery.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.zte.android.lib.media.R;
import com.zte.android.lib.media.gallery.photoloader.Configuration;

import java.util.List;

/**
 * 展示选中图适配器
 * Created by Jungle on 2017/7/16.
 */
public class PickedAdapter extends RecyclerView.Adapter<PickedAdapter.ViewHolder> {
    private Context context;
    private List<String> medias;

    public PickedAdapter(Context context, List<String> medias) {
        this.context = context;
        this.medias = medias;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_pick_multi_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.vLayoutRoot.setLayoutParams(new RecyclerView.LayoutParams(Configuration.GalleryConstants.lenght, Configuration.GalleryConstants.lenght));
//        ImageDisplayUtils.display(context, medias.get(position), holder.vImgDisplay);
    }

    @Override
    public int getItemCount() {
        return medias == null ? 0 : medias.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
//        @BindView(R.id.root)
        RelativeLayout vLayoutRoot;
//        @BindView(R.id.img)
        ImageView vImgDisplay;

        public ViewHolder(View itemView) {
            super(itemView);
//            ButterKnife.bind(this, itemView);
            vLayoutRoot = itemView.findViewById(R.id.root);
            vImgDisplay = itemView.findViewById(R.id.img);
        }
    }
}
