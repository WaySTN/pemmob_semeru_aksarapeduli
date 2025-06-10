package com.example.aksaraaa.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Donasi implements Parcelable {
    private String id;
    private String judul;
    private String deskripsi;
    private String gambarUrl;
    private double targetDana;
    private double terkumpul;
    private Date deadline;
    private boolean aktif;
    private Date createdAt;

    // Empty constructor required for Firebase
    public Donasi() {
    }

    public Donasi(String judul, String deskripsi, String gambarUrl, double targetDana, Date deadline) {
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.gambarUrl = gambarUrl;
        this.targetDana = targetDana;
        this.terkumpul = 0;
        this.deadline = deadline;
        this.aktif = true;
        this.createdAt = new Date();
    }

    // Parcelable constructor
    protected Donasi(Parcel in) {
        id = in.readString();
        judul = in.readString();
        deskripsi = in.readString();
        gambarUrl = in.readString();
        targetDana = in.readDouble();
        terkumpul = in.readDouble();
        long tmpDeadline = in.readLong();
        deadline = tmpDeadline == -1 ? null : new Date(tmpDeadline);
        aktif = in.readByte() != 0;
        long tmpCreatedAt = in.readLong();
        createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
    }

    // Parcelable creator
    public static final Creator<Donasi> CREATOR = new Creator<Donasi>() {
        @Override
        public Donasi createFromParcel(Parcel in) {
            return new Donasi(in);
        }

        @Override
        public Donasi[] newArray(int size) {
            return new Donasi[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(judul);
        dest.writeString(deskripsi);
        dest.writeString(gambarUrl);
        dest.writeDouble(targetDana);
        dest.writeDouble(terkumpul);
        dest.writeLong(deadline != null ? deadline.getTime() : -1);
        dest.writeByte((byte) (aktif ? 1 : 0));
        dest.writeLong(createdAt != null ? createdAt.getTime() : -1);
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getGambarUrl() {
        return gambarUrl;
    }

    public void setGambarUrl(String gambarUrl) {
        this.gambarUrl = gambarUrl;
    }

    public double getTargetDana() {
        return targetDana;
    }

    public void setTargetDana(double targetDana) {
        this.targetDana = targetDana;
    }

    public double getTerkumpul() {
        return terkumpul;
    }

    public void setTerkumpul(double terkumpul) {
        this.terkumpul = terkumpul;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public boolean isAktif() {
        return aktif;
    }

    public void setAktif(boolean aktif) {
        this.aktif = aktif;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    // Helper method to format currency
    public String getFormattedTargetDana() {
        return String.format(Locale.getDefault(), "Rp%,.0f", targetDana);
    }

    // Helper method to format collected amount
    public String getFormattedTerkumpul() {
        return String.format(Locale.getDefault(), "Rp%,.0f", terkumpul);
    }

    // Helper method to format progress percentage
    public int getProgressPercentage() {
        if (targetDana == 0) return 0;
        return (int) ((terkumpul / targetDana) * 100);
    }

    // Helper method to format deadline date
    public String getFormattedDeadline() {
        if (deadline == null) return "";
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(deadline);
    }

    // Method to calculate remaining time
    public String getSisaWaktu() {
        if (deadline == null) {
            return "Tidak ada deadline";
        }

        Date now = new Date();
        long diffInMillis = deadline.getTime() - now.getTime();

        if (diffInMillis <= 0) {
            return "Waktu habis";
        }

        long days = TimeUnit.MILLISECONDS.toDays(diffInMillis);
        long weeks = days / 7;
        long remainingDays = days % 7;

        if (weeks > 0) {
            if (remainingDays > 0) {
                return String.format(Locale.getDefault(), "Sisa %d minggu %d hari", weeks, remainingDays);
            } else {
                return String.format(Locale.getDefault(), "Sisa %d minggu", weeks);
            }
        } else {
            return String.format(Locale.getDefault(), "Sisa %d hari", days);
        }
    }

    // Helper method to check if campaign is active
    public boolean isCampaignActive() {
        if (!aktif) return false;
        if (deadline == null) return true;
        return new Date().before(deadline);
    }
}