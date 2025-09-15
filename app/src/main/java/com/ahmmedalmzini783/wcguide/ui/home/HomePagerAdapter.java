package com.ahmmedalmzini783.wcguide.ui.home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.ahmmedalmzini783.wcguide.ui.home.sections.BannersFragment;
import com.ahmmedalmzini783.wcguide.ui.home.sections.AttractionsFragment;
import com.ahmmedalmzini783.wcguide.ui.home.sections.HotelsFragment;
import com.ahmmedalmzini783.wcguide.ui.home.sections.RestaurantsFragment;

public class HomePagerAdapter extends FragmentStateAdapter {

    private HomeViewModel viewModel;

    public HomePagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public void setViewModel(HomeViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return BannersFragment.newInstance(viewModel);
            case 1:
                return AttractionsFragment.newInstance(viewModel);
            case 2:
                return HotelsFragment.newInstance(viewModel);
            case 3:
                return RestaurantsFragment.newInstance(viewModel);
            default:
                return new BannersFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
