package com.example.aksaraaa.features.donasi.user;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.aksaraaa.R;
import com.example.aksaraaa.models.Donasi;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DonasiFragmentUser extends Fragment {

    private static final String TAG = "DonasiFragmentUser";
    private RecyclerView rvDonasiUser;
    private View layoutEmpty;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DonasiUserAdapter adapter;
    private List<Donasi> donasiList = new ArrayList<>();
    private FirebaseFirestore db;
    private com.google.firebase.firestore.ListenerRegistration listenerRegistration;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donasi_user, container, false);

        db = FirebaseFirestore.getInstance();
        initializeViews(view);
        setupRecyclerView();
        setupSwipeRefresh();
        setupRealtimeListener(); // Gunakan real-time listener yang sudah diperbaiki

        return view;
    }

    private void initializeViews(View view) {
        rvDonasiUser = view.findViewById(R.id.rvDonasiUser);
        layoutEmpty = view.findViewById(R.id.layout_empty_donasi_user);
        progressBar = view.findViewById(R.id.progressBar);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
    }

    private void setupRecyclerView() {
        adapter = new DonasiUserAdapter(donasiList, this::showDetailDonasi);
        rvDonasiUser.setLayoutManager(new LinearLayoutManager(getContext()));
        rvDonasiUser.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Refresh data manually jika diperlukan
            loadDonasiData();
        });
    }

    // SOLUSI: Real-time listener tanpa ordering yang kompleks
    private void setupRealtimeListener() {
        progressBar.setVisibility(View.VISIBLE);

        // Query sederhana tanpa orderBy untuk menghindari error index
        listenerRegistration = db.collection("donasi")
                .whereEqualTo("aktif", true)
                .addSnapshotListener((value, error) -> {
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);

                    if (error != null) {
                        Log.e(TAG, "Listen failed.", error);
                        showError("Gagal memuat data: " + error.getMessage());
                        return;
                    }

                    if (value != null) {
                        Log.d(TAG, "Real-time update: found " + value.size() + " active documents");

                        donasiList.clear();
                        for (QueryDocumentSnapshot document : value) {
                            try {
                                Boolean isAktif = document.getBoolean("aktif");
                                Log.d(TAG, "Document " + document.getId() + " aktif status: " + isAktif);

                                if (isAktif != null && isAktif) {
                                    Donasi donasi = document.toObject(Donasi.class);
                                    donasi.setId(document.getId());
                                    donasiList.add(donasi);
                                    Log.d(TAG, "Added active donation: " + donasi.getJudul());
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing document", e);
                            }
                        }

                        // Sort data di client side berdasarkan createdAt
                        sortDonasiList();

                        Log.d(TAG, "Total active donasi in real-time: " + donasiList.size());

                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                        updateEmptyState();
                    }
                });
    }

    // Method untuk sorting data di client side
    private void sortDonasiList() {
        try {
            Collections.sort(donasiList, (d1, d2) -> {
                // Asumsikan ada method getCreatedAt() di model Donasi
                // Jika tidak ada, Anda bisa menggunakan field lain atau timestamp
                if (d1.getCreatedAt() != null && d2.getCreatedAt() != null) {
                    return d2.getCreatedAt().compareTo(d1.getCreatedAt()); // Descending order
                }
                return 0;
            });
        } catch (Exception e) {
            Log.w(TAG, "Error sorting donasi list", e);
        }
    }

    // Method loadDonasiData yang diperbaiki
    private void loadDonasiData() {
        progressBar.setVisibility(View.VISIBLE);

        Log.d(TAG, "Starting to load donasi data...");

        // Query sederhana tanpa orderBy untuk menghindari index error
        db.collection("donasi")
                .whereEqualTo("aktif", true)
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);

                    if (task.isSuccessful() && task.getResult() != null) {
                        Log.d(TAG, "Query successful, found " + task.getResult().size() + " active documents");

                        donasiList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                Log.d(TAG, "Processing document: " + document.getId());

                                Boolean isAktif = document.getBoolean("aktif");
                                Log.d(TAG, "Document aktif status: " + isAktif);

                                if (isAktif != null && isAktif) {
                                    Donasi donasi = document.toObject(Donasi.class);
                                    donasi.setId(document.getId());
                                    donasiList.add(donasi);
                                    Log.d(TAG, "Successfully loaded active donation: " + donasi.getJudul());
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing document: " + document.getId(), e);
                            }
                        }

                        // Sort data di client side
                        sortDonasiList();

                        Log.d(TAG, "Total active donasi loaded: " + donasiList.size());

                        // Update adapter
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }

                        updateEmptyState();
                    } else {
                        Log.e(TAG, "Error getting documents: ", task.getException());
                        showError("Gagal memuat data donasi: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    Log.e(TAG, "Failed to load data", e);
                    showError("Gagal memuat data: " + e.getMessage());
                });
    }

    // Alternative method: Load semua data lalu filter di client
    private void loadDonasiDataAlternative() {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("donasi")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);

                    donasiList.clear();
                    Log.d(TAG, "Found " + queryDocumentSnapshots.size() + " documents");

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            // Filter aktif di client side
                            Boolean aktif = document.getBoolean("aktif");
                            if (aktif != null && aktif) {
                                Donasi donasi = document.toObject(Donasi.class);
                                donasi.setId(document.getId());
                                donasiList.add(donasi);
                                Log.d(TAG, "Added active donation: " + donasi.getJudul());
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing document", e);
                        }
                    }

                    // Sort data di client side
                    sortDonasiList();

                    adapter.notifyDataSetChanged();
                    updateEmptyState();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    Log.e(TAG, "Error loading data", e);
                    showError("Gagal memuat data: " + e.getMessage());
                });
    }

    private void updateEmptyState() {
        Log.d(TAG, "Updating empty state. List size: " + donasiList.size());

        if (donasiList.isEmpty()) {
            rvDonasiUser.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
            Log.d(TAG, "Showing empty state");
        } else {
            rvDonasiUser.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
            Log.d(TAG, "Showing RecyclerView with " + donasiList.size() + " items");
        }
    }

    private void showDetailDonasi(Donasi donasi) {
        Log.d(TAG, "Show detail for: " + donasi.getJudul());

        try {
            if (donasi != null && getActivity() != null) {
                // Buat instance DetailDonasiFragment
                DetailDonasiFragment detailFragment = DetailDonasiFragment.newInstance(donasi);

                // Navigate ke detail fragment
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.UserFragment, detailFragment) // Ganti dengan ID container Anda
                        .addToBackStack(null)
                        .commit();

                Log.d(TAG, "Navigated to detail fragment");
            } else {
                Log.e(TAG, "Cannot navigate: donasi or activity is null");
                showError("Tidak dapat membuka detail donasi");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to detail", e);
            showError("Gagal membuka detail donasi");
        }
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
        Log.e(TAG, "Error: " + message);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Hapus listener saat fragment di-destroy
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data saat fragment kembali terlihat
        if (donasiList.isEmpty()) {
            loadDonasiData();
        }
    }
}