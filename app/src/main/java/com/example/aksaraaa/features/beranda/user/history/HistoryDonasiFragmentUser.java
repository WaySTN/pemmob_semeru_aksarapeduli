package com.example.aksaraaa.features.beranda.user.history;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aksaraaa.R;
import com.example.aksaraaa.models.DonasiHistory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistoryDonasiFragmentUser extends Fragment {

    private static final String TAG = "HistoryDonasiFragment";

    // UI Components
    private RecyclerView rvHistoryDonasi;
    private LinearLayout layoutEmpty, layoutLoading;
    private TextView tvTotalDonasi, tvJumlahDonasi;
    private ImageView ivBack;
    private View cardSummary;

    // Data
    private List<DonasiHistory> historyList;
    private DonationHistoryAdapter adapter;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String currentUserId;

    // Statistics
    private long totalDonasi = 0;
    private int jumlahDonasi = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView started");

        View view = null;
        try {
            view = inflater.inflate(R.layout.fragment_history_donasi, container, false);
            Log.d(TAG, "Layout inflated successfully");

            // Initialize Firebase first
            initializeFirebase();

            // Initialize views
            initViews(view);

            // Setup RecyclerView
            setupRecyclerView();

            // Setup listeners
            setupListeners();

            Log.d(TAG, "onCreateView completed successfully");

        } catch (Exception e) {
            Log.e(TAG, "CRITICAL ERROR in onCreateView", e);
            if (getContext() != null) {
                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated called");

        try {
            // Load data after view is created
            loadDonationHistory();
        } catch (Exception e) {
            Log.e(TAG, "Error in onViewCreated", e);
            showError("Gagal memuat data: " + e.getMessage());
        }
    }

    private void initializeFirebase() {
        try {
            Log.d(TAG, "Initializing Firebase...");
            db = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();

            if (db == null) {
                throw new RuntimeException("FirebaseFirestore instance is null");
            }
            if (mAuth == null) {
                throw new RuntimeException("FirebaseAuth instance is null");
            }

            Log.d(TAG, "Firebase initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Firebase", e);
            throw e;
        }
    }

    private void initViews(View view) {
        try {
            Log.d(TAG, "Initializing views...");

            rvHistoryDonasi = view.findViewById(R.id.rvHistoryDonasi);
            if (rvHistoryDonasi == null) {
                throw new RuntimeException("rvHistoryDonasi not found in layout");
            }

            layoutEmpty = view.findViewById(R.id.layoutEmpty);
            if (layoutEmpty == null) {
                throw new RuntimeException("layoutEmpty not found in layout");
            }

            layoutLoading = view.findViewById(R.id.layoutLoading);
            if (layoutLoading == null) {
                throw new RuntimeException("layoutLoading not found in layout");
            }

            tvTotalDonasi = view.findViewById(R.id.tvTotalDonasi);
            if (tvTotalDonasi == null) {
                Log.w(TAG, "tvTotalDonasi not found in layout");
            }

            tvJumlahDonasi = view.findViewById(R.id.tvJumlahDonasi);
            if (tvJumlahDonasi == null) {
                Log.w(TAG, "tvJumlahDonasi not found in layout");
            }

            ivBack = view.findViewById(R.id.ivBack);
            if (ivBack == null) {
                Log.w(TAG, "ivBack not found in layout");
            }

            cardSummary = view.findViewById(R.id.cardSummary);
            if (cardSummary == null) {
                Log.w(TAG, "cardSummary not found in layout");
            }

            // Initialize list
            historyList = new ArrayList<>();

            Log.d(TAG, "Views initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views", e);
            throw e;
        }
    }

    private void setupRecyclerView() {
        try {
            Log.d(TAG, "Setting up RecyclerView...");

            if (rvHistoryDonasi == null) {
                throw new RuntimeException("RecyclerView is null");
            }

            if (historyList == null) {
                historyList = new ArrayList<>();
            }

            adapter = new DonationHistoryAdapter(historyList);
            rvHistoryDonasi.setLayoutManager(new LinearLayoutManager(getContext()));
            rvHistoryDonasi.setAdapter(adapter);

            Log.d(TAG, "RecyclerView setup completed");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up RecyclerView", e);
            throw e;
        }
    }

    private void setupListeners() {
        try {
            Log.d(TAG, "Setting up listeners...");

            if (ivBack != null) {
                ivBack.setOnClickListener(v -> {
                    try {
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error in back button click", e);
                    }
                });
            }

            Log.d(TAG, "Listeners setup completed");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up listeners", e);
        }
    }

    private void loadDonationHistory() {
        try {
            Log.d(TAG, "Loading donation history...");

            if (mAuth == null) {
                showError("FirebaseAuth tidak tersedia");
                return;
            }

            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                Log.w(TAG, "No current user found");
                showError("Silakan login terlebih dahulu");
                showEmpty(true);
                return;
            }

            currentUserId = currentUser.getUid();
            Log.d(TAG, "Loading data for user: " + currentUserId);

            showLoading(true);

            if (db == null) {
                throw new RuntimeException("Firestore database is null");
            }

            // Simple query without orderBy to avoid index issues
            db.collection("transactions")
                    .whereEqualTo("userId", currentUserId)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        try {
                            Log.d(TAG, "Firestore query successful");

                            if (historyList == null) {
                                historyList = new ArrayList<>();
                            }

                            historyList.clear();
                            totalDonasi = 0;
                            jumlahDonasi = 0;

                            Log.d(TAG, "Documents found: " + queryDocumentSnapshots.size());

                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                try {
                                    Log.d(TAG, "Processing document: " + document.getId());

                                    DonasiHistory history = new DonasiHistory();
                                    history.setId(document.getId());

                                    // Safe string retrieval
                                    history.setCampaignId(document.getString("campaignId"));
                                    history.setCampaignTitle(document.getString("campaignTitle"));
                                    history.setPaymentMethod(document.getString("paymentMethod"));
                                    history.setStatus(document.getString("status"));
                                    history.setUserId(document.getString("userId"));
                                    history.setUserName(document.getString("nama"));

                                    // Safe amount retrieval
                                    Long amount = document.getLong("amount");
                                    history.setAmount(amount != null ? amount : 0L);

                                    // Safe timestamp retrieval
                                    history.setTimestamp(document.getTimestamp("timestamp"));

                                    historyList.add(history);

                                    // Calculate statistics for successful donations
                                    if ("success".equals(history.getStatus()) && history.getAmount() != null) {
                                        totalDonasi += history.getAmount();
                                        jumlahDonasi++;
                                    }

                                    Log.d(TAG, "Added donation: " + history.getCampaignTitle() + " - " + history.getAmount());

                                } catch (Exception e) {
                                    Log.e(TAG, "Error processing document: " + document.getId(), e);
                                    // Continue with other documents
                                }
                            }

                            Log.d(TAG, "Processing completed. Total records: " + historyList.size());

                            showLoading(false);
                            updateUI();

                        } catch (Exception e) {
                            Log.e(TAG, "Error processing Firestore results", e);
                            showLoading(false);
                            showError("Gagal memproses data: " + e.getMessage());
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Firestore query failed", e);
                        showLoading(false);
                        showError("Gagal memuat data: " + e.getMessage());
                    });

        } catch (Exception e) {
            Log.e(TAG, "Error in loadDonationHistory", e);
            showLoading(false);
            showError("Terjadi kesalahan: " + e.getMessage());
        }
    }

    private void updateUI() {
        try {
            Log.d(TAG, "Updating UI...");

            if (historyList == null || historyList.isEmpty()) {
                Log.d(TAG, "No data found, showing empty state");
                showEmpty(true);
                hideSummaryCard();
            } else {
                Log.d(TAG, "Data found, updating RecyclerView");
                showEmpty(false);

                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                } else {
                    Log.w(TAG, "Adapter is null, recreating...");
                    setupRecyclerView();
                }

                updateSummaryCard();
                showSummaryCard();
            }

            Log.d(TAG, "UI update completed");
        } catch (Exception e) {
            Log.e(TAG, "Error updating UI", e);
        }
    }

    private void updateSummaryCard() {
        try {
            if (tvTotalDonasi != null && tvJumlahDonasi != null) {
                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                String formattedTotal = currencyFormat.format(totalDonasi);

                tvTotalDonasi.setText(formattedTotal);
                tvJumlahDonasi.setText(String.valueOf(jumlahDonasi));

                Log.d(TAG, "Summary updated - Total: " + formattedTotal + ", Count: " + jumlahDonasi);
            } else {
                Log.w(TAG, "Summary TextViews are null");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating summary card", e);
        }
    }

    private void showSummaryCard() {
        try {
            if (cardSummary != null) {
                cardSummary.setVisibility(View.VISIBLE);
                Log.d(TAG, "Summary card shown");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing summary card", e);
        }
    }

    private void hideSummaryCard() {
        try {
            if (cardSummary != null) {
                cardSummary.setVisibility(View.GONE);
                Log.d(TAG, "Summary card hidden");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error hiding summary card", e);
        }
    }

    private void showLoading(boolean show) {
        try {
            if (layoutLoading != null) {
                layoutLoading.setVisibility(show ? View.VISIBLE : View.GONE);
            }
            if (rvHistoryDonasi != null) {
                rvHistoryDonasi.setVisibility(show ? View.GONE : View.VISIBLE);
            }
            if (layoutEmpty != null) {
                layoutEmpty.setVisibility(View.GONE);
            }

            Log.d(TAG, "Loading state: " + show);
        } catch (Exception e) {
            Log.e(TAG, "Error in showLoading", e);
        }
    }

    private void showEmpty(boolean show) {
        try {
            if (layoutEmpty != null) {
                layoutEmpty.setVisibility(show ? View.VISIBLE : View.GONE);
            }
            if (rvHistoryDonasi != null) {
                rvHistoryDonasi.setVisibility(show ? View.GONE : View.VISIBLE);
            }

            Log.d(TAG, "Empty state: " + show);
        } catch (Exception e) {
            Log.e(TAG, "Error in showEmpty", e);
        }
    }

    private void showError(String message) {
        try {
            if (getContext() != null && isAdded()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
            Log.e(TAG, "Error: " + message);
        } catch (Exception e) {
            Log.e(TAG, "Error showing error message", e);
        }
    }

    public void refreshData() {
        try {
            Log.d(TAG, "Refreshing data...");
            loadDonationHistory();
        } catch (Exception e) {
            Log.e(TAG, "Error refreshing data", e);
            showError("Gagal me-refresh data");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Fragment destroyed");

        // Clear references
        historyList = null;
        adapter = null;
        cardSummary = null;
    }
}