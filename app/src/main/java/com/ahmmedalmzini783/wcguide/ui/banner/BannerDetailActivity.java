package com.ahmmedalmzini783.wcguide.ui.banner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.data.model.Banner;
import com.ahmmedalmzini783.wcguide.databinding.ActivityBannerDetailBinding;
import com.ahmmedalmzini783.wcguide.util.ImageLoader;

public class BannerDetailActivity extends AppCompatActivity {

    private static final String EXTRA_BANNER_ID = "extra_banner_id";
    private static final String EXTRA_BANNER_TITLE = "extra_banner_title";
    private static final String EXTRA_BANNER_DESCRIPTION = "extra_banner_description";
    private static final String EXTRA_BANNER_IMAGE_URL = "extra_banner_image_url";

    private ActivityBannerDetailBinding binding;

    public static Intent createIntent(Context context, Banner banner) {
        Intent intent = new Intent(context, BannerDetailActivity.class);
        intent.putExtra(EXTRA_BANNER_ID, banner.getId());
        intent.putExtra(EXTRA_BANNER_TITLE, banner.getTitle());
        intent.putExtra(EXTRA_BANNER_DESCRIPTION, banner.getDescription());
        intent.putExtra(EXTRA_BANNER_IMAGE_URL, banner.getImageUrl());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        binding = ActivityBannerDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupContent();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.banner_details_title));
        }
    }

    private void setupContent() {
        Intent intent = getIntent();
        String title = intent.getStringExtra(EXTRA_BANNER_TITLE);
        String description = intent.getStringExtra(EXTRA_BANNER_DESCRIPTION);
        String imageUrl = intent.getStringExtra(EXTRA_BANNER_IMAGE_URL);

        // Set content
        binding.bannerTitle.setText(title);
        binding.bannerDescription.setText(description != null ? description : getString(R.string.no_description_available));

        // Load image
        ImageLoader.loadImageWithCacheBusting(
            this,
            imageUrl,
            binding.bannerImage,
            R.drawable.placeholder_banner
        );
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
