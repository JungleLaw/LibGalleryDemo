package com.zte.android.lib.media.gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileBatchCallback;
import com.zxy.tiny.callback.FileCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jungle on 2017/8/21.
 */

public class ImageUtils {
    private ImageUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 批量压缩
     *
     * @param context   上下文
     * @param filesPath 文件路径
     * @return
     */
    public static void compress(final Context context, ArrayList<String> filesPath, final CompressCallback callback) {
        try {
            File[] files = new File[filesPath.size()];
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            for (int i = 0; i < filesPath.size(); i++) {
                File outFile = new File(context.getExternalFilesDir(null), "Mge_" + System.currentTimeMillis() + "_" + i + ".jpg");
                if (!context.getExternalFilesDir(null).exists()) {
                    context.getExternalFilesDir(null).mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(outFile);
                FileInputStream fis = new FileInputStream(new File(filesPath.get(i)));
                byte[] buffer = new byte[4096];
                int len = -1;
                while ((len = fis.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                fis.close();
                files[i] = outFile;
            }
            Tiny.FileCompressOptions compressOptions = new Tiny.FileCompressOptions();
            compressOptions.config = Bitmap.Config.RGB_565;
            Tiny.getInstance().source(files).batchAsFile().withOptions(compressOptions).batchCompress(new FileBatchCallback() {
                @Override
                public void callback(boolean isSuccess, String[] outfile, Throwable throwable) {
                    if (!isSuccess) {
                        Toast.makeText(context, "压缩失败", Toast.LENGTH_SHORT).show();
                        return;
                    }
//                    paths.addAll();
                    callback.compressFiles(Arrays.asList(outfile));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 压缩
     *
     * @param context  上下文
     * @param filePath 文件路径
     * @return
     */
    public static void compress(final Context context, String filePath, final CompressCallback callback) {
        try {
            File outfile = new File(context.getExternalFilesDir(null), "Mge_" + System.currentTimeMillis() + ".jpg");
            if (!context.getExternalFilesDir(null).exists()) {
                context.getExternalFilesDir(null).mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(outfile);
            FileInputStream fis = new FileInputStream(new File(filePath));
            byte[] buffer = new byte[4096];
            int len = -1;
            while ((len = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            fis.close();

            Tiny.FileCompressOptions compressOptions = new Tiny.FileCompressOptions();
            compressOptions.config = Bitmap.Config.RGB_565;
            Tiny.getInstance().source(outfile).asFile().withOptions(compressOptions).compress(new FileCallback() {

                @Override
                public void callback(boolean isSuccess, String outFilePath, Throwable throwable) {
                    if (!isSuccess) {
                        Toast.makeText(context, "压缩失败", Toast.LENGTH_SHORT).show();
                        return;
                    }
//                    retreiveFilePath = outFilePath;
                    callback.compressFile(outFilePath);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除裁剪或压缩后产生的缓存
     *
     * @param context
     */
    public static void cleanImageCache(final Context context) {
        File file = context.getExternalFilesDir(null);
        if (file.isDirectory()) {
            File[] cacheFiles = file.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (name.startsWith("Mge_") && name.endsWith(".jpg")) {
                        return true;
                    }
                    return false;
                }
            });
            for (File cache : cacheFiles) {
                cache.delete();
            }
        }
    }

    public interface CompressCallback {
        void compressFile(String path);

        void compressFiles(List<String> paths);
    }
}
