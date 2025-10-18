package com.ahmmedalmzini783.wcguide.ui.comments;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.util.AuthManager;
import com.ahmmedalmzini783.wcguide.util.UserPreferences;

import java.util.ArrayList;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {

    private RecyclerView recyclerComments;
    private LinearLayout layoutEmptyState;
    
    private AuthManager authManager;
    private UserPreferences userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        
        initializeViews();
        initializeManagers();
        setupToolbar();
        setupRecyclerView();
        loadComments();
    }

    private void initializeViews() {
        recyclerComments = findViewById(R.id.recycler_comments);
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
        // For now, we'll show empty state
        // In a real implementation, you would create a CommentAdapter
        recyclerComments.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadComments() {
        // For now, we'll show empty state
        // In a real implementation, you would load from database
        showEmptyState();
    }

    private void showEmptyState() {
        recyclerComments.setVisibility(View.GONE);
        layoutEmptyState.setVisibility(View.VISIBLE);
    }

    private void showComments() {
        recyclerComments.setVisibility(View.VISIBLE);
        layoutEmptyState.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh comments when returning to this activity
        loadComments();
    }
}
