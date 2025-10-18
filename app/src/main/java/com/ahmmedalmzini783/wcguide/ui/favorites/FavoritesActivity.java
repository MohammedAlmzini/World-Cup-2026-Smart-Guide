package com.ahmmedalmzini783.wcguide.ui.favorites;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.data.model.Event;
import com.ahmmedalmzini783.wcguide.ui.events.EventAdapter;
import com.ahmmedalmzini783.wcguide.ui.events.EventDetailsActivity;
import com.ahmmedalmzini783.wcguide.util.AuthManager;
import com.ahmmedalmzini783.wcguide.util.UserPreferences;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerFavorites;
    private LinearLayout layoutEmptyState;
    private EventAdapter eventAdapter;
    private List<Event> favoriteEvents;
    
    private AuthManager authManager;
    private UserPreferences userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        
        initializeViews();
        initializeManagers();
        setupToolbar();
        setupRecyclerView();
        loadFavoriteEvents();
    }

    private void initializeViews() {
        recyclerFavorites = findViewById(R.id.recycler_favorites);
        layoutEmptyState = findViewById(R.id.layout_empty_state);
    }

    private void initializeManagers() {
        authManager = AuthManager.getInstance(this);
        userPreferences = UserPreferences.getInstance(this);
    }

    private void setupToolbar() {
        findViewById(R.id.toolbar).setOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        favoriteEvents = new ArrayList<>();
        eventAdapter = new EventAdapter(favoriteEvents, this::onEventClick);
        recyclerFavorites.setLayoutManager(new LinearLayoutManager(this));
        recyclerFavorites.setAdapter(eventAdapter);
    }

    private void loadFavoriteEvents() {
        // For now, we'll show empty state
        // In a real implementation, you would load from database
        showEmptyState();
    }

    private void onEventClick(Event event) {
        // Open event details
        startActivity(EventDetailsActivity.createIntent(this, event));
    }

    private void showEmptyState() {
        recyclerFavorites.setVisibility(View.GONE);
        layoutEmptyState.setVisibility(View.VISIBLE);
    }

    private void showFavorites() {
        recyclerFavorites.setVisibility(View.VISIBLE);
        layoutEmptyState.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh favorites when returning to this activity
        loadFavoriteEvents();
    }
}
