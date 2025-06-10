package com.example.aksaraaa.features.donasi.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aksaraaa.R;
import com.example.aksaraaa.models.Donasi;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

public class PaymentConfirmationActivity extends AppCompatActivity {

    private static final String TAG = "PaymentConfirmation";

    // UI Components
    private TextInputEditText etAmount;
    private MaterialButton btnConfirm;
    private RadioGroup rgPaymentMethods;
    private MaterialRadioButton rbBni, rbBri, rbBca, rbDana, rbShopeePay, rbGopay;

    // Data
    private Donasi donasi;
    private long donationAmount = 0;
    private String selectedPaymentMethod = "";
    private String currentUserName = "";
    private String currentUserId = "";

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_confirmation);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Get data from intent
        getIntentData();

        // Initialize views
        initViews();

        // Setup listeners
        setupListeners();

        // Load campaign data
        loadCampaignData();

        // Get current user data
        getCurrentUserData();
    }

    private void getCurrentUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();

            // Try to get user name from Firestore users collection
            db.collection("users")
                    .document(currentUserId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            currentUserName = documentSnapshot.getString("nama");
                            if (currentUserName == null || currentUserName.isEmpty()) {
                                // Fallback to display name from Firebase Auth
                                currentUserName = currentUser.getDisplayName();
                                if (currentUserName == null || currentUserName.isEmpty()) {
                                    // Fallback to email
                                    currentUserName = currentUser.getEmail();
                                }
                            }
                            Log.d(TAG, "Current user name: " + currentUserName);
                        } else {
                            // User document doesn't exist, use display name or email
                            currentUserName = currentUser.getDisplayName();
                            if (currentUserName == null || currentUserName.isEmpty()) {
                                currentUserName = currentUser.getEmail();
                            }
                            Log.d(TAG, "User document not found, using: " + currentUserName);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error getting user data", e);
                        // Fallback to display name or email
                        currentUserName = currentUser.getDisplayName();
                        if (currentUserName == null || currentUserName.isEmpty()) {
                            currentUserName = currentUser.getEmail();
                        }
                    });
        } else {
            // No user logged in, redirect to login
            Toast.makeText(this, "Silakan login terlebih dahulu", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void getIntentData() {
        try {
            Intent intent = getIntent();
            if (intent != null && intent.hasExtra("DONASI_DATA")) {
                donasi = intent.getParcelableExtra("DONASI_DATA");
                if (donasi == null) {
                    showErrorAndFinish("Data donasi tidak valid");
                    return;
                }
                Log.d(TAG, "Received donasi: " + donasi.getJudul());
            } else {
                showErrorAndFinish("Tidak ada data donasi");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting intent data", e);
            showErrorAndFinish("Error: " + e.getMessage());
        }
    }

    private void showErrorAndFinish(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        finish();
    }

    private void initViews() {
        etAmount = findViewById(R.id.etAmount);
        btnConfirm = findViewById(R.id.btnConfirm);
        rgPaymentMethods = findViewById(R.id.rgPaymentMethods);

        // Initialize radio buttons
        rbBni = findViewById(R.id.rbBni);
        rbBri = findViewById(R.id.rbBri);
        rbBca = findViewById(R.id.rbBca);
        rbDana = findViewById(R.id.rbDana);
        rbShopeePay = findViewById(R.id.rbShopeePay);
        rbGopay = findViewById(R.id.rbGopay);

        // Set default payment method
        rgPaymentMethods.check(R.id.rbBni);
        selectedPaymentMethod = getString(R.string.bank_bni);
    }

    private void setupListeners() {
        // Amount input listener
        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    String cleanString = s.toString().replaceAll("[^\\d]", "");
                    donationAmount = cleanString.isEmpty() ? 0 : Long.parseLong(cleanString);
                } catch (NumberFormatException e) {
                    donationAmount = 0;
                    Log.e(TAG, "Error parsing amount", e);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Quick amount buttons
        findViewById(R.id.btn10000).setOnClickListener(v -> setAmount(10000));
        findViewById(R.id.btn15000).setOnClickListener(v -> setAmount(15000));
        findViewById(R.id.btn25000).setOnClickListener(v -> setAmount(25000));
        findViewById(R.id.btn50000).setOnClickListener(v -> setAmount(50000));
        findViewById(R.id.btn100000).setOnClickListener(v -> setAmount(100000));

        // Payment method selection
        rgPaymentMethods.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbBni) {
                selectedPaymentMethod = getString(R.string.bank_bni);
            } else if (checkedId == R.id.rbBri) {
                selectedPaymentMethod = getString(R.string.bank_bri);
            } else if (checkedId == R.id.rbBca) {
                selectedPaymentMethod = getString(R.string.bank_bca);
            } else if (checkedId == R.id.rbDana) {
                selectedPaymentMethod = getString(R.string.wallet_dana);
            } else if (checkedId == R.id.rbShopeePay) {
                selectedPaymentMethod = getString(R.string.wallet_shopeepay);
            } else if (checkedId == R.id.rbGopay) {
                selectedPaymentMethod = getString(R.string.wallet_gopay);
            }
            Log.d(TAG, "Selected payment method: " + selectedPaymentMethod);
        });

        // Whole layout click listeners - FIXED VERSION
        findViewById(R.id.layoutBni).setOnClickListener(v -> {
            rgPaymentMethods.check(R.id.rbBni);
        });
        findViewById(R.id.layoutBri).setOnClickListener(v -> {
            rgPaymentMethods.check(R.id.rbBri);
        });
        findViewById(R.id.layoutBca).setOnClickListener(v -> {
            rgPaymentMethods.check(R.id.rbBca);
        });
        findViewById(R.id.layoutDana).setOnClickListener(v -> {
            rgPaymentMethods.check(R.id.rbDana);
        });
        findViewById(R.id.layoutShopeePay).setOnClickListener(v -> {
            rgPaymentMethods.check(R.id.rbShopeePay);
        });
        findViewById(R.id.layoutGopay).setOnClickListener(v -> {
            rgPaymentMethods.check(R.id.rbGopay);
        });

        // Back button
        findViewById(R.id.ivBack).setOnClickListener(v -> finish());

        // Confirm button
        btnConfirm.setOnClickListener(v -> {
            if (validateInput()) {
                showConfirmationDialog();
            }
        });
    }

    private void showConfirmationDialog() {
        // Format amount with currency
        String formattedAmount = String.format("Rp %,d", donationAmount);

        // Create confirmation message
        String message = String.format(
                "Apakah Anda yakin akan melakukan donasi?\n\n" +
                        "👤 Nama Donatur: %s\n\n" +
                        "📋 Kampanye: %s\n\n" +
                        "💰 Jumlah Donasi: %s\n\n" +
                        "💳 Metode Pembayaran: %s\n\n" +
                        "Donasi yang telah dikonfirmasi tidak dapat dibatalkan.",
                currentUserName,
                donasi.getJudul(),
                formattedAmount,
                selectedPaymentMethod
        );

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Donasi")
                .setMessage(message)
                .setIcon(R.drawable.ic_info) // Anda bisa ganti dengan icon yang sesuai
                .setPositiveButton("Ya, Lanjutkan", (dialog, which) -> {
                    dialog.dismiss();
                    processDonation();
                })
                .setNegativeButton("Batal", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setCancelable(false) // Tidak bisa dismiss dengan back button
                .show();
    }

    private boolean validateInput() {
        if (donationAmount < 10000) {
            Toast.makeText(this, R.string.min_donation_error, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedPaymentMethod.isEmpty()) {
            Toast.makeText(this, R.string.select_payment_error, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (currentUserName == null || currentUserName.isEmpty()) {
            Toast.makeText(this, "Data pengguna tidak ditemukan, silakan login ulang", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void loadCampaignData() {
        if (donasi != null) {
            ((TextView) findViewById(R.id.tvTitle)).setText(donasi.getJudul());
            ((TextView) findViewById(R.id.tvDescription)).setText(donasi.getDeskripsi());
        }
    }

    private void setAmount(long amount) {
        donationAmount = amount;
        etAmount.setText(String.valueOf(amount));
        etAmount.setSelection(etAmount.getText().length());
    }

    private void processDonation() {
        btnConfirm.setEnabled(false);
        btnConfirm.setText(R.string.processing);

        // Update campaign data in Firestore
        db.collection("donasi")
                .document(donasi.getId())
                .update("terkumpul", FieldValue.increment(donationAmount))
                .addOnSuccessListener(aVoid -> saveTransaction())
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update donation", e);
                    showError(getString(R.string.donation_failed));
                    resetButton();
                });
    }

    private void saveTransaction() {
        Map<String, Object> transaction = new HashMap<>();
        transaction.put("campaignId", donasi.getId());
        transaction.put("campaignTitle", donasi.getJudul());
        transaction.put("amount", donationAmount);
        transaction.put("paymentMethod", selectedPaymentMethod);
        transaction.put("nama", currentUserName); // Added user name
        transaction.put("userId", currentUserId); // Added user ID for reference
        transaction.put("timestamp", FieldValue.serverTimestamp());
        transaction.put("status", "success");

        db.collection("transactions")
                .add(transaction)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Transaction saved with ID: " + documentReference.getId());
                    navigateToInvoice(documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to save transaction", e);
                    showError(getString(R.string.transaction_failed));
                    resetButton();
                });
    }

    private void navigateToInvoice(String transactionId) {
        try {
            Intent intent = new Intent(this, InvoicePembayaranActivity.class);
            intent.putExtra("TRANSACTION_ID", transactionId);
            intent.putExtra("CAMPAIGN_TITLE", donasi.getJudul());
            intent.putExtra("DONATION_AMOUNT", donationAmount);
            intent.putExtra("PAYMENT_METHOD", selectedPaymentMethod);
            intent.putExtra("USER_NAME", currentUserName); // Added user name
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Failed to navigate to invoice", e);
            showError("Gagal membuka halaman invoice");
            resetButton();
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void resetButton() {
        btnConfirm.setEnabled(true);
        btnConfirm.setText(R.string.continue_payment);
    }
}