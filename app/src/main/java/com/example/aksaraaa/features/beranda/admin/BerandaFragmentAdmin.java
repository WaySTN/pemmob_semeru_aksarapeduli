package com.example.aksaraaa.features.beranda.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.aksaraaa.R;
import com.example.aksaraaa.features.beranda.admin.history.HistoryDonasiAdminFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class BerandaFragmentAdmin extends Fragment {

    private static final String TAG = "BerandaFragmentAdmin";

    // UI Components
    private TextView tvAdminName;
    private TextView tvTotalCampaign;
    private TextView tvTotalArtikel;
    private Button btnLihatSemuaTransaksi;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_beranda_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupUI();
        loadData();
    }

    private void initViews(View view) {
        tvAdminName = view.findViewById(R.id.tvAdminName);
        tvTotalCampaign = view.findViewById(R.id.tvTotalCampaign);
        tvTotalArtikel = view.findViewById(R.id.tvTotalArtikel);
        btnLihatSemuaTransaksi = view.findViewById(R.id.btnLihatSemuaTransaksi);
    }

    private void setupUI() {
        // Set admin name from Firebase Auth
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String displayName = currentUser.getDisplayName();
            if (displayName != null && !displayName.isEmpty()) {
                tvAdminName.setText(displayName);
            } else {
                tvAdminName.setText("Administrator");
            }
        } else {
            tvAdminName.setText("Administrator");
        }

        btnLihatSemuaTransaksi.setOnClickListener(v -> {
            try {
                Fragment fragment = new HistoryDonasiAdminFragment();
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.AdminFragment, fragment)
                        .addToBackStack(null)
                        .commit();
            } catch (Exception e) {
                Log.e(TAG, "Fragment transaction error: " + e.getMessage());
                Toast.makeText(getContext(), "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadData() {
        loadCampaignCount();
        loadArtikelCount();
    }

    private void loadCampaignCount() {
        db.collection("donasi")
                .whereEqualTo("aktif", true) // Only count active campaigns
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            int campaignCount = querySnapshot.size();
                            tvTotalCampaign.setText(String.valueOf(campaignCount));
                            Log.d(TAG, "Total active campaigns: " + campaignCount);
                        } else {
                            tvTotalCampaign.setText("0");
                        }
                    } else {
                        Log.e(TAG, "Error getting campaign documents: ", task.getException());
                        tvTotalCampaign.setText("0");
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Gagal memuat data campaign", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void loadArtikelCount() {
        db.collection("kabar")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            int artikelCount = querySnapshot.size();
                            tvTotalArtikel.setText(String.valueOf(artikelCount));
                            Log.d(TAG, "Total articles: " + artikelCount);
                        } else {
                            tvTotalArtikel.setText("0");
                        }
                    } else {
                        Log.e(TAG, "Error getting article documents: ", task.getException());
                        tvTotalArtikel.setText("0");
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Gagal memuat data artikel", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when fragment resumes
        loadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up references
        tvAdminName = null;
        tvTotalCampaign = null;
        tvTotalArtikel = null;
        btnLihatSemuaTransaksi = null;
    }
}