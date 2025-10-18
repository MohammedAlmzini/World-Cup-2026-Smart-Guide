package com.ahmmedalmzini783.wcguide.ui.news;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ahmmedalmzini783.wcguide.R;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NewsDetailActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView newsDetailImage;
    private TextView newsDetailTitle, newsDetailCategory, newsDetailDate;
    private TextView newsDetailDescription, newsDetailContent;

    private NewsItem newsItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        initViews();
        setupToolbar();
        loadNewsData();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        newsDetailImage = findViewById(R.id.news_detail_image);
        newsDetailTitle = findViewById(R.id.news_detail_title);
        newsDetailCategory = findViewById(R.id.news_detail_category);
        newsDetailDate = findViewById(R.id.news_detail_date);
        newsDetailDescription = findViewById(R.id.news_detail_description);
        newsDetailContent = findViewById(R.id.news_detail_content);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void loadNewsData() {
        newsItem = (NewsItem) getIntent().getSerializableExtra("news_item");
        
        if (newsItem != null) {
            // Load image
            if (newsItem.getImageUrl() != null && !newsItem.getImageUrl().isEmpty()) {
                Glide.with(this)
                    .load(newsItem.getImageUrl())
                    .placeholder(R.drawable.ic_news)
                    .error(R.drawable.ic_news)
                    .into(newsDetailImage);
            }

            // Set text content
            newsDetailTitle.setText(newsItem.getTitle());
            newsDetailCategory.setText(newsItem.getCategory());
            newsDetailDescription.setText(newsItem.getDescription());
            newsDetailContent.setText(newsItem.getContent());

            // Format date
            if (newsItem.getCreatedAt() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                newsDetailDate.setText(sdf.format(newsItem.getCreatedAt()));
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
