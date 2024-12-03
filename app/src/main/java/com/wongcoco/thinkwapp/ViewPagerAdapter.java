package com.wongcoco.thinkwapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new SuratPerjanjianFragment(); // Fragment Surat Perjanjian
            case 1:
                return new KriteriaUmumFragment(); // Fragment Kriteria Umum
            default:
                throw new IllegalStateException("Invalid position");
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Jumlah tab
    }
}
