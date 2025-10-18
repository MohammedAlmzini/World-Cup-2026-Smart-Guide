package com.ahmmedalmzini783.wcguide.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.ui.main.MainActivity;
import com.ahmmedalmzini783.wcguide.ui.profile.ProfileActivity;
import com.ahmmedalmzini783.wcguide.util.AuthManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.GoogleAuthProvider;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private AuthManager authManager;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase
        try {
            // Check if Firebase is already initialized
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this);
            }
            mAuth = FirebaseAuth.getInstance();
            authManager = AuthManager.getInstance(this);
            
            // Set Firebase Auth settings
            mAuth.setLanguageCode("en");
            
            // Configure Google Sign-In
            String webClientId = getString(R.string.default_web_client_id);
            Log.d("RegisterActivity", "Using Web Client ID: " + webClientId);
            
            // Verify the Web Client ID is not empty or placeholder
            if (webClientId == null || webClientId.isEmpty() || webClientId.contains("REPLACE_WITH_WEB_CLIENT_ID")) {
                Log.e("RegisterActivity", "Invalid Web Client ID: " + webClientId);
                Toast.makeText(this, "Google Sign-In not properly configured. Please check Firebase Console settings.", Toast.LENGTH_LONG).show();
                return;
            }
            
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(webClientId)
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            
            Log.d("RegisterActivity", "Firebase and Google Sign-In initialized successfully");
        } catch (Exception e) {
            Log.e("RegisterActivity", "Firebase initialization failed", e);
            Toast.makeText(this, "Firebase initialization failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        // Setup UI
        setupUI();
    }

    private void setupUI() {
        // Register button
        findViewById(R.id.register_button).setOnClickListener(v -> registerUser());
        
        // Sign in link
        findViewById(R.id.sign_in_link).setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
        
        // Setup toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
    }

    private void registerUser() {
        String email = ((android.widget.EditText) findViewById(R.id.register_email)).getText().toString().trim();
        String password = ((android.widget.EditText) findViewById(R.id.register_password)).getText().toString().trim();
        String confirmPassword = ((android.widget.EditText) findViewById(R.id.register_confirm_password)).getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            ((android.widget.EditText) findViewById(R.id.register_email)).setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            ((android.widget.EditText) findViewById(R.id.register_password)).setError("Password is required");
            return;
        }

        if (password.length() < 6) {
            ((android.widget.EditText) findViewById(R.id.register_password)).setError("Password must be at least 6 characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            ((android.widget.EditText) findViewById(R.id.register_confirm_password)).setError("Passwords do not match");
            return;
        }

        // Check if Firebase Auth is available
        if (mAuth == null) {
            Toast.makeText(this, "Firebase Auth not initialized. Please restart the app.", Toast.LENGTH_LONG).show();
            return;
        }

        findViewById(R.id.register_progress_bar).setVisibility(View.VISIBLE);

        // Try Firebase first, then fallback to local registration
        try {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                        findViewById(R.id.register_progress_bar).setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                            Log.d("RegisterActivity", "Firebase registration successful");
                        
                        // Save auth state
                        authManager.saveAuthState(mAuth.getCurrentUser());
                        
                        Toast.makeText(this, "تم إنشاء الحساب بنجاح!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                            Exception exception = task.getException();
                            Log.e("RegisterActivity", "Firebase registration failed", exception);
                            
                            // Check if it's a configuration error
                            if (exception != null && exception.getMessage() != null && 
                                exception.getMessage().contains("CONFIGURATION_NOT_FOUND")) {
                                Log.e("RegisterActivity", "Firebase configuration not found. Please check Firebase Console settings.");
                                Toast.makeText(this, "Firebase configuration error. Please check Firebase Console.", Toast.LENGTH_LONG).show();
                                return;
                            }
                            
                            // Handle other Firebase errors
                            String errorMessage = "Registration failed";
                            if (exception instanceof FirebaseAuthException) {
                                FirebaseAuthException authException = (FirebaseAuthException) exception;
                                String errorCode = authException.getErrorCode();
                                
                                switch (errorCode) {
                                    case "ERROR_INVALID_EMAIL":
                                        errorMessage = "Invalid email address";
                                        break;
                                    case "ERROR_EMAIL_ALREADY_IN_USE":
                                        errorMessage = "Email already in use";
                                        break;
                                    case "ERROR_WEAK_PASSWORD":
                                        errorMessage = "Password is too weak";
                                        break;
                                    case "ERROR_NETWORK_REQUEST_FAILED":
                                        errorMessage = "Network error. Please check your connection";
                                        break;
                                    default:
                                        errorMessage = "Registration failed: " + exception.getMessage();
                                        break;
                                }
                            } else if (exception != null) {
                                errorMessage = "Registration failed: " + exception.getMessage();
                            }
                            
                            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });
        } catch (Exception e) {
            findViewById(R.id.register_progress_bar).setVisibility(View.GONE);
            Log.e("RegisterActivity", "Registration exception", e);
            Toast.makeText(this, "Registration failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("RegisterActivity", "Google sign in successful, proceeding with Firebase auth");
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.e("RegisterActivity", "Google sign in failed", e);
                String errorMessage = "Google sign in failed";
                if (e.getStatusCode() == 10) {
                    errorMessage = "Google Sign-In configuration error. Please check Firebase Console settings.";
                } else {
                    errorMessage = "Google sign in failed: " + e.getMessage();
                }
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }
        }
    }
    
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("RegisterActivity", "Google sign in successful");
                        
                        // Save auth state
                        authManager.saveAuthState(mAuth.getCurrentUser());
                        
                        Toast.makeText(this, "تم تسجيل الدخول بنجاح!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.e("RegisterActivity", "Google sign in failed", task.getException());
                        Toast.makeText(this, "Google sign in failed: " + task.getException().getMessage(), 
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}