package com.example.aksaraaa.features.donasi.admin;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.aksaraaa.R;
import com.example.aksaraaa.models.Donasi;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddEditDonasiFragment extends Fragment {

    private TextInputEditText etJudulCampaign, etDeskripsiCampaign, etTargetAmount, etDeadline;
    private MaterialButton btnSimpan, btnBatal;
    private ImageButton btnCalendar;
    private Donasi donasi;
    private FirebaseFirestore db;
    private DonasiListener listener;

    public interface DonasiListener {
        void onDonasiSaved();
    }

    public static AddEditDonasiFragment newInstance(Donasi donasi) {
        AddEditDonasiFragment fragment = new AddEditDonasiFragment();
        Bundle args = new Bundle();
        if (donasi != null) {
            args.putParcelable("donasi", donasi);
        }
        fragment.setArguments(args);
        return fragment;
    }

    public void setDonasiListener(DonasiListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        if (getArguments() != null && getArguments().containsKey("donasi")) {
            donasi = getArguments().getParcelable("donasi");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_edit_donasi, container, false);

        // Inisialisasi view
        etJudulCampaign = view.findViewById(R.id.etJudulCampaign);
        etDeskripsiCampaign = view.findViewById(R.id.etDeskripsiCampaign);
        etTargetAmount = view.findViewById(R.id.etTargetAmount);
        etDeadline = view.findViewById(R.id.etDeadline);
        btnSimpan = view.findViewById(R.id.btnSimpanCampaign);
        btnBatal = view.findViewById(R.id.btnBatal);
        btnCalendar = view.findViewById(R.id.btnCalendar);

        // Jika mode edit, isi data
        if (donasi != null) {
            etJudulCampaign.setText(donasi.getJudul());
            etDeskripsiCampaign.setText(donasi.getDeskripsi());
            etTargetAmount.setText(String.valueOf((int) donasi.getTargetDana()));

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            if (donasi.getDeadline() != null) {
                etDeadline.setText(dateFormat.format(donasi.getDeadline()));
            }
        }

        // Date picker
        btnCalendar.setOnClickListener(v -> showDatePicker());

        // Button listeners
        btnBatal.setOnClickListener(v -> requireActivity().onBackPressed());

        btnSimpan.setOnClickListener(v -> {
            if (validateForm()) {
                saveDonasi();
            }
        });

        return view;
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    etDeadline.setText(dateFormat.format(selectedDate.getTime()));
                },
                year, month, day);

        // Set minimal date ke hari ini
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private boolean validateForm() {
        boolean valid = true;

        if (etJudulCampaign.getText().toString().trim().isEmpty()) {
            etJudulCampaign.setError("Judul campaign harus diisi");
            valid = false;
        } else {
            etJudulCampaign.setError(null);
        }

        if (etDeskripsiCampaign.getText().toString().trim().isEmpty()) {
            etDeskripsiCampaign.setError("Deskripsi campaign harus diisi");
            valid = false;
        } else {
            etDeskripsiCampaign.setError(null);
        }

        if (etTargetAmount.getText().toString().trim().isEmpty()) {
            etTargetAmount.setError("Target donasi harus diisi");
            valid = false;
        } else {
            etTargetAmount.setError(null);
        }

        if (etDeadline.getText().toString().trim().isEmpty()) {
            etDeadline.setError("Batas waktu harus diisi");
            valid = false;
        } else {
            etDeadline.setError(null);
        }

        return valid;
    }

    private void saveDonasi() {
        String judul = etJudulCampaign.getText().toString().trim();
        String deskripsi = etDeskripsiCampaign.getText().toString().trim();
        double targetDana = Double.parseDouble(etTargetAmount.getText().toString().trim());

        // Parse tanggal deadline
        String[] dateParts = etDeadline.getText().toString().split("/");
        Calendar deadline = Calendar.getInstance();
        deadline.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateParts[0]));
        deadline.set(Calendar.MONTH, Integer.parseInt(dateParts[1]) - 1);
        deadline.set(Calendar.YEAR, Integer.parseInt(dateParts[2]));

        // URL gambar default (bisa diganti dengan upload gambar nanti)
        String gambarUrl = "https://example.com/default_campaign.jpg";

        Map<String, Object> donasiData = new HashMap<>();
        donasiData.put("judul", judul);
        donasiData.put("deskripsi", deskripsi);
        donasiData.put("gambarUrl", gambarUrl);
        donasiData.put("targetDana", targetDana);
        donasiData.put("terkumpul", 0);
        donasiData.put("deadline", deadline.getTime());
        donasiData.put("aktif", true);
        donasiData.put("createdAt", new Date());

        if (donasi == null) {
            // Tambah campaign baru
            db.collection("donasi")
                    .add(donasiData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getContext(), "Campaign berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                        if (listener != null) {
                            listener.onDonasiSaved();
                        }
                        requireActivity().onBackPressed();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Gagal menambahkan campaign", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Update campaign yang ada
            db.collection("donasi").document(donasi.getId())
                    .update(donasiData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Campaign berhasil diperbarui", Toast.LENGTH_SHORT).show();
                        if (listener != null) {
                            listener.onDonasiSaved();
                        }
                        requireActivity().onBackPressed();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Gagal memperbarui campaign", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}