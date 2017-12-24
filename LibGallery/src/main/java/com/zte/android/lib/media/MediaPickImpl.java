package com.zte.android.lib.media;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.android.maiguoer.config.IConfig;
import com.android.maiguoer.widget.media.crop.CropActivity;
import com.android.maiguoer.widget.media.gallery.ImagePickActivity;
import com.android.maiguoer.widget.media.gallery.ImageUtils;
import com.apkfuns.logutils.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnegative;

/**
 * 多媒体抽象实现
 * Created by Jungle on 2017/7/20.
 */

public class MediaPickImpl implements MediaPick {


    private static final int PICK_MAX_SIZE = 9;

    private Activity activity;
    private String cropSrc;
    private ResultCallback callback;
    private boolean includeVideo = false;
    private boolean activeOrigin = false;

    private MediaPickImpl(Activity activity, ResultCallback callback) {
        this.activity = activity;
        this.callback = callback;
    }

    private MediaPickImpl(Activity activity, ResultCallback callback, boolean includeVideo, boolean activeOrigin) {
        this.activity = activity;
        this.callback = callback;
        this.includeVideo = includeVideo;
        this.activeOrigin = activeOrigin;
    }

    public static final MediaPickImpl newInstance(Activity activity, ResultCallback callback) {
        return new MediaPickImpl(activity, callback);
    }

    public static final MediaPickImpl newInstance(Activity activity, ResultCallback callback, boolean includeVideo, boolean activeOrigin) {
        return new MediaPickImpl(activity, callback, includeVideo, activeOrigin);
    }

    @Override
    public void pickImage() {
        ImagePickActivity.navigateToImagePickActivity(activity, ImagePickActivity.MODE_SINGLE_IMG, false, false, IConfig.REQUEST_CODE_PICK_IMG_SINGLE);
    }

    @Override
    public void pickImages() {
        ImagePickActivity.navigateToImagePickActivity(activity, ImagePickActivity.MODE_MULTI_IMG, PICK_MAX_SIZE, false, activeOrigin, IConfig.REQUEST_CODE_PICK_IMG_MULTI);
    }

    @Override
    public void pickImages(int maxSize) {
        ImagePickActivity.navigateToImagePickActivity(activity, ImagePickActivity.MODE_MULTI_IMG, maxSize, false, activeOrigin, IConfig.REQUEST_CODE_PICK_IMG_MULTI);
    }

    @Override
    public void pickSingleMedia() {
        ImagePickActivity.navigateToImagePickActivity(activity, ImagePickActivity.MODE_SINGLE_MEDIA, includeVideo, activeOrigin, IConfig.REQUEST_CODE_PICK_MEDIA_SINGLE);
    }

    @Override
    public void pickMultiMedias() {
        ImagePickActivity.navigateToImagePickActivity(activity, ImagePickActivity.MODE_MULTI_MEDIA, PICK_MAX_SIZE, includeVideo, activeOrigin, IConfig.REQUEST_CODE_PICK_MEDIA_MULTI);
    }

