package com.zte.android.lib.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;


/**
 * 图册模块使用演示
 */
public class MainActivity extends AppCompatActivity {

//    @BindView(R.id.btn_bottomsheet)
//    Button vBtnBottomSheet;
//
//    @BindView(R.id.btn_camera)
//    Button vBtnCamera;
//    @BindView(R.id.btn_crop_default)
//    Button vBtnCropDefault;
//    @BindView(R.id.btn_crop_ratio)
//    Button vBtnCropRatio;
//    @BindView(R.id.btn_crop_size)
//    Button vBtnCropSize;
//    @BindView(R.id.btn_pick_single)
//    Button vBtnPickSingle;
//    @BindView(R.id.btn_pick_multi)
//    Button vBtnPickMulti;
//    @BindView(R.id.btn_pick_single_media)
//    Button vBtnPickSingleMedia;
//    @BindView(R.id.btn_pick_multi_medias)
//    Button vBtnPickMultiMedias;
//    @BindView(R.id.et_width)
//    EditText vEtWidth;
//    @BindView(R.id.et_height)
//    EditText vEtHeight;
//    @BindView(R.id.et_ratio_width)
//    EditText vEtRatioWidth;
//    @BindView(R.id.et_ratio_height)
//    EditText vEtRatioHeight;
//
//    @BindView(R.id.img_display)
//    ImageView vImgDisplay;
//    @BindView(R.id.recyclerview)
//    RecyclerView vRecyclerView;
//
//    private PickedAdapter mAdapter;
//    private List<String> mMedias;
//
//    private String cropSrc;
//    private MediaPick mMediaPick;
//
//    private BottomSheet bottomSheet;
//    private CustomDialog customDialog;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_crop_demo_main);
//        ButterKnife.bind(this);
//
//        List<BottomSheetItem> items = new ArrayList<>();
////        items.add(new BottomSheetItem("相机", R.drawable.icon_camera));
////        items.add(new BottomSheetItem("相册", R.drawable.icon_gallery));
//        items.add(new BottomSheetItem("相机"));
//        items.add(new BottomSheetItem("相册"));
//        bottomSheet = new BottomSheet.Builder(this)
//                .setItems(items)
////                .setShowCancel(true)
//                .setCallback(new BottomSheetAdapter.ItemClickCallback() {
//                    @Override
//                    public void callback(int position) {
//                        switch (position) {
//                            case 0:
//                                mMediaPick.takeCamera();
//                                break;
//                            case 1:
//                                mMediaPick.pickImage();
//                                break;
//                        }
//                        bottomSheet.dismiss();
//                    }
//                }).build();
//
//        customDialog = new CustomDialog.Builder(this, CustomDialog.MODE_LIST).setItems(new String[]{"相机", "相册"}, new CustomDialog.SimpleAdapter.Callback() {
//            @Override
//            public void itemCallback(int position) {
//                switch (position) {
//                    case 0:
//                        mMediaPick.takeCamera();
//                        break;
//                    case 1:
//                        mMediaPick.pickImage();
//                        break;
//                }
//                customDialog.dismiss();
//            }
//        }).build();
//
//        mMediaPick = MediaPickImpl.newInstance(this, new SimpleResultCallback() {
//            @Override
//            public void pickImageCallback(String path) {
//                vImgDisplay.setVisibility(View.VISIBLE);
//                vRecyclerView.setVisibility(View.GONE);
//                cropSrc = path;
//                ImageDisplayUtils.display(MainActivity.this, cropSrc, vImgDisplay);
//            }
//
//            @Override
//            public void pickImagesCallback(List<String> paths) {
//                vImgDisplay.setVisibility(View.GONE);
//                vRecyclerView.setVisibility(View.VISIBLE);
//                mMedias.clear();
//                ArrayList<String> mediasSelected = new ArrayList<>();
//                mediasSelected.addAll(paths);
//                mMedias.addAll(mediasSelected);
//                mAdapter.notifyDataSetChanged();
//                cropSrc = null;
//            }
//
//            @Override
//            public void takeCameraCallback(String path) {
//                cropSrc = path;
//                mMediaPick.crop(cropSrc);
////                LogUtils.i("cameraFilePath = " + cropSrc);
//////                File file = new File(cameraFilePath);
////                vImgDisplay.setVisibility(View.VISIBLE);
////                vRecyclerView.setVisibility(View.GONE);
////                ImageDisplayUtils.display(MainActivity.this, path, vImgDisplay);
//            }
//
//            @Override
//            public void cropCallback(String path) {
//                cropSrc = path;
//                vImgDisplay.setVisibility(View.VISIBLE);
//                vRecyclerView.setVisibility(View.GONE);
//                ImageDisplayUtils.display(MainActivity.this, cropSrc, vImgDisplay);
//                LogUtils.i("TAKE_CROP_REQUEST_CODE : " + cropSrc);
//            }
//
////            @Override
////            public void pickSingleMedia(String path) {
////                super.pickSingleMedia(path);
////            }
//
//
//            @Override
//            public void pickSingleMedia(String path, boolean isImage) {
//                super.pickSingleMedia(path, isImage);
//            }
//
//            @Override
//            public void pickMultiMedias(List<String> paths) {
//                super.pickMultiMedias(paths);
//            }
//        }, true, true);
//
//        mMedias = new ArrayList<>();
//        vRecyclerView.setHasFixedSize(true);
//        vRecyclerView.setNestedScrollingEnabled(false);
//        vRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
//        mAdapter = new PickedAdapter(this, mMedias);
//        vRecyclerView.setAdapter(mAdapter);
//    }
//
//    @OnClick(R.id.btn_bottomsheet)
//    public void showBottomSheet(View view) {
//        bottomSheet.show();
//    }
//
//    @OnClick(R.id.btn_dialog)
//    public void showDialog(View view) {
//        customDialog.show();
//    }
//
//    @OnClick(R.id.btn_camera)
//    public void takeCamera(View view) {
//        mMediaPick.takeCamera();
//    }
//
//    @OnClick({R.id.btn_pick_single, R.id.btn_pick_multi, R.id.btn_pick_single_media, R.id.btn_pick_multi_medias})
//    public void pickPhoto(View view) {
//        switch (view.getId()) {
//            case R.id.btn_pick_single:
//                mMediaPick.pickImage();
//                break;
//            case R.id.btn_pick_multi:
//                mMediaPick.pickImages();
//                break;
//            case R.id.btn_pick_single_media:
//                mMediaPick.pickSingleMedia();
//                break;
//            case R.id.btn_pick_multi_medias:
//                mMediaPick.pickMultiMedias();
//                break;
//        }
//    }
//
//    @OnClick({R.id.btn_crop_default, R.id.btn_crop_ratio, R.id.btn_crop_size})
//    public void crop(View view) {
//        if (TextUtils.isEmpty(cropSrc)) {
//            Toast.makeText(this, "通过Camera或者Pick Single选择一张待裁剪图片", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        switch (view.getId()) {
//            case R.id.btn_crop_default:
//                mMediaPick.crop(cropSrc);
//                break;
//            case R.id.btn_crop_ratio:
//                if (TextUtils.isEmpty(vEtRatioWidth.getText()) || TextUtils.isEmpty(vEtRatioHeight.getText())) {
//                    Toast.makeText(this, "设置比例参数", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                int widthRatio = Integer.parseInt(vEtRatioWidth.getText().toString().trim());
//                int heightRatio = Integer.parseInt(vEtRatioHeight.getText().toString().trim());
//                mMediaPick.cropWithRatio(cropSrc, widthRatio, heightRatio);
//                break;
//            case R.id.btn_crop_size:
//                if (TextUtils.isEmpty(vEtWidth.getText()) || TextUtils.isEmpty(vEtHeight.getText())) {
//                    Toast.makeText(this, "设置输出参数", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                int outWidth = Integer.parseInt(vEtWidth.getText().toString().trim());
//                int outHeight = Integer.parseInt(vEtHeight.getText().toString().trim());
//                mMediaPick.cropWithOutSize(cropSrc, outWidth, outHeight);
//                break;
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (mMediaPick.handlePickResult(requestCode, resultCode, data)) {
//            return;
//        }
//    }
}
