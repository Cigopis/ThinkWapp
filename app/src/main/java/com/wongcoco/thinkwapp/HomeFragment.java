package com.wongcoco.thinkwapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private ImageView bulat1, bulat2, bulat3, produkImage, pahamiImage, imageContent;
    private TextView sejarahTitle, thinkWoodTitle, sejarahDesc, thinkWoodDesc;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout registerClick,syarat,tanya;

    private ScrollView scrollView;
    private GoogleMap mMap;

    // Interface untuk mendapatkan username
    interface OnUsernameRetrievedListener {
        void onUsernameRetrieved(String username);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Inisialisasi elemen
        bulat1 = view.findViewById(R.id.ellips1);
        bulat2 = view.findViewById(R.id.ellips2);
        bulat3 = view.findViewById(R.id.ellips3);
        registerClick = view.findViewById(R.id.registerClick);
        sejarahTitle = view.findViewById(R.id.sejarahTitle);
        sejarahDesc = view.findViewById(R.id.sejarahDesc);
        thinkWoodTitle = view.findViewById(R.id.thinkWoodTitle);
        thinkWoodDesc = view.findViewById(R.id.thinkWoodDesc);
        produkImage = view.findViewById(R.id.produkImage);
        pahamiImage = view.findViewById(R.id.pahamiImage);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        scrollView = view.findViewById(R.id.scrollView);
        imageContent = view.findViewById(R.id.imageContent);
        syarat = view.findViewById(R.id.syarat);
        tanya = view.findViewById(R.id.tanya);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.frameMaps);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this); // Meminta peta asinkron
        }

        // Load animasi
        final Animation zoomIn = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_in);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Dapatkan userId

        // Ambil username dari Firestore
        getUsernameFromFirestore(userId, new OnUsernameRetrievedListener() {
            @Override
            public void onUsernameRetrieved(String username) {
                if (username != null) {
                    // Panggil metode untuk memeriksa pendaftaran
                    checkUserRegistration( userId);
                } else {
                    Log.w(TAG, "Username tidak ditemukan.");
                }
            }
        });

        // Panggil metode untuk animasi
        animateEllips();
        animateRegisterClick();

        // Event onClick untuk registerClick
        registerClick.setOnClickListener(v -> {
            v.startAnimation(zoomIn);
            Intent intent = new Intent(getActivity(), RegistrationActivity.class);
            startActivity(intent);
        });

        // Set OnClickListener untuk syarat
        syarat.setOnClickListener(v -> {
            v.startAnimation(zoomIn);
            Intent intent = new Intent(getActivity(), PanduanActivity.class);
            startActivity(intent);
        });

        // Set OnClickListener untuk tanya
        tanya.setOnClickListener(v -> {
            v.startAnimation(zoomIn);
            // Tambahkan aksi lain yang diinginkan
        });

        // Set OnClickListener untuk produkImage
        produkImage.setOnClickListener(v -> {
            v.startAnimation(zoomIn);
            // Tambahkan aksi lain yang diinginkan
        });

        // Set OnClickListener untuk pahamiImage
        pahamiImage.setOnClickListener(v -> {
            v.startAnimation(zoomIn);
            // Tambahkan aksi lain yang diinginkan
        });

        // Setup SwipeRefreshLayout
        setupSwipeRefreshLayout();
        setupScrollListener();

        return view;
    }

    // Metode untuk mendapatkan username dari Firestore
    private void getUsernameFromFirestore(String userId, OnUsernameRetrievedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userId);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String username = documentSnapshot.getString("username");
                listener.onUsernameRetrieved(username);
            } else {
                listener.onUsernameRetrieved(null);
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error getting username", e);
            listener.onUsernameRetrieved(null);
        });
    }

    private void animateRegisterClick() {
        ObjectAnimator moveRight = ObjectAnimator.ofFloat(registerClick, "translationX", 0f, 16f);
        moveRight.setDuration(300);
        moveRight.setInterpolator(new OvershootInterpolator());
        moveRight.setRepeatMode(ObjectAnimator.REVERSE);
        moveRight.setRepeatCount(ObjectAnimator.INFINITE);

        ObjectAnimator moveLeft = ObjectAnimator.ofFloat(registerClick, "translationX", 10f, 0f);
        moveLeft.setDuration(300);
        moveLeft.setInterpolator(new OvershootInterpolator());
        moveLeft.setRepeatMode(ObjectAnimator.REVERSE);
        moveLeft.setRepeatCount(ObjectAnimator.INFINITE);

        AnimatorSet moveAnimatorSet = new AnimatorSet();
        moveAnimatorSet.playSequentially(moveRight, moveLeft);
        moveAnimatorSet.start();
    }

    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Logika untuk memperbarui data
            refreshData();
        });
    }

    private void refreshData() {
        swipeRefreshLayout.postDelayed(() -> {
            // Logika untuk memperbarui data
            boolean dataFetched = true; // Ganti dengan logika pengambilan data yang sebenarnya

            if (dataFetched) {
                setViewsVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Data updated successfully!", Toast.LENGTH_SHORT).show();
            } else {
                showError("Failed to load data. Please try again.");
                setViewsVisibility(View.INVISIBLE);
            }

            // Berhenti dari animasi refresh
            swipeRefreshLayout.setRefreshing(false);
        }, 2000); // Simulasi delay untuk pengambilan data
    }


    private void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    private void setViewsVisibility(int visibility) {
        bulat1.setVisibility(visibility);
        bulat2.setVisibility(visibility);
        bulat3.setVisibility(visibility);
        registerClick.setVisibility(visibility);
        sejarahTitle.setVisibility(visibility);
        sejarahDesc.setVisibility(visibility);
        thinkWoodTitle.setVisibility(visibility);
        thinkWoodDesc.setVisibility(visibility);
        produkImage.setVisibility(visibility);
        pahamiImage.setVisibility(visibility);
    }

    private void animateEllips() {
        AnimatorSet ellipsAnimatorSet = new AnimatorSet();
        ObjectAnimator[] animators = new ObjectAnimator[3];

        for (int i = 0; i < 3; i++) {
            animators[i] = ObjectAnimator.ofFloat(getEllipseByIndex(i), "translationY", -300f, 0f);
            animators[i].setInterpolator(new OvershootInterpolator());
            animators[i].setDuration(700);
            animators[i].setStartDelay(i * 200);
            ellipsAnimatorSet.playTogether(animators[i]);
        }

        ellipsAnimatorSet.start();
    }

    private View getEllipseByIndex(int index) {
        switch (index) {
            case 0: return bulat1;
            case 1: return bulat2;
            case 2: return bulat3;
            default: return null;
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set lokasi untuk ditampilkan di peta
        LatLng lokasi = new LatLng(-7.667999570445097, 112.14894567646326); // Contoh koordinat Surabaya
        mMap.addMarker(new MarkerOptions().position(lokasi).title("PT. Keong Nusantara Abadi (Thinkwood)"));
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasi, 15));
    }

    private void setupScrollListener() {
        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                float height = scrollView.getHeight();
                float totalHeight = scrollView.getChildAt(0).getHeight();
                float scrollPercentage = (float) scrollY / (totalHeight - height);

                // Call animateOnScroll for each view
                animateOnScroll(sejarahTitle, scrollPercentage);
                animateOnScroll(sejarahDesc, scrollPercentage);
                animateOnScroll(imageContent, scrollPercentage);
                animateOnScroll(thinkWoodTitle, scrollPercentage);
                animateOnScroll(thinkWoodDesc, scrollPercentage);
//                animateOnScroll(produkImage, scrollPercentage);
//                animateOnScroll(pahamiImage, scrollPercentage);
            }
        });
    }

    private void animateOnScroll(View view, float scrollPercentage) {
        float threshold = 0.5f; // Ubah ini untuk menyesuaikan sensitivitas
        float translationY = -200 * (1 - Math.abs(scrollPercentage - threshold) / threshold);

        // Pastikan elemen tidak menghilang sepenuhnya
        view.setTranslationY(translationY);
        view.setAlpha(1 - scrollPercentage);

        // Atur alpha agar tetap terlihat meskipun di-scroll
        if (scrollPercentage < threshold) {
            view.setAlpha(1f); // Set alpha saat scroll ke atas
        } else {
            view.setAlpha(1f); // Set alpha penuh saat scroll ke bawah
        }
    }

    private void checkUserRegistration(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Cek apakah userId ada di koleksi registrations
        db.collection("registrations").whereEqualTo("userId", userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        Log.d(TAG, "User ID ditemukan di koleksi registrations");

                        registerClick.setVisibility(View.GONE);

                    } else {
                        Log.d(TAG, "User ID tidak ditemukan di koleksi registrations");
                        Toast.makeText(getContext(), "Anda belum terdaftar sebagai supplier. Silakan mendaftar.", Toast.LENGTH_SHORT).show();
                        registerClick.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Gagal mendapatkan data dari koleksi registrations", e);
                    Toast.makeText(getContext(), "Terjadi kesalahan saat memeriksa pendaftaran.", Toast.LENGTH_SHORT).show();
                });
    }

}
