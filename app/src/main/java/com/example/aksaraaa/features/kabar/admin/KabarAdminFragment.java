package com.example.aksaraaa.features.kabar.admin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.aksaraaa.R;
import com.example.aksaraaa.models.Kabar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class KabarAdminFragment extends Fragment implements KabarAdminAdapter.OnKabarClickListener {
    private static final String TAG = "KabarAdminFragment";

    private RecyclerView rvKabar;
    private MaterialButton btnTambah;
    private KabarAdminAdapter adapter;
    private List<Kabar> kabarList = new ArrayList<>();
    private FirebaseFirestore db;
    private ListenerRegistration listenerRegistration;
    private View emptyView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kabar_admin, container, false);

        rvKabar = view.findViewById(R.id.rvKabar);
        btnTambah = view.findViewById(R.id.btnTambahKabar);
        emptyView = view.findViewById(R.id.layout_empty);

        setupRecyclerView();
        setupButtonListeners();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setupFirestoreListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }

    private void setupRecyclerView() {
        adapter = new KabarAdminAdapter(kabarList, this);
        rvKabar.setLayoutManager(new LinearLayoutManager(getContext()));
        rvKabar.setAdapter(adapter);
    }

    private void setupButtonListeners() {
        btnTambah.setOnClickListener(v -> {
            AddEditKabarFragment fragment = new AddEditKabarFragment();
            fragment.setTargetFragment(this, 1);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.AdminFragment, fragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void setupFirestoreListener() {
        listenerRegistration = db.collection("kabar")
                .orderBy("tanggal", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Listen failed.", error);
                        Toast.makeText(getContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    kabarList.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        Kabar kabar = doc.toObject(Kabar.class);
                        kabar.setId(doc.getId());
                        kabarList.add(kabar);
                    }
                    adapter.notifyDataSetChanged();

                    updateEmptyState();
                });
    }

    private void updateEmptyState() {
        if (emptyView != null && rvKabar != null) {
            emptyView.setVisibility(kabarList.isEmpty() ? View.VISIBLE : View.GONE);
            rvKabar.setVisibility(kabarList.isEmpty() ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onEditClick(Kabar kabar) {
        AddEditKabarFragment fragment = AddEditKabarFragment.newInstance(kabar);
        fragment.setTargetFragment(this, 1);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.AdminFragment, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDeleteClick(Kabar kabar) {
        // Create confirmation dialog
        new AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi Hapus")
                .setMessage("Apakah Anda yakin ingin menghapus kabar ini?")
                .setPositiveButton("Ya", (dialog, which) -> {
                    // User confirmed, proceed with deletion
                    deleteKabar(kabar);
                })
                .setNegativeButton("Tidak", (dialog, which) -> {
                    // User cancelled, do nothing
                    dialog.dismiss();
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteKabar(Kabar kabar) {
        // Show loading indicator
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Menghapus kabar...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        db.collection("kabar").document(kabar.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Kabar berhasil dihapus", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Gagal menghapus kabar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error deleting document", e);
                });
    }
}