package com.ahmmedalmzini783.wcguide.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.data.model.Landmark;
import com.ahmmedalmzini783.wcguide.databinding.ActivityManageLandmarksBinding;
import com.ahmmedalmzini783.wcguide.util.Resource;

import java.util.ArrayList;
import java.util.List;

public class ManageLandmarksActivity extends AppCompatActivity {

    private ActivityManageLandmarksBinding binding;
    private LandmarkAdapter adapter;
    private LandmarkViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        binding = ActivityManageLandmarksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupViews();
        setupFab();
        observeViewModel();
        loadLandmarks();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("إدارة المعالم السياحية");
        }
    }

    private void setupViews() {
        viewModel = new ViewModelProvider(this).get(LandmarkViewModel.class);
        
        adapter = new LandmarkAdapter(new ArrayList<>(), new LandmarkAdapter.OnLandmarkClickListener() {
            @Override
            public void onEditClick(Landmark landmark) {
                editLandmark(landmark);
            }

            @Override
            public void onDeleteClick(Landmark landmark) {
                deleteLandmark(landmark);
            }

            @Override
            public void onViewClick(Landmark landmark) {
                viewLandmarkDetails(landmark);
            }
        });

        binding.recyclerViewLandmarks.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewLandmarks.setAdapter(adapter);
    }

    private void setupFab() {
        binding.fabAddLandmark.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditLandmarkActivity.class);
            startActivity(intent);
        });
    }

    private void observeViewModel() {
        viewModel.getLandmarksLiveData().observe(this, resource -> {
            if (resource != null) {
                switch (resource.getStatus()) {
                    case LOADING:
                        binding.progressBar.setVisibility(View.VISIBLE);
                        binding.emptyStateLayout.setVisibility(View.GONE);
                        break;
                    case SUCCESS:
                        binding.progressBar.setVisibility(View.GONE);
                        List<Landmark> landmarks = resource.getData();
                        if (landmarks != null && !landmarks.isEmpty()) {
                            binding.emptyStateLayout.setVisibility(View.GONE);
                            adapter.updateLandmarks(landmarks);
                        } else {
                            binding.emptyStateLayout.setVisibility(View.VISIBLE);
                        }
                        break;
                    case ERROR:
                        binding.progressBar.setVisibility(View.GONE);
                        binding.emptyStateLayout.setVisibility(View.VISIBLE);
                        String errorMessage = resource.getMessage();
                        if (errorMessage != null) {
                            Toast.makeText(this, "خطأ: " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                        break;
                }
            }
        });
    }

    private void loadLandmarks() {
        viewModel.loadLandmarks();
    }

    private void editLandmark(Landmark landmark) {
        Intent intent = new Intent(this, AddEditLandmarkActivity.class);
        intent.putExtra("landmark", landmark);
        intent.putExtra("isEdit", true);
        startActivity(intent);
    }

    private void deleteLandmark(Landmark landmark) {
        new AlertDialog.Builder(this)
                .setTitle("تأكيد الحذف")
                .setMessage("هل أنت متأكد من حذف معلم \"" + landmark.getName() + "\"؟")
                .setPositiveButton("حذف", (dialog, which) -> {
                    viewModel.deleteLandmark(landmark.getId()).observe(this, resource -> {
                        if (resource != null) {
                            switch (resource.getStatus()) {
                                case LOADING:
                                    Toast.makeText(this, "جاري الحذف...", Toast.LENGTH_SHORT).show();
                                    break;
                                case SUCCESS:
                                    Toast.makeText(this, "تم حذف المعلم بنجاح", Toast.LENGTH_SHORT).show();
                                    loadLandmarks(); // Reload the list
                                    break;
                                case ERROR:
                                    Toast.makeText(this, "فشل في حذف المعلم: " + resource.getMessage(), Toast.LENGTH_LONG).show();
                                    break;
                            }
                        }
                    });
                })
                .setNegativeButton("إلغاء", null)
                .show();
    }

    private void viewLandmarkDetails(Landmark landmark) {
        Intent intent = new Intent(this, LandmarkDetailsActivity.class);
        intent.putExtra("landmark", landmark);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLandmarks(); // Refresh the list when returning from add/edit
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
