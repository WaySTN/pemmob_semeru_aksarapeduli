package com.example.aksaraaa.features.beranda.admin.history;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.aksaraaa.R;
import com.example.aksaraaa.models.Transaction;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistoryDonasiAdminFragment extends Fragment {

    private static final String TAG = "HistoryDonasiAdmin";

    // UI Components
    private ImageView ivBack;
    private TextView tvTotalDonasi;
    private TextView tvTotalTransaksi;
    private TextInputEditText etSearch;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView rvTransactions;
    private LinearLayout layoutLoading;
    private LinearLayout layoutEmpty;
    private TextView tvErrorMessage;

    // Data
    private List<Transaction> transactionList;
    private List<Transaction> filteredList;
    private TransactionAdminAdapter adapter;

    // Firebase
    private FirebaseFirestore db;

    // Statistics
    private double totalDonasi = 0.0;
    private int totalTransaksi = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");

        try {
            // Initialize Firebase
            db = FirebaseFirestore.getInstance();
            Log.d(TAG, "Firebase initialized successfully");

            // Initialize lists
            transactionList = new ArrayList<>();
            filteredList = new ArrayList<>();
            Log.d(TAG, "Lists initialized successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            showSafeToast("Error saat inisialisasi: " + e.getMessage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");

        try {
            View view = inflater.inflate(R.layout.fragment_history_donasi_admin, container, false);
            Log.d(TAG, "Layout inflated successfully");
            return view;
        } catch (Exception e) {
            Log.e(TAG, "Error inflating layout: " + e.getMessage(), e);
            showSafeToast("Error memuat tampilan: Layout tidak ditemukan");

            // Return empty view sebagai fallback
            return new View(requireContext());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated called");

        try {
            if (!initViews(view)) {
                showSafeToast("Gagal memuat tampilan: Beberapa komponen tidak ditemukan");
                return;
            }

            if (!setupRecyclerView()) {
                showSafeToast("Gagal mengatur daftar transaksi");
                return;
            }

            setupListeners();
            loadTransactions();

        } catch (Exception e) {
            Log.e(TAG, "Error in onViewCreated: " + e.getMessage(), e);
            showSafeToast("Terjadi kesalahan saat memuat halaman: " + e.getMessage());
        }
    }

    private boolean initViews(View view) {
        try {
            Log.d(TAG, "Initializing views...");

            ivBack = view.findViewById(R.id.ivBack);
            tvTotalDonasi = view.findViewById(R.id.tvTotalDonasi);
            tvTotalTransaksi = view.findViewById(R.id.tvTotalTransaksi);
            etSearch = view.findViewById(R.id.etSearch);
            swipeRefresh = view.findViewById(R.id.swipeRefresh);
            rvTransactions = view.findViewById(R.id.rvTransactions);
            layoutLoading = view.findViewById(R.id.layoutLoading);
            layoutEmpty = view.findViewById(R.id.layoutEmpty);

            // Check for missing critical views
            boolean allViewsFound = true;

            if (rvTransactions == null) {
                Log.e(TAG, "RecyclerView (rvTransactions) not found in layout");
                allViewsFound = false;
            }

            if (tvTotalDonasi == null) {
                Log.w(TAG, "tvTotalDonasi not found - will skip total display");
            }

            if (tvTotalTransaksi == null) {
                Log.w(TAG, "tvTotalTransaksi not found - will skip count display");
            }

            if (layoutLoading == null) {
                Log.w(TAG, "layoutLoading not found - will skip loading indicator");
            }

            if (layoutEmpty == null) {
                Log.w(TAG, "layoutEmpty not found - will skip empty state");
            }

            Log.d(TAG, "Views initialization completed. All critical views found: " + allViewsFound);
            return allViewsFound;

        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage(), e);
            return false;
        }
    }

    private boolean setupRecyclerView() {
        try {
            if (rvTransactions == null) {
                Log.e(TAG, "Cannot setup RecyclerView - view is null");
                return false;
            }

            adapter = new TransactionAdminAdapter(filteredList, getContext());
            rvTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
            rvTransactions.setAdapter(adapter);

            Log.d(TAG, "RecyclerView setup completed successfully");
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Error setting up RecyclerView: " + e.getMessage(), e);
            return false;
        }
    }

    private void setupListeners() {
        try {
            // Back button
            if (ivBack != null) {
                ivBack.setOnClickListener(v -> {
                    try {
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error in back button: " + e.getMessage());
                    }
                });
            }

            // Search functionality
            if (etSearch != null) {
                etSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        try {
                            filterTransactions(s.toString());
                        } catch (Exception e) {
                            Log.e(TAG, "Error filtering: " + e.getMessage());
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                });
            }

            // Swipe refresh
            if (swipeRefresh != null) {
                swipeRefresh.setOnRefreshListener(() -> {
                    try {
                        loadTransactions();
                    } catch (Exception e) {
                        Log.e(TAG, "Error in swipe refresh: " + e.getMessage());
                        if (swipeRefresh != null) {
                            swipeRefresh.setRefreshing(false);
                        }
                    }
                });
            }

            Log.d(TAG, "Listeners setup completed");

        } catch (Exception e) {
            Log.e(TAG, "Error setting up listeners: " + e.getMessage(), e);
        }
    }

    private void loadTransactions() {
        try {
            Log.d(TAG, "Loading transactions...");
            showLoading(true);

            if (db == null) {
                Log.e(TAG, "Firestore instance is null");
                showSafeToast("Database tidak tersedia");
                showLoading(false);
                return;
            }

            db.collection("transactions")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(task -> {
                        try {
                            if (swipeRefresh != null) {
                                swipeRefresh.setRefreshing(false);
                            }

                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                if (querySnapshot != null) {
                                    processTransactionData(querySnapshot);
                                } else {
                                    Log.w(TAG, "Query result is null");
                                    showSafeToast("Data transaksi tidak ditemukan");
                                }
                            } else {
                                Log.e(TAG, "Error getting transactions: ", task.getException());
                                String errorMsg = task.getException() != null ?
                                        task.getException().getMessage() : "Unknown error";
                                showSafeToast("Gagal memuat data: " + errorMsg);
                            }

                        } catch (Exception e) {
                            Log.e(TAG, "Error in transaction loading callback: " + e.getMessage(), e);
                            showSafeToast("Error memproses data: " + e.getMessage());
                        } finally {
                            showLoading(false);
                        }
                    })
                    .addOnFailureListener(e -> {
                        try {
                            Log.e(TAG, "Failed to load transactions: " + e.getMessage(), e);
                            if (swipeRefresh != null) {
                                swipeRefresh.setRefreshing(false);
                            }
                            showLoading(false);
                            showSafeToast("Gagal terhubung ke database: " + e.getMessage());
                        } catch (Exception ex) {
                            Log.e(TAG, "Error in failure callback: " + ex.getMessage(), ex);
                        }
                    });

        } catch (Exception e) {
            Log.e(TAG, "Error in loadTransactions: " + e.getMessage(), e);
            showLoading(false);
            showSafeToast("Terjadi kesalahan saat memuat data: " + e.getMessage());
        }
    }

    private void processTransactionData(QuerySnapshot querySnapshot) {
        try {
            transactionList.clear();
            filteredList.clear();

            totalDonasi = 0.0;
            totalTransaksi = 0;

            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                try {
                    Transaction transaction = document.toObject(Transaction.class);
                    if (transaction != null) {
                        transaction.setId(document.getId());
                        transactionList.add(transaction);

                        // Calculate statistics for successful transactions
                        if ("success".equalsIgnoreCase(transaction.getStatus()) ||
                                "berhasil".equalsIgnoreCase(transaction.getStatus())) {
                            totalDonasi += transaction.getAmount();
                            totalTransaksi++;
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing transaction document: " + document.getId() + " - " + e.getMessage());
                }
            }

            filteredList.addAll(transactionList);
            updateUI();

            Log.d(TAG, "Successfully loaded " + transactionList.size() + " transactions");

            if (transactionList.isEmpty()) {
                showSafeToast("Belum ada transaksi yang tercatat");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error processing transaction data: " + e.getMessage(), e);
            showSafeToast("Error memproses data transaksi: " + e.getMessage());
        }
    }

    private void filterTransactions(String query) {
        try {
            if (transactionList == null) {
                return;
            }

            filteredList.clear();

            if (query == null || query.trim().isEmpty()) {
                filteredList.addAll(transactionList);
            } else {
                String searchQuery = query.toLowerCase().trim();

                for (Transaction transaction : transactionList) {
                    if (transaction == null) continue;

                    // Search by donor name, campaign title, or transaction ID
                    if ((transaction.getDonorName() != null &&
                            transaction.getDonorName().toLowerCase().contains(searchQuery)) ||
                            (transaction.getCampaignTitle() != null &&
                                    transaction.getCampaignTitle().toLowerCase().contains(searchQuery)) ||
                            (transaction.getId() != null &&
                                    transaction.getId().toLowerCase().contains(searchQuery))) {

                        filteredList.add(transaction);
                    }
                }
            }

            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            updateEmptyState();

        } catch (Exception e) {
            Log.e(TAG, "Error filtering transactions: " + e.getMessage(), e);
        }
    }

    private void updateUI() {
        try {
            // Update statistics
            if (tvTotalDonasi != null) {
                try {
                    NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                    tvTotalDonasi.setText(formatter.format(totalDonasi));
                } catch (Exception e) {
                    tvTotalDonasi.setText("Rp " + String.format("%.0f", totalDonasi));
                }
            }

            if (tvTotalTransaksi != null) {
                tvTotalTransaksi.setText(String.valueOf(totalTransaksi));
            }

            // Update adapter
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }

            // Update empty state
            updateEmptyState();

        } catch (Exception e) {
            Log.e(TAG, "Error updating UI: " + e.getMessage(), e);
        }
    }

    private void updateEmptyState() {
        try {
            boolean isEmpty = filteredList == null || filteredList.isEmpty();

            if (rvTransactions != null) {
                rvTransactions.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
            }

            if (layoutEmpty != null) {
                layoutEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error updating empty state: " + e.getMessage(), e);
        }
    }

    private void showLoading(boolean show) {
        try {
            if (show) {
                if (layoutLoading != null) {
                    layoutLoading.setVisibility(View.VISIBLE);
                }
                if (rvTransactions != null) {
                    rvTransactions.setVisibility(View.GONE);
                }
                if (layoutEmpty != null) {
                    layoutEmpty.setVisibility(View.GONE);
                }
            } else {
                if (layoutLoading != null) {
                    layoutLoading.setVisibility(View.GONE);
                }
                updateEmptyState();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing loading: " + e.getMessage(), e);
        }
    }

    private void showSafeToast(String message) {
        try {
            if (getContext() != null && isAdded()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
            Log.w(TAG, "Toast message: " + message);
        } catch (Exception e) {
            Log.e(TAG, "Error showing toast: " + e.getMessage() + " - Original message: " + message);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            Log.d(TAG, "onResume called");
            // Refresh data when fragment resumes, but only if we don't have data yet
            if (transactionList == null || transactionList.isEmpty()) {
                loadTransactions();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onResume: " + e.getMessage(), e);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            Log.d(TAG, "onDestroyView called");

            // Clean up references
            if (transactionList != null) {
                transactionList.clear();
            }
            if (filteredList != null) {
                filteredList.clear();
            }

            // Nullify views
            ivBack = null;
            tvTotalDonasi = null;
            tvTotalTransaksi = null;
            etSearch = null;
            swipeRefresh = null;
            rvTransactions = null;
            layoutLoading = null;
            layoutEmpty = null;
            adapter = null;

        } catch (Exception e) {
            Log.e(TAG, "Error in onDestroyView: " + e.getMessage(), e);
        }
    }
}