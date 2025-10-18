package com.ahmmedalmzini783.wcguide.ui.events;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.google.android.material.card.MaterialCardView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.appcompat.app.AppCompatActivity;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.data.model.Event;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.chip.Chip;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class EventDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String EXTRA_EVENT = "extra_event";
    
    private ImageView ivEvent;
    private TextView tvTitle, tvDescription, tvDate, tvLocation, tvVenueName, tvCapacity;
    private TextView tvCountdown;
    private MaterialCardView layoutCelebrationDetails, layoutMatchDetails;
    
    // Map related views
    private MaterialCardView cardMap;
    private View mapContainer;
    private View mapPlaceholder;
    private TextView tvMapLocation;
    private android.widget.ProgressBar mapLoading;
    private MapView mapView;
    private GoogleMap googleMap;
    
    // عناصر تفاصيل الاحتفالات
    private TextView tvCelebrationType, tvDuration, tvActivities, tvPerformers;
    
    // عناصر تفاصيل المباريات
    private TextView tvHomeTeam, tvAwayTeam, tvReferee, tvMatchType, tvGroup;
    private ImageView ivHomeTeamFlag, ivAwayTeamFlag;
    
    private Event event;
    private Handler countdownHandler;
    private Runnable countdownRunnable;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        
        initializeViews();
        getEventFromIntent();
        setupToolbar();
        setupCountdownTimer();
        displayEventDetails();
        setupActionButtons();
        setupMap();
    }

    private void initializeViews() {
        // Hero Image
        ivEvent = findViewById(R.id.iv_hero_image);
        
        // Basic Info
        tvTitle = findViewById(R.id.tv_event_title);
        tvDescription = findViewById(R.id.tv_event_description);
        tvDate = findViewById(R.id.tv_event_date);
        tvLocation = findViewById(R.id.tv_event_location);
        tvVenueName = findViewById(R.id.tv_event_venue);
        tvCapacity = findViewById(R.id.tv_event_capacity);
        
        // Countdown
        tvCountdown = findViewById(R.id.tv_countdown);
        
        // Celebration Details
        layoutCelebrationDetails = findViewById(R.id.card_celebration_details);
        tvCelebrationType = findViewById(R.id.tv_celebration_type);
        tvDuration = findViewById(R.id.tv_celebration_duration);
        tvActivities = findViewById(R.id.tv_celebration_activities);
        tvPerformers = findViewById(R.id.tv_celebration_performers);
        
        // Match Details
        layoutMatchDetails = findViewById(R.id.card_match_details);
        tvHomeTeam = findViewById(R.id.tv_home_team);
        tvAwayTeam = findViewById(R.id.tv_away_team);
        tvReferee = findViewById(R.id.tv_match_referee);
        tvMatchType = findViewById(R.id.tv_match_type);
        tvGroup = findViewById(R.id.tv_match_group);
        ivHomeTeamFlag = findViewById(R.id.iv_home_team_flag);
        ivAwayTeamFlag = findViewById(R.id.iv_away_team_flag);
        
        // Map Views
        cardMap = findViewById(R.id.card_map);
        mapContainer = findViewById(R.id.map_container);
        mapPlaceholder = findViewById(R.id.map_placeholder);
        tvMapLocation = findViewById(R.id.tv_map_location);
        mapLoading = findViewById(R.id.map_loading);
        mapView = findViewById(R.id.map_view);
        
        dateFormat = new SimpleDateFormat("EEEE، dd MMMM yyyy - HH:mm", Locale.getDefault());
        countdownHandler = new Handler();
    }

    private void setupToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(event != null ? event.getEventTitle() : "");
        }
        
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void getEventFromIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_EVENT)) {
            event = (Event) intent.getSerializableExtra(EXTRA_EVENT);
        }
        
        if (event == null) {
            finish();
            return;
        }
    }

    private void setupCountdownTimer() {
        if (event == null || !event.shouldShowCountdown() || !event.isHasCountdown()) {
            return;
        }
        
        countdownRunnable = new Runnable() {
            @Override
            public void run() {
                updateCountdown();
                countdownHandler.postDelayed(this, 1000); // تحديث كل ثانية
            }
        };
        
        countdownHandler.post(countdownRunnable);
    }

    private void updateCountdown() {
        if (event == null) return;
        
        Event.CountdownTime countdown = event.getCountdownTime();
        if (countdown != null) {
            tvCountdown.setText(countdown.toArabicString());
            View countdownLayout = findViewById(R.id.layout_countdown);
            if (countdownLayout != null) {
                countdownLayout.setVisibility(View.VISIBLE);
            }
        } else {
            View countdownLayout = findViewById(R.id.layout_countdown);
            if (countdownLayout != null) {
                countdownLayout.setVisibility(View.GONE);
            }
        }
    }

    private void displayEventDetails() {
        // العنوان والوصف
        tvTitle.setText(event.getEventTitle());
        tvDescription.setText(event.getDescription());
        
        // التاريخ
        if (event.getDate() != null) {
            tvDate.setText(dateFormat.format(event.getDate()));
        }
        
        // الموقع
        if (!TextUtils.isEmpty(event.getLocation())) {
            tvLocation.setText(event.getLocation());
        } else {
            tvLocation.setVisibility(View.GONE);
        }
        
        // اسم المكان
        if (!TextUtils.isEmpty(event.getVenueName())) {
            tvVenueName.setText(event.getVenueName());
        } else {
            tvVenueName.setVisibility(View.GONE);
        }
        
        // السعة
        if (event.getCapacity() > 0) {
            tvCapacity.setText(String.valueOf(event.getCapacity()) + " شخص");
        } else {
            tvCapacity.setVisibility(View.GONE);
        }
        
        // الصورة
        if (!TextUtils.isEmpty(event.getImageUrl())) {
            Glide.with(this)
                .load(event.getImageUrl())
                .placeholder(R.drawable.placeholder_event)
                .error(R.drawable.placeholder_event)
                .into(ivEvent);
        }
        
        // عرض الفعالية المميزة
        Chip chipFeatured = findViewById(R.id.chip_featured);
        if (chipFeatured != null) {
            chipFeatured.setVisibility(event.isFeatured() ? View.VISIBLE : View.GONE);
        }
        
        // عرض نوع الفعالية
        Chip chipEventType = findViewById(R.id.chip_event_type);
        if (chipEventType != null) {
            String eventType = getEventTypeInArabic(event.getType());
            chipEventType.setText(eventType);
        }
        
        // عرض التفاصيل حسب النوع
        if (event.isCelebration()) {
            displayCelebrationDetails();
        } else if (event.isMatch()) {
            displayMatchDetails();
        }
    }

    private void displayCelebrationDetails() {
        layoutCelebrationDetails.setVisibility(View.VISIBLE);
        layoutMatchDetails.setVisibility(View.GONE);
        
        // نوع الاحتفال
        if (!TextUtils.isEmpty(event.getCelebrationType())) {
            tvCelebrationType.setText(event.getCelebrationType());
        } else {
            tvCelebrationType.setVisibility(View.GONE);
        }
        
        // المدة
        if (!TextUtils.isEmpty(event.getDuration())) {
            tvDuration.setText(event.getDuration());
        } else {
            tvDuration.setVisibility(View.GONE);
        }
        
        // الأنشطة
        if (event.getActivities() != null && !event.getActivities().isEmpty()) {
            String activities = String.join("، ", event.getActivities());
            tvActivities.setText(activities);
        } else {
            tvActivities.setVisibility(View.GONE);
        }
        
        // المؤدون
        if (event.getPerformers() != null && !event.getPerformers().isEmpty()) {
            String performers = String.join("، ", event.getPerformers());
            tvPerformers.setText(performers);
        } else {
            tvPerformers.setVisibility(View.GONE);
        }
    }

    private void displayMatchDetails() {
        layoutMatchDetails.setVisibility(View.VISIBLE);
        layoutCelebrationDetails.setVisibility(View.GONE);
        
        // أسماء الفرق
        if (!TextUtils.isEmpty(event.getHomeTeam())) {
            tvHomeTeam.setText(event.getHomeTeam());
        }
        
        if (!TextUtils.isEmpty(event.getAwayTeam())) {
            tvAwayTeam.setText(event.getAwayTeam());
        }
        
        // أعلام الفرق
        if (!TextUtils.isEmpty(event.getHomeTeamFlag())) {
            Glide.with(this).load(event.getHomeTeamFlag()).into(ivHomeTeamFlag);
        }
        
        if (!TextUtils.isEmpty(event.getAwayTeamFlag())) {
            Glide.with(this).load(event.getAwayTeamFlag()).into(ivAwayTeamFlag);
        }
        
        // الحكم
        if (!TextUtils.isEmpty(event.getReferee())) {
            tvReferee.setText(event.getReferee());
        } else {
            tvReferee.setVisibility(View.GONE);
        }
        
        // نوع المباراة
        if (!TextUtils.isEmpty(event.getMatchType())) {
            tvMatchType.setText(event.getMatchType());
        } else {
            tvMatchType.setVisibility(View.GONE);
        }
        
        // المجموعة
        if (!TextUtils.isEmpty(event.getGroup())) {
            tvGroup.setText("المجموعة " + event.getGroup());
        } else {
            tvGroup.setVisibility(View.GONE);
        }
    }

    private void setupActionButtons() {
        // زر المشاركة
        findViewById(R.id.btn_share).setOnClickListener(v -> {
            shareEvent();
        });
        
        // زر التذاكر
        findViewById(R.id.btn_tickets).setOnClickListener(v -> {
            openTicketUrl();
        });
    }

    private void shareEvent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, event.getEventTitle());
        shareIntent.putExtra(Intent.EXTRA_TEXT, 
            event.getEventTitle() + "\n\n" + 
            event.getDescription() + "\n\n" +
            "التاريخ: " + (event.getDate() != null ? dateFormat.format(event.getDate()) : "") + "\n" +
            "الموقع: " + event.getLocation());
        
        startActivity(Intent.createChooser(shareIntent, "مشاركة الفعالية"));
    }

    private void openTicketUrl() {
        if (!TextUtils.isEmpty(event.getTicketUrl())) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.getTicketUrl()));
            startActivity(intent);
        }
    }

    private String getEventTypeInArabic(String type) {
        switch (type) {
            case "celebration":
                return "احتفال";
            case "match":
                return "مباراة";
            case "general":
                return "فعالية عامة";
            default:
                return "غير محدد";
        }
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
        if (countdownHandler != null && countdownRunnable != null) {
            countdownHandler.removeCallbacks(countdownRunnable);
        }
        if (mapView != null) {
            mapView.onDestroy();
        }
    }
    
    private void setupMap() {
        // Check if event has coordinates
        if (event != null && event.getLat() != 0 && event.getLng() != 0) {
            cardMap.setVisibility(View.VISIBLE);
            
            // Set location text
            String locationText = event.getLocation() != null ? event.getLocation() : 
                (event.getVenueName() != null ? event.getVenueName() : "موقع غير محدد");
            tvMapLocation.setText(locationText);
            
            // Initialize Google Maps
            initializeGoogleMaps();
            
            // Setup open in maps button
            findViewById(R.id.btn_open_maps).setOnClickListener(v -> openInGoogleMaps());
            
        } else {
            cardMap.setVisibility(View.GONE);
        }
    }
    
    private void initializeGoogleMaps() {
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }
    
    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        
        if (event != null && event.getLat() != 0 && event.getLng() != 0) {
            // Create LatLng object for the event location
            LatLng eventLocation = new LatLng(event.getLat(), event.getLng());
            
            // Add marker for the event location
            String locationName = event.getLocation() != null ? event.getLocation() : 
                (event.getVenueName() != null ? event.getVenueName() : "موقع الفعالية");
            
            googleMap.addMarker(new MarkerOptions()
                .position(eventLocation)
                .title(locationName)
                .snippet("موقع الفعالية"));
            
            // Move camera to the event location
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLocation, 15));
            
            // Hide placeholder and show map
            mapPlaceholder.setVisibility(View.GONE);
            mapView.setVisibility(View.VISIBLE);
            mapLoading.setVisibility(View.GONE);
        }
    }
    
    private void openInGoogleMaps() {
        if (event != null && event.getLat() != 0 && event.getLng() != 0) {
            String locationName = event.getLocation() != null ? event.getLocation() : 
                (event.getVenueName() != null ? event.getVenueName() : "موقع الفعالية");
            
            // Create Google Maps intent
            String uri = String.format("geo:%f,%f?q=%f,%f(%s)", 
                event.getLat(), event.getLng(), 
                event.getLat(), event.getLng(), 
                Uri.encode(locationName));
            
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            mapIntent.setPackage("com.google.android.apps.maps");
            
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                // Fallback to web browser
                String webUri = String.format("https://www.google.com/maps/search/?api=1&query=%f,%f", 
                    event.getLat(), event.getLng());
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUri));
                startActivity(webIntent);
            }
        }
    }

    public static Intent createIntent(Context context, Event event) {
        Intent intent = new Intent(context, EventDetailsActivity.class);
        intent.putExtra(EXTRA_EVENT, event);
        return intent;
    }
}
