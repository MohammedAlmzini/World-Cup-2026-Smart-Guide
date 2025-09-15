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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.data.model.Hotel;
import com.ahmmedalmzini783.wcguide.databinding.ActivityAddEditHotelBinding;
import com.ahmmedalmzini783.wcguide.util.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddEditHotelActivity extends AppCompatActivity {

    private static final String EXTRA_HOTEL_ID = "hotel_id";
    private static final String EXTRA_IS_EDIT_MODE = "is_edit_mode";

    private ActivityAddEditHotelBinding binding;
    private HotelViewModel viewModel;
    private String hotelId;
    private boolean isEditMode;
    private Hotel currentHotel;

    // Adapters for dynamic lists
    private AdditionalImagesAdapter additionalImagesAdapter;
    private HotelRoomsAdapter hotelRoomsAdapter;

    public static Intent createAddIntent(Context context) {
        Intent intent = new Intent(context, AddEditHotelActivity.class);
        intent.putExtra(EXTRA_IS_EDIT_MODE, false);
        return intent;
    }

    public static Intent createEditIntent(Context context, String hotelId) {
        Intent intent = new Intent(context, AddEditHotelActivity.class);
        intent.putExtra(EXTRA_HOTEL_ID, hotelId);
        intent.putExtra(EXTRA_IS_EDIT_MODE, true);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditHotelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentData();
        setupToolbar();
        setupRecyclerViews();
        setupViewModel();
        setupClickListeners();
        observeViewModel();

        if (isEditMode && hotelId != null) {
            loadHotelData();
        }
    }

    private void getIntentData() {
        hotelId = getIntent().getStringExtra(EXTRA_HOTEL_ID);
        isEditMode = getIntent().getBooleanExtra(EXTRA_IS_EDIT_MODE, false);
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(isEditMode ? "تعديل الفندق" : "إضافة فندق جديد");
        }
    }

    private void setupRecyclerViews() {
        // Additional Images RecyclerView
        additionalImagesAdapter = new AdditionalImagesAdapter();
        binding.rvAdditionalImages.setLayoutManager(new LinearLayoutManager(this));
        binding.rvAdditionalImages.setAdapter(additionalImagesAdapter);

        // Hotel Rooms RecyclerView
        hotelRoomsAdapter = new HotelRoomsAdapter();
        binding.rvHotelRooms.setLayoutManager(new LinearLayoutManager(this));
        binding.rvHotelRooms.setAdapter(hotelRoomsAdapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(HotelViewModel.class);
    }

    private void setupClickListeners() {
        binding.btnSave.setOnClickListener(v -> saveHotel());
        binding.btnAddImage.setOnClickListener(v -> addAdditionalImage());
        binding.btnAddRoom.setOnClickListener(v -> addHotelRoom());
    }

    private void observeViewModel() {
        // Observe hotel data when editing
        if (isEditMode && hotelId != null) {
            viewModel.getHotelById(hotelId).observe(this, resource -> {
                if (resource.getStatus() == Resource.Status.SUCCESS && resource.getData() != null) {
                    currentHotel = resource.getData();
                    populateFields(currentHotel);
                } else if (resource.getStatus() == Resource.Status.ERROR) {
                    Toast.makeText(this, "خطأ في تحميل بيانات الفندق", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }

        // Observe operation results
        viewModel.getOperationResult().observe(this, result -> {
            if (result != null) {
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
                if (result.contains("بنجاح")) {
                    finish();
                }
                viewModel.clearOperationResult();
            }
        });

        // Observe loading state
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnSave.setEnabled(!isLoading);
        });
    }

    private void loadHotelData() {
        // Data will be loaded through ViewModel observer
    }

    private void populateFields(Hotel hotel) {
        binding.etHotelName.setText(hotel.getName());
        binding.etHotelDescription.setText(hotel.getDescription());
        binding.etMainImageUrl.setText(hotel.getMainImageUrl());
        binding.etLocation.setText(hotel.getLocation());
        binding.etWorkingHours.setText(hotel.getWorkingHours());
        binding.etCountry.setText(hotel.getCountry());
        binding.etCity.setText(hotel.getCity());
        binding.etAddress.setText(hotel.getAddress());
        binding.etPhone.setText(hotel.getPhone());
        binding.etEmail.setText(hotel.getEmail());
        binding.etWebsite.setText(hotel.getWebsite());
        binding.etLatitude.setText(String.valueOf(hotel.getLat()));
        binding.etLongitude.setText(String.valueOf(hotel.getLng()));
        binding.ratingBar.setRating(hotel.getRating());
        binding.switchIsOpen.setChecked(hotel.isOpen());

        // Services
        if (hotel.getServices() != null) {
            binding.etServices.setText(String.join(", ", hotel.getServices()));
        }

        // Facilities
        if (hotel.getFacilities() != null) {
            binding.etFacilities.setText(String.join(", ", hotel.getFacilities()));
        }

        // World Cup Services
        if (hotel.getWorldCupServices() != null) {
            binding.etWorldCupServices.setText(String.join(", ", hotel.getWorldCupServices()));
        }

        // Additional Images
        if (hotel.getAdditionalImages() != null) {
            additionalImagesAdapter.setImages(hotel.getAdditionalImages());
        }

        // Hotel Rooms
        if (hotel.getRooms() != null) {
            hotelRoomsAdapter.setRooms(hotel.getRooms());
        }
    }

    private void saveHotel() {
        if (!validateInput()) {
            return;
        }

        Hotel hotel = collectHotelData();
        
        if (isEditMode && currentHotel != null) {
            hotel.setId(currentHotel.getId());
            hotel.setCreatedAt(currentHotel.getCreatedAt());
            viewModel.updateHotel(hotel);
        } else {
            viewModel.addHotel(hotel);
        }
    }

    private boolean validateInput() {
        boolean isValid = true;

        if (TextUtils.isEmpty(binding.etHotelName.getText())) {
            binding.etHotelName.setError("اسم الفندق مطلوب");
            isValid = false;
        }

        if (TextUtils.isEmpty(binding.etHotelDescription.getText())) {
            binding.etHotelDescription.setError("وصف الفندق مطلوب");
            isValid = false;
        }

        if (TextUtils.isEmpty(binding.etLocation.getText())) {
            binding.etLocation.setError("موقع الفندق مطلوب");
            isValid = false;
        }

        if (TextUtils.isEmpty(binding.etCountry.getText())) {
            binding.etCountry.setError("الدولة مطلوبة");
            isValid = false;
        }

        if (TextUtils.isEmpty(binding.etCity.getText())) {
            binding.etCity.setError("المدينة مطلوبة");
            isValid = false;
        }

        return isValid;
    }

    private Hotel collectHotelData() {
        Hotel hotel = new Hotel();
        
        hotel.setName(binding.etHotelName.getText().toString().trim());
        hotel.setDescription(binding.etHotelDescription.getText().toString().trim());
        hotel.setMainImageUrl(binding.etMainImageUrl.getText().toString().trim());
        hotel.setLocation(binding.etLocation.getText().toString().trim());
        hotel.setWorkingHours(binding.etWorkingHours.getText().toString().trim());
        hotel.setCountry(binding.etCountry.getText().toString().trim());
        hotel.setCity(binding.etCity.getText().toString().trim());
        hotel.setAddress(binding.etAddress.getText().toString().trim());
        hotel.setPhone(binding.etPhone.getText().toString().trim());
        hotel.setEmail(binding.etEmail.getText().toString().trim());
        hotel.setWebsite(binding.etWebsite.getText().toString().trim());
        hotel.setRating(binding.ratingBar.getRating());
        hotel.setOpen(binding.switchIsOpen.isChecked());

        // Parse coordinates
        try {
            if (!TextUtils.isEmpty(binding.etLatitude.getText())) {
                hotel.setLat(Double.parseDouble(binding.etLatitude.getText().toString()));
            }
            if (!TextUtils.isEmpty(binding.etLongitude.getText())) {
                hotel.setLng(Double.parseDouble(binding.etLongitude.getText().toString()));
            }
        } catch (NumberFormatException e) {
            // Handle parsing error
        }

        // Parse services
        String servicesText = binding.etServices.getText().toString().trim();
        if (!TextUtils.isEmpty(servicesText)) {
            hotel.setServices(Arrays.asList(servicesText.split("\\s*,\\s*")));
        }

        // Parse facilities
        String facilitiesText = binding.etFacilities.getText().toString().trim();
        if (!TextUtils.isEmpty(facilitiesText)) {
            hotel.setFacilities(Arrays.asList(facilitiesText.split("\\s*,\\s*")));
        }

        // Parse World Cup services
        String worldCupServicesText = binding.etWorldCupServices.getText().toString().trim();
        if (!TextUtils.isEmpty(worldCupServicesText)) {
            hotel.setWorldCupServices(Arrays.asList(worldCupServicesText.split("\\s*,\\s*")));
        }

        // Get additional images from adapter
        hotel.setAdditionalImages(additionalImagesAdapter.getImages());

        // Get rooms from adapter
        hotel.setRooms(hotelRoomsAdapter.getRooms());

        return hotel;
    }

    private void addAdditionalImage() {
        Hotel.AdditionalImage newImage = new Hotel.AdditionalImage("", "");
        additionalImagesAdapter.addImage(newImage);
    }

    private void addHotelRoom() {
        Hotel.HotelRoom newRoom = new Hotel.HotelRoom("", 1, 0.0, new ArrayList<>(), "");
        hotelRoomsAdapter.addRoom(newRoom);
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
