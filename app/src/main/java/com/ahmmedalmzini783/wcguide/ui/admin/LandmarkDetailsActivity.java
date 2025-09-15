package com.ahmmedalmzini783.wcguide.ui.admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.data.model.Landmark;
import com.ahmmedalmzini783.wcguide.databinding.ActivityLandmarkDetailsBinding;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

public class LandmarkDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ActivityLandmarkDetailsBinding binding;
    private Landmark landmark;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        binding = ActivityLandmarkDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get landmark from intent
        landmark = getIntent().getParcelableExtra("landmark");
        if (landmark == null) {
            finish();
            return;
        }

        setupToolbar();
        setupViews();
        setupMap();
        populateData();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("تفاصيل المعلم");
        }
    }

    private void setupViews() {
        binding.fabOpenInMaps.setOnClickListener(v -> openInGoogleMaps());
        binding.btnEdit.setOnClickListener(v -> editLandmark());
        binding.btnAddReview.setOnClickListener(v -> addReview());
    }

    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        
        LatLng location = new LatLng(landmark.getLatitude(), landmark.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(location).title(landmark.getName()));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
    }

    private void populateData() {
        binding.textLandmarkName.setText(landmark.getName());
        binding.textLandmarkCategory.setText(landmark.getCategory());
        binding.textLandmarkDescription.setText(landmark.getDescription());
        binding.textLandmarkAddress.setText(landmark.getAddress());
        binding.textCoordinates.setText(String.format(Locale.getDefault(), 
            "%.6f, %.6f", landmark.getLatitude(), landmark.getLongitude()));

        // Load image
        if (landmark.getImageUrl() != null && !landmark.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(landmark.getImageUrl())
                    .placeholder(R.drawable.ic_location_city)
                    .error(R.drawable.ic_location_city)
                    .centerCrop()
                    .into(binding.imageLandmark);
        } else {
            binding.imageLandmark.setImageResource(R.drawable.ic_location_city);
        }
    }

    private void openInGoogleMaps() {
        String uri = String.format(Locale.getDefault(), 
            "geo:%f,%f?q=%f,%f(%s)", 
            landmark.getLatitude(), landmark.getLongitude(),
            landmark.getLatitude(), landmark.getLongitude(),
            landmark.getName());
        
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // Fallback to web version
            String webUri = String.format(Locale.getDefault(),
                "https://www.google.com/maps/search/?api=1&query=%f,%f",
                landmark.getLatitude(), landmark.getLongitude());
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUri));
            startActivity(webIntent);
        }
    }

    private void editLandmark() {
        Intent intent = new Intent(this, AddEditLandmarkActivity.class);
        intent.putExtra("landmark", landmark);
        intent.putExtra("isEdit", true);
        startActivity(intent);
    }

    private void addReview() {
        Intent intent = new Intent(this, AddReviewActivity.class);
        intent.putExtra("landmark", landmark);
        startActivity(intent);
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
