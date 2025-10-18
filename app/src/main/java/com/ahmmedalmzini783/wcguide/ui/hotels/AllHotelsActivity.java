package com.ahmmedalmzini783.wcguide.ui.hotels;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.data.model.Place;
import com.ahmmedalmzini783.wcguide.databinding.ActivityAllHotelsBinding;
import com.ahmmedalmzini783.wcguide.ui.home.PlaceAdapter;
import com.ahmmedalmzini783.wcguide.util.Resource;

public class AllHotelsActivity extends AppCompatActivity {
    private static final String TAG = "AllHotelsActivity";
    
    private ActivityAllHotelsBinding binding;
    private AllHotelsViewModel viewModel;
    private PlaceAdapter hotelsAdapter;

    public static Intent createIntent(Context context) {
        return new Intent(context, AllHotelsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllHotelsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupRecyclerView();
        setupViewModel();
        observeData();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("جميع الفنادق");
        }
    }

    private void setupRecyclerView() {
        hotelsAdapter = new PlaceAdapter(this::onHotelClick);
        
        // Use GridLayoutManager for better display of hotels
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        binding.recyclerViewHotels.setLayoutManager(layoutManager);
        binding.recyclerViewHotels.setAdapter(hotelsAdapter);
        binding.recyclerViewHotels.setHasFixedSize(true);
        
        // Setup retry button
        if (binding.buttonRetry != null) {
            binding.buttonRetry.setOnClickListener(v -> {
                Log.d(TAG, "Retry button clicked");
                viewModel.refreshHotels();
            });
        }
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(AllHotelsViewModel.class);
    }
    
    private void observeData() {
        // Load all hotels
        viewModel.getAllHotels().observe(this, resource -> {
            Log.d(TAG, "Hotels resource received with status: " + (resource != null ? resource.getStatus() : "null"));
            
            if (resource != null) {
                switch (resource.getStatus()) {
                    case LOADING:
                        Log.d(TAG, "Loading all hotels...");
                        showLoading(true);
                        break;
                    case SUCCESS:
                        Log.d(TAG, "Hotels loaded successfully");
                        showLoading(false);
                        if (resource.getData() != null && !resource.getData().isEmpty()) {
                            Log.d(TAG, "Displaying " + resource.getData().size() + " hotels");
                            hotelsAdapter.submitList(resource.getData());
                            showEmptyState(false);
                        } else {
                            Log.d(TAG, "No hotels data available");
                            showEmptyState(true);
                        }
                        break;
                    case ERROR:
                        Log.e(TAG, "Error loading all hotels: " + resource.getMessage());
                        showLoading(false);
                        showEmptyState(true);
                        break;
                }
            } else {
                Log.e(TAG, "Resource is null");
            }
        });
    }

    private void onHotelClick(Place hotel) {
        Log.d(TAG, "Hotel clicked: " + hotel.getName());
        
        // Convert Place to Landmark for display
        com.ahmmedalmzini783.wcguide.data.model.Landmark landmark = new com.ahmmedalmzini783.wcguide.data.model.Landmark();
        landmark.setId(hotel.getId());
        landmark.setName(hotel.getName());
        landmark.setDescription(hotel.getDescription() != null ? hotel.getDescription() : "فندق متميز في " + hotel.getCity());
        landmark.setAddress(hotel.getAddress() != null ? hotel.getAddress() : hotel.getCity() + ", " + hotel.getCountry());
        landmark.setLatitude(hotel.getLat());
        landmark.setLongitude(hotel.getLng());
        landmark.setCategory("فندق");
        landmark.setRating(hotel.getAvgRating());
        
        // Set image if available
        if (hotel.getImages() != null && !hotel.getImages().isEmpty()) {
            landmark.setImageUrl(hotel.getImages().get(0));
        }
        
        Intent intent = new Intent(this, com.ahmmedalmzini783.wcguide.ui.admin.LandmarkDetailsActivity.class);
        intent.putExtra("landmark", landmark);
        startActivity(intent);
    }

    private void showLoading(boolean show) {
        if (binding.progressBar != null) {
            binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void showEmptyState(boolean show) {
        if (binding.emptyStateLayout != null) {
            binding.emptyStateLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        }
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
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
