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

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private AuthManager authManager;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
            Log.d("LoginActivity", "Using Web Client ID: " + webClientId);
            
            // Verify the Web Client ID is not empty or placeholder
            if (webClientId == null || webClientId.isEmpty() || webClientId.contains("REPLACE_WITH_WEB_CLIENT_ID")) {
                Log.e("LoginActivity", "Invalid Web Client ID: " + webClientId);
                Toast.makeText(this, "Google Sign-In not properly configured. Please check Firebase Console settings.", Toast.LENGTH_LONG).show();
                return;
            }
            
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(webClientId)
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            
            Log.d("LoginActivity", "Firebase and Google Sign-In initialized successfully");
        } catch (Exception e) {
            Log.e("LoginActivity", "Firebase initialization failed", e);
            Toast.makeText(this, "Firebase initialization failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        // Setup UI
        setupUI();
    }

    private void setupUI() {
        // Login button
        findViewById(R.id.login_button).setOnClickListener(v -> loginUser());
        
        // Google Sign-In button
        findViewById(R.id.google_sign_in_button).setOnClickListener(v -> signInWithGoogle());
        
        // Create account link
        findViewById(R.id.create_account_link).setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
        
        // Setup toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
    }

    private void loginUser() {
        String email = ((android.widget.EditText) findViewById(R.id.login_email)).getText().toString().trim();
        String password = ((android.widget.EditText) findViewById(R.id.login_password)).getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            ((android.widget.EditText) findViewById(R.id.login_email)).setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            ((android.widget.EditText) findViewById(R.id.login_password)).setError("Password is required");
            return;
        }

        // Check if Firebase Auth is available
        if (mAuth == null) {
            Toast.makeText(this, "Firebase Auth not initialized. Please restart the app.", Toast.LENGTH_LONG).show();
            return;
        }

        findViewById(R.id.login_progress_bar).setVisibility(View.VISIBLE);

        try {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        findViewById(R.id.login_progress_bar).setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Log.d("LoginActivity", "Firebase login successful");
                            
                            // Save auth state
                            authManager.saveAuthState(mAuth.getCurrentUser());
                            
                            Toast.makeText(this, "تم تسجيل الدخول بنجاح!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Exception exception = task.getException();
                            Log.e("LoginActivity", "Firebase login failed", exception);
                            
                            // Check if it's a configuration error
                            if (exception != null && exception.getMessage() != null && 
                                exception.getMessage().contains("CONFIGURATION_NOT_FOUND")) {
                                Log.e("LoginActivity", "Firebase configuration not found. Please check Firebase Console settings.");
                                Toast.makeText(this, "Firebase configuration error. Please check Firebase Console.", Toast.LENGTH_LONG).show();
                                return;
                            }
                            
                            // Handle other Firebase errors
                            if (exception instanceof FirebaseAuthException) {
                                FirebaseAuthException authException = (FirebaseAuthException) exception;
                                String errorCode = authException.getErrorCode();
                                
                                switch (errorCode) {
                                    case "ERROR_INVALID_EMAIL":
                                        Toast.makeText(this, "Invalid email address", Toast.LENGTH_LONG).show();
                                        break;
                                    case "ERROR_USER_NOT_FOUND":
                                        Toast.makeText(this, "No account found with this email", Toast.LENGTH_LONG).show();
                                        break;
                                    case "ERROR_WRONG_PASSWORD":
                                        Toast.makeText(this, "Incorrect password", Toast.LENGTH_LONG).show();
                                        break;
                                    case "ERROR_NETWORK_REQUEST_FAILED":
                                        Toast.makeText(this, "Network error. Please check your connection", Toast.LENGTH_LONG).show();
                                        break;
                                    default:
                                        Toast.makeText(this, "Login failed: " + exception.getMessage(), Toast.LENGTH_LONG).show();
                                        break;
                                }
                            } else {
                                Toast.makeText(this, "Login failed: " + (exception != null ? exception.getMessage() : "Unknown error"), 
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } catch (Exception e) {
            findViewById(R.id.login_progress_bar).setVisibility(View.GONE);
            Log.e("LoginActivity", "Login exception", e);
            Toast.makeText(this, "Login failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
                Log.d("LoginActivity", "Google sign in successful, proceeding with Firebase auth");
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.e("LoginActivity", "Google sign in failed", e);
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
                        Log.d("LoginActivity", "Google sign in successful");
                        
                        // Save auth state
                        authManager.saveAuthState(mAuth.getCurrentUser());
                        
                        Toast.makeText(this, "تم تسجيل الدخول بنجاح!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.e("LoginActivity", "Google sign in failed", task.getException());
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