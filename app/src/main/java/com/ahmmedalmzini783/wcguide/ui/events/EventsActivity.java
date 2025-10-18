package com.ahmmedalmzini783.wcguide.ui.events;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.ui.admin.AdminEventsActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class EventsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView eventsRecyclerView;
    private LinearLayout emptyStateLayout;
    private FloatingActionButton fabAddEvent;
    
    private EventsAdapter eventsAdapter;
    private List<EventItem> eventsList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupClickListeners();
        checkUserStatus();
        loadEvents();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        eventsRecyclerView = findViewById(R.id.events_recycler_view);
        emptyStateLayout = findViewById(R.id.empty_state_layout);
        fabAddEvent = findViewById(R.id.fab_add_event);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        eventsList = new ArrayList<>();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupRecyclerView() {
        eventsAdapter = new EventsAdapter(eventsList, this::onEventItemClick);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventsRecyclerView.setAdapter(eventsAdapter);
    }

    private void setupClickListeners() {
        fabAddEvent.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminEventsActivity.class);
            startActivity(intent);
        });
    }

    private void checkUserStatus() {
        currentUser = mAuth.getCurrentUser();
        
        // Show FAB only for admin users
        if (currentUser != null && isAdminUser()) {
            fabAddEvent.setVisibility(View.VISIBLE);
        } else {
            fabAddEvent.setVisibility(View.GONE);
        }
    }

    private boolean isAdminUser() {
        // TODO: Implement admin check logic
        // For now, return true for testing
        return true;
    }

    private void loadEvents() {
        db.collection("events")
            .orderBy("eventDate", com.google.firebase.firestore.Query.Direction.ASCENDING)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    eventsList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        EventItem eventItem = document.toObject(EventItem.class);
                        eventItem.setId(document.getId());
                        eventsList.add(eventItem);
                    }
                    
                    eventsAdapter.notifyDataSetChanged();
                    updateEmptyState();
                }
            });
    }

    private void updateEmptyState() {
        if (eventsList.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            eventsRecyclerView.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            eventsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void onEventItemClick(EventItem eventItem) {
        // TODO: Implement event detail activity
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
