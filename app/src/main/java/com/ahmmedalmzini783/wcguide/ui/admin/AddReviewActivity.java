package com.ahmmedalmzini783.wcguide.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.data.model.Landmark;
import com.ahmmedalmzini783.wcguide.data.model.Review;
import com.ahmmedalmzini783.wcguide.data.model.ReviewImage;
import com.ahmmedalmzini783.wcguide.databinding.ActivityAddReviewBinding;
import com.ahmmedalmzini783.wcguide.util.Resource;

import java.util.ArrayList;
import java.util.List;

public class AddReviewActivity extends AppCompatActivity {

    private ActivityAddReviewBinding binding;
    private ReviewViewModel reviewViewModel;
    private ReviewImageAdapter imageAdapter;
    private List<ReviewImage> reviewImages;
    private Landmark landmark;
    private Review editingReview;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        binding = ActivityAddReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        reviewViewModel = new ViewModelProvider(this).get(ReviewViewModel.class);
        reviewImages = new ArrayList<>();

        // Get landmark from intent
        Intent intent = getIntent();
        if (intent.hasExtra("landmark")) {
            landmark = intent.getParcelableExtra("landmark");
        }
        
        // Check if this is edit mode
        if (intent.hasExtra("review") && intent.getBooleanExtra("isEdit", false)) {
            isEditMode = true;
            editingReview = intent.getParcelableExtra("review");
        }

        if (landmark == null && !isEditMode) {
            Toast.makeText(this, "خطأ: لم يتم تحديد المعلم", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupToolbar();
        setupRecyclerView();
        setupClickListeners();

        if (isEditMode && editingReview != null) {
            populateFields();
        } else if (landmark != null) {
            binding.textLandmarkName.setText("تقييم: " + landmark.getName());
        }
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(isEditMode ? "تعديل التقييم" : "إضافة تقييم جديد");
        }
    }

    private void setupRecyclerView() {
        imageAdapter = new ReviewImageAdapter(reviewImages, new ReviewImageAdapter.OnImageClickListener() {
            @Override
            public void onDeleteClick(ReviewImage image) {
                showDeleteImageDialog(image);
            }

            @Override
            public void onEditClick(ReviewImage image) {
                showEditImageDialog(image);
            }
        });

        binding.recyclerViewImages.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewImages.setAdapter(imageAdapter);
    }

    private void setupClickListeners() {
        binding.btnAddImage.setOnClickListener(v -> showAddImageDialog());
        binding.btnSaveReview.setOnClickListener(v -> saveReview());
    }

    private void populateFields() {
        if (editingReview != null) {
            binding.editTextReviewerName.setText(editingReview.getReviewerName());
            binding.editTextReviewerEmail.setText(editingReview.getReviewerEmail());
            binding.editTextTitle.setText(editingReview.getTitle());
            binding.editTextDescription.setText(editingReview.getDescription());
            binding.ratingBar.setRating(editingReview.getRating());
            
            if (editingReview.getImages() != null) {
                reviewImages.clear();
                reviewImages.addAll(editingReview.getImages());
                imageAdapter.notifyDataSetChanged();
            }
            
            // Update landmark name
            if (landmark != null) {
                binding.textLandmarkName.setText("تعديل تقييم: " + landmark.getName());
            }
        }
    }

