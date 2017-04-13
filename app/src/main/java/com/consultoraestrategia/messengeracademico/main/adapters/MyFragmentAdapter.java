package com.consultoraestrategia.messengeracademico.main.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Steve on 7/03/2017.
 */

public class MyFragmentAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = "MyFragmentAdapter";
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    private SparseArray<Fragment> registeredFragments = new SparseArray<>();

    public MyFragmentAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, "getItem position: " + position);
        // Check if the fragment at this position has been retained by the PagerAdapter
        if (registeredFragments.indexOfKey(position) >= 0) {
            Log.d(TAG, "registeredFragments.indexOfKey(position) >= 0");
            return registeredFragments.get(position);
        }

        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        Log.d(TAG, "addFragment" + title);
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Log.d(TAG, "getPageTitle position: " + position);
        return mFragmentTitleList.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.d(TAG, "instantiateItem position: " + position);
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Log.d(TAG, "destroyItem position: " + position + ", Object: " + object);
        // Check if the fragment at this position has been retained by the PagerAdapter
        if (registeredFragments.indexOfKey(position) >= 0) {
            Log.d(TAG, "destroyItem registeredFragments.indexOfKey");
            registeredFragments.remove(position);
        }

        super.destroyItem(container, position, object);
    }


}
