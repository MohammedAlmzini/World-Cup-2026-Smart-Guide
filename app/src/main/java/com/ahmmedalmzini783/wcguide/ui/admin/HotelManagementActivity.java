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
import com.ahmmedalmzini783.wcguide.databinding.ActivityHotelManagementBinding;
import com.ahmmedalmzini783.wcguide.util.Resource;

public class HotelManagementActivity extends AppCompatActivity {

    private ActivityHotelManagementBinding binding;
    private HotelViewModel viewModel;
    private HotelAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHotelManagementBinding.inflate(getLayoutInflater());
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
            getSupportActionBar().setTitle("إدارة الفنادق");
        }
    }

    private void setupRecyclerView() {
        adapter = new HotelAdapter(hotel -> {
            // Handle hotel item click - navigate to edit hotel
            Intent intent = AddEditHotelActivity.createEditIntent(this, hotel.getId());
            startActivity(intent);
        }, hotel -> {
            // Handle delete hotel
            showDeleteConfirmationDialog(hotel);
        });

        binding.hotelsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.hotelsRecyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(HotelViewModel.class);
    }

    private void setupFab() {
        binding.fabAddHotel.setOnClickListener(v -> {
            Intent intent = AddEditHotelActivity.createAddIntent(this);
            startActivity(intent);
        });
    }

    private void observeViewModel() {
        // Observe hotels list
        viewModel.getAllHotels().observe(this, resource -> {
            if (resource.getStatus() == Resource.Status.LOADING) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.emptyStateLayout.setVisibility(View.GONE);
            } else if (resource.getStatus() == Resource.Status.SUCCESS) {
                binding.progressBar.setVisibility(View.GONE);
                if (resource.getData() != null && !resource.getData().isEmpty()) {
                    adapter.setHotels(resource.getData());
                    binding.hotelsRecyclerView.setVisibility(View.VISIBLE);
                    binding.emptyStateLayout.setVisibility(View.GONE);
                } else {
                    binding.hotelsRecyclerView.setVisibility(View.GONE);
                    binding.emptyStateLayout.setVisibility(View.VISIBLE);
                }
            } else if (resource.getStatus() == Resource.Status.ERROR) {
                binding.progressBar.setVisibility(View.GONE);
                binding.hotelsRecyclerView.setVisibility(View.GONE);
                binding.emptyStateLayout.setVisibility(View.VISIBLE);
                Toast.makeText(this, "خطأ في تحميل الفنادق: " + resource.getMessage(), Toast.LENGTH_LONG).show();
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
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }

    private void showDeleteConfirmationDialog(com.ahmmedalmzini783.wcguide.data.model.Hotel hotel) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("حذف الفندق")
                .setMessage("هل أنت متأكد من رغبتك في حذف فندق \"" + hotel.getName() + "\"؟")
                .setPositiveButton("حذف", (dialog, which) -> {
                    viewModel.deleteHotel(hotel.getId());
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
        // Refresh data when returning from add/edit activity
        if (viewModel != null) {
            // The LiveData will automatically refresh
        }
    }
}
