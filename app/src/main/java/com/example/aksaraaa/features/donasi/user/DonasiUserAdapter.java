package com.example.aksaraaa.features.donasi.user;

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

public class DonasiUserAdapter extends RecyclerView.Adapter<DonasiUserAdapter.DonasiViewHolder> {

    private List<Donasi> donasiList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Donasi donasi);
    }

    public DonasiUserAdapter(List<Donasi> donasiList, OnItemClickListener listener) {
        this.donasiList = donasiList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DonasiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_donasi_user, parent, false);
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
        TextView tvTitle, tvDeadline, tvDescription, tvTarget, tvProgress, tvDonors;
        ProgressBar progressBar;

        public DonasiViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCampaign = itemView.findViewById(R.id.imgCampaign);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDeadline = itemView.findViewById(R.id.tvDeadline);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTarget = itemView.findViewById(R.id.tvTarget);
            tvProgress = itemView.findViewById(R.id.tvProgress);
            tvDonors = itemView.findViewById(R.id.tvDonors);
            progressBar = itemView.findViewById(R.id.progressBar);
        }

        public void bind(final Donasi donasi, final OnItemClickListener listener) {
            // Format untuk mata uang
            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            formatRupiah.setMaximumFractionDigits(0);

            // Set data ke view
            tvTitle.setText(donasi.getJudul());
            tvDeadline.setText("Batas waktu: " + donasi.getFormattedDeadline());
            tvDescription.setText(donasi.getDeskripsi());
            tvTarget.setText("Target: " + formatRupiah.format(donasi.getTargetDana()));

            // Progress donasi
            int progress = donasi.getProgressPercentage();
            progressBar.setProgress(progress);
            tvProgress.setText(progress + "% tercapai");

            // Jumlah donatur (sementara dummy)
            tvDonors.setText("");

            // Load gambar menggunakan Glide
            if (donasi.getGambarUrl() != null && !donasi.getGambarUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(donasi.getGambarUrl())
                        .placeholder(R.drawable.pendidikan)
                        .into(imgCampaign);
            }

            // Item click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(donasi);
                }
            });
        }
    }
}