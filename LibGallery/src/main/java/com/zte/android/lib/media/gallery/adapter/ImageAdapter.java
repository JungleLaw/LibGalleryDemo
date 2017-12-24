package com.zte.android.lib.media.gallery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.zte.android.lib.media.R;
import com.zte.android.lib.media.gallery.ImagePickActivity;
import com.zte.android.lib.media.gallery.photoloader.Configuration;
import com.zte.android.lib.media.gallery.photoloader.entity.Media;

import java.util.ArrayList;
import java.util.List;


/**
 * 图片选择页面GRIDVIEW适配器
 * Created by Jungle on 2017/7/15.
 */
public class ImageAdapter extends BaseAdapter {
    private static final String DURATION_FORMAT = "%02d:%02d";

    private static final int[] COLORSID = {R.color.color_for_photo_fri, R.color.color_for_photo_sec, R.color.color_for_photo_thi, R.color.color_for_photo_fou, R.color.color_for_photo_fif};

    private Context mContext;
    private List<Media> mMedias;
    private int mode;
    private ArrayList<Long> selectedPositions = null;
    private int maxSize;
    private ItemClickCallback callback;

    public void setCallback(ItemClickCallback callback) {
        this.callback = callback;
    }

    public ImageAdapter(Context mContext, List<Media> mMedias, int mode) {
        this.mContext = mContext;
        this.mMedias = mMedias;
        this.mode = mode;
        selectedPositions = new ArrayList<>();
    }

    public ImageAdapter(Context mContext, List<Media> mMedias, int mode, int maxSize) {
        this.mContext = mContext;
        this.mMedias = mMedias;
        this.mode = mode;
        this.maxSize = maxSize;
        selectedPositions = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mMedias == null ? 0 : mMedias.size();
    }

    @Override
    public Object getItem(int i) {
        return mMedias == null ? null : mMedias.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        final Media media = mMedias.get(i);
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.row_image_item, viewGroup, false);
            holder = new ViewHolder(view);
            holder.vLayoutRoot.setLayoutParams(new AbsListView.LayoutParams(Configuration.GalleryConstants.lenght, Configuration.GalleryConstants.lenght));
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

//        ImageDisplayUtils.displayWithPlaceholder(mContext, media.getPath(), holder.vIvPhoto, COLORSID[i % COLORSID.length]);
        switch (mode) {
            case ImagePickActivity.MODE_MULTI_IMG:
            case ImagePickActivity.MODE_MULTI_MEDIA:
                holder.vIvCheckBox.setVisibility(View.VISIBLE);
                if (selectedPositions.contains(media.getId())) {
                    holder.vIvCheckBox.setSelected(true);
                    holder.vMask.setVisibility(View.VISIBLE);
                } else {
                    holder.vIvCheckBox.setSelected(false);
                    holder.vMask.setVisibility(View.GONE);
                }

                if (media.isVideo()) {
                    holder.vIvCheckBox.setVisibility(View.GONE);
                    holder.vTvVideoDuration.setVisibility(View.VISIBLE);
                    holder.vTvVideoDuration.setText(getVideoDuration(media.getDuration()));
                } else {
                    holder.vIvCheckBox.setVisibility(View.VISIBLE);
                    holder.vTvVideoDuration.setVisibility(View.GONE);
                }

                holder.vIvCheckBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (selectedPositions.contains(media.getId())) {
                            media.setSelected(false);
                            selectedPositions.remove(media.getId());
                        } else {
                            if (selectedPositions.size() == maxSize) {
                                Toast.makeText(mContext, "至多可以选择" + maxSize + "张图片", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            media.setSelected(true);
                            selectedPositions.add(media.getId());
                        }
                        if (callback != null) {
                            callback.photoSelect(selectedPositions.size());
                        }
                        notifyDataSetChanged();
                    }
                });

                holder.vLayoutRoot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (callback != null) {
                            if (media.isVideo()) {
                                callback.videoClick(i);
                            } else {
                                callback.photoClick(i);
                            }
                        }
                    }
                });
                break;
            case ImagePickActivity.MODE_SINGLE_IMG:
            case ImagePickActivity.MODE_SINGLE_MEDIA:
                holder.vLayoutRoot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (callback != null) {
                            if (media.isVideo()) {
                                callback.videoClick(i);
                            } else {
                                callback.photoClick(i);
                            }
                        }
                    }
                });

                if (media.isVideo()) {
                    holder.vTvVideoDuration.setVisibility(View.VISIBLE);
                    holder.vTvVideoDuration.setText(getVideoDuration(media.getDuration()));
                } else {
                    holder.vTvVideoDuration.setVisibility(View.GONE);
                }
                break;
        }
        return view;
    }

    public void refresh(List<Media> medias) {
        this.mMedias = medias;
        notifyDataSetChanged();
    }

    private String getVideoDuration(long duration) {
        String durationDisplay;
        int mins = 0;
        int secs = 0;
        mins = (int) (duration / 1000) / 60;
        secs = (int) (duration / 1000 - (mins * 60));
        if (duration % 1000 > 500) {
            secs += 1;
        }
//        if (mins > 0) {
//            durationDisplay = mins + ":" + secs + "s";
//        } else {
//            durationDisplay = secs + "s";
//        }
        durationDisplay = String.format(DURATION_FORMAT, mins, secs);
        return durationDisplay;
    }

    public static class ViewHolder {
//        @BindView(R.id.layout_photos_item_root)
        RelativeLayout vLayoutRoot;
//        @BindView(R.id.img_photos_item)
        ImageView vIvPhoto;
//        @BindView(R.id.iv_item_select)
        ImageView vIvCheckBox;
//        @BindView(R.id.v_mask)
        View vMask;
//        @BindView(R.id.tv_video_duration)
        TextView vTvVideoDuration;

        public ViewHolder(View itemView) {
//            ButterKnife.bind(this, itemView);
            vLayoutRoot = itemView.findViewById(R.id.layout_photos_item_root);
            vIvPhoto = itemView.findViewById(R.id.img_photos_item);
            vIvCheckBox = itemView.findViewById(R.id.iv_item_select);
            vMask = itemView.findViewById(R.id.v_mask);
            vTvVideoDuration = itemView.findViewById(R.id.tv_video_duration);
        }
    }

    public interface ItemClickCallback {
        void photoClick(int position);

        void photoSelect(int count);

        void videoClick(int position);
    }

    public ArrayList<Long> getSelectedList() {
        return selectedPositions;
    }

    public void setSelectedList(ArrayList<Long> selectedPositions) {
        this.selectedPositions = selectedPositions;
        notifyDataSetChanged();
    }
}
