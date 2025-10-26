package com.ahmmedalmzini783.wcguide.ui.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.text.style.BulletSpan;
import android.graphics.Typeface;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.ui.news.NewsItem;
import com.ahmmedalmzini783.wcguide.data.repository.NotificationRepository;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddEditNewsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextInputEditText etNewsTitle, etNewsDescription, etNewsContent;
    private TextInputEditText etNewsImageUrl, etNewsCategory;
    private com.google.android.material.switchmaterial.SwitchMaterial switchNewsPriority;
    private Button btnCancel, btnSave;
    
    // Formatting buttons
    private ImageButton btnBold, btnItalic, btnUnderline;
    private ImageButton btnBullet, btnNumbered, btnClearFormat;

    private DatabaseReference databaseReference;
    private NewsItem newsItem;
    private boolean isEditMode = false;
    private NotificationRepository notificationRepository;

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
        
        // Initialize formatting buttons
        btnBold = findViewById(R.id.btn_bold);
        btnItalic = findViewById(R.id.btn_italic);
        btnUnderline = findViewById(R.id.btn_underline);
        btnBullet = findViewById(R.id.btn_bullet);
        btnNumbered = findViewById(R.id.btn_numbered);
        btnClearFormat = findViewById(R.id.btn_clear_format);

        databaseReference = FirebaseDatabase.getInstance().getReference("news");
        notificationRepository = new NotificationRepository();
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
        
        // Formatting button listeners
        btnBold.setOnClickListener(v -> applyBoldFormatting());
        btnItalic.setOnClickListener(v -> applyItalicFormatting());
        btnUnderline.setOnClickListener(v -> applyUnderlineFormatting());
        btnBullet.setOnClickListener(v -> applyBulletFormatting());
        btnNumbered.setOnClickListener(v -> applyNumberedFormatting());
        btnClearFormat.setOnClickListener(v -> clearFormatting());
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
            databaseReference.child(newsItem.getId())
                .updateChildren(newsData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "تم تحديث الخبر بنجاح", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    handleDatabaseError(e, "تحديث");
                });
        } else {
            // Add new news
            String newsId = databaseReference.push().getKey();
            databaseReference.child(newsId)
                .setValue(newsData)
                .addOnSuccessListener(aVoid -> {
                    // Create notification for new news
                    createNewsNotification(newsId, newsData.get("title").toString(), newsData.get("description").toString(), newsData.get("imageUrl").toString());
                    Toast.makeText(this, "تم إضافة الخبر بنجاح", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    handleDatabaseError(e, "إضافة");
                });
        }
    }

    private void handleDatabaseError(Exception e, String operation) {
        String errorMessage;
        
        if (e instanceof DatabaseException) {
            DatabaseException databaseException = (DatabaseException) e;
            errorMessage = "خطأ في قاعدة البيانات: " + databaseException.getMessage();
        } else {
            errorMessage = "حدث خطأ في " + operation + " الخبر: " + e.getMessage();
        }
        
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }
    
    private void createNewsNotification(String newsId, String title, String description, String imageUrl) {
        String notificationTitle = "خبر جديد: " + title;
        String notificationMessage = description.length() > 100 ? 
            description.substring(0, 100) + "..." : description;
        
        notificationRepository.createContentNotification(
            "news",
            notificationTitle,
            notificationMessage,
            newsId,
            imageUrl
        );
    }
    
    // Formatting methods
    private void applyBoldFormatting() {
        int start = etNewsContent.getSelectionStart();
        int end = etNewsContent.getSelectionEnd();
        
        if (start != end) {
            SpannableString spannable = new SpannableString(etNewsContent.getText());
            spannable.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            etNewsContent.setText(spannable);
            etNewsContent.setSelection(start, end);
        } else {
            // Insert bold markers
            String text = etNewsContent.getText().toString();
            String newText = text.substring(0, start) + "**" + text.substring(start);
            etNewsContent.setText(newText);
            etNewsContent.setSelection(start + 2);
        }
    }
    
    private void applyItalicFormatting() {
        int start = etNewsContent.getSelectionStart();
        int end = etNewsContent.getSelectionEnd();
        
        if (start != end) {
            SpannableString spannable = new SpannableString(etNewsContent.getText());
            spannable.setSpan(new StyleSpan(Typeface.ITALIC), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            etNewsContent.setText(spannable);
            etNewsContent.setSelection(start, end);
        } else {
            // Insert italic markers
            String text = etNewsContent.getText().toString();
            String newText = text.substring(0, start) + "*" + text.substring(start);
            etNewsContent.setText(newText);
            etNewsContent.setSelection(start + 1);
        }
    }
    
    private void applyUnderlineFormatting() {
        int start = etNewsContent.getSelectionStart();
        int end = etNewsContent.getSelectionEnd();
        
        if (start != end) {
            SpannableString spannable = new SpannableString(etNewsContent.getText());
            spannable.setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            etNewsContent.setText(spannable);
            etNewsContent.setSelection(start, end);
        } else {
            // Insert underline markers
            String text = etNewsContent.getText().toString();
            String newText = text.substring(0, start) + "__" + text.substring(start);
            etNewsContent.setText(newText);
            etNewsContent.setSelection(start + 2);
        }
    }
    
    private void applyBulletFormatting() {
        int start = etNewsContent.getSelectionStart();
        String text = etNewsContent.getText().toString();
        String newText = text.substring(0, start) + "• " + text.substring(start);
        etNewsContent.setText(newText);
        etNewsContent.setSelection(start + 2);
    }
    
    private void applyNumberedFormatting() {
        int start = etNewsContent.getSelectionStart();
        String text = etNewsContent.getText().toString();
        String newText = text.substring(0, start) + "1. " + text.substring(start);
        etNewsContent.setText(newText);
        etNewsContent.setSelection(start + 3);
    }
    
    private void clearFormatting() {
        String text = etNewsContent.getText().toString();
        // Remove markdown formatting
        text = text.replaceAll("\\*\\*(.*?)\\*\\*", "$1"); // Bold
        text = text.replaceAll("\\*(.*?)\\*", "$1"); // Italic
        text = text.replaceAll("__(.*?)__", "$1"); // Underline
        text = text.replaceAll("• ", ""); // Bullets
        text = text.replaceAll("\\d+\\. ", ""); // Numbered
        
        etNewsContent.setText(text);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
