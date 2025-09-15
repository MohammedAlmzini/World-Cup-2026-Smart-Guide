package com.ahmmedalmzini783.wcguide.ui.admin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.data.model.Banner;
import com.ahmmedalmzini783.wcguide.databinding.ActivityAdminBinding;
import com.ahmmedalmzini783.wcguide.util.AdminAuthHelper;
import com.ahmmedalmzini783.wcguide.util.FirebaseDiagnostics;
import com.ahmmedalmzini783.wcguide.util.Resource;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private ActivityAdminBinding binding;
    private AdminBannerAdapter adapter;
    private AdminViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Check if admin is logged in - if not, try to login
        if (!AdminAuthHelper.isAdminLoggedIn(this)) {
            Toast.makeText(this, "جاري تسجيل دخول الأدمن...", Toast.LENGTH_SHORT).show();
            
            AdminAuthHelper.loginAsAdmin(this, new AdminAuthHelper.AdminLoginListener() {
                @Override
                public void onAdminLoginSuccess() {
                    Toast.makeText(AdminActivity.this, "تم تسجيل دخول الأدمن بنجاح", Toast.LENGTH_SHORT).show();
                    initializeActivity();
                }
                
                @Override
                public void onAdminLoginFailure(String error) {
                    Toast.makeText(AdminActivity.this, "فشل في تسجيل دخول الأدمن: " + error, Toast.LENGTH_LONG).show();
                    // استمر بالنشاط رغم الفشل للتطوير
                    initializeActivity();
                }
            });
        } else {
            initializeActivity();
        }
    }
    
    private void initializeActivity() {
        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);

        setupToolbar();
        setupViews();
        setupFab();
        observeViewModel();
        loadBanners();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.admin_panel_title));
        }
    }

    private void setupViews() {
        adapter = new AdminBannerAdapter(banner -> {
            // Edit banner
            Intent intent = ManageBannerActivity.createEditIntent(this, banner);
            startActivity(intent);
        }, banner -> {
            // Delete banner - show confirmation dialog
            new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(getString(R.string.delete_banner_title))
                .setMessage(getString(R.string.delete_banner_message))
                .setPositiveButton(getString(R.string.delete), (dialog, which) -> {
                    viewModel.deleteBanner(banner.getId());
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
        });

        binding.bannersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.bannersRecyclerView.setAdapter(adapter);

        // Setup refresh button
        binding.btnRefresh.setOnClickListener(v -> loadBanners());
        
        // Setup landmarks management button
        binding.btnManageLandmarks.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageLandmarksActivity.class);
            startActivity(intent);
        });
        
        // Setup hotels management button
        binding.btnManageHotels.setOnClickListener(v -> {
            Intent intent = new Intent(this, HotelManagementActivity.class);
            startActivity(intent);
        });
        
        // Setup restaurants management button
        binding.btnManageRestaurants.setOnClickListener(v -> {
            Intent intent = new Intent(this, RestaurantManagementActivity.class);
            startActivity(intent);
        });
        
        // Setup reviews management button
        binding.btnManageReviews.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageReviewsActivity.class);
            startActivity(intent);
        });
    }

    private void setupFab() {
        binding.fabAddBanner.setOnClickListener(v -> {
            Intent intent = ManageBannerActivity.createAddIntent(this);
            startActivity(intent);
        });
        
        // Long click للتشخيص
        binding.fabAddBanner.setOnLongClickListener(v -> {
            // Show options dialog
            new AlertDialog.Builder(this)
                .setTitle("خيارات المطور")
                .setItems(new String[]{"تشخيص Firebase العام", "تشخيص المطاعم"}, (dialog, which) -> {
                    if (which == 0) {
                        runFirebaseDiagnostics();
                    } else if (which == 1) {
                        // Open Firebase Debug Activity
                        Intent debugIntent = new Intent(this, com.ahmmedalmzini783.wcguide.debug.FirebaseDebugActivity.class);
                        startActivity(debugIntent);
                    }
                })
                .show();
            return true;
        });
    }

    private void observeViewModel() {
        viewModel.getBanners().observe(this, resource -> {
            if (resource.getStatus() == Resource.Status.LOADING) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.emptyStateLayout.setVisibility(View.GONE);
            } else if (resource.getStatus() == Resource.Status.SUCCESS) {
                binding.progressBar.setVisibility(View.GONE);
                List<Banner> banners = resource.getData();
                if (banners != null && !banners.isEmpty()) {
                    adapter.setBanners(banners);
                    binding.bannersRecyclerView.setVisibility(View.VISIBLE);
                    binding.emptyStateLayout.setVisibility(View.GONE);
                } else {
                    binding.bannersRecyclerView.setVisibility(View.GONE);
                    binding.emptyStateLayout.setVisibility(View.VISIBLE);
                }
            } else if (resource.getStatus() == Resource.Status.ERROR) {
                binding.progressBar.setVisibility(View.GONE);
                binding.bannersRecyclerView.setVisibility(View.GONE);
                binding.emptyStateLayout.setVisibility(View.VISIBLE);
                Toast.makeText(this, "خطأ في تحميل البيانات: " + resource.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getOperationResult().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                viewModel.clearOperationResult();
                // Refresh data after operation
                loadBanners();
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void loadBanners() {
        // Data is automatically loaded through ViewModel and LiveData
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload banners when returning to this activity
        loadBanners();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    // وظيفة تشخيص Firebase للمساعدة في حل المشاكل
    private void runFirebaseDiagnostics() {
        Log.d("AdminActivity", "🔍 بدء تشخيص Firebase المتقدم...");
        
        // إظهار ProgressBar
        binding.progressBar.setVisibility(View.VISIBLE);
        
        FirebaseDiagnostics.runComprehensiveDiagnostics(this, new FirebaseDiagnostics.DiagnosticsListener() {
            @Override
            public void onProgress(String step) {
                runOnUiThread(() -> {
                    Toast.makeText(AdminActivity.this, "🔍 " + step + "...", Toast.LENGTH_SHORT).show();
                });
            }
            
            @Override
            public void onDiagnosticsComplete(String report) {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    
                    new AlertDialog.Builder(AdminActivity.this)
                        .setTitle("🔍 تشخيص Firebase")
                        .setMessage(report)
                        .setPositiveButton("موافق", null)
                        .setNeutralButton("نسخ", (dialog, which) -> {
                            // نسخ التقرير للحافظة
                            android.content.ClipboardManager clipboard = 
                                (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            android.content.ClipData clip = android.content.ClipData.newPlainText("Firebase Diagnostics", report);
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(AdminActivity.this, "تم نسخ التقرير", Toast.LENGTH_SHORT).show();
                        })
                        .show();
                });
            }
        });
    }
}
