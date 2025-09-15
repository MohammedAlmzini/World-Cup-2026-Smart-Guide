package com.ahmmedalmzini783.wcguide.ui.admin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.data.model.Banner;
import com.ahmmedalmzini783.wcguide.databinding.ActivityManageBannerBinding;
import com.ahmmedalmzini783.wcguide.util.AdminAuthHelper;
import com.ahmmedalmzini783.wcguide.util.ImageLoader;

public class ManageBannerActivity extends AppCompatActivity {

    private static final String EXTRA_MODE = "extra_mode";
    private static final String EXTRA_BANNER_ID = "extra_banner_id";
    private static final String EXTRA_BANNER_TITLE = "extra_banner_title";
    private static final String EXTRA_BANNER_DESCRIPTION = "extra_banner_description";
    private static final String EXTRA_BANNER_IMAGE_URL = "extra_banner_image_url";
    private static final String EXTRA_BANNER_DEEPLINK = "extra_banner_deeplink";

    private static final String MODE_ADD = "add";
    private static final String MODE_EDIT = "edit";

    private ActivityManageBannerBinding binding;
    private String mode;
    private Banner currentBanner;
    private AdminViewModel viewModel;

    public static Intent createAddIntent(Context context) {
        Intent intent = new Intent(context, ManageBannerActivity.class);
        intent.putExtra(EXTRA_MODE, MODE_ADD);
        return intent;
    }

    public static Intent createEditIntent(Context context, Banner banner) {
        Intent intent = new Intent(context, ManageBannerActivity.class);
        intent.putExtra(EXTRA_MODE, MODE_EDIT);
        intent.putExtra(EXTRA_BANNER_ID, banner.getId());
        intent.putExtra(EXTRA_BANNER_TITLE, banner.getTitle());
        intent.putExtra(EXTRA_BANNER_DESCRIPTION, banner.getDescription());
        intent.putExtra(EXTRA_BANNER_IMAGE_URL, banner.getImageUrl());
        intent.putExtra(EXTRA_BANNER_DEEPLINK, banner.getDeeplink());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        binding = ActivityManageBannerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Check if admin is logged in
        if (!AdminAuthHelper.isAdminLoggedIn(this)) {
            Toast.makeText(this, "يجب تسجيل الدخول كأدمن أولاً", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);
        mode = getIntent().getStringExtra(EXTRA_MODE);
        
        setupToolbar();
        setupViews();
        observeViewModel();
        
        if (MODE_EDIT.equals(mode)) {
            loadBannerData();
        }
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            String title = MODE_ADD.equals(mode) ? 
                getString(R.string.add_banner_title) : 
                getString(R.string.edit_banner_title);
            getSupportActionBar().setTitle(title);
        }
    }

    private void setupViews() {
        binding.btnSelectImage.setOnClickListener(v -> {
            // TODO: Implement image selection
            Toast.makeText(this, getString(R.string.image_selection_coming_soon), Toast.LENGTH_SHORT).show();
        });

        binding.btnSaveBanner.setOnClickListener(v -> {
            saveBanner();
        });

        // Show/hide image preview based on mode
        if (MODE_ADD.equals(mode)) {
            binding.currentImageCard.setVisibility(View.GONE);
        }
    }

    private void observeViewModel() {
        viewModel.getOperationResult().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                viewModel.clearOperationResult();
                if (message.contains("بنجاح")) {
                    finish();
                }
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnSaveBanner.setEnabled(!isLoading);
        });
    }

    private void loadBannerData() {
        Intent intent = getIntent();
        
        String title = intent.getStringExtra(EXTRA_BANNER_TITLE);
        String description = intent.getStringExtra(EXTRA_BANNER_DESCRIPTION);
        String imageUrl = intent.getStringExtra(EXTRA_BANNER_IMAGE_URL);
        String deeplink = intent.getStringExtra(EXTRA_BANNER_DEEPLINK);

        binding.etBannerTitle.setText(title);
        binding.etBannerDescription.setText(description);
        binding.etBannerImageUrl.setText(imageUrl);
        binding.etBannerDeeplink.setText(deeplink);

        // Load current image
        if (!TextUtils.isEmpty(imageUrl)) {
            binding.currentImageCard.setVisibility(View.VISIBLE);
            ImageLoader.loadImageWithCacheBusting(
                this,
                imageUrl,
                binding.currentBannerImage,
                R.drawable.placeholder_banner
            );
        }

        // Create current banner object
        currentBanner = new Banner();
        currentBanner.setId(intent.getStringExtra(EXTRA_BANNER_ID));
        currentBanner.setTitle(title);
        currentBanner.setDescription(description);
        currentBanner.setImageUrl(imageUrl);
        currentBanner.setDeeplink(deeplink);
    }

    private void saveBanner() {
        String title = binding.etBannerTitle.getText().toString().trim();
        String description = binding.etBannerDescription.getText().toString().trim();
        String imageUrl = binding.etBannerImageUrl.getText().toString().trim();
        String deeplink = binding.etBannerDeeplink.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(title)) {
            binding.etBannerTitle.setError(getString(R.string.field_required));
            return;
        }

        if (TextUtils.isEmpty(imageUrl)) {
            binding.etBannerImageUrl.setError(getString(R.string.field_required));
            return;
        }

        // Create or update banner
        Banner banner = MODE_EDIT.equals(mode) ? currentBanner : new Banner();
        
        if (MODE_ADD.equals(mode)) {
            banner.setId("banner_" + System.currentTimeMillis());
        }
        
        banner.setTitle(title);
        banner.setDescription(description);
        banner.setImageUrl(imageUrl);
        banner.setDeeplink(deeplink);

        // Save banner to Firebase
        if (MODE_ADD.equals(mode)) {
            viewModel.addBanner(banner);
        } else {
            viewModel.updateBanner(banner);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
