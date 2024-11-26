package com.wongcoco.thinkwapp;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EarthEngineRequest {

    private static final String API_URL = "https://earthengine.googleapis.com/v1alpha/projects/hip-sight-437517-r7/operations";
    private static final String API_KEY = "AIzaSyC8TdCf_dTVt1DeaHO3FcP9kFU7cWHgxVw";

    public void fetchEarthEngineData() {
        OkHttpClient client = new OkHttpClient();

        // Membuat request untuk mengakses data dari Google Earth Engine
        Request request = new Request.Builder()
                .url(API_URL + "?key=" + API_KEY)
                .build();

        // Eksekusi request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // Berhasil mendapatkan data dari Earth Engine
                String responseData = response.body().string();
                // Proses data yang diterima
                // Misalnya, tampilkan data atau olah lebih lanjut
            } else {
                // Jika request gagal
                System.out.println("Request failed: " + response.message());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
