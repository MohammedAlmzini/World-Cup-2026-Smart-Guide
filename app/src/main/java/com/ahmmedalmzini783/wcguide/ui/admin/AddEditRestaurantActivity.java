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
import com.ahmmedalmzini783.wcguide.data.model.Restaurant;
import com.ahmmedalmzini783.wcguide.databinding.ActivityAddEditRestaurantBinding;
import com.ahmmedalmzini783.wcguide.util.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddEditRestaurantActivity extends AppCompatActivity {

    private static final String EXTRA_RESTAURANT_ID = "restaurant_id";
    private static final String EXTRA_IS_EDIT_MODE = "is_edit_mode";

    private ActivityAddEditRestaurantBinding binding;
    private RestaurantViewModel viewModel;
    private String restaurantId;
    private boolean isEditMode;
    private Restaurant currentRestaurant;

    // Adapters for dynamic lists
    private AdditionalImagesAdapter additionalImagesAdapter;
    private RestaurantMenuAdapter menuAdapter;

    public static Intent createAddIntent(Context context) {
        Intent intent = new Intent(context, AddEditRestaurantActivity.class);
        intent.putExtra(EXTRA_IS_EDIT_MODE, false);
        return intent;
    }

    public static Intent createEditIntent(Context context, String restaurantId) {
        Intent intent = new Intent(context, AddEditRestaurantActivity.class);
        intent.putExtra(EXTRA_RESTAURANT_ID, restaurantId);
        intent.putExtra(EXTRA_IS_EDIT_MODE, true);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditRestaurantBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentData();
        setupToolbar();
        setupRecyclerViews();
        setupViewModel();
        setupClickListeners();
        observeViewModel();

        if (isEditMode) {
            loadRestaurantData();
        }
    }

    private void getIntentData() {
        restaurantId = getIntent().getStringExtra(EXTRA_RESTAURANT_ID);
        isEditMode = getIntent().getBooleanExtra(EXTRA_IS_EDIT_MODE, false);
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(isEditMode ? "تعديل المطعم" : "إضافة مطعم جديد");
        }
    }

    private void setupRecyclerViews() {
        // Additional Images RecyclerView
        additionalImagesAdapter = new AdditionalImagesAdapter();
        binding.rvAdditionalImages.setLayoutManager(new LinearLayoutManager(this));
        binding.rvAdditionalImages.setAdapter(additionalImagesAdapter);

        // Restaurant Menu RecyclerView
        menuAdapter = new RestaurantMenuAdapter();
        binding.rvRestaurantMenu.setLayoutManager(new LinearLayoutManager(this));
        binding.rvRestaurantMenu.setAdapter(menuAdapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(RestaurantViewModel.class);
    }

    private void setupClickListeners() {
        binding.btnSave.setOnClickListener(v -> saveRestaurant());
        binding.btnAddImage.setOnClickListener(v -> addAdditionalImage());
        binding.btnAddMenuItem.setOnClickListener(v -> addMenuItem());
    }

    private void observeViewModel() {
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

        // Observe restaurant details if in edit mode
        if (isEditMode && restaurantId != null) {
            viewModel.getRestaurantById(restaurantId).observe(this, resource -> {
                if (resource.getStatus() == Resource.Status.SUCCESS && resource.getData() != null) {
                    currentRestaurant = resource.getData();
                    populateFields(currentRestaurant);
                } else if (resource.getStatus() == Resource.Status.ERROR) {
                    Toast.makeText(this, "خطأ في تحميل بيانات المطعم", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }

        // Observe loading state
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnSave.setEnabled(!isLoading);
        });
    }

    private void loadRestaurantData() {
        // Data will be loaded through ViewModel observer
    }

    private void populateFields(Restaurant restaurant) {
        binding.etRestaurantName.setText(restaurant.getName());
        binding.etRestaurantDescription.setText(restaurant.getDescription());
        binding.etMainImageUrl.setText(restaurant.getMainImageUrl());
        binding.etLocation.setText(restaurant.getLocation());
        binding.etWorkingHours.setText(restaurant.getWorkingHours());
        binding.etCountry.setText(restaurant.getCountry());
        binding.etCity.setText(restaurant.getCity());
        binding.etAddress.setText(restaurant.getAddress());
        binding.etPhone.setText(restaurant.getPhone());
        binding.etEmail.setText(restaurant.getEmail());
        binding.etWebsite.setText(restaurant.getWebsite());
        binding.etLatitude.setText(String.valueOf(restaurant.getLat()));
        binding.etLongitude.setText(String.valueOf(restaurant.getLng()));
        binding.etCuisineType.setText(restaurant.getCuisineType());
        binding.etPriceRange.setText(restaurant.getPriceRange());
        binding.etCapacity.setText(String.valueOf(restaurant.getCapacity()));
        binding.ratingBar.setRating(restaurant.getRating());
        binding.switchIsOpen.setChecked(restaurant.isOpen());
        binding.switchHasDelivery.setChecked(restaurant.isHasDelivery());
        binding.switchHasReservation.setChecked(restaurant.isHasReservation());

        // Services
        if (restaurant.getServices() != null) {
            binding.etServices.setText(String.join(", ", restaurant.getServices()));
        }

        // Facilities
        if (restaurant.getFacilities() != null) {
            binding.etFacilities.setText(String.join(", ", restaurant.getFacilities()));
        }

        // World Cup Services
        if (restaurant.getWorldCupServices() != null) {
            binding.etWorldCupServices.setText(String.join(", ", restaurant.getWorldCupServices()));
        }

        // Additional Images
        // TODO: Implement additional images adapter for restaurants
        // if (restaurant.getAdditionalImages() != null) {
        //     additionalImagesAdapter.setImages(restaurant.getAdditionalImages());
        // }

        // Restaurant Menu
        if (restaurant.getMenu() != null) {
            menuAdapter.setMenuItems(restaurant.getMenu());
        }
    }

    private void saveRestaurant() {
        if (!validateInput()) {
            return;
        }

        Restaurant restaurant = collectRestaurantData();
        
        if (isEditMode && currentRestaurant != null) {
            restaurant.setId(currentRestaurant.getId());
            restaurant.setCreatedAt(currentRestaurant.getCreatedAt());
            viewModel.updateRestaurant(restaurant);
        } else {
            viewModel.addRestaurant(restaurant);
        }
    }

    private boolean validateInput() {
        boolean isValid = true;

        if (TextUtils.isEmpty(binding.etRestaurantName.getText())) {
            binding.etRestaurantName.setError("اسم المطعم مطلوب");
            isValid = false;
        }

        if (TextUtils.isEmpty(binding.etRestaurantDescription.getText())) {
            binding.etRestaurantDescription.setError("وصف المطعم مطلوب");
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

        if (TextUtils.isEmpty(binding.etCuisineType.getText())) {
            binding.etCuisineType.setError("نوع المطبخ مطلوب");
            isValid = false;
        }

        return isValid;
    }

    private Restaurant collectRestaurantData() {
        Restaurant restaurant = new Restaurant();
        
        restaurant.setName(binding.etRestaurantName.getText().toString().trim());
        restaurant.setDescription(binding.etRestaurantDescription.getText().toString().trim());
        restaurant.setMainImageUrl(binding.etMainImageUrl.getText().toString().trim());
        restaurant.setLocation(binding.etLocation.getText().toString().trim());
        restaurant.setWorkingHours(binding.etWorkingHours.getText().toString().trim());
        restaurant.setCountry(binding.etCountry.getText().toString().trim());
        restaurant.setCity(binding.etCity.getText().toString().trim());
        restaurant.setAddress(binding.etAddress.getText().toString().trim());
        restaurant.setPhone(binding.etPhone.getText().toString().trim());
        restaurant.setEmail(binding.etEmail.getText().toString().trim());
        restaurant.setWebsite(binding.etWebsite.getText().toString().trim());
        restaurant.setCuisineType(binding.etCuisineType.getText().toString().trim());
        restaurant.setPriceRange(binding.etPriceRange.getText().toString().trim());
        restaurant.setRating(binding.ratingBar.getRating());
        restaurant.setOpen(binding.switchIsOpen.isChecked());
        restaurant.setHasDelivery(binding.switchHasDelivery.isChecked());
        restaurant.setHasReservation(binding.switchHasReservation.isChecked());

        // Parse coordinates
        try {
            restaurant.setLat(Double.parseDouble(binding.etLatitude.getText().toString().trim()));
            restaurant.setLng(Double.parseDouble(binding.etLongitude.getText().toString().trim()));
        } catch (NumberFormatException e) {
            restaurant.setLat(0.0);
            restaurant.setLng(0.0);
        }

        // Parse capacity
        try {
            restaurant.setCapacity(Integer.parseInt(binding.etCapacity.getText().toString().trim()));
        } catch (NumberFormatException e) {
            restaurant.setCapacity(0);
        }

        // Parse services
        String servicesText = binding.etServices.getText().toString().trim();
        if (!TextUtils.isEmpty(servicesText)) {
            restaurant.setServices(Arrays.asList(servicesText.split(",\\s*")));
        }

        // Parse facilities
        String facilitiesText = binding.etFacilities.getText().toString().trim();
        if (!TextUtils.isEmpty(facilitiesText)) {
            restaurant.setFacilities(Arrays.asList(facilitiesText.split(",\\s*")));
        }

        // Parse World Cup services
        String worldCupServicesText = binding.etWorldCupServices.getText().toString().trim();
        if (!TextUtils.isEmpty(worldCupServicesText)) {
            restaurant.setWorldCupServices(Arrays.asList(worldCupServicesText.split(",\\s*")));
        }

        // Get additional images and menu from adapters
        // TODO: Implement additional images adapter for restaurants
        // restaurant.setAdditionalImages(additionalImagesAdapter.getImages());
        restaurant.setAdditionalImages(new ArrayList<>());
        restaurant.setMenu(menuAdapter.getMenuItems());

        return restaurant;
    }

    private void addAdditionalImage() {
        // Show dialog to add additional image
        // This would typically involve image picker or URL input
        // For now, we'll create a simple implementation
    }

    private void addMenuItem() {
        // Show dialog to add menu item
        // This would involve a form to enter item details
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
