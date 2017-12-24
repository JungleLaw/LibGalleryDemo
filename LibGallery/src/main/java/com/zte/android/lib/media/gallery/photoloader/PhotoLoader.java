package com.zte.android.lib.media.gallery.photoloader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.zte.android.lib.media.gallery.photoloader.entity.Album;
import com.zte.android.lib.media.gallery.photoloader.entity.Media;
import com.zte.android.lib.media.gallery.photoloader.entity.Video;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 图片载入器
 * Created by Jungle on 2017/7/15.
 */

public class PhotoLoader {
    //MediaStore.Images.Media.INTERNAL_CONTENT_URI,
    public static final String VIDEO_ALBUM_NAME = "视频";

    //    public static final Uri[] PHOTO_URI = new Uri[]{MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.INTERNAL_CONTENT_URI};
//    public static final Uri[] VIDEO_URI = new Uri[]{MediaStore.Video.Media.EXTERNAL_CONTENT_URI, MediaStore.Video.Media.INTERNAL_CONTENT_URI};
    public static final Uri[] PHOTO_URI = new Uri[]{MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.INTERNAL_CONTENT_URI};
    public static final Uri[] VIDEO_URI = new Uri[]{MediaStore.Video.Media.EXTERNAL_CONTENT_URI, MediaStore.Video.Media.INTERNAL_CONTENT_URI};

    public static final String[] PHOTO_PROJECTIONS = new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DISPLAY_NAME, MediaStore.Images.ImageColumns.BUCKET_ID, MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, MediaStore.Images.ImageColumns.SIZE, MediaStore.Images.ImageColumns.WIDTH, MediaStore.Images.ImageColumns.HEIGHT, MediaStore.Images.ImageColumns.ORIENTATION, MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DATE_ADDED};
    public static final String[] ALBUM_PROJECTIONS = new String[]{"distinct " + MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, MediaStore.Images.ImageColumns.BUCKET_ID};
    public static final String[] VEDIO_PROJECTIONS = new String[]{MediaStore.Video.VideoColumns._ID, MediaStore.Video.VideoColumns.DISPLAY_NAME, MediaStore.Video.VideoColumns.BUCKET_ID, MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME, MediaStore.Video.VideoColumns.SIZE, MediaStore.Video.VideoColumns.WIDTH, MediaStore.Video.VideoColumns.HEIGHT, MediaStore.Video.VideoColumns.DATA, MediaStore.Video.VideoColumns.DATE_ADDED, MediaStore.Video.VideoColumns.DURATION};
