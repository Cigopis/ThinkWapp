package com.wongcoco.thinkwapp;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class HomeFragment extends Fragment {

    private ImageView bulat1, bulat2, bulat3, produkImage, pahamiImage, imageContent;
    private TextView registerClick, sejarahTitle, thinkWoodTitle, sejarahDesc, thinkWoodDesc, syarat, tanya;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FrameLayout backgroundLayout;
    private ScrollView scrollView;

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
        backgroundLayout = view.findViewById(R.id.frameMaps);
        scrollView = view.findViewById(R.id.scrollView);
        imageContent = view.findViewById(R.id.imageContent);
        syarat = view.findViewById(R.id.syarat);
        tanya = view.findViewById(R.id.tanya);

        // Load animasi
        final Animation zoomIn = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_in);


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
        syarat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(zoomIn);
                // Tambahkan aksi lain yang diinginkan
            }
        });

        // Set OnClickListener untuk tanya
        tanya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(zoomIn);
                // Tambahkan aksi lain yang diinginkan
            }
        });

        // Set OnClickListener untuk produkImage
        produkImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(zoomIn);
                // Tambahkan aksi lain yang diinginkan
            }
        });

        // Set OnClickListener untuk pahamiImage
        pahamiImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(zoomIn);
                // Tambahkan aksi lain yang diinginkan
            }
        });

        // Setup SwipeRefreshLayout
        setupSwipeRefreshLayout();
        setupScrollListener();

        return view;
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
        // Simulasi proses refresh data, misalnya mengambil data dari API
        swipeRefreshLayout.postDelayed(() -> {
            boolean dataFetched = true; // Ganti ini dengan logika sebenarnya

            if (dataFetched) {
                animateEllips(); // Memanggil metode animasi
                setViewsVisibility(View.VISIBLE);
            } else {
                showError("Gagal memuat data. Silakan coba lagi.");
                setViewsVisibility(View.INVISIBLE);
            }

            // Matikan animasi refresh setelah data diperbarui
            swipeRefreshLayout.setRefreshing(false);
        }, 2000);
    }

    private void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
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
        backgroundLayout.setVisibility(visibility);
    }

    private void animateEllips() {
        ObjectAnimator ellips1AnimY = ObjectAnimator.ofFloat(bulat1, "translationY", -300f, 0f);
        ellips1AnimY.setInterpolator(new OvershootInterpolator());
        ellips1AnimY.setDuration(700);
        ObjectAnimator ellips1AnimAlpha = ObjectAnimator.ofFloat(bulat1, "alpha", 0f, 1f);
        ellips1AnimAlpha.setDuration(700);

        ObjectAnimator ellips2AnimY = ObjectAnimator.ofFloat(bulat2, "translationY", -300f, 0f);
        ellips2AnimY.setInterpolator(new OvershootInterpolator());
        ellips2AnimY.setDuration(700);
        ellips2AnimY.setStartDelay(200);
        ObjectAnimator ellips2AnimAlpha = ObjectAnimator.ofFloat(bulat2, "alpha", 0f, 1f);
        ellips2AnimAlpha.setDuration(700);
        ellips2AnimAlpha.setStartDelay(200);

        ObjectAnimator ellips3AnimY = ObjectAnimator.ofFloat(bulat3, "translationY", -300f, 0f);
        ellips3AnimY.setInterpolator(new OvershootInterpolator());
        ellips3AnimY.setDuration(700);
        ellips3AnimY.setStartDelay(400);
        ObjectAnimator ellips3AnimAlpha = ObjectAnimator.ofFloat(bulat3, "alpha", 0f, 1f);
        ellips3AnimAlpha.setDuration(700);
        ellips3AnimAlpha.setStartDelay(400);

        AnimatorSet ellipsAnimatorSet = new AnimatorSet();
        ellipsAnimatorSet.playTogether(ellips1AnimY, ellips1AnimAlpha, ellips2AnimY, ellips2AnimAlpha, ellips3AnimY, ellips3AnimAlpha);

        ellipsAnimatorSet.addListener(new AnimatorSet.AnimatorListener() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                animateContent();
            }
            @Override public void onAnimationStart(android.animation.Animator animation) {}
            @Override public void onAnimationCancel(android.animation.Animator animation) {}
            @Override public void onAnimationRepeat(android.animation.Animator animation) {}
        });

        ellipsAnimatorSet.start();
    }

    private void animateContent() {
        registerClick.setAlpha(1f);
        registerClick.setTranslationY(0f);

        animateImageContent();

        ObjectAnimator sejarahTitleAnimY = ObjectAnimator.ofFloat(sejarahTitle, "translationY", -200f, 0f);
        sejarahTitleAnimY.setInterpolator(new OvershootInterpolator());
        sejarahTitleAnimY.setDuration(600);
        ObjectAnimator sejarahTitleAlpha = ObjectAnimator.ofFloat(sejarahTitle, "alpha", 0f, 1f);
        sejarahTitleAlpha.setDuration(600);

        ObjectAnimator sejarahDescAnimY = ObjectAnimator.ofFloat(sejarahDesc, "translationY", -200f, 0f);
        sejarahDescAnimY.setInterpolator(new OvershootInterpolator());
        sejarahDescAnimY.setDuration(600);
        ObjectAnimator sejarahDescAlpha = ObjectAnimator.ofFloat(sejarahDesc, "alpha", 0f, 1f);
        sejarahDescAlpha.setDuration(600);

        ObjectAnimator thinkWoodTitleAnimY = ObjectAnimator.ofFloat(thinkWoodTitle, "translationY", -200f, 0f);
        thinkWoodTitleAnimY.setInterpolator(new OvershootInterpolator());
        thinkWoodTitleAnimY.setDuration(600);
        ObjectAnimator thinkWoodTitleAlpha = ObjectAnimator.ofFloat(thinkWoodTitle, "alpha", 0f, 1f);
        thinkWoodTitleAlpha.setDuration(600);

        ObjectAnimator thinkWoodDescAnimY = ObjectAnimator.ofFloat(thinkWoodDesc, "translationY", -200f, 0f);
        thinkWoodDescAnimY.setInterpolator(new OvershootInterpolator());
        thinkWoodDescAnimY.setDuration(600);
        ObjectAnimator thinkWoodDescAlpha = ObjectAnimator.ofFloat(thinkWoodDesc, "alpha", 0f, 1f);
        thinkWoodDescAlpha.setDuration(600);

        ObjectAnimator produkImageAnimY = ObjectAnimator.ofFloat(produkImage, "translationY", -200f, 0f);
        produkImageAnimY.setInterpolator(new OvershootInterpolator());
        produkImageAnimY.setDuration(600);
        ObjectAnimator produkImageAlpha = ObjectAnimator.ofFloat(produkImage, "alpha", 0f, 1f);
        produkImageAlpha.setDuration(600);

        ObjectAnimator pahamiImageAnimY = ObjectAnimator.ofFloat(pahamiImage, "translationY", -200f, 0f);
        pahamiImageAnimY.setInterpolator(new OvershootInterpolator());
        pahamiImageAnimY.setDuration(600);
        ObjectAnimator pahamiImageAlpha = ObjectAnimator.ofFloat(pahamiImage, "alpha", 0f, 1f);
        pahamiImageAlpha.setDuration(600);

        AnimatorSet contentAnimatorSet = new AnimatorSet();
        contentAnimatorSet.playTogether(
                sejarahTitleAnimY, sejarahTitleAlpha,
                sejarahDescAnimY, sejarahDescAlpha,
                thinkWoodTitleAnimY, thinkWoodTitleAlpha,
                thinkWoodDescAnimY, thinkWoodDescAlpha,
                produkImageAnimY, produkImageAlpha,
                pahamiImageAnimY, pahamiImageAlpha
        );

        contentAnimatorSet.start();
    }

    private void animateImageContent() {
        // Animasi untuk imageContent jika diperlukan
        ObjectAnimator imageContentAnimY = ObjectAnimator.ofFloat(imageContent, "translationY", -200f, 0f);
        imageContentAnimY.setInterpolator(new OvershootInterpolator());
        imageContentAnimY.setDuration(600);
        imageContentAnimY.start();
    }

    private void setupScrollListener() {
        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                float height = scrollView.getHeight();
                float totalHeight = scrollView.getChildAt(0).getHeight();
                float scrollPercentage = (float) scrollY / (totalHeight - height);

                animateOnScroll(sejarahTitle, scrollPercentage);
                animateOnScroll(sejarahDesc, scrollPercentage);
                animateOnScroll(thinkWoodTitle, scrollPercentage);
                animateOnScroll(thinkWoodDesc, scrollPercentage);
                animateOnScroll(produkImage, scrollPercentage);
                animateOnScroll(pahamiImage, scrollPercentage);
            }
        });
    }

    private void animateOnScroll(View view, float scrollPercentage) {
        float threshold = 0.5f; // Ubah ini untuk menyesuaikan sensitivitas
        float translationY = -200 * (1 - Math.abs(scrollPercentage - threshold) / threshold);

        // Pastikan elemen tidak menghilang sepenuhnya
        view.setTranslationY(translationY);

        // Atur alpha agar tetap terlihat meskipun di-scroll
        if (scrollPercentage < threshold) {
            view.setAlpha(1f); // Set alpha saat scroll ke atas
        } else {
            view.setAlpha(1f); // Set alpha penuh saat scroll ke bawah
        }
    }

}
