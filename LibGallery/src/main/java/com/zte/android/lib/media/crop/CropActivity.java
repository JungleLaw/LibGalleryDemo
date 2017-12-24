package com.zte.android.lib.media.crop;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;


import com.zte.android.lib.media.Constants;
import com.zte.android.lib.media.R;
import com.zte.android.lib.media.crop.view.CropImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Created by Jungle on 2017/7/14.
 */
public class CropActivity extends AppCompatActivity implements CropImageView.OnCropImageCompleteListener {
    private static final String PATH_KEY = "path";
    private static final String RATIO_WIDTH_KEY = "width_ratio";
    private static final String RATIO_HEIGHT_KEY = "height_ratio";
    private static final String OUT_WIDTH_KEY = "out_width";
    private static final String OUT_HEIGHT_KEY = "out_height";

    private static final int MODE_DYNAMIC = 1;
    private static final int MODE_RATIO = 2;
    private static final int MODE_SIZE = 3;


    //    @BindView(R.id.crop_view)
    private CropImageView vCropView;

    private int widthRatio = -1;
    private int heightRatio = -1;
    private int outWidth = -1;
    private int outHeight = -1;

    private String path;
    private int mode = -1;

    /**
     * the options that were set for the crop image
     */

    public static void navigetToCropActivity(Activity activity, String path, int requestCode) {
        Intent intent = new Intent(activity, CropActivity.class);
        intent.putExtra(PATH_KEY, path);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void navigetToCropActivityWithRatio(Activity activity, String path, int widthRatio, int heightRatio, int requestCode) {
        Intent intent = new Intent(activity, CropActivity.class);
        intent.putExtra(PATH_KEY, path);
        intent.putExtra(RATIO_WIDTH_KEY, widthRatio);
        intent.putExtra(RATIO_HEIGHT_KEY, heightRatio);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void navigetToCropActivityWithOutSize(Activity activity, String path, int width, int height, int requestCode) {
        Intent intent = new Intent(activity, CropActivity.class);
        intent.putExtra(PATH_KEY, path);
        intent.putExtra(OUT_WIDTH_KEY, width);
        intent.putExtra(OUT_HEIGHT_KEY, height);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
//        ButterKnife.bind(this);
        vCropView = (CropImageView) findViewById(R.id.crop_view);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        vCropView.setOnCropImageCompleteListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        vCropView.setOnCropImageCompleteListener(null);
    }

    private void init() {
        if (getIntent().hasExtra(RATIO_WIDTH_KEY) && getIntent().hasExtra(RATIO_HEIGHT_KEY)) {
            widthRatio = getIntent().getIntExtra(RATIO_WIDTH_KEY, 0);
            heightRatio = getIntent().getIntExtra(RATIO_HEIGHT_KEY, 0);
            if (widthRatio <= 0 || heightRatio <= 0) {
                throw new IllegalArgumentException("widthRatio and heightRatio must be > 0.");
            }
            mode = MODE_RATIO;
        } else if (getIntent().hasExtra(OUT_WIDTH_KEY) && getIntent().hasExtra(OUT_HEIGHT_KEY)) {
            outWidth = getIntent().getIntExtra(OUT_WIDTH_KEY, 0);
            outHeight = getIntent().getIntExtra(OUT_HEIGHT_KEY, 0);
            if (outWidth <= 0 || outHeight <= 0) {
                throw new IllegalArgumentException("outWidth and outHeight must be > 0.");
            }
            mode = MODE_SIZE;
        } else {
            mode = MODE_DYNAMIC;
        }

        path = getIntent().getStringExtra(PATH_KEY);
        Uri uri = Uri.fromFile(new File(path));
        int divisor;
        switch (mode) {
            case MODE_RATIO:
//                vCropView.configureOverlay().setAspectRatio(new AspectRatio(widthRatio, heightRatio)).apply();
                divisor = getMaxCommonDivisor(widthRatio, heightRatio);
                vCropView.setAspectRatio(widthRatio / divisor, heightRatio / divisor);
                break;
            case MODE_SIZE:
                divisor = getMaxCommonDivisor(outWidth, outHeight);
                vCropView.setAspectRatio(outWidth / divisor, outHeight / divisor);
                break;
            case MODE_DYNAMIC:
//                vCropView.configureOverlay().setDynamicCrop(true).setAspectRatio(AspectRatio.IMG_SRC).apply();
                break;
        }
        vCropView.setImageUriAsync(uri);
    }

    //    @OnClick(R.id.btn_back)
    public void back(View view) {
        finish();
    }

    //    @OnClick(R.id.save)
    public void save(View view) {
        switch (mode) {
            case MODE_RATIO:
                vCropView.getCroppedImageAsync();
                break;
            case MODE_SIZE:
                vCropView.getCroppedImageAsync(outWidth, outHeight);
                break;
            case MODE_DYNAMIC:
                vCropView.getCroppedImageAsync();
                break;
        }
    }

    //辗转相除法：返回公约数
    public static int getMaxCommonDivisor(int x, int y) {
        int a, b, c;
        a = x;
        b = y;
        while (b != 0) {
            c = a % b;
            a = b;
            b = c;
        }
        return a;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detach();
    }

    public void detach() {
        widthRatio = -1;
        heightRatio = -1;
        outWidth = -1;
        outHeight = -1;

        path = null;
        mode = -1;
        vCropView = null;
    }

    @Override
    public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
        if (result.getError() == null) {
            Intent intent = new Intent();
            Uri uri = result.getUri();
            if (uri != null) {
                intent.putExtra(Constants.CROP_IMG_RESULT, uri.getPath());
            } else {
                Bitmap bitmap = result.getBitmap();
                File cropFile = new File(getExternalFilesDir(null), "Mge_" + System.currentTimeMillis() + "_crop.jpg");
                if (!getExternalFilesDir(null).exists()) {
                    getExternalFilesDir(null).mkdirs();
                }
                try {
                    saveFile(bitmap, cropFile.getAbsolutePath());
                    intent.putExtra(Constants.CROP_IMG_RESULT, cropFile.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "裁剪失败", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Toast.makeText(this, "Image crop failed: " + result.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 保存文件
     *
     * @param bm
     * @throws IOException
     */
    public void saveFile(Bitmap bm, String path) throws IOException {
//        File dirFile = new File(path);
//        if(!dirFile.exists()){
//            dirFile.mkdir();
//        }
        File myCaptureFile = new File(path);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
    }
}
