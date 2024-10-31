package com.wongcoco.thinkwapp;

public class RegistrationData {
    private String userId;  // Primary key for the user
    private String nik;
    private String nama;
    private String alamat;
    private String nomorTelepon;
    private String luasLahan;
    private String uriKTP;
    private String uriLahan;

    public RegistrationData(String userId, String nik, String nama, String alamat, String nomorTelepon, String luasLahan, String uriKTP, String uriLahan) {
        this.userId = userId;
        this.nik = nik;
        this.nama = nama;
        this.alamat = alamat;
        this.nomorTelepon = nomorTelepon;
        this.luasLahan = luasLahan;
        this.uriKTP = uriKTP;
        this.uriLahan = uriLahan;
    }

    // Getter dan setter untuk userId
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Setter untuk URI KTP
    public void setUriKTP(String uriKTP) {
        this.uriKTP = uriKTP;
    }

    // Setter untuk URI lahan
    public void setUriLahan(String uriLahan) {
        this.uriLahan = uriLahan;
    }

    // Getter untuk URI KTP
    public String getUriKTP() {
        return uriKTP;
    }

    // Getter untuk URI lahan
    public String getUriLahan() {
        return uriLahan;
    }




    // Getter dan setter lainnya
    public String getNik() { return nik; }
    public String getNama() { return nama; }
    public String getAlamat() { return alamat; }
    public String getNomorTelepon() { return nomorTelepon; }
    public String getLuasLahan() { return luasLahan; }

}
