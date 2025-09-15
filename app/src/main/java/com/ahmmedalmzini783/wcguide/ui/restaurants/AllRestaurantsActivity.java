package com.ahmmedalmzini783.wcguide.ui.restaurants;

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
import com.ahmmedalmzini783.wcguide.databinding.ActivityAllRestaurantsBinding;
import com.ahmmedalmzini783.wcguide.ui.home.PlaceAdapter;
import com.ahmmedalmzini783.wcguide.util.Resource;

public class AllRestaurantsActivity extends AppCompatActivity {
    private static final String TAG = "AllRestaurantsActivity";
    
    private ActivityAllRestaurantsBinding binding;
    private AllRestaurantsViewModel viewModel;
    private PlaceAdapter restaurantsAdapter;

    public static Intent createIntent(Context context) {
        return new Intent(context, AllRestaurantsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllRestaurantsBinding.inflate(getLayoutInflater());
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
            getSupportActionBar().setTitle("جميع المطاعم");
        }
    }

    private void setupRecyclerView() {
        restaurantsAdapter = new PlaceAdapter(this::onRestaurantClick);
        
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        binding.recyclerViewRestaurants.setLayoutManager(gridLayoutManager);
        binding.recyclerViewRestaurants.setAdapter(restaurantsAdapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(AllRestaurantsViewModel.class);
    }
    
    private void observeData() {
        // Load all restaurants
        viewModel.getAllRestaurants().observe(this, resource -> {
            if (resource != null) {
                switch (resource.getStatus()) {
                    case LOADING:
                        showLoading(true);
                        break;
                    case SUCCESS:
                        showLoading(false);
                        if (resource.getData() != null && !resource.getData().isEmpty()) {
                            restaurantsAdapter.submitList(resource.getData());
                            showEmptyState(false);
                        } else {
                            showEmptyState(true);
                        }
                        break;
                    case ERROR:
                        showLoading(false);
                        showEmptyState(true);
                        break;
                }
            }
        });
    }

    private void onRestaurantClick(Place restaurant) {
        Log.d(TAG, "Restaurant clicked: " + restaurant.getName());
        // TODO: Navigate to restaurant detail activity
        // Intent intent = RestaurantDetailActivity.createIntent(this, restaurant);
        // startActivity(intent);
    }

    private void showLoading(boolean show) {
        if (binding.progressBar != null) {
            binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        binding.recyclerViewRestaurants.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showEmptyState(boolean show) {
        if (binding.emptyStateLayout != null) {
            binding.emptyStateLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        binding.recyclerViewRestaurants.setVisibility(show ? View.GONE : View.VISIBLE);
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
