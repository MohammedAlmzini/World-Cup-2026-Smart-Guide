package com.ahmmedalmzini783.wcguide.ui.news;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.ui.admin.AdminNewsActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView newsRecyclerView;
    private LinearLayout emptyStateLayout;
    private FloatingActionButton fabAddNews;
    
    private NewsAdapter newsAdapter;
    private List<NewsItem> newsList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupClickListeners();
        checkUserStatus();
        loadNews();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        newsRecyclerView = findViewById(R.id.news_recycler_view);
        emptyStateLayout = findViewById(R.id.empty_state_layout);
        fabAddNews = findViewById(R.id.fab_add_news);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
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
        newsAdapter = new NewsAdapter(newsList, this::onNewsItemClick);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsRecyclerView.setAdapter(newsAdapter);
    }

    private void setupClickListeners() {
        fabAddNews.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminNewsActivity.class);
            startActivity(intent);
        });
    }

    private void checkUserStatus() {
        currentUser = mAuth.getCurrentUser();
        
        // Show FAB only for admin users
        if (currentUser != null && isAdminUser()) {
            fabAddNews.setVisibility(View.VISIBLE);
        } else {
            fabAddNews.setVisibility(View.GONE);
        }
    }

    private boolean isAdminUser() {
        // TODO: Implement admin check logic
        // For now, return true for testing
        return true;
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
                    
                    newsAdapter.notifyDataSetChanged();
                    updateEmptyState();
                }
            });
    }

    private void updateEmptyState() {
        if (newsList.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            newsRecyclerView.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            newsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void onNewsItemClick(NewsItem newsItem) {
        Intent intent = new Intent(this, NewsDetailActivity.class);
        intent.putExtra("news_item", newsItem);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
