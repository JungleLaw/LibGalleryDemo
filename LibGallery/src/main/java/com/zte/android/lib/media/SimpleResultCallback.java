package com.zte.android.lib.media;

import java.util.List;

/**
 * 多媒体抽象选择抽象空实现
 * Created by Jungle on 2017/7/21.
 */

public class SimpleResultCallback implements ResultCallback {

    @Override
    public void pickImageCallback(String path) {

    }

    @Override
    public void pickImagesCallback(List<String> paths) {

    }

    @Override
    public void takeCameraCallback(String path) {

    }

    @Override
    public void cropCallback(String path) {

    }

    @Override
    public void pickSingleMedia(String path, boolean isImage) {

    }

    @Override
    public void pickMultiMedias(List<String> paths) {

    }

}
