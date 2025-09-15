package com.ahmmedalmzini783.wcguide.ui.main;

import android.content.Intent;
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
import com.ahmmedalmzini783.wcguide.ui.admin.AdminActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        
        // Wait for the layout to be inflated before setting up navigation
        binding.getRoot().post(() -> setupNavigation());
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.app_name));
        }
    }

    private void setupNavigation() {
        try {
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
        } catch (Exception e) {
            // Log error and continue without navigation setup
            android.util.Log.e("MainActivity", "Navigation setup failed", e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_admin) {
            openAdminPanel();
            return true;
        }

        if (navController != null) {
            return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    private void openAdminPanel() {
        // Simple password check (in production, use proper authentication)
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        
        final android.widget.EditText input = new android.widget.EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setHint("Admin Password");
        
        builder.setTitle("Admin Access")
               .setMessage("Enter admin password to access control panel")
               .setView(input)
               .setPositiveButton("Login", (dialog, which) -> {
                   String password = input.getText().toString();
                   if ("admin2026".equals(password)) {
                       Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                       startActivity(intent);
                   } else {
                       android.widget.Toast.makeText(MainActivity.this, "Invalid password", android.widget.Toast.LENGTH_SHORT).show();
                   }
               })
               .setNegativeButton("Cancel", null)
               .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (navController != null) {
            return NavigationUI.navigateUp(navController, new AppBarConfiguration.Builder().build())
                    || super.onSupportNavigateUp();
        }
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}