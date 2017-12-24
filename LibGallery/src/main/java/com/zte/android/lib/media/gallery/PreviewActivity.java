package com.zte.android.lib.media.gallery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;


import com.zte.android.lib.media.R;
import com.zte.android.lib.media.gallery.adapter.DisplayPagerAdapter;
import com.zte.android.lib.media.gallery.adapter.ImagePreviewAdapter;
import com.zte.android.lib.media.gallery.photoloader.entity.Media;
import com.zte.android.lib.media.widget.HackyViewPager;

import java.util.ArrayList;
import java.util.List;


/**
 * 预览
 * Created by Jungle on 2017/7/14.
 */
public class PreviewActivity extends AppCompatActivity {
    public static final String SELECTED_RESULT_KEY = "result";
    public static final String SELECTED_ORIGIN_KEY = "origin";

    private static final String MEDIAS_KEY = "medias";
    private static final String SELECTED_KEY = "selected";
    private static final String SIZE_KEY = "size";
    private static final String ORIGIN_KEY = "origin";
    private static final String ACTIVE_ORIGIN_KEY = "active_origin";

    HackyViewPager vViewPager;
    TextView vIndex;
    TextView vTvSelect;
    TextView vBtnConfirm;
    RecyclerView vRecyclerView;
    TextView vBtnOrigin;

    private List<Media> mMedias;
    private ArrayList<Long> selectedList;
    private ArrayList<Long> removedList = new ArrayList<>();
    private DisplayPagerAdapter mDisplayPagerAdapter;
    private int maxSize;
    private ImagePreviewAdapter mPreviewAdapter;
    private boolean isOrigin = false;
    private boolean activeOrigin = false;

    public static void navigateToPreviewActivity(Activity activity, int maxSize, ArrayList<Media> medias, ArrayList<Long> selectedList, boolean isOrigin, boolean activeOrigin, int requestCode) {
        Intent intent = new Intent(activity, PreviewActivity.class);
        intent.putExtra(SIZE_KEY, maxSize);
        intent.putParcelableArrayListExtra(MEDIAS_KEY, medias);
        intent.putExtra(SELECTED_KEY, selectedList);
        intent.putExtra(ORIGIN_KEY, isOrigin);
        intent.putExtra(ACTIVE_ORIGIN_KEY, activeOrigin);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
//        ButterKnife.bind(this);
        vViewPager = findViewById(R.id.vp_imgs);
        vIndex = findViewById(R.id.tv_index);
        vTvSelect = findViewById(R.id.tv_select);
        vBtnConfirm = findViewById(R.id.btn_confirm);
        vRecyclerView = findViewById(R.id.recyclerview);
        vBtnOrigin = findViewById(R.id.tv_origin);

        init();
    }

    /**
     * 初始化
     */
    private void init() {
        maxSize = getIntent().getIntExtra(SIZE_KEY, 0);
        mMedias = new ArrayList<>();
        ArrayList<Media> medias = getIntent().getParcelableArrayListExtra(MEDIAS_KEY);
        mMedias.addAll(medias);
        selectedList = (ArrayList<Long>) getIntent().getSerializableExtra(SELECTED_KEY);
        isOrigin = getIntent().getBooleanExtra(ORIGIN_KEY, false);
        activeOrigin = getIntent().getBooleanExtra(ACTIVE_ORIGIN_KEY, false);

        vBtnOrigin.setSelected(isOrigin);
        if (removedList != null) {
            removedList.clear();
        }

        vIndex.setText(1 + "/" + mMedias.size());
        if (selectedList.size() == 0) {
            vBtnConfirm.setText("确定");
        } else {
            vBtnConfirm.setText("确定(" + selectedList.size() + "/" + maxSize + ")");
        }
        mDisplayPagerAdapter = new DisplayPagerAdapter(getSupportFragmentManager(), mMedias);
        vViewPager.setPageMargin(10);
        vViewPager.setOffscreenPageLimit(2);
        vViewPager.setAdapter(mDisplayPagerAdapter);
        if (selectedList.contains(mMedias.get(0).getId())) {
            vTvSelect.setSelected(true);
        } else {
            vTvSelect.setSelected(false);
        }

        if (activeOrigin) {
            vBtnOrigin.setVisibility(View.VISIBLE);
        } else {
            vBtnOrigin.setVisibility(View.GONE);
        }

        vViewPager.setCurrentItem(0);
        vViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                vIndex.setText((position + 1) + "/" + mMedias.size());
                if (!removedList.contains(mMedias.get(position).getId())) {
                    vTvSelect.setSelected(true);
                } else {
                    vTvSelect.setSelected(false);
                }
                mPreviewAdapter.setIndex(position);
                vRecyclerView.getLayoutManager().scrollToPosition(position);
            }
        });

        vRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//        List<Media> previewMedias = new ArrayList<>();
//        previewMedias.addAll(medias);

        mPreviewAdapter = new ImagePreviewAdapter(this, medias);
        mPreviewAdapter.setCallback(new ImagePreviewAdapter.Callback() {
            @Override
            public void imgClick(int position) {
                vViewPager.setCurrentItem(position);
                mPreviewAdapter.setIndex(position);
            }
        });
        vRecyclerView.setAdapter(mPreviewAdapter);
    }

    /**
     * 选择当前预览
     */
//    @OnClick(R.id.tv_select)
    public void selectImg(View view) {
        int currentPosition = vViewPager.getCurrentItem();
        if (!removedList.contains((Long) mMedias.get(currentPosition).getId())) {
            vTvSelect.setSelected(false);
            removedList.add((Long) mMedias.get(currentPosition).getId());
        } else {
            vTvSelect.setSelected(true);
            removedList.remove((Long) mMedias.get(currentPosition).getId());
        }

        mPreviewAdapter.setRemovedMediasId(removedList);

        if (mPreviewAdapter.getSelectedMediasId().size() == 0) {
            vBtnConfirm.setText("确定");
        } else {
            vBtnConfirm.setText("确定(" + mPreviewAdapter.getSelectedMediasId().size() + "/" + maxSize + ")");
        }
    }

    /**
     * 返回
     */
//    @OnClick(R.id.btn_back)
    public void back(View view) {
        finish();
    }

    /**
     * 确认
     */
//    @OnClick(R.id.btn_confirm)
    public void confirm(View view) {
        Intent intent = new Intent();
        ArrayList<Long> selectedIds = new ArrayList<>();
        selectedIds.addAll(mPreviewAdapter.getSelectedMediasId());
        intent.putExtra(SELECTED_RESULT_KEY, selectedIds);
        intent.putExtra(SELECTED_ORIGIN_KEY, isOrigin);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * 原图
     */
//    @OnClick(R.id.tv_origin)
    public void origin(View view) {
        isOrigin = !isOrigin;
        vBtnOrigin.setSelected(isOrigin);
    }
}
