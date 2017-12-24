package com.zte.android.lib.media.gallery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.zte.android.lib.media.R;
import com.zte.android.lib.media.gallery.adapter.DisplayPagerAdapter;
import com.zte.android.lib.media.gallery.photoloader.entity.Media;
import com.zte.android.lib.media.widget.HackyViewPager;

import java.util.ArrayList;
import java.util.List;


/**
 * 浏览图册
 * Created by Jungle on 2017/7/16.
 */

public class GalleryActivity extends AppCompatActivity {
    public static final String SELECTED_RESULT_KEY = "result";
    public static final String SELECTED_ORIGIN_KEY = "origin";

    private static final String ALBUM_KEY = "album";
    private static final String INDEX_KEY = "index";
    private static final String MEDIAS_KEY = "medias";
    private static final String SELECTED_KEY = "selected";
    private static final String SIZE_KEY = "size";
    private static final String ORIGIN_KEY = "origin";
    private static final String ACTIVE_ORIGIN_KEY = "active_origin";

    HackyViewPager vViewPager;
    TextView vTitle;
    TextView vIndex;
    TextView vTvSelect;
    TextView vBtnConfirm;
    TextView vBtnOrigin;

    private String albumName;
    private int index;
    private int maxSize;
    private List<Media> mMedias;
    private ArrayList<Long> selectedList;
    private boolean isOrigin = false;
    private boolean activeOrigin = false;
    private DisplayPagerAdapter mDisplayPagerAdapter;

    public static void navigateToGalleryActivity(Activity activity, String albumName, int index, int maxSize, ArrayList<Media> medias, ArrayList<Long> selectedList, boolean isOrigin, boolean activeOrigin, int requestCode) {
        Intent intent = new Intent(activity, GalleryActivity.class);
        intent.putExtra(ALBUM_KEY, albumName);
        intent.putExtra(INDEX_KEY, index);
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
        setContentView(R.layout.activity_gallery);
        vViewPager = findViewById(R.id.vp_imgs);
        vTitle = findViewById(R.id.tv_title);
        vIndex = findViewById(R.id.tv_index);
        vTvSelect = findViewById(R.id.tv_select);
        vBtnConfirm = findViewById(R.id.btn_confirm);
        vBtnOrigin = findViewById(R.id.tv_origin);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        albumName = getIntent().getStringExtra(ALBUM_KEY);
        index = getIntent().getIntExtra(INDEX_KEY, 0);
        maxSize = getIntent().getIntExtra(SIZE_KEY, 0);
        mMedias = new ArrayList<>();
        ArrayList<Media> medias = getIntent().getParcelableArrayListExtra(MEDIAS_KEY);
        mMedias.addAll(medias);
        selectedList = (ArrayList<Long>) getIntent().getSerializableExtra(SELECTED_KEY);
        isOrigin = getIntent().getBooleanExtra(ORIGIN_KEY, false);
        activeOrigin = getIntent().getBooleanExtra(ACTIVE_ORIGIN_KEY, false);

        vBtnOrigin.setSelected(isOrigin);
        vTitle.setText(albumName);
        vIndex.setText((index + 1) + "/" + mMedias.size());
        if (selectedList.size() == 0) {
            vBtnConfirm.setText("确定");
        } else {
            vBtnConfirm.setText("确定(" + selectedList.size() + "/" + maxSize + ")");
        }
        mDisplayPagerAdapter = new DisplayPagerAdapter(getSupportFragmentManager(), mMedias);
        vViewPager.setPageMargin(10);
        vViewPager.setOffscreenPageLimit(2);
        vViewPager.setAdapter(mDisplayPagerAdapter);
        if (selectedList.contains(mMedias.get(index).getId())) {
            vTvSelect.setSelected(true);
        } else {
            vTvSelect.setSelected(false);
        }
        if (activeOrigin) {
            vBtnOrigin.setVisibility(View.VISIBLE);
        } else {
            vBtnOrigin.setVisibility(View.GONE);
        }

        vViewPager.setCurrentItem(index);

        vViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                vIndex.setText((position + 1) + "/" + mMedias.size());

                if (selectedList.contains(mMedias.get(position).getId())) {
                    vTvSelect.setSelected(true);
                } else {
                    vTvSelect.setSelected(false);
                }
            }
        });
    }

    /**
     * 选择当前这张图片
     */
//    @OnClick(R.id.tv_select)
    public void selectImg(View view) {

        int currentPosition = vViewPager.getCurrentItem();
        if (selectedList.contains(mMedias.get(currentPosition).getId())) {
            vTvSelect.setSelected(false);
            selectedList.remove(mMedias.get(currentPosition).getId());
        } else {
            if (selectedList.size() == maxSize) {
                Toast.makeText(this, "至多可以选择" + maxSize + "张图片", Toast.LENGTH_SHORT).show();
                return;
            }
            vTvSelect.setSelected(true);
            selectedList.add(mMedias.get(currentPosition).getId());
        }

        if (selectedList.size() == 0) {
            vBtnConfirm.setText("确定");
        } else {
            vBtnConfirm.setText("确定(" + selectedList.size() + "/" + maxSize + ")");
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
     * 确认选择
     */
//    @OnClick(R.id.btn_confirm)
    public void confirm(View view) {
        Intent intent = new Intent();
        intent.putExtra(SELECTED_RESULT_KEY, selectedList);
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
