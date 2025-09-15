package com.ahmmedalmzini783.wcguide.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.databinding.ActivityManageReviewsBinding;
import com.ahmmedalmzini783.wcguide.ui.viewmodel.ReviewViewModel;
import com.ahmmedalmzini783.wcguide.util.ImageLoader;

public class ManageReviewsActivity extends AppCompatActivity implements ReviewAdapter.OnReviewActionListener {

    private ActivityManageReviewsBinding binding;
    private ReviewAdapter adapter;
    private ReviewViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageReviewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupRecyclerView();
        setupViewModel();
        setupSwipeRefresh();
        loadReviews();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("إدارة التقييمات");
        }
    }

    private void setupRecyclerView() {
        adapter = new ReviewAdapter(this, this);
        binding.recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewReviews.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ReviewViewModel.class);
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(this::refreshData); // Use refreshData
        binding.swipeRefresh.setColorSchemeResources(R.color.secondary_variant);
    }

    private void loadReviews() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.layoutEmptyState.setVisibility(View.GONE);

        // Clear any previous observers to avoid memory leaks
        viewModel.getAllReviews().removeObservers(this);
        
        viewModel.getAllReviews().observe(this, reviews -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.swipeRefresh.setRefreshing(false);

            if (reviews != null && !reviews.isEmpty()) {
                binding.layoutEmptyState.setVisibility(View.GONE);
                adapter.updateReviews(reviews);
            } else {
                binding.layoutEmptyState.setVisibility(View.VISIBLE);
                adapter.updateReviews(null);
            }
        });
    }

    private void refreshData() {
        // Clear image cache to ensure fresh images are loaded
        ImageLoader.clearImageCache(this);
        
        // Force refresh by creating a new ViewModel instance
        viewModel = new ViewModelProvider(this).get(ReviewViewModel.class);
        loadReviews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_manage_reviews, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_add_review) {
            startActivity(new Intent(this, AddReviewActivity.class));
            return true;
        } else if (id == R.id.action_add_landmark_rating) {
            showLandmarkSelectionDialog();
            return true;
        } else if (id == R.id.action_refresh) {
            refreshData(); // Use refreshData instead of loadReviews
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData(); // Use refreshData to ensure fresh data when returning
    }

    @Override
    public void onReviewDeleted() {
        Toast.makeText(this, "تم حذف التقييم بنجاح", Toast.LENGTH_SHORT).show();
        refreshData(); // Use refreshData
    }

    @Override
    public void onReviewUpdated() {
        Toast.makeText(this, "تم تحديث التقييم بنجاح", Toast.LENGTH_SHORT).show();
        refreshData(); // Use refreshData
    }

    private void showLandmarkSelectionDialog() {
        // قائمة مؤقتة للمعالم - يمكن تحسينها لاحقاً بجلب البيانات من Firebase
        String[] landmarks = {
            "برج خليفة",
            "متحف اللوفر أبوظبي", 
            "جامع الشيخ زايد الكبير",
            "نافورة دبي",
            "جزيرة ياس",
            "الحي التاريخي في الشارقة",
            "قصر الإمارات",
            "مول دبي",
            "جبل جيس",
            "قرية التراث"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("اختر المعلم السياحي")
                .setItems(landmarks, (dialog, which) -> {
                    String selectedLandmark = landmarks[which];
                    Intent intent = new Intent(this, AddLandmarkRatingActivity.class);
                    intent.putExtra("landmarkId", "landmark_" + which);
                    intent.putExtra("landmarkName", selectedLandmark);
                    startActivity(intent);
                })
                .setNegativeButton("إلغاء", null)
                .show();
    }
}