    @Override
    public void takeCamera() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // 使用相机拍照
            Intent takephotoIntent = new Intent(
                    android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            File mImageTemp = new File(activity.getExternalFilesDir(null), "Mge_" + System.currentTimeMillis() + "_camera.jpg");
            if (!activity.getExternalFilesDir(null).exists()) {
                activity.getExternalFilesDir(null).mkdirs();
            }
            cropSrc = mImageTemp.getAbsolutePath();
            takephotoIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(mImageTemp));
            activity.startActivityForResult(takephotoIntent, IConfig.REQUEST_CODE_TAKE_CAMERA);
        } else {
            Toast.makeText(activity, "请确认已经插入SD卡", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void crop(String filePath) {
        CropActivity.navigetToCropActivity(activity, filePath, IConfig.REQUEST_CODE_TAKE_CROP);
    }

    @Override
    public void cropWithRatio(@NonNull String filePath, @IntRange(from = 1) int widthRatio, @IntRange(from = 1) int heightRatio) {
        CropActivity.navigetToCropActivityWithRatio(activity, filePath, widthRatio, heightRatio, IConfig.REQUEST_CODE_TAKE_CROP);
    }

    @Override
    public void cropWithOutSize(@NonNull String filePath, @Nonnegative int outWidth, @Nonnegative int outHeight) {
        CropActivity.navigetToCropActivityWithOutSize(activity, filePath, outWidth, outHeight, IConfig.REQUEST_CODE_TAKE_CROP);
    }

    @Override
    public boolean handlePickResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK && (requestCode == IConfig.REQUEST_CODE_PICK_IMG_SINGLE || requestCode == IConfig.REQUEST_CODE_PICK_IMG_MULTI || requestCode == IConfig.REQUEST_CODE_TAKE_CAMERA || requestCode == IConfig.REQUEST_CODE_TAKE_CROP)) {
            Toast.makeText(activity, "多媒体处理失败", Toast.LENGTH_SHORT).show();
            return true;
        } else if (resultCode == Activity.RESULT_OK && (requestCode == IConfig.REQUEST_CODE_PICK_IMG_SINGLE || requestCode == IConfig.REQUEST_CODE_PICK_IMG_MULTI || requestCode == IConfig.REQUEST_CODE_TAKE_CAMERA || requestCode == IConfig.REQUEST_CODE_TAKE_CROP)) {
            switch (requestCode) {
                case IConfig.REQUEST_CODE_PICK_IMG_SINGLE:
                    cropSrc = data.getStringExtra(Constants.PICK_SINGLE_IMG_RESULT);
                    if (callback != null)
                        callback.pickImageCallback(cropSrc);
                    break;
                case IConfig.REQUEST_CODE_PICK_IMG_MULTI:
                    ArrayList<String> mediasSelected = data.getStringArrayListExtra(Constants.PICK_MULTI_IMG_RESULT);
                    if (callback != null)
                        callback.pickImagesCallback(mediasSelected);
                    break;
                case IConfig.REQUEST_CODE_TAKE_CAMERA:
                    LogUtils.i("cameraFilePath = " + cropSrc);
                    compress(cropSrc);
                    break;
                case IConfig.REQUEST_CODE_TAKE_CROP:
                    cropSrc = data.getStringExtra(Constants.CROP_IMG_RESULT);
                    LogUtils.i("TAKE_CROP_REQUEST_CODE : " + cropSrc);
                    if (callback != null)
                        callback.cropCallback(cropSrc);
                    break;
                case IConfig.REQUEST_CODE_PICK_MEDIA_SINGLE:
                    break;
                case IConfig.REQUEST_CODE_PICK_MEDIA_MULTI:
                    break;
            }
            return true;
        }
        return false;
    }

    private void compress(String filePath) {
//        try {
//            File outfile = new File(activity.getExternalFilesDir(null), "Mge_" + System.currentTimeMillis() + ".jpg");
//            if(!activity.getExternalFilesDir(null).exists()){
//                activity.getExternalFilesDir(null).mkdirs();
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
//
//            Tiny.FileCompressOptions compressOptions = new Tiny.FileCompressOptions();
//            compressOptions.config = Bitmap.Config.RGB_565;
//            Tiny.getInstance().source(outfile).asFile().withOptions(compressOptions).compress(new FileCallback() {
//                @Override
//                public void callback(boolean isSuccess, String outFilePath) {
//                    if (!isSuccess) {
//                        Toast.makeText(activity, "压缩失败", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    if (callback != null)
//                        callback.takeCameraCallback(cropSrc);
//                }
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        ImageUtils.compress(activity, filePath, new ImageUtils.CompressCallback() {
            @Override
            public void compressFile(String path) {
                if (callback != null)
                    callback.takeCameraCallback(path);
            }

            @Override
            public void compressFiles(List<String> paths) {

            }
        });
    }
}
