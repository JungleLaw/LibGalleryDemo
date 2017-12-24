package com.zte.android.lib.media;

import java.util.List;

/**
 * 图片结果回调
 * Created by Jungle on 2017/7/21.
 */
public interface ResultCallback {

    /**
     * 选择一张,返回一张的绝对路径
     *
     * @param path
     */
    void pickImageCallback(String path);

    /**
     * 选择多张图片，返回绝对路径集合
     *
     * @param paths
     */
    void pickImagesCallback(List<String> paths);

    /**
     * 拍照，返回绝对路径
     *
     * @param path
     */
    void takeCameraCallback(String path);

    /**
     * 裁剪，返回绝对路径
     *
     * @param path
     */
    void cropCallback(String path);

    void pickSingleMedia(String path, boolean isImage);

    void pickMultiMedias(List<String> paths);
}
