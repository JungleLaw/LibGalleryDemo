package com.zte.android.lib.media.gallery.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.zte.android.lib.media.gallery.fragment.ImageFragment;
import com.zte.android.lib.media.gallery.photoloader.entity.Media;

import java.util.List;


/**
 * 图片浏览ViewPager适配器
 * Created by Law on 2016/9/25.
 */
public class DisplayPagerAdapter extends FragmentStatePagerAdapter {
    private List<Media> medias;
    private SparseArray<Fragment> fragments = new SparseArray<>();

    public DisplayPagerAdapter(FragmentManager fm, List<Media> medias) {
        super(fm);
        this.medias = medias;
    }

    @Override
    public Fragment getItem(int position) {
        Media media = medias.get(position);
        return ImageFragment.newInstance(media);
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public int getCount() {
        return medias != null ? medias.size() : 0;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        fragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        fragments.remove(position);
        super.destroyItem(container, position, object);
    }
}
