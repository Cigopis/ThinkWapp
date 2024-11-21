package com.wongcoco.thinkwapp;

import static android.view.Gravity.BOTTOM;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsetsController;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setNotchColor();

        fab = findViewById(R.id.fab);
        bottomNav = findViewById(R.id.bottomNav);
        // Mengatur warna ikon dan teks navbar menjadi putih
        bottomNav.setItemIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        bottomNav.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));

        moveFabToBottomRight();

        // Load HomeFragment secara default
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            updateFabIcon(R.drawable.baseline_home_filled_24);


            centerFab();
        }

        // Listener untuk BottomNavigationView
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                // If-else untuk navigasi
                if (item.getItemId() == R.id.nav_messages) {
                    selectedFragment = new MessageFragment();
                    updateFabIcon(R.drawable.baseline_message_24);
                } else if (item.getItemId() == R.id.nav_home) {
                    selectedFragment = new HomeFragment();
                    updateFabIcon(R.drawable.baseline_home_filled_24);
                } else if (item.getItemId() == R.id.nav_account) {
                    selectedFragment = new AccountFragment();
                    updateFabIcon(R.drawable.baseline_person_24);
                }

                // Ganti fragment jika ada pilihan yang valid
                if (selectedFragment != null) {
                    Log.d("MainActivity", "Loading fragment: " + selectedFragment.getClass().getSimpleName());
                    loadFragment(selectedFragment);
                }

                // Sembunyikan item yang terpilih
                hideSelectedItem(item);

                // Memindahkan FAB
                moveFabToMenu(item);

                return true;
            }
        });
    }

    private void setNotchColor() {
        // Using getWindow() directly for activity context
        Window window = getWindow();

        // Set status bar color
        window.setStatusBarColor(getResources().getColor(R.color.Primary, getTheme()));

        // Option to set light/dark status bar text
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

    private void hideSelectedItem(MenuItem item) {
        // Reset visibilitas semua item
        for (int i = 0; i < bottomNav.getMenu().size(); i++) {
            MenuItem menuItem = bottomNav.getMenu().getItem(i);
            menuItem.setEnabled(true); // Pastikan semua item diaktifkan
            menuItem.setCheckable(true); // Pastikan semua item bisa dicentang

            // Set icon ke gambar yang benar
            if (menuItem.getItemId() == item.getItemId()) {
                menuItem.setIcon(R.drawable.ic_transparent); // Gambar transparan untuk item yang dipilih
                menuItem.setTitle(""); // Sembunyikan judul item yang dipilih
            } else {
                // Set icon sesuai dengan status item
                menuItem.setIcon(getIconForMenuItem(menuItem.getItemId()));
                menuItem.setTitle(getTitleForMenuItem(menuItem.getItemId())); // Set judul untuk item lainnya
            }
        }

        // Nonaktifkan item yang dipilih
        item.setEnabled(false); // Nonaktifkan item yang dipilih
        item.setChecked(false);  // Uncheck item yang dipilih
    }

    private String getTitleForMenuItem(int itemId) {
        if (itemId == R.id.nav_messages) {
            return "Beranda"; // Judul untuk Home
        } else if (itemId == R.id.nav_home) {
            return "Pesan"; // Judul untuk Company
        } else if (itemId == R.id.nav_account) {
            return "Akun"; // Judul untuk Message
        } else {
            return ""; // Kosongkan jika tidak ada yang cocok
        }
    }

    private int getIconForMenuItem(int itemId) {
        if (itemId == R.id.nav_messages) {
            return R.drawable.baseline_message_24; // Ikon home
        } else if (itemId == R.id.nav_home) {
            return R.drawable.baseline_home_filled_24; // Ikon company
        } else if (itemId == R.id.nav_account) {
            return R.drawable.baseline_person_24; // Ikon message
        } else {
            return R.drawable.ic_transparent; // Gambar transparan sebagai default
        }
    }



    private void moveFabToMenu(MenuItem item) {
        int fabTargetX = getFabTargetX(item.getItemId());

        Log.d("FABAnimation", "Target X: " + fabTargetX);

        ObjectAnimator moveAnimator = ObjectAnimator.ofFloat(fab, "translationX", fabTargetX);
        moveAnimator.setDuration(300);

        // Animasi skala dan rotasi FAB
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
        // Menentukan posisi FAB berdasarkan item yang dipilih
        if (itemId == R.id.nav_messages) {
            return -400; // Posisi FAB untuk Home
        } else if (itemId == R.id.nav_home) {
            return 0; // Posisi FAB untuk Messages
        } else if (itemId == R.id.nav_account) {
            return 400; // Posisi FAB untuk Account
        } else {
            return 0; // Default
        }
    }

    private void centerFab() {
        FloatingActionButton fab = findViewById(R.id.fab);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        ViewGroup.MarginLayoutParams margin = (ViewGroup.MarginLayoutParams) fab.getLayoutParams();

        // Mengatur aturan layout untuk memastikan FAB berada di tengah
        params.gravity = Gravity.BOTTOM;
        margin.leftMargin = 467;
        fab.setLayoutParams(params);
        fab.setLayoutParams(margin);
    }

    private void updateFabIcon(int drawableResId) {
        fab.setImageResource(drawableResId);
    }

    @Override
    public void onBackPressed() {
        // Jika ada fragment di back stack, hapus fragment tersebut
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            super.onBackPressed(); // Kembali ke fragment sebelumnya

            // Setelah kembali ke fragment sebelumnya, periksa fragment aktif
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (currentFragment != null) { // Pastikan fragment valid
                updateFabIconBasedOnFragment(currentFragment);
            }
        } else {
            super.onBackPressed(); // Jika tidak ada fragment, kembali ke default
        }
    }

    private void updateFabIconBasedOnFragment(Fragment fragment) {
        if (fragment instanceof HomeFragment) {
            updateFabIcon(R.drawable.baseline_home_filled_24);
        } else if (fragment instanceof MessageFragment) {
            updateFabIcon(R.drawable.baseline_message_24);
        } else if (fragment instanceof AccountFragment) {
            updateFabIcon(R.drawable.baseline_person_24);
        }
    }


    private void moveFabToBottomRight() {
        CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        // Mengatur posisi FAB di pojok kanan bawah
        params.gravity = BOTTOM | Gravity.START;

        // Menambahkan margin (misalnya, 16dp)
        int marginLeft = (int) getResources().getDimension(R.dimen.fab_margin_left); // Ubah sesuai kebutuhan
        int marginBottom = (int) getResources().getDimension(R.dimen.fab_margin_bottom); // Ubah sesuai kebutuhan
        params.setMargins(marginLeft, 0, 0, marginBottom);

        fab.setLayoutParams(params);
    }

}
