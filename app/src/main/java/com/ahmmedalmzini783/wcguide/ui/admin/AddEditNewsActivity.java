package com.ahmmedalmzini783.wcguide.ui.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.ui.news.NewsItem;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddEditNewsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextInputEditText etNewsTitle, etNewsDescription, etNewsContent;
    private TextInputEditText etNewsImageUrl, etNewsCategory;
    private Switch switchNewsPriority;
    private Button btnCancel, btnSave;

    private FirebaseFirestore db;
    private NewsItem newsItem;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_news);

        initViews();
        setupToolbar();
        setupClickListeners();
        loadData();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        etNewsTitle = findViewById(R.id.et_news_title);
        etNewsDescription = findViewById(R.id.et_news_description);
        etNewsContent = findViewById(R.id.et_news_content);
        etNewsImageUrl = findViewById(R.id.et_news_image_url);
        etNewsCategory = findViewById(R.id.et_news_category);
        switchNewsPriority = findViewById(R.id.switch_news_priority);
        btnCancel = findViewById(R.id.btn_cancel);
        btnSave = findViewById(R.id.btn_save);

        db = FirebaseFirestore.getInstance();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupClickListeners() {
        btnCancel.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> saveNews());
    }

    private void loadData() {
        newsItem = (NewsItem) getIntent().getSerializableExtra("news_item");
        isEditMode = getIntent().getBooleanExtra("is_edit", false);

        if (isEditMode && newsItem != null) {
            etNewsTitle.setText(newsItem.getTitle());
            etNewsDescription.setText(newsItem.getDescription());
            etNewsContent.setText(newsItem.getContent());
            etNewsImageUrl.setText(newsItem.getImageUrl());
            etNewsCategory.setText(newsItem.getCategory());
            switchNewsPriority.setChecked(newsItem.isPriority());
        }
    }

    private void saveNews() {
        String title = etNewsTitle.getText().toString().trim();
        String description = etNewsDescription.getText().toString().trim();
        String content = etNewsContent.getText().toString().trim();
        String imageUrl = etNewsImageUrl.getText().toString().trim();
        String category = etNewsCategory.getText().toString().trim();
        boolean priority = switchNewsPriority.isChecked();

        if (title.isEmpty() || description.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "يرجى ملء جميع الحقول المطلوبة", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> newsData = new HashMap<>();
        newsData.put("title", title);
        newsData.put("description", description);
        newsData.put("content", content);
        newsData.put("imageUrl", imageUrl);
        newsData.put("category", category);
        newsData.put("priority", priority);
        newsData.put("updatedAt", new Date());

        if (!isEditMode) {
            newsData.put("createdAt", new Date());
        }

        if (isEditMode && newsItem != null) {
            // Update existing news
            db.collection("news").document(newsItem.getId())
                .update(newsData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "تم تحديث الخبر بنجاح", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "حدث خطأ في تحديث الخبر", Toast.LENGTH_SHORT).show();
                });
        } else {
            // Add new news
            db.collection("news")
                .add(newsData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "تم إضافة الخبر بنجاح", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "حدث خطأ في إضافة الخبر", Toast.LENGTH_SHORT).show();
                });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
