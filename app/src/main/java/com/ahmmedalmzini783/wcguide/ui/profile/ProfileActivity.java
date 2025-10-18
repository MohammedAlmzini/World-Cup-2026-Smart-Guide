package com.ahmmedalmzini783.wcguide.ui.profile;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.material.card.MaterialCardView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.ui.auth.LoginActivity;
import com.ahmmedalmzini783.wcguide.ui.auth.RegisterActivity;
import com.ahmmedalmzini783.wcguide.ui.settings.SettingsActivity;
import com.ahmmedalmzini783.wcguide.util.ImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int REQUEST_PERMISSION_CAMERA = 100;
    private static final int REQUEST_PERMISSION_STORAGE = 101;

    private Toolbar toolbar;
    private ImageView profileAvatar;
    private TextView profileName, profileEmail;
    private MaterialCardView guestActionsCard;
    private Button btnEditProfile, btnChangePassword, btnSignOut;
    private Button btnSignIn, btnSignUp;
    
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        
        initViews();
        setupToolbar();
        setupClickListeners();
        checkUserStatus();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        profileAvatar = findViewById(R.id.profile_avatar);
        profileName = findViewById(R.id.profile_name);
        profileEmail = findViewById(R.id.profile_email);
        guestActionsCard = findViewById(R.id.guest_actions_card);
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnChangePassword = findViewById(R.id.btn_change_password);
        btnSignOut = findViewById(R.id.btn_sign_out);
        btnSignIn = findViewById(R.id.btn_sign_in);
        btnSignUp = findViewById(R.id.btn_sign_up);

        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        
        // Initialize progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("جاري رفع الصورة...");
        progressDialog.setCancelable(false);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupClickListeners() {
        // Guest user actions
        btnSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });

        btnSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });

        // Authenticated user actions
        btnEditProfile.setOnClickListener(v -> {
            showImageSelectionDialog();
        });
        
        // Profile avatar click listener
        profileAvatar.setOnClickListener(v -> {
            if (currentUser != null) {
                showImageSelectionDialog();
            }
        });

        btnChangePassword.setOnClickListener(v -> {
            // TODO: Implement change password functionality
        });

        btnSignOut.setOnClickListener(v -> {
            mAuth.signOut();
            checkUserStatus();
        });
    }

    private void checkUserStatus() {
        currentUser = mAuth.getCurrentUser();
        
        if (currentUser != null) {
            // User is signed in
            profileName.setText(currentUser.getDisplayName() != null ? 
                currentUser.getDisplayName() : getString(R.string.profile_authenticated_name));
            profileEmail.setText(currentUser.getEmail());
            profileEmail.setVisibility(View.VISIBLE);
            
            // Load user profile image if available
            if (currentUser.getPhotoUrl() != null) {
                ImageLoader.loadCircularImage(this, currentUser.getPhotoUrl().toString(), profileAvatar, R.drawable.ic_user_default);
            } else {
                profileAvatar.setImageResource(R.drawable.ic_user_default);
            }
            
            // Show authenticated user actions
            btnEditProfile.setVisibility(View.VISIBLE);
            btnChangePassword.setVisibility(View.VISIBLE);
            btnSignOut.setVisibility(View.VISIBLE);
            guestActionsCard.setVisibility(View.GONE);
        } else {
            // User is not signed in
            profileName.setText(getString(R.string.profile_guest_name));
            profileEmail.setText("guest@wcguide2026.com");
            profileEmail.setVisibility(View.GONE);
            profileAvatar.setImageResource(R.drawable.ic_user_default);
            
            // Show guest user actions
            btnEditProfile.setVisibility(View.GONE);
            btnChangePassword.setVisibility(View.GONE);
            btnSignOut.setVisibility(View.GONE);
            guestActionsCard.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void showImageSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("اختر صورة الملف الشخصي");
        builder.setItems(new String[]{"التقاط صورة", "اختيار من المعرض"}, (dialog, which) -> {
            switch (which) {
                case 0:
                    captureImage();
                    break;
                case 1:
                    pickImageFromGallery();
                    break;
            }
        });
        builder.show();
    }

    private void captureImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void pickImageFromGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_STORAGE);
            return;
        }

        Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageIntent.setType("image/*");
        startActivityForResult(pickImageIntent, REQUEST_IMAGE_PICK);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case REQUEST_PERMISSION_CAMERA:
                    captureImage();
                    break;
                case REQUEST_PERMISSION_STORAGE:
                    pickImageFromGallery();
                    break;
            }
        } else {
            Toast.makeText(this, "الإذن مطلوب لرفع الصور", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK) {
            Uri imageUri = null;
            
            if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                imageUri = data.getData();
                if (imageUri == null) {
                    // For some devices, the image data is in the intent extras
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        android.graphics.Bitmap imageBitmap = (android.graphics.Bitmap) extras.get("data");
                        if (imageBitmap != null) {
                            // Save bitmap to temporary file and get URI
                            imageUri = saveBitmapToTempFile(imageBitmap);
                        }
                    }
                }
            } else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                imageUri = data.getData();
            }
            
            if (imageUri != null) {
                uploadImageToFirebase(imageUri);
            }
        }
    }

    private Uri saveBitmapToTempFile(android.graphics.Bitmap bitmap) {
        try {
            java.io.File tempFile = java.io.File.createTempFile("profile_image", ".jpg", getCacheDir());
            java.io.FileOutputStream out = new java.io.FileOutputStream(tempFile);
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, out);
            out.close();
            return Uri.fromFile(tempFile);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        if (currentUser == null) {
            Toast.makeText(this, "يجب تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        // Create a reference to the user's profile image
        String userId = currentUser.getUid();
        StorageReference profileImageRef = storageRef.child("profile_images").child(userId + ".jpg");

        // Upload the image
        UploadTask uploadTask = profileImageRef.putFile(imageUri);
        
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Get the download URL
            profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Update user profile with the new photo URL
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(uri)
                        .build();

                currentUser.updateProfile(profileUpdates)
                        .addOnCompleteListener(task -> {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "تم تحديث صورة الملف الشخصي بنجاح", Toast.LENGTH_SHORT).show();
                                // Update the UI with the new image
                                ImageLoader.loadCircularImage(this, uri.toString(), profileAvatar, R.drawable.ic_user_default);
                            } else {
                                Toast.makeText(this, "فشل في تحديث صورة الملف الشخصي", Toast.LENGTH_SHORT).show();
                            }
                        });
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(this, "فشل في الحصول على رابط الصورة", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(this, "فشل في رفع الصورة: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}