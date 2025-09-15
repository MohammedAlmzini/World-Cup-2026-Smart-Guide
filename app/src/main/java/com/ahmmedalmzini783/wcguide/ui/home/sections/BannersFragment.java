package com.ahmmedalmzini783.wcguide.ui.home.sections;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.ui.home.BannerAdapter;
import com.ahmmedalmzini783.wcguide.ui.home.HomeViewModel;
import com.ahmmedalmzini783.wcguide.ui.banner.BannerDetailActivity;

public class BannersFragment extends Fragment {
    
    private RecyclerView recyclerView;
    private BannerAdapter adapter;
    private HomeViewModel viewModel;

    public static BannersFragment newInstance(HomeViewModel viewModel) {
        BannersFragment fragment = new BannersFragment();
        fragment.viewModel = viewModel;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_banners_horizontal, container, false);
        
        recyclerView = view.findViewById(R.id.banners_recycler);
        adapter = new BannerAdapter(banner -> {
            // Open banner detail activity when clicked
            startActivity(BannerDetailActivity.createIntent(getContext(), banner));
        });
        
        // Setup horizontal scrolling with modern layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        
        // Add modern scroll behavior and performance optimizations
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        
        // Smooth scrolling settings
        recyclerView.setNestedScrollingEnabled(false);
        
        // Add snap to start behavior for better UX (less jarring than center)
        androidx.recyclerview.widget.LinearSnapHelper snapHelper = new androidx.recyclerview.widget.LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        
        // Add custom item decoration for modern spacing
        recyclerView.addItemDecoration(new androidx.recyclerview.widget.RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull android.graphics.Rect outRect, @NonNull View view, 
                                     @NonNull androidx.recyclerview.widget.RecyclerView parent, 
                                     @NonNull androidx.recyclerview.widget.RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                if (position == 0) {
                    outRect.left = 16; // First item padding
                }
                if (position == state.getItemCount() - 1) {
                    outRect.right = 16; // Last item padding
                }
            }
        });
        
        // Observe data if viewModel is available
        if (viewModel != null && viewModel.getBanners() != null) {
            viewModel.getBanners().observe(getViewLifecycleOwner(), resource -> {
                if (resource != null && resource.getData() != null) {
                    adapter.submitList(resource.getData());
                }
            });
        }
        
        return view;
    }
}
