package com.example.aksaraaa.features.donasi.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aksaraaa.R;
import com.example.aksaraaa.dashboard.DashboardUser;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InvoicePembayaranActivity extends AppCompatActivity {

    // UI Components
    private ImageView ivBack;
    private TextView tvCampaignTitle;
    private TextView tvDonaturName;
    private TextView tvDonationAmount;
    private TextView tvPaymentMethod;
    private TextView tvTransactionTime;
    private TextView tvTotalAmount;
    private MaterialButton btnShare;
    private MaterialButton btnDone;

    // Data
    private String transactionId;
    private String campaignTitle;
    private long donationAmount;
    private String paymentMethod;
    private String transactionTime;
    private String donaturName;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoice_pembayaran);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Handle back button
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateToHome();
            }
        });

        // Get data from Intent
        getIntentData();

        // Initialize views
        initViews();

        // Setup listeners
        setupListeners();

        // Load user data and display invoice
        loadUserDataAndDisplayInvoice();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        transactionId = intent.getStringExtra("TRANSACTION_ID");
        campaignTitle = intent.getStringExtra("CAMPAIGN_TITLE");
        donationAmount = intent.getLongExtra("DONATION_AMOUNT", 0);
        paymentMethod = intent.getStringExtra("PAYMENT_METHOD");

        // Get current transaction time
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", new Locale("id", "ID"));
        transactionTime = sdf.format(new Date()) + " WIB";
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        tvCampaignTitle = findViewById(R.id.tvCampaignTitle);
        tvDonaturName = findViewById(R.id.tvDonaturName);
        tvDonationAmount = findViewById(R.id.tvDonationAmount);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        tvTransactionTime = findViewById(R.id.tvTransactionTime);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnShare = findViewById(R.id.btnShare);
        btnDone = findViewById(R.id.btnDone);
    }

    private void setupListeners() {
        ivBack.setOnClickListener(v -> navigateToHome());
        btnShare.setOnClickListener(v -> shareInvoice());
        btnDone.setOnClickListener(v -> navigateToHome());
    }

    private void loadUserDataAndDisplayInvoice() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Get user data from Firestore
            db.collection("users").document(currentUser.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Get name from Firestore, fallback to display name if not available
                                donaturName = document.getString("nama");
                                if (donaturName == null || donaturName.isEmpty()) {
                                    donaturName = currentUser.getDisplayName();
                                }
                            } else {
                                // If no document, use display name or email
                                donaturName = currentUser.getDisplayName();
                                if (donaturName == null || donaturName.isEmpty()) {
                                    donaturName = currentUser.getEmail();
                                }
                            }
                        } else {
                            // If Firestore fails, fallback to basic user info
                            donaturName = currentUser.getDisplayName();
                            if (donaturName == null || donaturName.isEmpty()) {
                                donaturName = currentUser.getEmail();
                            }
                        }

                        // Ensure we have at least some name to display
                        if (donaturName == null || donaturName.isEmpty()) {
                            donaturName = "Donatur";
                        }

                        // Now display all invoice data
                        displayInvoiceData();
                    });
        } else {
            // If no user is logged in (shouldn't happen in normal flow)
            donaturName = "Donatur";
            displayInvoiceData();
        }
    }

    private void displayInvoiceData() {
        tvCampaignTitle.setText(campaignTitle);
        tvDonaturName.setText(donaturName);
        tvDonationAmount.setText(formatCurrency(donationAmount));
        tvPaymentMethod.setText(paymentMethod);
        tvTransactionTime.setText(transactionTime);
        tvTotalAmount.setText(formatCurrency(donationAmount));
    }

    private void shareInvoice() {
        String shareText = "🎉 Donasi Berhasil! 🎉\n\n" +
                "Atas nama: " + donaturName + "\n" +
                "Campaign: " + campaignTitle + "\n" +
                "Nominal: " + formatCurrency(donationAmount) + "\n" +
                "Metode: " + paymentMethod + "\n" +
                "Waktu: " + transactionTime + "\n\n" +
                "Terima kasih telah berdonasi! 🙏\n" +
                "Bersama kita bisa membuat perubahan yang lebih baik.";

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Invoice Donasi - " + campaignTitle);

        startActivity(Intent.createChooser(shareIntent, "Bagikan invoice donasi"));
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, DashboardUser.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private String formatCurrency(long amount) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return "Rp" + formatter.format(amount);
    }
}