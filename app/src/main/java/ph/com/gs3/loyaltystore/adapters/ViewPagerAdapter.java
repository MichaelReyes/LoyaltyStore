package ph.com.gs3.loyaltystore.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    private final Bundle fragmentBundle;

    public ViewPagerAdapter(FragmentManager manager, Bundle bundle) {
        super(manager);
        fragmentBundle = bundle;
    }

    @Override
    public Fragment getItem(int position) {
        final Fragment fragment = mFragmentList.get(position);
        fragment.setArguments(this.fragmentBundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    public Fragment getRegisteredFragment(int position) {
        return mFragmentList.get(position);
    }


}