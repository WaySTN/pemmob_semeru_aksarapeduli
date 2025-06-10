// File: Transaction.java (Model Class)
package com.example.aksaraaa.models;

import com.google.firebase.Timestamp;

public class Transaction {
    private String id;
    private String nama; // Field ini sesuai dengan Firestore
    private String campaignTitle;
    private double amount;
    private String paymentMethod;
    private Timestamp timestamp;
    private String status;

    // Constructor kosong untuk Firestore
    public Transaction() {}

    // Constructor dengan parameter
    public Transaction(String nama, String campaignTitle, double amount,
                       String paymentMethod, Timestamp timestamp, String status) {
        this.nama = nama;
        this.campaignTitle = campaignTitle;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.timestamp = timestamp;
        this.status = status;
    }

    // Getter dan Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    // Method ini untuk backward compatibility jika ada kode lain yang menggunakan getDonorName()
    public String getDonorName() {
        return nama;
    }

    public void setDonorName(String donorName) {
        this.nama = donorName;
    }

    public String getCampaignTitle() {
        return campaignTitle;
    }

    public void setCampaignTitle(String campaignTitle) {
        this.campaignTitle = campaignTitle;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}