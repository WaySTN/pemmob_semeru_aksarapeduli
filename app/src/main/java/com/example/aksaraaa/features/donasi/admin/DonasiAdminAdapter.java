package com.example.aksaraaa.features.donasi.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.aksaraaa.R;
import com.example.aksaraaa.models.Donasi;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class DonasiAdminAdapter extends RecyclerView.Adapter<DonasiAdminAdapter.DonasiViewHolder> {

    private List<Donasi> donasiList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(Donasi donasi);
        void onDeleteClick(Donasi donasi);
        void onToggleStatusClick(Donasi donasi);
    }

    public DonasiAdminAdapter(List<Donasi> donasiList, OnItemClickListener listener) {
        this.donasiList = donasiList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DonasiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_donasi_admin, parent, false);
        return new DonasiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DonasiViewHolder holder, int position) {
        Donasi donasi = donasiList.get(position);
        holder.bind(donasi, listener);
    }

    @Override
    public int getItemCount() {
        return donasiList.size();
    }

    public void updateData(List<Donasi> newDonasiList) {
        donasiList = newDonasiList;
        notifyDataSetChanged();
    }

    static class DonasiViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCampaign;
        TextView tvTitle, tvStatus, tvDeadline, tvDonors, tvProgress, tvAmount;
        ProgressBar progressBar;

        public DonasiViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCampaign = itemView.findViewById(R.id.imgCampaign);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDeadline = itemView.findViewById(R.id.tvDeadline);
            tvDonors = itemView.findViewById(R.id.tvDonors);
            tvProgress = itemView.findViewById(R.id.tvProgress);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            progressBar = itemView.findViewById(R.id.progressBar);
        }

        public void bind(final Donasi donasi, final OnItemClickListener listener) {
            // Format untuk mata uang
            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            formatRupiah.setMaximumFractionDigits(0);

            // Format tanggal
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            // Set data ke view
            tvTitle.setText(donasi.getJudul());

            // Status campaign
            if (donasi.isAktif()) {
                tvStatus.setText("Aktif");
                tvStatus.setBackgroundResource(R.drawable.bg_status_active);
            } else {
                tvStatus.setText("Nonaktif");
                tvStatus.setBackgroundResource(R.drawable.bg_status_inactive);
            }

            // Deadline
            tvDeadline.setText(dateFormat.format(donasi.getDeadline()));

            // Jumlah donatur (sementara dummy)
            tvDonors.setText("");

            // Progress donasi
            int progress = (int) ((donasi.getTerkumpul() / donasi.getTargetDana()) * 100);
            progressBar.setProgress(progress);
            tvProgress.setText(progress + "% tercapai");
            tvAmount.setText(formatRupiah.format(donasi.getTerkumpul()) + "/" +
                    formatRupiah.format(donasi.getTargetDana()));

            // Load gambar menggunakan Glide
            if (donasi.getGambarUrl() != null && !donasi.getGambarUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(donasi.getGambarUrl())
                        .placeholder(R.drawable.pendidikan)
                        .into(imgCampaign);
            }

            // Button listeners
            itemView.findViewById(R.id.btnEdit).setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(donasi);
                }
            });

            itemView.findViewById(R.id.btnDelete).setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(donasi);
                }
            });

            itemView.findViewById(R.id.btnToggleStatus).setOnClickListener(v -> {
                if (listener != null) {
                    listener.onToggleStatusClick(donasi);
                }
            });
        }
    }
}