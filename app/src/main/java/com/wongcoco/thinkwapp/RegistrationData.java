package com.wongcoco.thinkwapp;

public class RegistrationData {
    private String nik;
    private String nama;
    private String alamat;
    private String nomorTelepon;
    private String luasLahan;
    private String uriKTP;
    private String uriLahan;

    public RegistrationData(String nik, String nama, String alamat, String nomorTelepon, String luasLahan, String uriKTP, String uriLahan) {
        this.nik = nik;
        this.nama = nama;
        this.alamat = alamat;
        this.nomorTelepon = nomorTelepon;
        this.luasLahan = luasLahan;
        this.uriKTP = uriKTP;
        this.uriLahan = uriLahan;
    }

    // Getter dan setter jika diperlukan
    public String getNik() { return nik; }
    public String getNama() { return nama; }
    public String getAlamat() { return alamat; }
    public String getNomorTelepon() { return nomorTelepon; }
    public String getLuasLahan() { return luasLahan; }

    // Getter dan Setter untuk URI
    public String getUriKTP() {
        return uriKTP;
    }

    public void setUriKTP(String uriKTP) {
        this.uriKTP = uriKTP;
    }

    public String getUriLahan() {
        return uriLahan;
    }

    public void setUriLahan(String uriLahan) {
        this.uriLahan = uriLahan;
    }
}
