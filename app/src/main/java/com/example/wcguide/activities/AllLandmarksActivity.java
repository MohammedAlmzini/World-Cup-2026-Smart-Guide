package com.example.wcguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmmedalmzini783.wcguide.R;
import com.example.wcguide.adapters.LandmarkHomeAdapter;
import com.ahmmedalmzini783.wcguide.data.model.Landmark;
import com.ahmmedalmzini783.wcguide.data.repository.LandmarkRepository;
import com.example.wcguide.viewmodels.LandmarkHomeViewModel;
import com.google.android.material.appbar.MaterialToolbar;

public class AllLandmarksActivity extends AppCompatActivity {
    
    private RecyclerView recyclerViewLandmarks;
    private LandmarkHomeAdapter landmarkAdapter;
    private LandmarkHomeViewModel landmarkViewModel;
    private View progressBar, emptyStateView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_landmarks);
        
        initViews();
        setupToolbar();
        setupRecyclerView();
        setupViewModel();
        observeData();
    }
    
    private void initViews() {
        recyclerViewLandmarks = findViewById(R.id.recycler_view_landmarks);
        progressBar = findViewById(R.id.progress_bar);
        emptyStateView = findViewById(R.id.layout_empty_state);
    }
    
    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("جميع المعالم السياحية");
        }
        
        toolbar.setNavigationOnClickListener(v -> finish());
    }
    
    private void setupRecyclerView() {
        landmarkAdapter = new LandmarkHomeAdapter(this::onLandmarkClick);
        
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerViewLandmarks.setLayoutManager(gridLayoutManager);
        recyclerViewLandmarks.setAdapter(landmarkAdapter);
    }
    
    private void setupViewModel() {
        LandmarkRepository repository = new LandmarkRepository();
        LandmarkHomeViewModel.Factory factory = new LandmarkHomeViewModel.Factory(repository);
        landmarkViewModel = new ViewModelProvider(this, factory).get(LandmarkHomeViewModel.class);
    }
    
    private void observeData() {
        // Load all landmarks
        landmarkViewModel.getAllLandmarks().observe(this, resource -> {
            if (resource != null) {
                switch (resource.getStatus()) {
                    case LOADING:
                        showLoading(true);
                        break;
                    case SUCCESS:
                        showLoading(false);
                        if (resource.getData() != null && !resource.getData().isEmpty()) {
                            landmarkAdapter.submitList(resource.getData());
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
    
    private void onLandmarkClick(Landmark landmark) {
        // Navigate to landmark details
        Intent intent = new Intent(this, com.ahmmedalmzini783.wcguide.ui.admin.LandmarkDetailsActivity.class);
        intent.putExtra("landmark", landmark);
        startActivity(intent);
    }
    
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerViewLandmarks.setVisibility(show ? View.GONE : View.VISIBLE);
    }
    
    private void showEmptyState(boolean show) {
        emptyStateView.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerViewLandmarks.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}
