package com.zte.android.lib.media.gallery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.zte.android.lib.media.Constants;
import com.zte.android.lib.media.R;
import com.zte.android.lib.media.gallery.adapter.AlbumAdapter;
import com.zte.android.lib.media.gallery.adapter.ImageAdapter;
import com.zte.android.lib.media.gallery.photoloader.Configuration;
import com.zte.android.lib.media.gallery.photoloader.PhotoLoader;
import com.zte.android.lib.media.gallery.photoloader.entity.Album;
import com.zte.android.lib.media.gallery.photoloader.entity.Media;
import com.zte.android.lib.media.widget.DropView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Jungle on 2017/7/14.
 */
public class ImagePickActivity extends AppCompatActivity {
    private static final int GALLERY_REQUEST_CODE = 0x1521;
    private static final int PREVIEW_REQUEST_CODE = 0x1522;

    public static final int DEFAULT_MAX_SIZE = 9;

    public static final int MODE_SINGLE_IMG = 0;
    public static final int MODE_MULTI_IMG = 1;
    public static final int MODE_SINGLE_MEDIA = 2;
    public static final int MODE_MULTI_MEDIA = 3;

    private static final String RESULT_KEY = "result";
    private static final String VIDEO_KEY = "include_video";
    private static final String ACTIVE_ORIGIN_KEY = "active_origin";
    private static final String MODE_KEY = "mode";
    private static final String SIZE_KEY = "size";

//    @BindView(R.id.gv_image)
    GridView vGvImage;
//    @BindView(R.id.btn_confirm)
    TextView vBtnConfirm;
//    @BindView(R.id.layout_pick_multi_bottom)
    RelativeLayout vLayoutBottom;
//    @BindView(R.id.btn_preview)
    TextView vBtnPreview;
//    @BindView(R.id.tv_origin)
    TextView vBtnOrigin;
//    @BindView(R.id.dropview)
    DropView vDropView;
//    @BindView(R.id.tv_album_name)
    TextView vTvAlbumName;
//    @BindView(R.id.tv_title)
    TextView vTvTitle;

    RecyclerView vAlbumRecyclerView;

    private ArrayList<Album> mAlbums;
    private ArrayList<Media> mMedias;
    private boolean isOrigin = false;
    private boolean activeOrigin = false;

    private int mode;
    private boolean includeVideo = false;
    private int maxSize = -1;
    private ImageAdapter mImageAdapter;
    private AlbumAdapter mAlbumAdapter;

    private int currentAlbumIndex = -1;

