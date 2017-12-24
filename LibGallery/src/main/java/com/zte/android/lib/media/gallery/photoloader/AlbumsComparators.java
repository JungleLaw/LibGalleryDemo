package com.zte.android.lib.media.gallery.photoloader;



import com.zte.android.lib.media.gallery.photoloader.entity.Album;

import java.util.Comparator;

/**
 * 相册排序比较器-按照从多到少实现
 */
public class AlbumsComparators {
    boolean ascending = true;

    public AlbumsComparators(boolean ascending) {
        this.ascending = ascending;
    }


    public Comparator<Album> getSizeComparator() {
        return new Comparator<Album>() {
            public int compare(Album f1, Album f2) {
                return ascending ? f1.getCount() - f2.getCount() : f2.getCount() - f1.getCount();
            }
        };
    }
}
