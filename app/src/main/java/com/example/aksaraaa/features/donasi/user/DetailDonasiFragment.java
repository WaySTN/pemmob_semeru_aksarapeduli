package com.example.aksaraaa.features.donasi.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.aksaraaa.R;
import com.example.aksaraaa.models.Donasi;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.NumberFormat;
import java.util.Locale;

public class DetailDonasiFragment extends Fragment {

    private static final String TAG = "DetailDonasiFragment";
    private static final String ARG_DONASI = "donasi";
    private static final int PAYMENT_REQUEST_CODE = 100;

    private Donasi donasi;
    private FirebaseFirestore db;
    private ProgressBar progressBarLoading;
    private ProgressBar progressBar;

    public static DetailDonasiFragment newInstance(Donasi donasi) {
        DetailDonasiFragment fragment = new DetailDonasiFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_DONASI, donasi);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            donasi = getArguments().getParcelable(ARG_DONASI);
            Log.d(TAG, "Received donasi: " + (donasi != null ? donasi.getJudul() : "null"));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_detail_donasi, container, false);
        initViews(view);
        loadDonasiData(view);
        return view;
    }

    private void initViews(View view) {
        progressBarLoading = view.findViewById(R.id.progressBarLoading);
        progressBar = view.findViewById(R.id.progressBar);
        MaterialButton btnDonate = view.findViewById(R.id.btnDonate);
        ImageView imgCampaign = view.findViewById(R.id.imgCampaign);

        if (donasi != null && donasi.getGambarUrl() != null) {
            Glide.with(this)
                    .load(donasi.getGambarUrl())
                    .placeholder(R.drawable.pendidikan)
                    .error(R.drawable.pendidikan)
                    .into(imgCampaign);
        }

        btnDonate.setOnClickListener(v -> {
            if (donasi != null) {
                navigateToPaymentConfirmation();
            } else {
                showError("Data donasi tidak valid");
            }
        });
    }

    private void loadDonasiData(View view) {
        if (donasi != null) {
            Log.d(TAG, "Loading details for donasi: " + donasi.getJudul());
            loadDonasiDetails(view);
        } else {
            Log.e(TAG, "Donasi is null");
            showError("Data donasi tidak valid");
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        }
    }

    private void navigateToPaymentConfirmation() {
        try {
            if (getActivity() == null) {
                showError("Activity tidak tersedia");
                return;
            }

            if (!isDonationActive()) {
                showDonationNotActiveMessage();
                return;
            }

            Intent intent = new Intent(getActivity(), PaymentConfirmationActivity.class);
            intent.putExtra("DONASI_DATA", donasi);
            startActivityForResult(intent, PAYMENT_REQUEST_CODE);
            Log.d(TAG, "Navigated to PaymentConfirmationActivity");

        } catch (Exception e) {
            Log.e(TAG, "Error navigating to payment", e);
            showError("Gagal membuka halaman pembayaran");
        }
    }

    private boolean isDonationActive() {
        if (donasi == null) return false;

        double collected = donasi.getTerkumpul();
        double target = donasi.getTargetDana();
        return collected < target;
    }

    private void loadDonasiDetails(View view) {
        showLoading(true);

        if (donasi.getId() != null && !donasi.getId().isEmpty()) {
            db.collection("donasi").document(donasi.getId())
                    .get()
                    .addOnCompleteListener(task -> {
                        showLoading(false);

                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Donasi updatedDonasi = document.toObject(Donasi.class);
                                if (updatedDonasi != null) {
                                    updatedDonasi.setId(document.getId());
                                    this.donasi = updatedDonasi;
                                    updateUI(view);
                                } else {
                                    showError("Gagal memparse data donasi");
                                }
                            } else {
                                showError("Donasi tidak ditemukan");
                            }
                        } else {
                            showError("Gagal memuat detail donasi");
                        }
                    });
        } else {
            showLoading(false);
            updateUI(view);
        }
    }

    private void updateUI(View view) {
        try {
            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            formatRupiah.setMaximumFractionDigits(0);

            TextView tvTitle = view.findViewById(R.id.tvTitle);
            TextView tvSubtitle = view.findViewById(R.id.tvSubtitle);
            TextView tvDescription = view.findViewById(R.id.tvDescription);
            TextView tvProgress = view.findViewById(R.id.tvProgress);
            TextView tvCollected = view.findViewById(R.id.tvCollected);
            TextView tvTarget = view.findViewById(R.id.tvTarget);
            TextView tvDeadline = view.findViewById(R.id.tvDeadline);
            MaterialButton btnDonate = view.findViewById(R.id.btnDonate);

            if (tvTitle != null) tvTitle.setText(donasi.getJudul() != null ? donasi.getJudul() : "Judul tidak tersedia");
            if (tvSubtitle != null) tvSubtitle.setText(donasi.getDeskripsi() != null ? donasi.getDeskripsi() : "Deskripsi tidak tersedia");
            if (tvDescription != null) tvDescription.setText(donasi.getDeskripsi() != null ? donasi.getDeskripsi() : "Deskripsi tidak tersedia");

            int progress = donasi.getProgressPercentage();
            if (progressBar != null) progressBar.setProgress(progress);
            if (tvProgress != null) tvProgress.setText(progress + "% tercapai");
            if (tvCollected != null) tvCollected.setText(formatRupiah.format(donasi.getTerkumpul()));
            if (tvTarget != null) tvTarget.setText("Target: " + formatRupiah.format(donasi.getTargetDana()));
            if (tvDeadline != null) tvDeadline.setText("Batas waktu: " + donasi.getFormattedDeadline());

            updateDonateButtonState(btnDonate);

        } catch (Exception e) {
            Log.e(TAG, "Error updating UI", e);
            showError("Gagal menampilkan data donasi");
        }
    }

    private void updateDonateButtonState(MaterialButton btnDonate) {
        if (btnDonate == null) return;

        if (donasi == null) {
            setButtonInactive(btnDonate, "Data Tidak Valid");
            return;
        }

        if (donasi.getTerkumpul() >= donasi.getTargetDana()) {
            setButtonInactive(btnDonate, "Target Tercapai");
        } else if (!isDonationActive()) {
            setButtonInactive(btnDonate, "Donasi Berakhir");
        } else {
            setButtonActive(btnDonate, "Donasi Sekarang");
        }
    }

    private void setButtonActive(MaterialButton button, String text) {
        button.setText(text);
        button.setEnabled(true);
        button.setAlpha(1.0f);
    }

    private void setButtonInactive(MaterialButton button, String text) {
        button.setText(text);
        button.setEnabled(false);
        button.setAlpha(0.6f);
    }

    private void showDonationNotActiveMessage() {
        String message = donasi != null && donasi.getTerkumpul() >= donasi.getTargetDana()
                ? "Donasi ini sudah mencapai target"
                : "Donasi ini sudah tidak aktif";
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    private void showLoading(boolean isLoading) {
        if (progressBarLoading != null) {
            progressBarLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    private void showError(String message) {
        Log.e(TAG, message);
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PAYMENT_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                Toast.makeText(getContext(), "Terima kasih atas donasi Anda!", Toast.LENGTH_LONG).show();
                if (donasi != null && donasi.getId() != null) {
                    loadDonasiDetails(getView());
                }
            } else if (resultCode == getActivity().RESULT_CANCELED) {
                if (data != null && data.hasExtra("error_message")) {
                    showError("Pembayaran gagal: " + data.getStringExtra("error_message"));
                } else {
                    Toast.makeText(getContext(), "Pembayaran dibatalkan", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (donasi != null && donasi.getId() != null) {
            loadDonasiDetails(getView());
        }
    }
}