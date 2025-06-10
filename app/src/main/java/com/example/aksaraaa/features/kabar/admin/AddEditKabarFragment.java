package com.example.aksaraaa.features.kabar.admin;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.aksaraaa.R;
import com.example.aksaraaa.models.Kabar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddEditKabarFragment extends Fragment {
    private TextInputEditText etJudul, etIsi, etTanggal;
    private MaterialButton btnSimpan;
    private FirebaseFirestore db;
    private Kabar kabarToEdit;
    private boolean isEditMode = false;

    public static AddEditKabarFragment newInstance(Kabar kabar) {
        AddEditKabarFragment fragment = new AddEditKabarFragment();
        Bundle args = new Bundle();
        args.putSerializable("kabar", kabar);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_edit_kabar, container, false);

        initViews(view);
        checkEditMode();
        setupDatePicker();
        setupSaveButton();

        return view;
    }

    private void initViews(View view) {
        etJudul = view.findViewById(R.id.etJudulKabar);
        etIsi = view.findViewById(R.id.etIsiKabar);
        etTanggal = view.findViewById(R.id.etTanggalKabar);
        btnSimpan = view.findViewById(R.id.btnSimpanKabar);
    }

    private void checkEditMode() {
        if (getArguments() != null) {
            kabarToEdit = (Kabar) getArguments().getSerializable("kabar");
            isEditMode = true;
            populateData();
        }
    }

    private void populateData() {
        if (kabarToEdit != null) {
            etJudul.setText(kabarToEdit.getJudul());
            etIsi.setText(kabarToEdit.getIsi());

            if (kabarToEdit.getTanggal() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                etTanggal.setText(sdf.format(kabarToEdit.getTanggal().toDate()));
            }
        }
    }

    private void setupDatePicker() {
        etTanggal.setOnClickListener(v -> showDatePickerDialog());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(
                requireContext(),
                this::onDateSet,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePicker.show();
    }

    private void onDateSet(DatePicker view, int year, int month, int day) {
        String date = String.format(Locale.getDefault(), "%02d/%02d/%d", day, month + 1, year);
        etTanggal.setText(date);
    }

    private void setupSaveButton() {
        btnSimpan.setOnClickListener(v -> {
            if (validateInputs()) {
                saveOrUpdateKabar();
            }
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (etJudul.getText().toString().trim().isEmpty()) {
            etJudul.setError("Judul tidak boleh kosong");
            isValid = false;
        }

        if (etIsi.getText().toString().trim().isEmpty()) {
            etIsi.setError("Isi kabar tidak boleh kosong");
            isValid = false;
        }

        if (etTanggal.getText().toString().trim().isEmpty()) {
            etTanggal.setError("Tanggal tidak boleh kosong");
            isValid = false;
        }

        return isValid;
    }

    private void saveOrUpdateKabar() {
        Map<String, Object> kabarData = new HashMap<>();
        kabarData.put("judul", etJudul.getText().toString().trim());
        kabarData.put("isi", etIsi.getText().toString().trim());
        kabarData.put("tanggal", parseTanggal());

        if (isEditMode) {
            updateKabar(kabarData);
        } else {
            saveKabar(kabarData);
        }
    }

    private Timestamp parseTanggal() {
        String[] dateParts = etTanggal.getText().toString().split("/");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateParts[0]));
        cal.set(Calendar.MONTH, Integer.parseInt(dateParts[1]) - 1);
        cal.set(Calendar.YEAR, Integer.parseInt(dateParts[2]));
        return new Timestamp(cal.getTime());
    }

    private void saveKabar(Map<String, Object> kabarData) {
        db.collection("kabar")
                .add(kabarData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Kabar berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                    navigateBack();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Gagal menambahkan kabar", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }

    private void updateKabar(Map<String, Object> kabarData) {
        db.collection("kabar").document(kabarToEdit.getId())
                .update(kabarData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Kabar berhasil diperbarui", Toast.LENGTH_SHORT).show();
                    navigateBack();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Gagal memperbarui kabar", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }

    private void navigateBack() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}