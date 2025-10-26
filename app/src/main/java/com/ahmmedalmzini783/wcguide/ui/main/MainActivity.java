package com.ahmmedalmzini783.wcguide.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.ui.auth.LoginActivity;
import com.ahmmedalmzini783.wcguide.ui.auth.RegisterActivity;
import com.ahmmedalmzini783.wcguide.ui.profile.ProfileActivity;
import com.ahmmedalmzini783.wcguide.ui.logos.LogosActivity;
import com.ahmmedalmzini783.wcguide.ui.googleplaces.GooglePlacesActivity;
import com.ahmmedalmzini783.wcguide.ui.home.HomeFragmentWithTabs;
import com.ahmmedalmzini783.wcguide.util.AuthManager;
import com.ahmmedalmzini783.wcguide.util.ImageLoader;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private AuthManager authManager;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupToolbar();
        setupDrawer();
        setupAuth();
        
        // Wait for the layout to be inflated before setting up navigation
        findViewById(android.R.id.content).post(() -> setupNavigation());
    }

    private void setupToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.app_name));
        }
    }

    private void setupDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        authManager = AuthManager.getInstance(this);
        
        // Setup drawer toggle
        drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, findViewById(R.id.toolbar),
                R.string.drawer_menu, R.string.drawer_menu);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        
        // Ensure navigation icon is white
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_modern);
        }
        
        // Setup navigation view
        setupNavigationView();
    }

    private void setupNavigationView() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            
            if (id == R.id.nav_profile) {
                navigateToProfile();
            } else if (id == R.id.nav_contact_us) {
                navigateToContactUs();
            } else if (id == R.id.nav_about_us) {
                navigateToAboutUs();
            } else if (id == R.id.nav_privacy_policy) {
                navigateToPrivacyPolicy();
            } else if (id == R.id.nav_settings) {
                navigateToSettings();
            } else if (id == R.id.nav_terms_of_service) {
                navigateToTermsOfService();
            } else if (id == R.id.nav_about_app) {
                navigateToAboutApp();
            } else if (id == R.id.nav_events) {
                navigateToEvents();
            } else if (id == R.id.nav_news) {
                navigateToNews();
            } else if (id == R.id.nav_notifications) {
                navigateToNotifications();
            } else if (id == R.id.nav_google_places) {
                navigateToGooglePlaces();
            }
            
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
        
        // Setup drawer header buttons
        setupDrawerHeaderButtons();
    }
    
    private void setupDrawerHeaderButtons() {
        // Wait for the drawer header to be inflated
        navigationView.post(() -> {
            // Sign In button
            if (findViewById(R.id.drawer_header_sign_in_btn) != null) {
                findViewById(R.id.drawer_header_sign_in_btn).setOnClickListener(v -> {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.START);
                });
            }
            
            // Sign Up button
            if (findViewById(R.id.drawer_header_sign_up_btn) != null) {
                findViewById(R.id.drawer_header_sign_up_btn).setOnClickListener(v -> {
                    Intent intent = new Intent(this, RegisterActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.START);
                });
            }
            
            // Sign Out button (for authenticated users)
            if (findViewById(R.id.drawer_header_sign_out_btn) != null) {
                findViewById(R.id.drawer_header_sign_out_btn).setOnClickListener(v -> {
                    // TODO: Implement sign out functionality
                    drawerLayout.closeDrawer(GravityCompat.START);
                });
            }
        });
    }

    private void setupNavigation() {
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        
        // Setup bottom navigation only (remove ActionBar setup to avoid icon conflict)
        NavigationUI.setupWithNavController((com.google.android.material.bottomnavigation.BottomNavigationView) findViewById(R.id.bottom_navigation), navController);
    }

    private void navigateToProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    private void navigateToContactUs() {
        Intent intent = new Intent(this, com.ahmmedalmzini783.wcguide.ui.contact.ContactUsActivity.class);
        startActivity(intent);
    }

    private void navigateToAboutUs() {
        Intent intent = new Intent(this, com.ahmmedalmzini783.wcguide.ui.about.AboutUsActivity.class);
        startActivity(intent);
    }

    private void navigateToPrivacyPolicy() {
        Intent intent = new Intent(this, com.ahmmedalmzini783.wcguide.ui.privacy.PrivacyPolicyActivity.class);
        startActivity(intent);
    }

    private void navigateToSettings() {
        Intent intent = new Intent(this, com.ahmmedalmzini783.wcguide.ui.settings.SettingsActivity.class);
        startActivity(intent);
    }

    private void navigateToTermsOfService() {
        Intent intent = new Intent(this, com.ahmmedalmzini783.wcguide.ui.terms.TermsOfServiceActivity.class);
        startActivity(intent);
    }

    private void navigateToAboutApp() {
        Intent intent = new Intent(this, com.ahmmedalmzini783.wcguide.ui.about.AboutAppActivity.class);
        startActivity(intent);
    }

    private void navigateToEvents() {
        // TODO: Create EventsActivity or use existing activity
        android.widget.Toast.makeText(this, "Events page under development", android.widget.Toast.LENGTH_SHORT).show();
    }

    private void navigateToNews() {
        Intent intent = new Intent(this, com.ahmmedalmzini783.wcguide.ui.news.NewsActivity.class);
        startActivity(intent);
    }

    private void navigateToNotifications() {
        Intent intent = new Intent(this, com.ahmmedalmzini783.wcguide.ui.notifications.NotificationsActivity.class);
        startActivity(intent);
    }

    private void navigateToGooglePlaces() {
        Intent intent = GooglePlacesActivity.createIntent(this);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        
        int id = item.getItemId();
        // Profile navigation is handled by toolbar profile avatar click
        
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    
    private void setupAuth() {
        mAuth = FirebaseAuth.getInstance();
        updateUserProfile();
    }
    
    private void updateUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        
        if (currentUser != null) {
            // User is signed in
            updateDrawerHeader(currentUser);
        } else {
            // User is not signed in
            updateDrawerHeader(null);
        }
    }
    
    private void updateDrawerHeader(FirebaseUser user) {
        if (navigationView != null) {
            View headerView = navigationView.getHeaderView(0);
            if (headerView != null) {
                de.hdodenhof.circleimageview.CircleImageView avatar = 
                    headerView.findViewById(R.id.drawer_header_avatar);
                android.widget.TextView userName = 
                    headerView.findViewById(R.id.drawer_header_user_name);
                android.widget.TextView userEmail = 
                    headerView.findViewById(R.id.drawer_header_user_email);
                android.widget.LinearLayout authButtons = 
                    headerView.findViewById(R.id.drawer_header_auth_buttons);
                android.widget.Button signOutBtn = 
                    headerView.findViewById(R.id.drawer_header_sign_out_btn);
                
                if (user != null) {
                    // Authenticated user
                    userName.setText(user.getDisplayName() != null ? 
                        user.getDisplayName() : getString(R.string.drawer_guest_user));
                    userEmail.setText(user.getEmail());
                    userEmail.setVisibility(android.view.View.VISIBLE);
                    
                    // Load user profile image in drawer
                    if (user.getPhotoUrl() != null && !user.getPhotoUrl().toString().isEmpty()) {
                        String photoUrl = user.getPhotoUrl().toString();
                        android.util.Log.d("MainActivity", "Loading user photo: " + photoUrl);
                        
                        // Use Glide directly for better control
                        com.bumptech.glide.Glide.with(this)
                            .load(photoUrl)
                            .placeholder(R.drawable.ic_user_default)
                            .error(R.drawable.ic_user_default)
                            .circleCrop()
                            .into(avatar);
                    } else {
                        android.util.Log.d("MainActivity", "No user photo URL, using default icon");
                        avatar.setImageResource(R.drawable.ic_user_default);
                    }
                    
                    // Show sign out button, hide auth buttons
                    authButtons.setVisibility(android.view.View.GONE);
                    signOutBtn.setVisibility(android.view.View.VISIBLE);
                } else {
                    // Guest user
                    userName.setText(getString(R.string.drawer_guest_user));
                    userEmail.setText("guest@wcguide2026.com");
                    userEmail.setVisibility(android.view.View.GONE);
                    avatar.setImageResource(R.drawable.ic_user_default);
                    
                    // Show auth buttons, hide sign out button
                    authButtons.setVisibility(android.view.View.VISIBLE);
                    signOutBtn.setVisibility(android.view.View.GONE);
                }
            }
        }
        
        // Update toolbar profile avatar
        updateToolbarProfileAvatar(user);
    }
    
    private void updateToolbarProfileAvatar(FirebaseUser user) {
        de.hdodenhof.circleimageview.CircleImageView toolbarAvatar = 
            findViewById(R.id.toolbar_profile_avatar);
        
        if (toolbarAvatar != null) {
            if (user != null && user.getPhotoUrl() != null && !user.getPhotoUrl().toString().isEmpty()) {
                // Load user profile image in toolbar
                String photoUrl = user.getPhotoUrl().toString();
                android.util.Log.d("MainActivity", "Loading toolbar photo: " + photoUrl);
                
                com.bumptech.glide.Glide.with(this)
                    .load(photoUrl)
                    .placeholder(R.drawable.ic_user_default)
                    .error(R.drawable.ic_user_default)
                    .circleCrop()
                    .into(toolbarAvatar);
            } else {
                // Show default icon in toolbar
                android.util.Log.d("MainActivity", "No toolbar photo URL, using default icon");
                toolbarAvatar.setImageResource(R.drawable.ic_user_default);
            }
            
            // Set click listener to open profile
            toolbarAvatar.setOnClickListener(v -> {
                navigateToProfile();
            });
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Update user profile when returning to the activity
        updateUserProfile();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Update profile when returning from ProfileActivity
        if (requestCode == 1001) { // ProfileActivity result code
            updateUserProfile();
        }
    }
}