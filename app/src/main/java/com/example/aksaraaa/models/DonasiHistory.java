package com.example.aksaraaa.models;

import com.google.firebase.Timestamp;

public class DonasiHistory {
    private String id;
    private String campaignId;
    private String campaignTitle;
    private Long amount;
    private String paymentMethod;
    private String status;
    private Timestamp timestamp;
    private String userId;
    private String nama;

    // Default constructor required for Firestore
    public DonasiHistory() {
    }

    // Constructor with all parameters
    public DonasiHistory(String id, String campaignId, String campaignTitle, Long amount,
                         String paymentMethod, String status, Timestamp timestamp,
                         String userId, String userName) {
        this.id = id;
        this.campaignId = campaignId;
        this.campaignTitle = campaignTitle;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.timestamp = timestamp;
        this.userId = userId;
        this.nama = userName;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public String getCampaignTitle() {
        return campaignTitle;
    }

    public Long getAmount() {
        return amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return nama;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    public void setCampaignTitle(String campaignTitle) {
        this.campaignTitle = campaignTitle;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.nama = userName;
    }

    // Utility methods
    public String getFormattedAmount() {
        if (amount != null) {
            return String.format("Rp %,d", amount);
        }
        return "Rp 0";
    }

    public boolean isSuccessful() {
        return "success".equals(status);
    }

    @Override
    public String toString() {
        return "DonasiHistory{" +
                "id='" + id + '\'' +
                ", campaignId='" + campaignId + '\'' +
                ", campaignTitle='" + campaignTitle + '\'' +
                ", amount=" + amount +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", status='" + status + '\'' +
                ", timestamp=" + timestamp +
                ", userId='" + userId + '\'' +
                ", userName='" + nama + '\'' +
                '}';
    }
}