//    public static final String[] VEDIO_ALBUM_PROJECTIONS = new String[]{"distinct " + MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media.BUCKET_ID};

    private static PhotoLoader instance = new PhotoLoader();
    private Context mContext;
    private List<Album> albumList;

    private PhotoLoader() {
    }

    public static PhotoLoader getInstance() {
        return instance;
    }

    public void loadPhotos(Context context, boolean includeVideo, PhotosLoadHandler mPhotosLoadHandler) {
        this.mContext = context;
        new LoadPhotosTask(includeVideo, mPhotosLoadHandler).execute();
    }

    /**
     * 载入
     *
     * @return 返回所有图册
     */
    private List<Album> load(boolean includeVideo) {
        albumList = new ArrayList<>();
        List<String> mAlbumNames = getAlbumNames();
        Album video = getVideos();
        for (String name : mAlbumNames) {
            Log.i("TAG-albumname:", name);
            Album album = getMediasByAlbumName(name);
            if (album.getMedias().size() != 0)
                albumList.add(album);
        }
        ArrayList<Media> allMedias = new ArrayList<>();
        for (Album album : albumList) {
            allMedias.addAll(album.getMedias());
        }
        if (includeVideo && video != null) {
            allMedias.addAll(video.getMedias());
        }
        Album album = new Album();
        //        album.setStorageRootPath(root.getAbsolutePath());
        Collections.sort(allMedias, new MediaComparators(false).getDateComparator());
        album.setMedias(allMedias);
        //        album.setPath(root.getAbsolutePath());
        album.setCount(allMedias.size());
        album.setName("所有");
        albumList.add(0, album);
        AlbumsComparators albumsComparators = new AlbumsComparators(false);
        Collections.sort(albumList, albumsComparators.getSizeComparator());
        //        if (CollectionsEngine.getCollects() != null && CollectionsEngine.getCollects().getMedias().size() > 0) {
        //            albumList.add(CollectionsEngine.getCollects());
        //        }
        if (includeVideo && video != null) {
            albumList.add(1, video);
        }
        return albumList;
    }

    /**
     * 获得所有图册的名称
     *
     * @return
     */
    private List<String> getAlbumNames() {
        List<String> mAlbumNames = new ArrayList<>();
        for (int i = 0, size = PhotoLoader.PHOTO_URI.length; i < size; i++) {
            //加载图片
            Cursor mAlbumCursor = MediaStore.Images.Media.query(mContext.getContentResolver(), PhotoLoader.PHOTO_URI[i], PhotoLoader.ALBUM_PROJECTIONS, null, MediaStore.Images.ImageColumns.BUCKET_ID + " asc");
            if (mAlbumCursor.getCount() <= 0)
                continue;
            mAlbumCursor.moveToFirst();
            do {
                String albumName = mAlbumCursor.getString(mAlbumCursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME));
                if (!mAlbumNames.contains(albumName))
                    mAlbumNames.add(albumName);
            } while (mAlbumCursor.moveToNext());
            mAlbumCursor.close();
            mAlbumCursor = null;
        }
        return mAlbumNames;
    }

    private Album getVideos() {
        Album album = new Album();
        album.setName(VIDEO_ALBUM_NAME);
        List<Media> videos = new ArrayList<>();
        //加载视频
        for (int i = 0, size = VIDEO_URI.length; i < size; i++) {
            Cursor cursor = MediaStore.Video.query(mContext.getContentResolver(), VIDEO_URI[i], VEDIO_PROJECTIONS);
            if (cursor.getCount() <= 0) {
                continue;
            }
            cursor.moveToFirst();
            do {
                Media video = new Video();
                video.setId(cursor.getInt(cursor.getColumnIndex(MediaStore.Video.VideoColumns._ID)));
                video.setName(cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DISPLAY_NAME)));
                video.setSize(cursor.getInt(cursor.getColumnIndex(MediaStore.Video.VideoColumns.SIZE)));
                video.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA)));
                video.setModifiedDate(Long.valueOf(cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATE_ADDED))));
                video.setDuration(cursor.getLong(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DURATION)));
                videos.add(video);
                Log.i("TAG-video.getPath()", video.getPath());
            } while (cursor.moveToNext());
            cursor.close();
            cursor = null;
        }
        Collections.sort(videos, new MediaComparators(false).getDateComparator());
        album.setCount(videos.size());
        album.setMedias(videos);
        Log.i("TAG-videos.size()", album.getMedias().size() + "");

        if (album.getMedias().size() == 0) {
            return null;
        }
        return album;
    }

    /**
     * 通过图册名称获取图册实例
     *
     * @param albumName
     * @return
     */
    private Album getMediasByAlbumName(String albumName) {
        Album mAlbum = new Album();
        mAlbum.setName(albumName);
        mAlbum.setMedias(new ArrayList<Media>());
        for (int i = 0, size = PhotoLoader.PHOTO_URI.length; i < size; i++) {
            Cursor mPhotosCursor = MediaStore.Images.Media.query(mContext.getContentResolver(), PhotoLoader.PHOTO_URI[i], PhotoLoader.PHOTO_PROJECTIONS, MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME + " = '" + albumName + "'" + " AND " + MediaStore.Images.Media.MIME_TYPE + " in('image/jpeg','image/png')", MediaStore.Images.ImageColumns.DATE_ADDED + " desc");//,'image/gif'
            if (mPhotosCursor.getCount() <= 0)
                continue;
            mPhotosCursor.moveToFirst();
            do {
                Media mMedia = new Media();
                mMedia.setId(mPhotosCursor.getInt(mPhotosCursor.getColumnIndex(MediaStore.Images.ImageColumns._ID)));
                mMedia.setName(mPhotosCursor.getString(mPhotosCursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME)));
                mMedia.setSize(mPhotosCursor.getInt(mPhotosCursor.getColumnIndex(MediaStore.Images.ImageColumns.SIZE)));
                mMedia.setPath(mPhotosCursor.getString(mPhotosCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)));
                mMedia.setModifiedDate(Long.valueOf(mPhotosCursor.getString(mPhotosCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_ADDED))));
                mMedia.setOrientation(mPhotosCursor.getInt(mPhotosCursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION)));
                //                mMedia.setWidth(mPhotosCursor.getInt(mPhotosCursor.getColumnIndex(MediaStore.Images.ImageColumns.WIDTH)));
                //                mMedia.setHeight(mPhotosCursor.getInt(mPhotosCursor.getColumnIndex(MediaStore.Images.ImageColumns.HEIGHT)));
                if (TextUtils.isEmpty(mMedia.getPath()) || TextUtils.isEmpty(mMedia.getName()) || "null".equals(mMedia.getName()) || mMedia.getMIME().equals(Media.UNKNOWN_MIME) || !new File(mMedia.getPath()).exists()) {
                    continue;
                }
                mAlbum.getMedias().add(mMedia);
            } while (mPhotosCursor.moveToNext());
            mPhotosCursor.close();
            mPhotosCursor = null;
        }
        mAlbum.setCount(mAlbum.getMedias().size());
        if (mAlbum.getAlbumCover() != null && !TextUtils.isEmpty(mAlbum.getAlbumCover().getPath())) {
            mAlbum.setStorageRootPath(mAlbum.getAlbumCover().getPath().substring(0, mAlbum.getAlbumCover().getPath().lastIndexOf("/") + 1));
        }
        //        Logger.i(mAlbum.getAlbumCover().getPath().substring(0,mAlbum.getAlbumCover().getPath().lastIndexOf("/")));
        return mAlbum;
    }

    /**
     * 图册信息获取回调
     */
    public interface PhotosLoadHandler {
        /**
         * 获取成功
         */
        void getPhotosSuc(List<Album> albums);

        /**
         * 获取失败
         */
        void getPhotosFail();
    }

    /**
     * 获取图册信息异步任务
     */
    private final class LoadPhotosTask extends AsyncTask<Void, Void, List<Album>> {
        private boolean includeVideo = false;
        private PhotosLoadHandler mPhotosLoadHandler;

        public LoadPhotosTask(boolean includeVideo, PhotosLoadHandler mPhotosLoadHandler) {
            this.includeVideo = includeVideo;
            this.mPhotosLoadHandler = mPhotosLoadHandler;
        }

        @Override
        protected List<Album> doInBackground(Void... params) {
            // TODO Auto-generated method stub
            return load(includeVideo);
        }

        @Override
        protected void onPostExecute(List<Album> result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (result != null && !result.isEmpty()) {
                if (mPhotosLoadHandler != null)
                    mPhotosLoadHandler.getPhotosSuc(result);
            } else {
                if (mPhotosLoadHandler != null)
                    mPhotosLoadHandler.getPhotosFail();
            }
        }

    }
}
