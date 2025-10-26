package com.ahmmedalmzini783.wcguide.ui.details.hotel;

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
import com.ahmmedalmzini783.wcguide.util.ReviewAuthHelper;
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

public class HotelDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String EXTRA_HOTEL = "extra_hotel";
    
    // Views
    private ImageView ivHotelImage;
    private TextView tvHotelName;
    private TextView tvHotelAddress;
    private TextView tvHotelDescription;
    private Chip chipRating;
    private Chip chipWifi;
    private Chip chipParking;
    private Chip chipRestaurant;
    
    // Map related views
    private MaterialCardView cardMap;
    private View mapContainer;
    private View mapPlaceholder;
    private TextView tvMapLocation;
    private ProgressBar mapLoading;
    private MapView mapView;
    private GoogleMap googleMap;
    
    // Contact views
    private TextView tvHotelPhone;
    private TextView tvHotelWebsite;
    
    // Rooms
    private RecyclerView rvHotelRooms;
    
    // FAB
    private FloatingActionButton fabFavorite;
    
    private Place hotel;

    public static Intent createIntent(android.content.Context context, Place hotel) {
        Intent intent = new Intent(context, HotelDetailsActivity.class);
        intent.putExtra(EXTRA_HOTEL + "_id", hotel.getId());
        intent.putExtra(EXTRA_HOTEL + "_name", hotel.getName());
        intent.putExtra(EXTRA_HOTEL + "_description", hotel.getDescription());
        intent.putExtra(EXTRA_HOTEL + "_address", hotel.getAddress());
        intent.putExtra(EXTRA_HOTEL + "_city", hotel.getCity());
        intent.putExtra(EXTRA_HOTEL + "_country", hotel.getCountry());
        intent.putExtra(EXTRA_HOTEL + "_lat", hotel.getLat());
        intent.putExtra(EXTRA_HOTEL + "_lng", hotel.getLng());
        intent.putExtra(EXTRA_HOTEL + "_rating", hotel.getAvgRating());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_details);
        
        initializeViews();
        getHotelFromIntent();
        setupToolbar();
        displayHotelDetails();
        setupMap();
        setupClickListeners();
    }

    private void initializeViews() {
        // Main views
        ivHotelImage = findViewById(R.id.iv_hotel_image);
        tvHotelName = findViewById(R.id.tv_hotel_name);
        tvHotelAddress = findViewById(R.id.tv_hotel_address);
        tvHotelDescription = findViewById(R.id.tv_hotel_description);
        chipRating = findViewById(R.id.chip_rating);
        chipWifi = findViewById(R.id.chip_wifi);
        chipParking = findViewById(R.id.chip_parking);
        chipRestaurant = findViewById(R.id.chip_restaurant);
        
        // Map views
        cardMap = findViewById(R.id.card_map);
        mapContainer = findViewById(R.id.map_container);
        mapPlaceholder = findViewById(R.id.map_placeholder);
        tvMapLocation = findViewById(R.id.tv_map_location);
        mapLoading = findViewById(R.id.map_loading);
        mapView = findViewById(R.id.map_view);
        
        // Contact views
        tvHotelPhone = findViewById(R.id.tv_hotel_phone);
        tvHotelWebsite = findViewById(R.id.tv_hotel_website);
        
        // Rooms
        rvHotelRooms = findViewById(R.id.rv_hotel_rooms);
        
        // FAB
        fabFavorite = findViewById(R.id.fab_favorite);
    }

    private void getHotelFromIntent() {
        Intent intent = getIntent();
        String id = intent.getStringExtra(EXTRA_HOTEL + "_id");
        String name = intent.getStringExtra(EXTRA_HOTEL + "_name");
        String description = intent.getStringExtra(EXTRA_HOTEL + "_description");
        String address = intent.getStringExtra(EXTRA_HOTEL + "_address");
        String city = intent.getStringExtra(EXTRA_HOTEL + "_city");
        String country = intent.getStringExtra(EXTRA_HOTEL + "_country");
        double lat = intent.getDoubleExtra(EXTRA_HOTEL + "_lat", 0.0);
        double lng = intent.getDoubleExtra(EXTRA_HOTEL + "_lng", 0.0);
        float rating = intent.getFloatExtra(EXTRA_HOTEL + "_rating", 0.0f);
        
        if (id == null || name == null) {
            Toast.makeText(this, "خطأ في تحميل بيانات الفندق", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Create a temporary Place object with the data
        hotel = new Place();
        hotel.setId(id);
        hotel.setName(name);
        hotel.setDescription(description);
        hotel.setAddress(address);
        hotel.setCity(city);
        hotel.setCountry(country);
        hotel.setLat(lat);
        hotel.setLng(lng);
        hotel.setAvgRating(rating);
    }

    private void setupToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        if (collapsingToolbar != null && hotel != null) {
            collapsingToolbar.setTitle(hotel.getName());
        }
    }

    private void displayHotelDetails() {
        if (hotel == null) return;
        
        // Basic info
        tvHotelName.setText(hotel.getName());
        tvHotelAddress.setText(buildAddressText());
        tvHotelDescription.setText(hotel.getDescription() != null ? hotel.getDescription() : "لا يوجد وصف متاح");
        
        // Rating
        String ratingText = String.format("%.1f ⭐", hotel.getAvgRating());
        chipRating.setText(ratingText);
        
        // Features (you can customize these based on your data model)
        chipWifi.setVisibility(View.VISIBLE);
        chipParking.setVisibility(View.VISIBLE);
        chipRestaurant.setVisibility(View.VISIBLE);
        
        // Contact info
        tvHotelPhone.setText("+966 XX XXX XXXX"); // TODO: Add phone field to Place model
        tvHotelWebsite.setText("www.hotel.com"); // TODO: Add website field to Place model
        
        // Load image
        if (hotel.getImages() != null && !hotel.getImages().isEmpty()) {
            Glide.with(this)
                    .load(hotel.getImages().get(0))
                    .placeholder(R.drawable.placeholder_hotel)
                    .error(R.drawable.placeholder_hotel)
                    .into(ivHotelImage);
        } else {
            ivHotelImage.setImageResource(R.drawable.placeholder_hotel);
        }
        
        // Setup rooms (you can implement this based on your data model)
        setupRoomsRecyclerView();
    }

    private String buildAddressText() {
        if (hotel == null) return "";
        
        String city = hotel.getCity();
        String country = hotel.getCountry();
        String address = hotel.getAddress();
        
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
        if (hotel == null || hotel.getLat() == 0.0 || hotel.getLng() == 0.0) {
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
        
        LatLng location = new LatLng(hotel.getLat(), hotel.getLng());
        googleMap.addMarker(new MarkerOptions()
                .position(location)
                .title(hotel.getName()));
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
        
        // Add review button
        findViewById(R.id.btn_add_review).setOnClickListener(v -> addReview());
        
        // Favorite FAB
        fabFavorite.setOnClickListener(v -> toggleFavorite());
    }

    private void openInGoogleMaps() {
        if (hotel == null || hotel.getLat() == 0.0 || hotel.getLng() == 0.0) {
            Toast.makeText(this, "إحداثيات الموقع غير متاحة", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String uri = String.format("geo:%f,%f?q=%f,%f(%s)",
                hotel.getLat(), hotel.getLng(),
                hotel.getLat(), hotel.getLng(),
                hotel.getName());
        
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

    private void addReview() {
        if (hotel != null) {
            ReviewAuthHelper.handleAddReview(this, hotel, "hotel");
        }
    }

    private void toggleFavorite() {
        // TODO: Implement favorite functionality
        Toast.makeText(this, "تم إضافة الفندق للمفضلة", Toast.LENGTH_SHORT).show();
    }

    private void setupRoomsRecyclerView() {
        // TODO: Implement rooms adapter
        rvHotelRooms.setLayoutManager(new LinearLayoutManager(this));
        // rvHotelRooms.setAdapter(new HotelRoomsAdapter(hotel.getRooms()));
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
