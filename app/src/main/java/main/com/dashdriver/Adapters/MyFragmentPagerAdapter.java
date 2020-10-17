package main.com.dashdriver.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import java.util.List;

import main.com.dashdriver.Models.ModelFragmentPager;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    List<ModelFragmentPager> layout;

    public MyFragmentPagerAdapter(@NonNull FragmentManager fm, List<ModelFragmentPager> layout) {
        super(fm);
        this.layout = layout;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return layout.get(position).getFragment();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return layout.get(position).getTitle();
    }


    @Override
    public int getCount() {
        return layout.size();
    }

}
