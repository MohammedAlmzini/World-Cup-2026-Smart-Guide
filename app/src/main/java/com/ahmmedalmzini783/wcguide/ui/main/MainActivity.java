package com.ahmmedalmzini783.wcguide.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

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
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupToolbar();
        setupDrawer();
        
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
            
            if (id == R.id.nav_home) {
                navigateToHome();
            } else if (id == R.id.nav_events) {
                navigateToEvents();
            } else if (id == R.id.nav_teams) {
                navigateToTeams();
            } else if (id == R.id.nav_news) {
                navigateToNews();
            } else if (id == R.id.nav_settings) {
                navigateToSettings();
            } else if (id == R.id.nav_about) {
                navigateToAbout();
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

    private void navigateToHome() {
        navController.navigate(R.id.nav_home);
    }

    private void navigateToEvents() {
        navController.navigate(R.id.nav_events);
    }

    private void navigateToTeams() {
        // TODO: Implement teams navigation
    }

    private void navigateToNews() {
        // TODO: Implement news navigation
    }

    private void navigateToSettings() {
        // TODO: Implement settings navigation
    }

    private void navigateToAbout() {
        // TODO: Implement about navigation
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
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
}