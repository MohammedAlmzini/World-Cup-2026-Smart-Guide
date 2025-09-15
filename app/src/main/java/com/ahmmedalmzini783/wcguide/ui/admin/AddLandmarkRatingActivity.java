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
import com.ahmmedalmzini783.wcguide.data.model.LandmarkRating;
import com.ahmmedalmzini783.wcguide.data.model.ReviewImage;
import com.ahmmedalmzini783.wcguide.databinding.ActivityAddLandmarkRatingBinding;
import com.ahmmedalmzini783.wcguide.ui.viewmodel.LandmarkRatingViewModel;
import com.ahmmedalmzini783.wcguide.util.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddLandmarkRatingActivity extends AppCompatActivity {

    private ActivityAddLandmarkRatingBinding binding;
    private LandmarkRatingViewModel viewModel;
    private ReviewImageAdapter imageAdapter;
    private List<ReviewImage> ratingImages;
    private LandmarkRating editingRating;
    private String landmarkId;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddLandmarkRatingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupRecyclerView();
        setupViewModel();
        setupClickListeners();
        loadIntentData();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("تقييم المعلم السياحي");
        }
    }

    private void setupRecyclerView() {
        ratingImages = new ArrayList<>();
        imageAdapter = new ReviewImageAdapter(ratingImages, new ReviewImageAdapter.OnImageClickListener() {
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

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(LandmarkRatingViewModel.class);
    }

    private void setupClickListeners() {
        binding.btnAddImage.setOnClickListener(v -> showAddImageDialog());
        binding.btnSave.setOnClickListener(v -> saveLandmarkRating());
        binding.btnCancel.setOnClickListener(v -> finish());
    }

    private void loadIntentData() {
        Intent intent = getIntent();
        landmarkId = intent.getStringExtra("landmarkId");
        String landmarkName = intent.getStringExtra("landmarkName");
        editingRating = intent.getParcelableExtra("landmarkRating");

        if (editingRating != null) {
            isEditMode = true;
            getSupportActionBar().setTitle("تعديل تقييم المعلم");
            populateFields(editingRating);
        } else if (!TextUtils.isEmpty(landmarkName)) {
            binding.editTextLandmarkName.setText(landmarkName);
        }
    }

    private void populateFields(LandmarkRating rating) {
        binding.editTextLandmarkName.setText(rating.getLandmarkName());
        binding.ratingBarOverall.setRating(rating.getOverallRating());
        binding.editTextDescription.setText(rating.getDescription());
        binding.editTextHighlights.setText(rating.getHighlights());
        binding.editTextFacilities.setText(rating.getFacilities());
        binding.editTextAccessibility.setText(rating.getAccessibility());
        binding.editTextBestTime.setText(rating.getBestTimeToVisit());
        binding.editTextDuration.setText(rating.getDuration());

        // Populate pros and cons
        if (rating.getPros() != null) {
            binding.editTextPros.setText(String.join("\n", rating.getPros()));
        }
        if (rating.getCons() != null) {
            binding.editTextCons.setText(String.join("\n", rating.getCons()));
        }

        // Populate images
        if (rating.getImages() != null) {
            ratingImages.clear();
            ratingImages.addAll(rating.getImages());
            imageAdapter.updateImages(ratingImages);
        }
    }

    private void showAddImageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("إضافة صورة جديدة");
        
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_image, null);
        builder.setView(dialogView);
        
        builder.setPositiveButton("إضافة", (dialog, which) -> {
            String imageUrl = ((com.google.android.material.textfield.TextInputEditText) 
                dialogView.findViewById(R.id.edit_text_image_url)).getText().toString().trim();
            String imageName = ((com.google.android.material.textfield.TextInputEditText) 
                dialogView.findViewById(R.id.edit_text_image_name)).getText().toString().trim();
            String imageDescription = ((com.google.android.material.textfield.TextInputEditText) 
                dialogView.findViewById(R.id.edit_text_image_description)).getText().toString().trim();
            
            if (!TextUtils.isEmpty(imageUrl) && !TextUtils.isEmpty(imageName)) {
                ReviewImage newImage = new ReviewImage(imageUrl, imageName, imageDescription);
                ratingImages.add(newImage);
                imageAdapter.addImage(newImage);
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
                
                int position = ratingImages.indexOf(image);
                if (position != -1) {
                    imageAdapter.notifyItemChanged(position);
                }
                Toast.makeText(this, "تم تحديث الصورة بنجاح", Toast.LENGTH_SHORT).show();
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
                    int position = ratingImages.indexOf(image);
                    if (position != -1) {
                        ratingImages.remove(position);
                        imageAdapter.removeImage(position);
                        Toast.makeText(this, "تم حذف الصورة بنجاح", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("إلغاء", null)
                .show();
    }

    private void saveLandmarkRating() {
        String landmarkName = binding.editTextLandmarkName.getText().toString().trim();
        String description = binding.editTextDescription.getText().toString().trim();
        float overallRating = binding.ratingBarOverall.getRating();

        // Validate input
        if (TextUtils.isEmpty(landmarkName)) {
            binding.editTextLandmarkName.setError("يرجى إدخال اسم المعلم السياحي");
            return;
        }

        if (TextUtils.isEmpty(description)) {
            binding.editTextDescription.setError("يرجى إدخال وصف للمعلم");
            return;
        }

        if (overallRating < 1) {
            Toast.makeText(this, "يرجى إضافة تقييم (نجمة واحدة على الأقل)", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnSave.setEnabled(false);

        LandmarkRating rating;
        if (isEditMode && editingRating != null) {
            rating = editingRating;
        } else {
            rating = new LandmarkRating();
            rating.setLandmarkId(landmarkId != null ? landmarkId : "unknown");
        }

        // Set basic info
        rating.setLandmarkName(landmarkName);
        rating.setOverallRating(overallRating);
        rating.setDescription(description);
        rating.setHighlights(binding.editTextHighlights.getText().toString().trim());
        rating.setFacilities(binding.editTextFacilities.getText().toString().trim());
        rating.setAccessibility(binding.editTextAccessibility.getText().toString().trim());
        rating.setBestTimeToVisit(binding.editTextBestTime.getText().toString().trim());
        rating.setDuration(binding.editTextDuration.getText().toString().trim());

        // Parse pros and cons
        String prosText = binding.editTextPros.getText().toString().trim();
        if (!TextUtils.isEmpty(prosText)) {
            rating.setPros(Arrays.asList(prosText.split("\n")));
        }

        String consText = binding.editTextCons.getText().toString().trim();
        if (!TextUtils.isEmpty(consText)) {
            rating.setCons(Arrays.asList(consText.split("\n")));
        }

        // Set images
        rating.setImages(new ArrayList<>(ratingImages));

        // Save to database
        if (isEditMode) {
            viewModel.updateLandmarkRating(rating).observe(this, this::handleSaveResult);
        } else {
            viewModel.addOrUpdateLandmarkRating(rating).observe(this, this::handleSaveResult);
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
                    binding.btnSave.setEnabled(true);
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
