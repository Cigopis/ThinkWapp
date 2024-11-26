package com.wongcoco.thinkwapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private BottomNavigationView bottomNav;
    private BottomAppBar bottomAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setNotchColor();

        fab = findViewById(R.id.fab);
        bottomAppBar = findViewById(R.id.bottomApp);
        bottomNav = findViewById(R.id.bottomNav);

        moveFabToCradle();
        setFabCradleRadius(30);

        // Set proper styling for BottomNavigationView and BottomAppBar
        bottomAppBar.setBackgroundTint(ColorStateList.valueOf(getResources().getColor(R.color.Primary)));
        bottomNav.setBackgroundColor(getResources().getColor(R.color.transparent));
        bottomNav.setItemIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        bottomNav.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));

        // Pilih menu "Home" secara otomatis saat aplikasi dibuka
        bottomNav.setSelectedItemId(R.id.nav_home); // Pilih menu "Home"
        updateFabIcon(R.drawable.baseline_home_filled_24); // Update ikon FAB ke "Home"
        moveFabToMenu(bottomNav.getMenu().findItem(R.id.nav_home)); // Sinkronisasi FAB dengan menu awal
        hideSelectedItem(bottomNav.getMenu().findItem(R.id.nav_home));

        new Handler().postDelayed(() -> {
            loadFragment(new AccountFragment());
            // Tampilkan fragment Home
            if (savedInstanceState == null) {
                loadFragment(new HomeFragment());
            }
        }, 100); // Delay 100ms untuk memastikan layout selesai di-render


        // Set listener untuk navigasi menu
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                // Navigasi menu dengan logika if-else
                if (item.getItemId() == R.id.nav_maps) {
                    selectedFragment = new MitraMapsFragment();
                    updateFabIcon(R.drawable.baseline_share_location_24);
                } else if (item.getItemId() == R.id.nav_home) {
                    selectedFragment = new HomeFragment();
                    updateFabIcon(R.drawable.baseline_home_filled_24);
                } else if (item.getItemId() == R.id.nav_account) {
                    selectedFragment = new AccountFragment();
                    updateFabIcon(R.drawable.baseline_person_24);
                    loadFragment(selectedFragment);
                    moveFabToMenu(item);
                    hideSelectedItem(item);
                }

                // Ganti fragment jika valid
                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                }

                // Update FAB posisi dan menu visual
                moveFabToMenu(item);
                hideSelectedItem(item);

                return true;
            }
        });
    }


    private void setNotchColor() {
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.Primary, getTheme()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = window.getInsetsController();
            if (controller != null) {
                controller.setSystemBarsAppearance(
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                );
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void moveFabToCradle() {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        params.setAnchorId(R.id.bottomApp);
        fab.setLayoutParams(params);
    }

    private void setFabCradleRadius(int radiusDp) {
        float density = getResources().getDisplayMetrics().density;
        int radiusPx = (int) (radiusDp * density);
        bottomAppBar.setFabCradleRoundedCornerRadius(radiusPx);
    }

    private void hideSelectedItem(MenuItem item) {
        for (int i = 0; i < bottomNav.getMenu().size(); i++) {
            MenuItem menuItem = bottomNav.getMenu().getItem(i);
            menuItem.setEnabled(true);
            menuItem.setCheckable(true);
            if (menuItem.getItemId() == item.getItemId()) {
                menuItem.setIcon(R.drawable.ic_transparent);
                menuItem.setTitle("");
            } else {
                menuItem.setIcon(getIconForMenuItem(menuItem.getItemId()));
                menuItem.setTitle(getTitleForMenuItem(menuItem.getItemId()));
            }
        }
        item.setEnabled(false);
        item.setChecked(false);
    }

    private String getTitleForMenuItem(int itemId) {
        if (itemId == R.id.nav_maps) {
            return "Mitra Peta";
        } else if (itemId == R.id.nav_home) {
            return "Beranda";
        } else if (itemId == R.id.nav_account) {
            return "Akun";
        } else {
            return "";
        }
    }

    private int getIconForMenuItem(int itemId) {
        if (itemId == R.id.nav_maps) {
            return R.drawable.baseline_share_location_24;
        } else if (itemId == R.id.nav_home) {
            return R.drawable.baseline_home_filled_24;
        } else if (itemId == R.id.nav_account) {
            return R.drawable.baseline_person_24;
        } else {
            return R.drawable.ic_transparent;
        }
    }

    private void moveFabToMenu(MenuItem item) {
        int fabTargetX = getFabTargetX(item.getItemId());

        ObjectAnimator moveAnimator = ObjectAnimator.ofFloat(fab, "translationX", fabTargetX);
        moveAnimator.setDuration(300);

        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(fab, "scaleX", 1.2f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(fab, "scaleY", 1.2f);
        ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(fab, "rotation", 0f, 360f);
        rotateAnimator.setDuration(300);

        moveAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                scaleXAnimator.reverse();
                scaleYAnimator.reverse();
            }
        });

        moveAnimator.start();
        scaleXAnimator.start();
        scaleYAnimator.start();
        rotateAnimator.start();
    }

    private int getFabTargetX(int itemId) {
        if (itemId == R.id.nav_maps) {
            return -400;
        } else if (itemId == R.id.nav_home) {
            return 0;
        } else if (itemId == R.id.nav_account) {
            return 400;
        } else {
            return 400;
        }
    }

    private void updateFabIcon(int drawableResId) {
        fab.setImageResource(drawableResId);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            super.onBackPressed();

            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (currentFragment instanceof HomeFragment) {
                updateFabIcon(R.drawable.baseline_home_filled_24);
            } else if (currentFragment instanceof MitraMapsFragment) {
                updateFabIcon(R.drawable.baseline_share_location_24);
            } else if (currentFragment instanceof AccountFragment) {
                updateFabIcon(R.drawable.baseline_person_24);
            }
        } else {
            super.onBackPressed();
        }
    }

    public void updateFabPositionForFragment(String fragmentName) {
        if (fragmentName.equals(HomeFragment.class.getSimpleName())) {
            fab.setImageResource(R.drawable.baseline_home_filled_24);
        } else if (fragmentName.equals(MitraMapsFragment.class.getSimpleName())) {
            fab.setImageResource(R.drawable.baseline_share_location_24);
        } else if (fragmentName.equals(AccountFragment.class.getSimpleName())) {
            fab.setImageResource(R.drawable.baseline_person_24);
        }
    }
}