package com.ahmmedalmzini783.wcguide.ui.admin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.data.model.Landmark;
import com.ahmmedalmzini783.wcguide.databinding.ActivityAddEditLandmarkBinding;
import com.ahmmedalmzini783.wcguide.util.Resource;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddEditLandmarkActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private ActivityAddEditLandmarkBinding binding;
    private LandmarkViewModel viewModel;
    private GoogleMap googleMap;
    private LatLng selectedLocation;
    private Landmark editingLandmark;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        binding = ActivityAddEditLandmarkBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(LandmarkViewModel.class);

        // Check if this is edit mode
        Intent intent = getIntent();
        if (intent.hasExtra("landmark") && intent.getBooleanExtra("isEdit", false)) {
            isEditMode = true;
            editingLandmark = intent.getParcelableExtra("landmark");
        }

        setupToolbar();
        setupViews();
        setupMap();
        setupClickListeners();

        if (isEditMode && editingLandmark != null) {
            populateFields();
        }
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(isEditMode ? "تعديل المعلم" : "إضافة معلم جديد");
        }
    }

    private void setupViews() {
        // Set up image URL input with real-time preview
        binding.editTextImageUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String url = s.toString().trim();
                if (!TextUtils.isEmpty(url)) {
                    loadImagePreview(url);
                } else {
                    binding.imagePreview.setVisibility(View.GONE);
                }
            }
        });

        // Click on preview to reload image
        binding.imagePreview.setOnClickListener(v -> {
            String currentUrl = binding.editTextImageUrl.getText().toString().trim();
            if (!TextUtils.isEmpty(currentUrl)) {
                loadImagePreview(currentUrl);
            }
        });
    }

    private void loadImagePreview(String imageUrl) {
        if (!TextUtils.isEmpty(imageUrl)) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_location_city)
                    .error(R.drawable.ic_location_city)
                    .into(binding.imagePreview);
            binding.imagePreview.setVisibility(View.VISIBLE);
        } else {
            binding.imagePreview.setVisibility(View.GONE);
        }
    }

    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void setupClickListeners() {
        binding.btnSave.setOnClickListener(v -> saveLandmark());
        
        binding.btnFindOnMap.setOnClickListener(v -> {
            String address = binding.editTextAddress.getText().toString().trim();
            if (!TextUtils.isEmpty(address)) {
                findLocationOnMap(address);
            } else {
                Toast.makeText(this, "يرجى إدخال العنوان أولاً", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnOpenInMaps.setOnClickListener(v -> {
            if (selectedLocation != null) {
                openInGoogleMaps();
            } else {
                Toast.makeText(this, "يرجى اختيار موقع على الخريطة أولاً", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        
        // Enable location if permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

        // Set default location (FIFA headquarters or World Cup 2026 host cities)
        LatLng defaultLocation = new LatLng(39.8283, -98.5795); // Center of USA
        if (isEditMode && editingLandmark != null) {
            defaultLocation = new LatLng(editingLandmark.getLatitude(), editingLandmark.getLongitude());
            selectedLocation = defaultLocation;
            googleMap.addMarker(new MarkerOptions().position(defaultLocation).title(editingLandmark.getName()));
        }
        
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10));

        // Set map click listener
        googleMap.setOnMapClickListener(latLng -> {
            selectedLocation = latLng;
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(latLng).title("الموقع المحدد"));
            
            // Update coordinates in the form
            binding.textCoordinates.setText(String.format(Locale.getDefault(), 
                "الإحداثيات: %.6f, %.6f", latLng.latitude, latLng.longitude));
        });
    }

    private void populateFields() {
        binding.editTextName.setText(editingLandmark.getName());
        binding.editTextDescription.setText(editingLandmark.getDescription());
        binding.editTextAddress.setText(editingLandmark.getAddress());
        binding.editTextCategory.setText(editingLandmark.getCategory());
        
        selectedLocation = new LatLng(editingLandmark.getLatitude(), editingLandmark.getLongitude());
        binding.textCoordinates.setText(String.format(Locale.getDefault(), 
            "الإحداثيات: %.6f, %.6f", editingLandmark.getLatitude(), editingLandmark.getLongitude()));

        // Load existing image URL
        if (editingLandmark.getImageUrl() != null && !editingLandmark.getImageUrl().isEmpty()) {
            binding.editTextImageUrl.setText(editingLandmark.getImageUrl());
            loadImagePreview(editingLandmark.getImageUrl());
        }
    }

    private void findLocationOnMap(String address) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address foundAddress = addresses.get(0);
                LatLng location = new LatLng(foundAddress.getLatitude(), foundAddress.getLongitude());
                
                selectedLocation = location;
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(location).title(address));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
                
                binding.textCoordinates.setText(String.format(Locale.getDefault(), 
                    "الإحداثيات: %.6f, %.6f", location.latitude, location.longitude));
            } else {
                Toast.makeText(this, "لم يتم العثور على العنوان", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(this, "خطأ في البحث عن العنوان", Toast.LENGTH_SHORT).show();
        }
    }

    private void openInGoogleMaps() {
        if (selectedLocation != null) {
            String name = binding.editTextName.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                name = "معلم سياحي";
            }
            
            String uri = String.format(Locale.getDefault(), 
                "geo:%f,%f?q=%f,%f(%s)", 
                selectedLocation.latitude, selectedLocation.longitude,
                selectedLocation.latitude, selectedLocation.longitude,
                name);
            
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                // Fallback to web version
                String webUri = String.format(Locale.getDefault(),
                    "https://www.google.com/maps/search/?api=1&query=%f,%f",
                    selectedLocation.latitude, selectedLocation.longitude);
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUri));
                startActivity(webIntent);
            }
        }
    }

    private void saveLandmark() {
        String name = binding.editTextName.getText().toString().trim();
        String description = binding.editTextDescription.getText().toString().trim();
        String address = binding.editTextAddress.getText().toString().trim();
        String category = binding.editTextCategory.getText().toString().trim();
        String imageUrl = binding.editTextImageUrl.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(name)) {
            binding.editTextName.setError("يرجى إدخال اسم المعلم");
            return;
        }

        if (TextUtils.isEmpty(description)) {
            binding.editTextDescription.setError("يرجى إدخال وصف المعلم");
            return;
        }

        if (TextUtils.isEmpty(address)) {
            binding.editTextAddress.setError("يرجى إدخال عنوان المعلم");
            return;
        }

        if (selectedLocation == null) {
            Toast.makeText(this, "يرجى اختيار موقع على الخريطة", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnSave.setEnabled(false);

        // Save landmark with image URL
        saveLandmarkToDatabase(name, description, address, category, imageUrl);
    }

    private void saveLandmarkToDatabase(String name, String description, String address, String category, String imageUrl) {
        Landmark landmark;
        
        if (isEditMode && editingLandmark != null) {
            landmark = editingLandmark;
            landmark.setName(name);
            landmark.setDescription(description);
            landmark.setAddress(address);
            landmark.setCategory(category);
            landmark.setLatitude(selectedLocation.latitude);
            landmark.setLongitude(selectedLocation.longitude);
            if (!TextUtils.isEmpty(imageUrl)) {
                landmark.setImageUrl(imageUrl);
            }
        } else {
            landmark = new Landmark(name, description, address, selectedLocation.latitude, selectedLocation.longitude);
            landmark.setCategory(category);
            if (!TextUtils.isEmpty(imageUrl)) {
                landmark.setImageUrl(imageUrl);
            }
        }

        if (isEditMode) {
            viewModel.updateLandmark(landmark).observe(this, resource -> {
                handleSaveResult(resource);
            });
        } else {
            viewModel.addLandmark(landmark).observe(this, resource -> {
                handleSaveResult(resource);
            });
        }
    }

    private void handleSaveResult(Resource<Void> resource) {
        if (resource != null) {
            switch (resource.getStatus()) {
                case LOADING:
                    // Already showing loading
                    break;
                case SUCCESS:
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, isEditMode ? "تم تحديث المعلم بنجاح" : "تم إضافة المعلم بنجاح", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnSave.setEnabled(true);
                    String errorMessage = resource.getMessage();
                    Toast.makeText(this, "خطأ: " + (errorMessage != null ? errorMessage : "خطأ غير معروف"), Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (googleMap != null && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                }
            }
        }
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
