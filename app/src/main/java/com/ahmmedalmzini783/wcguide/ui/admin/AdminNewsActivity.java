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
import com.ahmmedalmzini783.wcguide.ui.news.NewsItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminNewsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView adminNewsRecyclerView;
    private LinearLayout emptyStateLayout;
    private FloatingActionButton fabAddNews;
    
    private AdminNewsAdapter adminNewsAdapter;
    private List<NewsItem> newsList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_news);

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupClickListeners();
        loadNews();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        adminNewsRecyclerView = findViewById(R.id.admin_news_recycler_view);
        emptyStateLayout = findViewById(R.id.empty_state_layout);
        fabAddNews = findViewById(R.id.fab_add_news);

        db = FirebaseFirestore.getInstance();
        newsList = new ArrayList<>();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupRecyclerView() {
        adminNewsAdapter = new AdminNewsAdapter(newsList, this::onNewsItemClick, this::onEditNewsClick, this::onDeleteNewsClick);
        adminNewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adminNewsRecyclerView.setAdapter(adminNewsAdapter);
    }

    private void setupClickListeners() {
        fabAddNews.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditNewsActivity.class);
            startActivity(intent);
        });
    }

    private void loadNews() {
        db.collection("news")
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    newsList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        NewsItem newsItem = document.toObject(NewsItem.class);
                        newsItem.setId(document.getId());
                        newsList.add(newsItem);
                    }
                    
                    adminNewsAdapter.notifyDataSetChanged();
                    updateEmptyState();
                }
            });
    }

    private void updateEmptyState() {
        if (newsList.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            adminNewsRecyclerView.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            adminNewsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void onNewsItemClick(NewsItem newsItem) {
        // TODO: Implement news detail view
    }

    private void onEditNewsClick(NewsItem newsItem) {
        Intent intent = new Intent(this, AddEditNewsActivity.class);
        intent.putExtra("news_item", newsItem);
        intent.putExtra("is_edit", true);
        startActivity(intent);
    }

    private void onDeleteNewsClick(NewsItem newsItem) {
        // TODO: Implement delete confirmation dialog
        // For now, just delete directly
        db.collection("news").document(newsItem.getId()).delete()
            .addOnSuccessListener(aVoid -> {
                loadNews(); // Refresh the list
            });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
