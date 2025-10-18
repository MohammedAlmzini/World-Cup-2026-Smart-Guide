package com.ahmmedalmzini783.wcguide.ui.logos;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.data.model.Banner;
import com.ahmmedalmzini783.wcguide.databinding.ActivityLogosBinding;
import com.ahmmedalmzini783.wcguide.ui.banner.BannerDetailActivity;
import com.ahmmedalmzini783.wcguide.ui.home.BannerAdapter;
import com.ahmmedalmzini783.wcguide.ui.home.HomeViewModel;
import com.ahmmedalmzini783.wcguide.util.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class LogosActivity extends AppCompatActivity {

    private ActivityLogosBinding binding;
    private BannerAdapter adapter;
    private HomeViewModel viewModel;
    private List<Banner> bannersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLogosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupRecyclerView();
        loadBanners();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.logos_title));
        }
    }

    private void setupRecyclerView() {
        adapter = new BannerAdapter(banner -> {
            // Open banner detail activity when clicked
            startActivity(BannerDetailActivity.createIntent(this, banner));
        });

        // Setup grid layout for logos
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        binding.recyclerViewLogos.setLayoutManager(gridLayoutManager);
        binding.recyclerViewLogos.setAdapter(adapter);

        // Add performance optimizations
        binding.recyclerViewLogos.setHasFixedSize(true);
        binding.recyclerViewLogos.setItemViewCacheSize(20);
        binding.recyclerViewLogos.setNestedScrollingEnabled(false);
    }

    private void loadBanners() {
        // Initialize ViewModel using ViewModelProvider
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        
        // Observe banners data
        viewModel.getBanners().observe(this, resource -> {
            if (resource != null && resource.getData() != null) {
                bannersList.clear();
                bannersList.addAll(resource.getData());
                adapter.submitList(bannersList);
                
                // Hide loading indicator if present
                if (binding.progressBar != null) {
                    binding.progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
