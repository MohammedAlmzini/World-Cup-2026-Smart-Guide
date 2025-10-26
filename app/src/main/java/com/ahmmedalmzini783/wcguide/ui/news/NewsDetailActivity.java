package com.ahmmedalmzini783.wcguide.ui.news;

import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ahmmedalmzini783.wcguide.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class NewsDetailActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView ivNewsImage;
    private TextView tvNewsTitle, tvNewsDescription, tvNewsContent, tvNewsCategory, tvNewsDate;
    
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
        ivNewsImage = findViewById(R.id.iv_news_image);
        tvNewsTitle = findViewById(R.id.tv_news_title);
        tvNewsDescription = findViewById(R.id.tv_news_description);
        tvNewsContent = findViewById(R.id.tv_news_content);
        tvNewsCategory = findViewById(R.id.tv_news_category);
        tvNewsDate = findViewById(R.id.tv_news_date);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("تفاصيل الخبر");
        }
    }

    private void loadNewsData() {
        newsItem = (NewsItem) getIntent().getSerializableExtra("news_item");
        
        if (newsItem != null) {
            // Set title
            tvNewsTitle.setText(newsItem.getTitle());
            
            // Set description
            tvNewsDescription.setText(newsItem.getDescription());
            
            // Set content with HTML formatting support
            String content = newsItem.getContent();
            if (content != null && !content.isEmpty()) {
                // Convert markdown-like formatting to HTML
                content = convertMarkdownToHtml(content);
                Spanned spannedContent = Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY);
                tvNewsContent.setText(spannedContent);
                tvNewsContent.setMovementMethod(LinkMovementMethod.getInstance());
            } else {
                tvNewsContent.setText("لا يوجد محتوى متاح");
            }
            
            // Set category
            if (newsItem.getCategory() != null && !newsItem.getCategory().isEmpty()) {
                tvNewsCategory.setText("التصنيف: " + newsItem.getCategory());
                tvNewsCategory.setVisibility(android.view.View.VISIBLE);
            } else {
                tvNewsCategory.setVisibility(android.view.View.GONE);
            }
            
            // Set date
            if (newsItem.getCreatedAt() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy - HH:mm", new Locale("ar"));
                String formattedDate = dateFormat.format(newsItem.getCreatedAt());
                tvNewsDate.setText("تاريخ النشر: " + formattedDate);
            } else {
                tvNewsDate.setText("تاريخ النشر: غير محدد");
            }
            
            // Load image
            if (newsItem.getImageUrl() != null && !newsItem.getImageUrl().isEmpty()) {
                Glide.with(this)
                    .load(newsItem.getImageUrl())
                    .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_news_placeholder)
                        .error(R.drawable.ic_news_placeholder)
                        .transform(new RoundedCorners(16)))
                    .into(ivNewsImage);
                ivNewsImage.setVisibility(android.view.View.VISIBLE);
            } else {
                ivNewsImage.setVisibility(android.view.View.GONE);
            }
        }
    }

    private String convertMarkdownToHtml(String content) {
        // Convert markdown-like formatting to HTML
        content = content.replaceAll("\\*\\*(.*?)\\*\\*", "<b>$1</b>"); // Bold
        content = content.replaceAll("\\*(.*?)\\*", "<i>$1</i>"); // Italic
        content = content.replaceAll("__(.*?)__", "<u>$1</u>"); // Underline
        
        // Handle bullet points
        content = content.replaceAll("(?m)^•\\s+(.+)$", "<li>$1</li>");
        
        // Handle numbered lists
        content = content.replaceAll("(?m)^\\d+\\.\\s+(.+)$", "<li>$1</li>");
        
        // Wrap lists in proper HTML structure
        content = content.replaceAll("(<li>.*</li>)", "<ul>$1</ul>");
        
        // Convert line breaks to HTML
        content = content.replaceAll("\\n", "<br>");
        
        // Wrap content in proper HTML structure
        content = "<div style='line-height: 1.6; font-size: 16px;'>" + content + "</div>";
        
        return content;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}