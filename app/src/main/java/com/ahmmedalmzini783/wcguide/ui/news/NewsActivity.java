package com.ahmmedalmzini783.wcguide.ui.news;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmmedalmzini783.wcguide.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView newsRecyclerView;
    private LinearLayout emptyStateLayout;
    
    private NewsAdapter newsAdapter;
    private List<NewsItem> newsList;
    private DatabaseReference databaseReference;
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

        databaseReference = FirebaseDatabase.getInstance().getReference("news");
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
        // No click listeners needed for FAB since it's removed
    }

    private void checkUserStatus() {
        currentUser = mAuth.getCurrentUser();
        // FAB removed, no need to check user status for FAB visibility
    }

    private void loadNews() {
        databaseReference.orderByChild("createdAt").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                newsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    NewsItem newsItem = snapshot.getValue(NewsItem.class);
                    if (newsItem != null) {
                        newsItem.setId(snapshot.getKey());
                        newsList.add(newsItem);
                    }
                }
                
                // ترتيب القائمة حسب التاريخ (الأحدث أولاً)
                newsList.sort((a, b) -> {
                    if (a.getCreatedAt() == null || b.getCreatedAt() == null) return 0;
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                });
                
                newsAdapter.notifyDataSetChanged();
                updateEmptyState();
            }
            
            @Override
            public void onCancelled(DatabaseError databaseError) {
                handleDatabaseError(databaseError.toException(), "تحميل");
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

    private void handleDatabaseError(Exception e, String operation) {
        String errorMessage = "حدث خطأ في " + operation + " الأخبار: " + e.getMessage();
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
