package com.ahmmedalmzini783.wcguide.ui.main;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        // Setup Navigation
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        // Setup Bottom Navigation
        BottomNavigationView bottomNav = binding.bottomNavigation;
        NavigationUI.setupWithNavController(bottomNav, navController);

        // Setup AppBar with Navigation
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_events, R.id.navigation_chatbot)
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // Handle deep links
        handleIntent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            // Handle search action
            return true;
        } else if (id == R.id.action_notifications) {
            // Handle notifications action
            return true;
        } else if (id == R.id.action_profile) {
            // Handle profile action
            return true;
        } else if (id == R.id.action_settings) {
            // Handle settings action
            return true;
        }

        return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, new AppBarConfiguration.Builder().build())
                || super.onSupportNavigateUp();
    }

    private void handleIntent() {
        // Handle deep links from notifications or external sources
        // Example: wcguide://event/evt_123
        // TODO: Implement deep link handling
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}