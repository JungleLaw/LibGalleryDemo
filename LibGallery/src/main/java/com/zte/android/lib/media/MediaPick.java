package com.zte.android.lib.media;

import android.content.Intent;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import javax.annotation.Nonnegative;

/**
 * 多媒体选择抽象
 * Created by Jungle on 2017/7/20.
 */
public interface MediaPick {
    /**
     * 选择图片
     */
    void pickImage();

    /**
     * 选择多张图片，默认9张
     */
    void pickImages();

    /**
     * 选择多张图片
     *
     * @param maxSize 可选数量
     */
    void pickImages(@IntRange(from = 2) int maxSize);

    void pickSingleMedia();

    void pickMultiMedias();

    /**
     * 拍照
     */
    void takeCamera();

    /**
     * 裁剪
     *
     * @param filePath 待裁剪图片绝对路径
     */
    void crop(@NonNull String filePath);

    /**
     * 按比例裁剪
     *
     * @param filePath    待裁剪图片绝对路径
     * @param widthRatio  宽系数
     * @param heightRatio 高系数
     */
    void cropWithRatio(@NonNull String filePath, @IntRange(from = 1) int widthRatio, @IntRange(from = 1) int heightRatio);

    /**
     * 按输出尺寸裁剪
     *
     * @param filePath  待裁剪图片绝对路径
     * @param outWidth  输出宽度
     * @param outHeight 输出高度
     */
    void cropWithOutSize(@NonNull String filePath, @Nonnegative int outWidth, @Nonnegative int outHeight);

    /**
     * 处理图片结果,放在onActivityResult最前面执行
     *
     * @param requestCode
     * @param resultCode
     * @param data
     * @return true表示被处理，false表示未处理
     */
    boolean handlePickResult(int requestCode, int resultCode, Intent data);
}
