package com.ahmmedalmzini783.wcguide.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.ui.events.EventItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminEventsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView adminEventsRecyclerView;
    private LinearLayout emptyStateLayout;
    private FloatingActionButton fabAddEvent;
    
    private AdminEventsAdapter adminEventsAdapter;
    private List<EventItem> eventsList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_news); // Using same layout as admin news

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupClickListeners();
        loadEvents();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        adminEventsRecyclerView = findViewById(R.id.admin_news_recycler_view);
        emptyStateLayout = findViewById(R.id.empty_state_layout);
        fabAddEvent = findViewById(R.id.fab_add_news);

        db = FirebaseFirestore.getInstance();
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
        adminEventsAdapter = new AdminEventsAdapter(eventsList, this::onEventItemClick, this::onEditEventClick, this::onDeleteEventClick);
        adminEventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adminEventsRecyclerView.setAdapter(adminEventsAdapter);
    }

    private void setupClickListeners() {
        fabAddEvent.setOnClickListener(v -> {
            // TODO: Implement add event activity
        });
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
                    
                    adminEventsAdapter.notifyDataSetChanged();
                    updateEmptyState();
                }
            });
    }

    private void updateEmptyState() {
        if (eventsList.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            adminEventsRecyclerView.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            adminEventsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void onEventItemClick(EventItem eventItem) {
        // TODO: Implement event detail view
    }

    private void onEditEventClick(EventItem eventItem) {
        // TODO: Implement edit event activity
    }

    private void onDeleteEventClick(EventItem eventItem) {
        // TODO: Implement delete confirmation dialog
        db.collection("events").document(eventItem.getId()).delete()
            .addOnSuccessListener(aVoid -> {
                loadEvents(); // Refresh the list
            });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
