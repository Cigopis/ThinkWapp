package com.wongcoco.thinkwapp;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.List;

public class MitraMapsFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;
    private FirebaseFirestore firestore;

    public MitraMapsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Menginflate layout fragment
        View view = inflater.inflate(R.layout.fragment_mitra_maps, container, false);

        // Inisialisasi Firestore
        firestore = FirebaseFirestore.getInstance();

        // Inisialisasi MapView dan setup
        mapView = view.findViewById(R.id.mapView);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this); // Panggil callback setelah peta siap


        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inisialisasi MapView dan setup
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this); // Panggil callback setelah peta siap
        fetchMitraLocations(); // Ambil lokasi mitra

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Lokasi default (contoh Jakarta)
        LatLng defaultLocation = new LatLng(-6.2088, 106.8456);

        // Memindahkan kamera ke lokasi default
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 2.5f)); // Zoom level default

        // Mengatur tipe peta menjadi globe
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID); // Tampilan satelit

        // Mengaktifkan tilt untuk tampilan 3D
        googleMap.getUiSettings().setTiltGesturesEnabled(true);

        // Konfigurasi pengaturan peta lainnya (opsional)
        googleMap.getUiSettings().setZoomControlsEnabled(true); // Menampilkan tombol zoom
        googleMap.getUiSettings().setCompassEnabled(true); // Menampilkan kompas
    }



    private void fetchMitraLocations() {
        firestore.collection("registrations") // Koleksi registrations
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot documentSnapshots = task.getResult();
                        if (documentSnapshots != null) {
                            for (QueryDocumentSnapshot document : documentSnapshots) {
                                String alamat = document.getString("alamat");

                                // Konversi alamat menjadi latitude dan longitude
                                convertAddressToCoordinates(alamat, document);
                            }
                        }
                    } else {
                        Toast.makeText(getContext(), "Gagal mengambil data lokasi mitra", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void convertAddressToCoordinates(String alamat, QueryDocumentSnapshot document) {
        Geocoder geocoder = new Geocoder(getContext());
        try {
            List<Address> addresses = geocoder.getFromLocationName(alamat, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                double latitude = address.getLatitude();
                double longitude = address.getLongitude();

                // Menyimpan latitude dan longitude ke Firestore jika perlu
                document.getReference().update("latitude", latitude, "longitude", longitude);

                // Menambahkan marker untuk lokasi mitra di peta
                LatLng mitraLocation = new LatLng(latitude, longitude);
                googleMap.addMarker(new MarkerOptions()
                        .position(mitraLocation)
                        .title("Mitra " + document.getString("nama")));

                // Optional: Fokuskan peta ke lokasi mitra yang baru ditambahkan
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mitraLocation, 10));
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error geocoding alamat", Toast.LENGTH_SHORT).show();
        }
    }

    // Mengelola siklus hidup MapView
    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }


    @Override
    public void onPause() {
        super.onPause();

        if (mapView != null) {
            mapView.onPause();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        // Menambahkan OnBackPressedCallback dengan benar
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().finish(); // Menyelesaikan aktivitas saat tombol kembali ditekan
            }
        });
    }
}
