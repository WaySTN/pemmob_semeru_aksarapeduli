package com.example.aksaraaa.features.kabar.admin.model;

import java.util.Date;

public class Kabar {
    private String id;
    private String judul;
    private String deskripsi;
    private String konten;
    private Date tanggal;
    private String penulis;

    // Empty constructor needed for Firestore
    public Kabar() {}

    public Kabar(String id, String judul, String deskripsi, String konten, Date tanggal, String penulis) {
        this.id = id;
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.konten = konten;
        this.tanggal = tanggal;
        this.penulis = penulis;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }
    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    public String getKonten() { return konten; }
    public void setKonten(String konten) { this.konten = konten; }
    public Date getTanggal() { return tanggal; }
    public void setTanggal(Date tanggal) { this.tanggal = tanggal; }
    public String getPenulis() { return penulis; }
    public void setPenulis(String penulis) { this.penulis = penulis; }
}