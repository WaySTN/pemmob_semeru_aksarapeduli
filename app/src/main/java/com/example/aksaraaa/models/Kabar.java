package com.example.aksaraaa.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;

public class Kabar implements Serializable {
    private String id;
    private String judul;
    private String isi;

    @ServerTimestamp
    private Timestamp tanggal;

    public Kabar() {}

    public Kabar(String judul, String isi) {
        this.judul = judul;
        this.isi = isi;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }
    public String getIsi() { return isi; }
    public void setIsi(String isi) { this.isi = isi; }
    public Timestamp getTanggal() { return tanggal; }
    public void setTanggal(Timestamp tanggal) { this.tanggal = tanggal; }
}