package com.zte.android.lib.media.gallery.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.zte.android.lib.media.R;
import com.zte.android.lib.media.gallery.photoloader.entity.Media;

import uk.co.senab.photoview.PhotoView;


/**
 * 图片展示的Fragment
 * Created by Law on 2016/9/25.
 */
public class ImageFragment extends Fragment {
    private static final String MEDIA = "media";

    private Media media;

    public static ImageFragment newInstance(Media media) {
        ImageFragment imageFragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putParcelable(MEDIA, media);
        imageFragment.setArguments(args);
        return imageFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        media = getArguments().getParcelable(MEDIA);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_img_display, container, false);
        initViews(view);
        return view;
    }

    public void initViews(@Nullable View view) {
        PhotoView mPhotoView = (PhotoView) view.findViewById(R.id.img_display_photoview);
//        ImageDisplayUtils.display(getContext(), media.getPath(), mPhotoView);


//        ImageLoader.with(getContext()).signature(media.signature()).load(media.getPath()).into(mPhotoView);
//        mPhotoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
//            @Override
//            public void onPhotoTap(View view, float x, float y) {
//                ((GalleryActivity) getActivity()).toggleUI();
//            }
//        });
    }
}