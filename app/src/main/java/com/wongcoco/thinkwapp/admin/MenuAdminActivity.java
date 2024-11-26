package com.wongcoco.thinkwapp.admin;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import com.wongcoco.thinkwapp.R;

public class MenuAdminActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_admin);

        // Initialize the views
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        // Set up the toolbar
        setSupportActionBar(toolbar);

        // Set the default fragment when the activity is first created
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AdminDashboardFragment())  // Replace with your default fragment
                    .commit();
            // Mark the Dashboard menu item as selected
            navigationView.setCheckedItem(R.id.nav_dashboard);  // This will select the Dashboard item in the navigation drawer
        }

        // Set up the navigation item click listener
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment = null;

                // Use if-else instead of switch-case
                if (menuItem.getItemId() == R.id.nav_dashboard) {
                    selectedFragment = new AdminDashboardFragment();  // Replace with your HomeFragment
                } else if (menuItem.getItemId() == R.id.nav_uploadAdmin) {
                    selectedFragment = new UploadGalleryFragment();  // Replace with your UploadFragment
                } else if (menuItem.getItemId() == R.id.nav_logout) {
                    // Handle logout logic here
                }

                // Replace the fragment
                if (selectedFragment != null) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, selectedFragment);
                    transaction.addToBackStack(null);  // Optionally add the transaction to the back stack
                    transaction.commit();
                }

                // Close the drawer after an item is selected
                drawerLayout.closeDrawer(GravityCompat.START);

                return true;
            }
        });
    }
}
