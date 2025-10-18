package com.ahmmedalmzini783.wcguide.ui.details.restaurant;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.data.model.Place;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class RestaurantDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String EXTRA_RESTAURANT = "extra_restaurant";
    
    // Views
    private ImageView ivRestaurantImage;
    private TextView tvRestaurantName;
    private TextView tvRestaurantAddress;
    private TextView tvRestaurantDescription;
    private Chip chipRating;
    private Chip chipCuisine;
    private Chip chipPriceRange;
    private Chip chipDelivery;
    
    // Map related views
    private MaterialCardView cardMap;
    private View mapContainer;
    private View mapPlaceholder;
    private TextView tvMapLocation;
    private ProgressBar mapLoading;
    private MapView mapView;
    private GoogleMap googleMap;
    
    // Contact views
    private TextView tvRestaurantPhone;
    private TextView tvRestaurantWebsite;
    
    // Menu
    private RecyclerView rvRestaurantMenu;
    
    // FAB
    private FloatingActionButton fabFavorite;
    
    private Place restaurant;

    public static Intent createIntent(android.content.Context context, Place restaurant) {
        Intent intent = new Intent(context, RestaurantDetailsActivity.class);
        intent.putExtra(EXTRA_RESTAURANT + "_id", restaurant.getId());
        intent.putExtra(EXTRA_RESTAURANT + "_name", restaurant.getName());
        intent.putExtra(EXTRA_RESTAURANT + "_description", restaurant.getDescription());
        intent.putExtra(EXTRA_RESTAURANT + "_address", restaurant.getAddress());
        intent.putExtra(EXTRA_RESTAURANT + "_city", restaurant.getCity());
        intent.putExtra(EXTRA_RESTAURANT + "_country", restaurant.getCountry());
        intent.putExtra(EXTRA_RESTAURANT + "_lat", restaurant.getLat());
        intent.putExtra(EXTRA_RESTAURANT + "_lng", restaurant.getLng());
        intent.putExtra(EXTRA_RESTAURANT + "_rating", restaurant.getAvgRating());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);
        
        initializeViews();
        getRestaurantFromIntent();
        setupToolbar();
        displayRestaurantDetails();
        setupMap();
        setupClickListeners();
    }

    private void initializeViews() {
        // Main views
        ivRestaurantImage = findViewById(R.id.iv_restaurant_image);
        tvRestaurantName = findViewById(R.id.tv_restaurant_name);
        tvRestaurantAddress = findViewById(R.id.tv_restaurant_address);
        tvRestaurantDescription = findViewById(R.id.tv_restaurant_description);
        chipRating = findViewById(R.id.chip_rating);
        chipCuisine = findViewById(R.id.chip_cuisine);
        chipPriceRange = findViewById(R.id.chip_price_range);
        chipDelivery = findViewById(R.id.chip_delivery);
        
        // Map views
        cardMap = findViewById(R.id.card_map);
        mapContainer = findViewById(R.id.map_container);
        mapPlaceholder = findViewById(R.id.map_placeholder);
        tvMapLocation = findViewById(R.id.tv_map_location);
        mapLoading = findViewById(R.id.map_loading);
        mapView = findViewById(R.id.map_view);
        
        // Contact views
        tvRestaurantPhone = findViewById(R.id.tv_restaurant_phone);
        tvRestaurantWebsite = findViewById(R.id.tv_restaurant_website);
        
        // Menu
        rvRestaurantMenu = findViewById(R.id.rv_restaurant_menu);
        
        // FAB
        fabFavorite = findViewById(R.id.fab_favorite);
    }

    private void getRestaurantFromIntent() {
        Intent intent = getIntent();
        String id = intent.getStringExtra(EXTRA_RESTAURANT + "_id");
        String name = intent.getStringExtra(EXTRA_RESTAURANT + "_name");
        String description = intent.getStringExtra(EXTRA_RESTAURANT + "_description");
        String address = intent.getStringExtra(EXTRA_RESTAURANT + "_address");
        String city = intent.getStringExtra(EXTRA_RESTAURANT + "_city");
        String country = intent.getStringExtra(EXTRA_RESTAURANT + "_country");
        double lat = intent.getDoubleExtra(EXTRA_RESTAURANT + "_lat", 0.0);
        double lng = intent.getDoubleExtra(EXTRA_RESTAURANT + "_lng", 0.0);
        float rating = intent.getFloatExtra(EXTRA_RESTAURANT + "_rating", 0.0f);
        
        if (id == null || name == null) {
            Toast.makeText(this, "خطأ في تحميل بيانات المطعم", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Create a temporary Place object with the data
        restaurant = new Place();
        restaurant.setId(id);
        restaurant.setName(name);
        restaurant.setDescription(description);
        restaurant.setAddress(address);
        restaurant.setCity(city);
        restaurant.setCountry(country);
        restaurant.setLat(lat);
        restaurant.setLng(lng);
        restaurant.setAvgRating(rating);
    }

    private void setupToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        if (collapsingToolbar != null && restaurant != null) {
            collapsingToolbar.setTitle(restaurant.getName());
        }
    }

    private void displayRestaurantDetails() {
        if (restaurant == null) return;
        
        // Basic info
        tvRestaurantName.setText(restaurant.getName());
        tvRestaurantAddress.setText(buildAddressText());
        tvRestaurantDescription.setText(restaurant.getDescription() != null ? restaurant.getDescription() : "لا يوجد وصف متاح");
        
        // Rating
        String ratingText = String.format("%.1f ⭐", restaurant.getAvgRating());
        chipRating.setText(ratingText);
        
        // Features (you can customize these based on your data model)
        chipCuisine.setText("مطبخ عربي"); // You can get this from restaurant data
        chipPriceRange.setText("$$"); // You can get this from restaurant data
        chipDelivery.setVisibility(View.VISIBLE);
        
        // Contact info
        tvRestaurantPhone.setText("+966 XX XXX XXXX"); // TODO: Add phone field to Place model
        tvRestaurantWebsite.setText("www.restaurant.com"); // TODO: Add website field to Place model
        
        // Load image
        if (restaurant.getImages() != null && !restaurant.getImages().isEmpty()) {
            Glide.with(this)
                    .load(restaurant.getImages().get(0))
                    .placeholder(R.drawable.placeholder_restaurant)
                    .error(R.drawable.placeholder_restaurant)
                    .into(ivRestaurantImage);
        } else {
            ivRestaurantImage.setImageResource(R.drawable.placeholder_restaurant);
        }
        
        // Setup menu (you can implement this based on your data model)
        setupMenuRecyclerView();
    }

    private String buildAddressText() {
        if (restaurant == null) return "";
        
        String city = restaurant.getCity();
        String country = restaurant.getCountry();
        String address = restaurant.getAddress();
        
        if (address != null && !address.isEmpty()) {
            return address;
        }
        
        if (city != null && !city.isEmpty() && country != null && !country.isEmpty()) {
            return city + ", " + country;
        }
        
        if (city != null && !city.isEmpty()) {
            return city;
        }
        
        return country != null ? country : "الموقع غير محدد";
    }

    private void setupMap() {
        if (restaurant == null || restaurant.getLat() == 0.0 || restaurant.getLng() == 0.0) {
            cardMap.setVisibility(View.GONE);
            return;
        }
        
        cardMap.setVisibility(View.VISIBLE);
        tvMapLocation.setText(buildAddressText());
        
        // Initialize map
        mapView.onCreate(null);
        mapView.onResume();
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.googleMap = map;
        
        LatLng location = new LatLng(restaurant.getLat(), restaurant.getLng());
        googleMap.addMarker(new MarkerOptions()
                .position(location)
                .title(restaurant.getName()));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
        
        // Hide placeholder and show map
        mapPlaceholder.setVisibility(View.GONE);
        mapLoading.setVisibility(View.GONE);
        mapView.setVisibility(View.VISIBLE);
    }

    private void setupClickListeners() {
        // Open in Google Maps
        findViewById(R.id.btn_open_maps).setOnClickListener(v -> openInGoogleMaps());
        
        // Call button
        findViewById(R.id.btn_call).setOnClickListener(v -> makeCall());
        
        // Visit website button
        findViewById(R.id.btn_visit_website).setOnClickListener(v -> visitWebsite());
        
        // Favorite FAB
        fabFavorite.setOnClickListener(v -> toggleFavorite());
    }

    private void openInGoogleMaps() {
        if (restaurant == null || restaurant.getLat() == 0.0 || restaurant.getLng() == 0.0) {
            Toast.makeText(this, "إحداثيات الموقع غير متاحة", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String uri = String.format("geo:%f,%f?q=%f,%f(%s)",
                restaurant.getLat(), restaurant.getLng(),
                restaurant.getLat(), restaurant.getLng(),
                restaurant.getName());
        
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "تطبيق الخرائط غير متاح", Toast.LENGTH_SHORT).show();
        }
    }

    private void makeCall() {
        // TODO: Add phone field to Place model
        Toast.makeText(this, "رقم الهاتف غير متاح", Toast.LENGTH_SHORT).show();
    }

    private void visitWebsite() {
        // TODO: Add website field to Place model
        Toast.makeText(this, "الموقع الإلكتروني غير متاح", Toast.LENGTH_SHORT).show();
    }

    private void toggleFavorite() {
        // TODO: Implement favorite functionality
        Toast.makeText(this, "تم إضافة المطعم للمفضلة", Toast.LENGTH_SHORT).show();
    }

    private void setupMenuRecyclerView() {
        // TODO: Implement menu adapter
        rvRestaurantMenu.setLayoutManager(new LinearLayoutManager(this));
        // rvRestaurantMenu.setAdapter(new RestaurantMenuAdapter(restaurant.getMenu()));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }
}
