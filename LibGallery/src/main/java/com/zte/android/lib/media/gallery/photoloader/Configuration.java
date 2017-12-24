package com.zte.android.lib.media.gallery.photoloader;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * 图片展示的配置参数
 * Created by Law on 2016/9/9.
 */
public class Configuration {
    public static Context mContext;

    public static void initGalleryConstants(Context context) {
        mContext = context.getApplicationContext();
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        GalleryConstants.screenWidth = metrics.widthPixels;
        GalleryConstants.screenHeight = metrics.heightPixels;
        GalleryConstants.numColumns = 3;
        for (int i = 1; ; i++) {
            if ((GalleryConstants.screenWidth - (GalleryConstants.numColumns + 1) * i) % GalleryConstants.numColumns == 0) {
                GalleryConstants.lenght = (GalleryConstants.screenWidth - (GalleryConstants.numColumns + 1) * i) / GalleryConstants.numColumns;
                GalleryConstants.divider = i;
                break;
            }
        }
        GalleryConstants.albumHeight = (int) (GalleryConstants.lenght * 1.25f);
    }

    public static class GalleryConstants {
        /**
         * 屏幕宽度
         */
        public static int screenWidth;
        /**
         * 屏幕高度
         */
        public static int screenHeight;
        /**
         * 图片展示列数
         */
        public static int numColumns;
        /**
         * 每格图片展示的长度
         */
        public static int lenght;
        /**
         * gridview的divider
         */
        public static int divider;
        /**
         * 相册高度
         */
        public static int albumHeight;
    }
}