    private void showAddImageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("إضافة صورة جديدة");
        
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_image, null);
        builder.setView(dialogView);
        
        builder.setPositiveButton("إضافة", (dialog, which) -> {
            // Get input from dialog
            String imageUrl = ((com.google.android.material.textfield.TextInputEditText) 
                dialogView.findViewById(R.id.edit_text_image_url)).getText().toString().trim();
            String imageName = ((com.google.android.material.textfield.TextInputEditText) 
                dialogView.findViewById(R.id.edit_text_image_name)).getText().toString().trim();
            String imageDescription = ((com.google.android.material.textfield.TextInputEditText) 
                dialogView.findViewById(R.id.edit_text_image_description)).getText().toString().trim();
            
            if (!TextUtils.isEmpty(imageUrl) && !TextUtils.isEmpty(imageName)) {
                ReviewImage newImage = new ReviewImage(imageUrl, imageName, imageDescription);
                reviewImages.add(newImage);
                imageAdapter.addImage(newImage); // Use the new method instead of notifyItemInserted
                Toast.makeText(this, "تم إضافة الصورة بنجاح", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "يرجى ملء رابط الصورة واسمها على الأقل", Toast.LENGTH_SHORT).show();
            }
        });
        
        builder.setNegativeButton("إلغاء", null);
        builder.show();
    }

    private void showEditImageDialog(ReviewImage image) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("تعديل الصورة");
        
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_image, null);
        builder.setView(dialogView);
        
        // Populate current values
        ((com.google.android.material.textfield.TextInputEditText) 
            dialogView.findViewById(R.id.edit_text_image_url)).setText(image.getImageUrl());
        ((com.google.android.material.textfield.TextInputEditText) 
            dialogView.findViewById(R.id.edit_text_image_name)).setText(image.getImageName());
        ((com.google.android.material.textfield.TextInputEditText) 
            dialogView.findViewById(R.id.edit_text_image_description)).setText(image.getDescription());
        
        builder.setPositiveButton("حفظ", (dialog, which) -> {
            String imageUrl = ((com.google.android.material.textfield.TextInputEditText) 
                dialogView.findViewById(R.id.edit_text_image_url)).getText().toString().trim();
            String imageName = ((com.google.android.material.textfield.TextInputEditText) 
                dialogView.findViewById(R.id.edit_text_image_name)).getText().toString().trim();
            String imageDescription = ((com.google.android.material.textfield.TextInputEditText) 
                dialogView.findViewById(R.id.edit_text_image_description)).getText().toString().trim();
            
            if (!TextUtils.isEmpty(imageUrl) && !TextUtils.isEmpty(imageName)) {
                image.setImageUrl(imageUrl);
                image.setImageName(imageName);
                image.setDescription(imageDescription);
                
                int position = reviewImages.indexOf(image);
                if (position != -1) {
                    imageAdapter.notifyItemChanged(position);
                }
            } else {
                Toast.makeText(this, "يرجى ملء رابط الصورة واسمها على الأقل", Toast.LENGTH_SHORT).show();
            }
        });
        
        builder.setNegativeButton("إلغاء", null);
        builder.show();
    }

    private void showDeleteImageDialog(ReviewImage image) {
        new AlertDialog.Builder(this)
                .setTitle("حذف الصورة")
                .setMessage("هل أنت متأكد من حذف هذه الصورة؟")
                .setPositiveButton("حذف", (dialog, which) -> {
                    int position = reviewImages.indexOf(image);
                    if (position != -1) {
                        reviewImages.remove(position);
                        imageAdapter.removeImage(position); // Use the new method
                        Toast.makeText(this, "تم حذف الصورة بنجاح", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("إلغاء", null)
                .show();
    }

    private void saveReview() {
        String reviewerName = binding.editTextReviewerName.getText().toString().trim();
        String reviewerEmail = binding.editTextReviewerEmail.getText().toString().trim();
        String title = binding.editTextTitle.getText().toString().trim();
        String description = binding.editTextDescription.getText().toString().trim();
        float rating = binding.ratingBar.getRating();

        // Validate input
        if (TextUtils.isEmpty(reviewerName)) {
            binding.editTextReviewerName.setError("يرجى إدخال اسم المراجع");
            return;
        }

        if (TextUtils.isEmpty(reviewerEmail)) {
            binding.editTextReviewerEmail.setError("يرجى إدخال البريد الإلكتروني");
            return;
        }

        if (TextUtils.isEmpty(title)) {
            binding.editTextTitle.setError("يرجى إدخال عنوان التقييم");
            return;
        }

        if (TextUtils.isEmpty(description)) {
            binding.editTextDescription.setError("يرجى إدخال وصف التقييم");
            return;
        }

        if (rating < 1) {
            Toast.makeText(this, "يرجى إضافة تقييم (نجمة واحدة على الأقل)", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnSaveReview.setEnabled(false);

        Review review;
        if (isEditMode && editingReview != null) {
            review = editingReview;
            review.setReviewerName(reviewerName);
            review.setReviewerEmail(reviewerEmail);
            review.setTitle(title);
            review.setDescription(description);
            review.setRating(rating);
            review.setImages(new ArrayList<>(reviewImages));
        } else {
            review = new Review(landmark.getId(), reviewerName, reviewerEmail, rating, title, description);
            review.setImages(new ArrayList<>(reviewImages));
        }

        if (isEditMode) {
            reviewViewModel.updateReview(review).observe(this, resource -> {
                handleSaveResult(resource);
            });
        } else {
            reviewViewModel.addReview(review).observe(this, resource -> {
                handleSaveResult(resource);
            });
        }
    }

    private void handleSaveResult(Resource<Void> resource) {
        if (resource != null) {
            switch (resource.getStatus()) {
                case LOADING:
                    // Already showing loading
                    break;
                case SUCCESS:
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, isEditMode ? "تم تحديث التقييم بنجاح" : "تم إضافة التقييم بنجاح", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnSaveReview.setEnabled(true);
                    String errorMessage = resource.getMessage();
                    Toast.makeText(this, "خطأ: " + (errorMessage != null ? errorMessage : "خطأ غير معروف"), Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
