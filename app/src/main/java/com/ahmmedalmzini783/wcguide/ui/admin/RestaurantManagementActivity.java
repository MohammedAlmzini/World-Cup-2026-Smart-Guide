package com.ahmmedalmzini783.wcguide.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.databinding.ActivityRestaurantManagementBinding;
import com.ahmmedalmzini783.wcguide.util.Resource;

public class RestaurantManagementActivity extends AppCompatActivity {

    private ActivityRestaurantManagementBinding binding;
    private RestaurantViewModel viewModel;
    private RestaurantAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRestaurantManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupRecyclerView();
        setupViewModel();
        setupFab();
        observeViewModel();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("إدارة المطاعم");
        }
    }

    private void setupRecyclerView() {
        adapter = new RestaurantAdapter(restaurant -> {
            // Handle restaurant item click - navigate to edit restaurant
            Intent intent = AddEditRestaurantActivity.createEditIntent(this, restaurant.getId());
            startActivity(intent);
        }, restaurant -> {
            // Handle delete restaurant
            showDeleteConfirmationDialog(restaurant);
        });

        binding.restaurantsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.restaurantsRecyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(RestaurantViewModel.class);
    }

    private void setupFab() {
        binding.fabAddRestaurant.setOnClickListener(v -> {
            Intent intent = AddEditRestaurantActivity.createAddIntent(this);
            startActivity(intent);
        });
    }

    private void observeViewModel() {
        // Observe restaurants list
        viewModel.getAllRestaurants().observe(this, resource -> {
            if (resource.getStatus() == Resource.Status.LOADING) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.emptyStateLayout.setVisibility(View.GONE);
            } else if (resource.getStatus() == Resource.Status.SUCCESS) {
                binding.progressBar.setVisibility(View.GONE);
                if (resource.getData() != null && !resource.getData().isEmpty()) {
                    adapter.setRestaurants(resource.getData());
                    binding.restaurantsRecyclerView.setVisibility(View.VISIBLE);
                    binding.emptyStateLayout.setVisibility(View.GONE);
                } else {
                    binding.restaurantsRecyclerView.setVisibility(View.GONE);
                    binding.emptyStateLayout.setVisibility(View.VISIBLE);
                }
            } else if (resource.getStatus() == Resource.Status.ERROR) {
                binding.progressBar.setVisibility(View.GONE);
                binding.restaurantsRecyclerView.setVisibility(View.GONE);
                binding.emptyStateLayout.setVisibility(View.VISIBLE);
                Toast.makeText(this, "خطأ في تحميل المطاعم: " + resource.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // Observe operation results
        viewModel.getOperationResult().observe(this, result -> {
            if (result != null) {
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
                viewModel.clearOperationResult();
            }
        });

        // Observe loading state
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showDeleteConfirmationDialog(com.ahmmedalmzini783.wcguide.data.model.Restaurant restaurant) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("حذف المطعم")
                .setMessage("هل أنت متأكد من رغبتك في حذف المطعم \"" + restaurant.getName() + "\"؟")
                .setPositiveButton("حذف", (dialog, which) -> {
                    viewModel.deleteRestaurant(restaurant.getId());
                })
                .setNegativeButton("إلغاء", null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to this activity
        // The LiveData will automatically refresh the list
    }
}
