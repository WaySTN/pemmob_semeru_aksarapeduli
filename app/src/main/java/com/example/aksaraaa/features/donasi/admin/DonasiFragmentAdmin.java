package com.example.aksaraaa.features.donasi.admin;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.aksaraaa.R;
import com.example.aksaraaa.models.Donasi;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DonasiFragmentAdmin extends Fragment implements DonasiAdminAdapter.OnItemClickListener {

    private RecyclerView rvDonasiAdmin;
    private View layoutEmpty;
    private MaterialButton btnTambahCampaign, btnTambahPertama;
    private DonasiAdminAdapter adapter;
    private List<Donasi> donasiList = new ArrayList<>();
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donasi_admin, container, false);

        db = FirebaseFirestore.getInstance();

        // Inisialisasi view
        rvDonasiAdmin = view.findViewById(R.id.rvDonasiAdmin);
        layoutEmpty = view.findViewById(R.id.layout_empty_donasi_admin);
        btnTambahCampaign = view.findViewById(R.id.btnTambahCampaign);
        btnTambahPertama = view.findViewById(R.id.btnTambahPertama);

        // Setup RecyclerView
        adapter = new DonasiAdminAdapter(donasiList, this);
        rvDonasiAdmin.setLayoutManager(new LinearLayoutManager(getContext()));
        rvDonasiAdmin.setAdapter(adapter);

        // Button listeners
        btnTambahCampaign.setOnClickListener(v -> showAddEditDonasiFragment(null));
        btnTambahPertama.setOnClickListener(v -> showAddEditDonasiFragment(null));

        // Load data dari Firebase
        loadDonasiData();

        return view;
    }

    private void loadDonasiData() {
        db.collection("donasi")
                .orderBy("createdAt")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        donasiList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Donasi donasi = document.toObject(Donasi.class);
                            donasi.setId(document.getId());
                            donasiList.add(donasi);
                        }
                        adapter.updateData(donasiList);
                        updateEmptyState();
                    } else {
                        Toast.makeText(getContext(), "Gagal memuat data donasi", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateEmptyState() {
        if (donasiList.isEmpty()) {
            rvDonasiAdmin.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            rvDonasiAdmin.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    private void showAddEditDonasiFragment(Donasi donasi) {
        AddEditDonasiFragment fragment = AddEditDonasiFragment.newInstance(donasi);
        fragment.setDonasiListener(new AddEditDonasiFragment.DonasiListener() {
            @Override
            public void onDonasiSaved() {
                loadDonasiData();
            }
        });
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.AdminFragment, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onEditClick(Donasi donasi) {
        showAddEditDonasiFragment(donasi);
    }

    @Override
    public void onDeleteClick(Donasi donasi) {
        // Buat AlertDialog untuk konfirmasi
        new AlertDialog.Builder(getContext())
                .setTitle("Konfirmasi Hapus")
                .setMessage("Apakah Anda yakin ingin menghapus campaign \"" + donasi.getJudul() + "\"?\n\nTindakan ini tidak dapat dibatalkan.")
                .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User konfirmasi hapus, lakukan penghapusan
                        deleteDonasiFromDatabase(donasi);
                    }
                })
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User membatalkan, tutup dialog
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteDonasiFromDatabase(Donasi donasi) {
        db.collection("donasi").document(donasi.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Campaign berhasil dihapus", Toast.LENGTH_SHORT).show();
                    loadDonasiData();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Gagal menghapus campaign", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onToggleStatusClick(Donasi donasi) {
        boolean newStatus = !donasi.isAktif();
        db.collection("donasi").document(donasi.getId())
                .update("aktif", newStatus)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(),
                            "Status campaign diubah menjadi " + (newStatus ? "Aktif" : "Nonaktif"),
                            Toast.LENGTH_SHORT).show();
                    loadDonasiData();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Gagal mengubah status campaign", Toast.LENGTH_SHORT).show();
                });
    }
}