    public static void navigateToImagePickActivity(Activity activity, int mode, boolean inclueVideo, boolean activeOrigin, int requestCode) {
        Intent intent = new Intent(activity, ImagePickActivity.class);
        intent.putExtra(MODE_KEY, mode);
        intent.putExtra(VIDEO_KEY, inclueVideo);
        intent.putExtra(ACTIVE_ORIGIN_KEY, activeOrigin);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void navigateToImagePickActivity(Activity activity, int mode, int maxSize, int requestCode) {
        Intent intent = new Intent(activity, ImagePickActivity.class);
        intent.putExtra(MODE_KEY, mode);
        intent.putExtra(SIZE_KEY, maxSize);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void navigateToImagePickActivity(Activity activity, int mode, int maxSize, boolean inclueVideo, boolean activeOrigin, int requestCode) {
        Intent intent = new Intent(activity, ImagePickActivity.class);
        intent.putExtra(MODE_KEY, mode);
        intent.putExtra(VIDEO_KEY, inclueVideo);
        intent.putExtra(SIZE_KEY, maxSize);
        intent.putExtra(ACTIVE_ORIGIN_KEY, activeOrigin);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_pick);

        vGvImage = findViewById(R.id.gv_image);
        vBtnConfirm = findViewById(R.id.btn_confirm);
        vLayoutBottom = findViewById(R.id.layout_pick_multi_bottom);
        vBtnPreview = findViewById(R.id.btn_preview);
        vBtnOrigin = findViewById(R.id.tv_origin);
        vDropView = findViewById(R.id.dropview);
        vTvAlbumName = findViewById(R.id.tv_album_name);
        vTvTitle = findViewById(R.id.tv_title);
        init();
    }

    @Override
    public void onBackPressed() {
        if (vDropView.isExpanded()) {
            vDropView.collapseDrop();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        ArrayList<Long> selectedList;
        switch (requestCode) {
            case GALLERY_REQUEST_CODE:
                selectedList = (ArrayList<Long>) data.getSerializableExtra(GalleryActivity.SELECTED_RESULT_KEY);
                isOrigin = data.getBooleanExtra(GalleryActivity.SELECTED_ORIGIN_KEY, false);

                mImageAdapter.setSelectedList(selectedList);
                if (selectedList == null || selectedList.isEmpty()) {
//                    for (Media media : mImageAdapter.getSelectedList()) {
//                        mMedias.get(mMedias.indexOf(media)).setSelected(false);
//                    }
                    vBtnConfirm.setText("确定");
                    vBtnConfirm.setEnabled(false);
                    vBtnPreview.setText("预览");
                    vBtnPreview.setEnabled(false);
                    vBtnOrigin.setSelected(isOrigin);
                    return;
                }
                vBtnConfirm.setText("确定(" + selectedList.size() + "/" + maxSize + ")");
                vBtnConfirm.setEnabled(true);
                vBtnPreview.setText("预览(" + selectedList.size() + ")");
                vBtnPreview.setEnabled(true);
                vBtnOrigin.setSelected(isOrigin);
                break;
            case PREVIEW_REQUEST_CODE:
                selectedList = (ArrayList<Long>) data.getSerializableExtra(GalleryActivity.SELECTED_RESULT_KEY);
                isOrigin = data.getBooleanExtra(GalleryActivity.SELECTED_ORIGIN_KEY, false);

                mImageAdapter.setSelectedList(selectedList);
                if (selectedList == null || selectedList.isEmpty()) {
                    vBtnConfirm.setText("确定");
                    vBtnConfirm.setEnabled(false);
                    vBtnPreview.setText("预览");
                    vBtnPreview.setEnabled(false);
                    vBtnOrigin.setSelected(isOrigin);
                    return;
                }
                vBtnConfirm.setText("确定(" + selectedList.size() + "/" + maxSize + ")");
                vBtnConfirm.setEnabled(true);
                vBtnPreview.setText("预览(" + selectedList.size() + ")");
                vBtnPreview.setEnabled(true);
                vBtnOrigin.setSelected(isOrigin);
                break;
        }
    }

    /**
     * 初始化
     */
    private void init() {
        mode = getIntent().getIntExtra(MODE_KEY, MODE_SINGLE_IMG);
        includeVideo = getIntent().getBooleanExtra(VIDEO_KEY, false);
        activeOrigin = getIntent().getBooleanExtra(ACTIVE_ORIGIN_KEY, false);

        mAlbums = new ArrayList<>();

        switch (mode) {
            case MODE_MULTI_IMG:
            case MODE_SINGLE_IMG:
                vTvTitle.setText("选择图片");
                break;
            case MODE_SINGLE_MEDIA:
            case MODE_MULTI_MEDIA:
                vTvTitle.setText("选择图片或者视频");
                break;
        }
        switch (mode) {
            case MODE_SINGLE_IMG:
            case MODE_SINGLE_MEDIA:
                mImageAdapter = new ImageAdapter(this, mMedias, mode);
                mImageAdapter.setCallback(new SimpleItemClickCallback() {
                    @Override
                    public void photoClick(int position) {
                        gcAndFinalize();
                        compress(mMedias.get(position).getPath());
                    }

                    @Override
                    public void videoClick(int position) {
                        if (mMedias.get(position).isVideo()) {
                            Toast.makeText(ImagePickActivity.this, "VIDEO in MODE_SINGLE_IMG", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        retrieveVideo(mMedias.get(position).getPath());
                    }
                });
                vBtnConfirm.setVisibility(View.GONE);
                vLayoutBottom.setVisibility(View.GONE);
                break;
            case MODE_MULTI_IMG:
            case MODE_MULTI_MEDIA:
                maxSize = getIntent().getIntExtra(SIZE_KEY, DEFAULT_MAX_SIZE);
                mImageAdapter = new ImageAdapter(this, mMedias, mode, maxSize);
                mImageAdapter.setCallback(new SimpleItemClickCallback() {
                    @Override
                    public void photoClick(int position) {
                        GalleryActivity.navigateToGalleryActivity(ImagePickActivity.this, mAlbums.get(currentAlbumIndex).getName(), position, maxSize, mMedias, mImageAdapter.getSelectedList(), isOrigin, activeOrigin, GALLERY_REQUEST_CODE);
                    }

                    @Override
                    public void photoSelect(int count) {
                        if (count == 0) {
                            vBtnConfirm.setText("确定");
                            vBtnConfirm.setEnabled(false);
                            vBtnPreview.setText("预览");
                            vBtnPreview.setEnabled(false);
                        } else {
                            vBtnConfirm.setText("确定(" + count + "/" + maxSize + ")");
                            vBtnConfirm.setEnabled(true);
                            vBtnPreview.setText("预览(" + count + ")");
                            vBtnPreview.setEnabled(true);
                        }
                    }

                    @Override
                    public void videoClick(int position) {
                        if (!mImageAdapter.getSelectedList().isEmpty()) {
                            Toast.makeText(ImagePickActivity.this, "不能同时选择图片或视频", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ImagePickActivity.this, "video click", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                vLayoutBottom.setVisibility(View.VISIBLE);

                if (activeOrigin) {
                    vBtnOrigin.setVisibility(View.VISIBLE);
                } else {
                    vBtnOrigin.setVisibility(View.GONE);
                }
                setupDropView();
                break;
        }
        vGvImage.setAdapter(mImageAdapter);
        vGvImage.setVerticalSpacing(Configuration.GalleryConstants.divider);
        vGvImage.setHorizontalSpacing(Configuration.GalleryConstants.divider);
        vGvImage.setNumColumns(Configuration.GalleryConstants.numColumns);
        vGvImage.setPadding(Configuration.GalleryConstants.divider, Configuration.GalleryConstants.divider, Configuration.GalleryConstants.divider, Configuration.GalleryConstants.divider);
        vBtnOrigin.setSelected(isOrigin);
        loadMedia(0);
    }

    /**
     * 初始化相册选择控件
     */
    private void setupDropView() {
        View view = getLayoutInflater().inflate(R.layout.layout_album_dropview, null);
        vAlbumRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        vAlbumRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAlbumAdapter = new AlbumAdapter(this, mAlbums);
        mAlbumAdapter.setCallback(new AlbumAdapter.ClickCallback() {
            @Override
            public void callback(int position) {
                if (vDropView.isExpanded()) {
                    vDropView.collapseDrop();
                }

                if (currentAlbumIndex == position) {
                    return;
                }

                currentAlbumIndex = position;
                mMedias.clear();
                mMedias.addAll(mAlbums.get(position).getMedias());
                ArrayList<Long> selectedMideas = new ArrayList<>();
                selectedMideas.addAll(mImageAdapter.getSelectedList());
                mImageAdapter = new ImageAdapter(ImagePickActivity.this, mMedias, mode, maxSize);
                mImageAdapter.setSelectedList(selectedMideas);
                mImageAdapter.setCallback(new SimpleItemClickCallback() {
                    @Override
                    public void photoClick(int position) {
                        GalleryActivity.navigateToGalleryActivity(ImagePickActivity.this, mAlbums.get(currentAlbumIndex).getName(), position, maxSize, mMedias, mImageAdapter.getSelectedList(), isOrigin, activeOrigin, GALLERY_REQUEST_CODE);
                    }

                    @Override
                    public void photoSelect(int count) {
                        if (count == 0) {
                            vBtnConfirm.setText("确定");
                            vBtnConfirm.setEnabled(false);
                            vBtnPreview.setText("预览");
                            vBtnPreview.setEnabled(false);
                        } else {
                            vBtnConfirm.setText("确定(" + count + "/" + maxSize + ")");
                            vBtnConfirm.setEnabled(true);
                            vBtnPreview.setText("预览(" + count + ")");
                            vBtnPreview.setEnabled(true);
                        }
                    }

                    @Override
                    public void videoClick(int position) {
                        if (!mImageAdapter.getSelectedList().isEmpty()) {
                            Toast.makeText(ImagePickActivity.this, "不能同时选择图片或视频", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ImagePickActivity.this, "video click", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                vGvImage.setAdapter(mImageAdapter);
                mImageAdapter.refresh(mMedias);
                mAlbumAdapter.setSelectedAlbum(currentAlbumIndex);
                vTvAlbumName.setText(mAlbums.get(position).getName());
            }
        });
        vDropView.setExpandedView(view);
        vDropView.setDropListener(new DropView.DropListener() {
            @Override
            public void onExpandDropDown() {

            }

            @Override
            public void onCollapseDropDown() {

            }
        });
        vAlbumRecyclerView.setAdapter(mAlbumAdapter);
    }

    /**
     * 加载图册
     *
     * @param albumIndex 图册排序索引
     */
    private void loadMedia(final int albumIndex) {
//        mLoadingDialogHolder.addBackPressListener(new LoadingDialog.BackPressedCallback() {
//            @Override
//            public void callback() {
//                finish();
//            }
//        });
//        showLoadingWithCancelable(false);
        PhotoLoader.getInstance().loadPhotos(Configuration.mContext, includeVideo, new PhotoLoader.PhotosLoadHandler() {
            @Override
            public void getPhotosSuc(List<Album> albums) {
                int index = albumIndex;
                mAlbums.clear();
                mAlbums.addAll(albums);
                if (index >= mAlbums.size()) {
                    index = 0;
                }
//                mAlbumAdapter.refresh(mAlbums);
//                updateHeaderView(index);
                mMedias = new ArrayList<>();
                mMedias.addAll(mAlbums.get(index).getMedias());
                mImageAdapter.refresh(mMedias);
                if (mode == MODE_MULTI_IMG) {
                    mAlbumAdapter.notifyDataSetChanged();
                }
                currentAlbumIndex = index;
                vTvAlbumName.setText(mAlbums.get(currentAlbumIndex).getName());
//                dismissLoading();
            }

            @Override
            public void getPhotosFail() {
//                dismissLoading();
                Toast.makeText(ImagePickActivity.this, "获取多媒体文件失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 返回
     */
//    @OnClick(R.id.btn_back)
    public void back(View view) {
        if (vDropView.isExpanded()) {
            vDropView.collapseDrop();
        } else {
            finish();
        }
    }

    /**
     * 确认
     */
//    @OnClick(R.id.btn_confirm)
    public void confirm(View view) {
        if (vDropView.isExpanded()) {
            vDropView.collapseDrop();
            return;
        }
        if (mImageAdapter.getSelectedList().isEmpty()) {
            return;
        }
        ArrayList<String> mediasPath = new ArrayList<>();
//        for (Long id : mImageAdapter.getSelectedList()) {
//            mediasPath.add(media.getPath());
//        }
        List<Media> allMedias = mAlbums.get(0).getMedias();
        for (Long id : mImageAdapter.getSelectedList()) {
            for (Media media : allMedias) {
//                if (mImageAdapter.getSelectedList().contains(media.getId())) {
                if (id == media.getId()) {
                    mediasPath.add(media.getPath());
                }
                if (mediasPath.size() == mImageAdapter.getSelectedList().size()) {
                    break;
                }
            }
        }
        if (isOrigin) {
            retrieveImage(mediasPath);
        } else {
            gcAndFinalize();
            compress(mediasPath);
        }
    }

    /**
     * 预览
     */
//    @OnClick(R.id.btn_preview)
    public void preview(View view) {
        if (vDropView.isExpanded()) {
            vDropView.collapseDrop();
            return;
        }
//        GalleryActivity.navigateToGalleryActivity(ImagePickActivity.this, position, maxSize, mMedias, mImageAdapter.getSelectedList(), GALLERY_REQUEST_CODE);
        ArrayList<Media> selectedMedias = new ArrayList<>();
        List<Media> allMedias = mAlbums.get(0).getMedias();
        for (Long selectedMediaId : mImageAdapter.getSelectedList()) {
            for (Media media : allMedias) {
//                if (mImageAdapter.getSelectedList().contains(media.getId())) {
                if (selectedMediaId == media.getId()) {
                    selectedMedias.add(media);
                }
            }
            if (selectedMedias.size() == mImageAdapter.getSelectedList().size()) {
                break;
            }
        }
        PreviewActivity.navigateToPreviewActivity(this, maxSize, selectedMedias, mImageAdapter.getSelectedList(), isOrigin, activeOrigin, PREVIEW_REQUEST_CODE);
    }

    /**
     * 原图
     */
//    @OnClick(R.id.tv_origin)
    public void origin(View view) {
        if (vDropView.isExpanded()) {
            vDropView.collapseDrop();
            return;
        }
        isOrigin = !isOrigin;
        vBtnOrigin.setSelected(isOrigin);
    }

    /**
     * 选择图册
     */
//    @OnClick(R.id.tv_album_name)
    public void pickAlbum(View view) {
        if (!vDropView.isExpanded()) {
            vDropView.expandDrop();
        } else {
            vDropView.collapseDrop();
        }
    }

    /**
     * 压缩
     */
    private void compress(ArrayList<String> filesPath) {
//        try {
//            File[] files = new File[filesPath.size()];
//            final BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inPreferredConfig = Bitmap.Config.RGB_565;
//            for (int i = 0; i < filesPath.size(); i++) {
//                File outFile = new File(getExternalFilesDir(null), "Mge_" + System.currentTimeMillis() + "_" + i + ".jpg");
//                if(!getExternalFilesDir(null).exists()){
//                    getExternalFilesDir(null).mkdirs();
//                }
//                FileOutputStream fos = new FileOutputStream(outFile);
//                FileInputStream fis = new FileInputStream(new File(filesPath.get(i)));
//                byte[] buffer = new byte[4096];
//                int len = -1;
//                while ((len = fis.read(buffer)) != -1) {
//                    fos.write(buffer, 0, len);
//                }
//                fos.close();
//                fis.close();
//                files[i] = outFile;
//            }
//            Tiny.FileCompressOptions compressOptions = new Tiny.FileCompressOptions();
//            compressOptions.config = Bitmap.Config.RGB_565;
//            Tiny.getInstance().source(files).batchAsFile().withOptions(compressOptions).batchCompress(new FileBatchCallback() {
//                @Override
//                public void callback(boolean isSuccess, String[] outfile) {
//                    if (!isSuccess) {
//                        Toast.makeText(ImagePickActivity.this, "压缩失败", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    ArrayList<String> paths = new ArrayList<>();
//                    paths.addAll(Arrays.asList(outfile));
//                    retrieveImage(paths);
//                }
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        ImageUtils.compress(ImagePickActivity.this, filesPath, new ImageUtils.CompressCallback() {

            @Override
            public void compressFile(String path) {

            }

            @Override
            public void compressFiles(List<String> paths) {
                ArrayList<String> results = new ArrayList<>();
                results.addAll(paths);
                retrieveImage(results);
            }
        });

    }

    /**
     * 压缩
     */
    private void compress(String filePath) {
//        try {
//            File outfile = new File(getExternalFilesDir(null), "Mge_" + System.currentTimeMillis() + ".jpg");
//            if (!getExternalFilesDir(null).exists()) {
//                getExternalFilesDir(null).mkdirs();
//            }
//            FileOutputStream fos = new FileOutputStream(outfile);
//            FileInputStream fis = new FileInputStream(new File(filePath));
//            byte[] buffer = new byte[4096];
//            int len = -1;
//            while ((len = fis.read(buffer)) != -1) {
//                fos.write(buffer, 0, len);
//            }
//            fos.close();
//            fis.close();
////            if (!outfile.exists()) {
////                outfile.mkdir();
////                outfile.createNewFile();
////            }
////            final BitmapFactory.Options options = new BitmapFactory.Options();
////            options.inPreferredConfig = Bitmap.Config.RGB_565;
////            Bitmap originBitmap = BitmapFactory.decodeFile(filePath, options);
//
////        setupOriginInfo(originBitmap, outfile.length());
//
//            Tiny.FileCompressOptions compressOptions = new Tiny.FileCompressOptions();
//            compressOptions.config = Bitmap.Config.RGB_565;
//            Tiny.getInstance().source(outfile).asFile().withOptions(compressOptions).compress(new FileCallback() {
//                @Override
//                public void callback(boolean isSuccess, String outFilePath) {
//                    if (!isSuccess) {
//                        Toast.makeText(ImagePickActivity.this, "压缩失败", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    retrieveImage(outFilePath);
//                }
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        ImageUtils.compress(ImagePickActivity.this, filePath, new ImageUtils.CompressCallback() {

            @Override
            public void compressFile(String path) {
                Log.i("TAG", "path = " + path);
                retrieveImage(path);
            }

            @Override
            public void compressFiles(List<String> paths) {

            }
        });
    }

    /**
     * 返回一张图片
     */
    private void retrieveImage(String path) {
        Intent intent = new Intent();
        intent.putExtra(Constants.PICK_SINGLE_IMG_RESULT, path);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * 返回多张图片
     */
    private void retrieveImage(ArrayList<String> paths) {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(Constants.PICK_MULTI_IMG_RESULT, paths);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void retrieveVideo(String path) {
        Intent intent = new Intent();
        intent.putExtra(Constants.PICK_MULTI_MEDIA_RESULT, path);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * 手动通知虚拟机GC
     */
    private void gcAndFinalize() {
        Runtime runtime = Runtime.getRuntime();
        System.gc();
        runtime.runFinalization();
        System.gc();
    }

}
