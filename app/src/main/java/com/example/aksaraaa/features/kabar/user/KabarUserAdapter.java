package com.example.aksaraaa.features.kabar.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.aksaraaa.R;
import com.example.aksaraaa.models.Kabar;
import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class KabarUserAdapter extends RecyclerView.Adapter<KabarUserAdapter.KabarViewHolder> {
    private List<Kabar> kabarList;
    private OnKabarClickListener listener;

    public interface OnKabarClickListener {
        void onKabarClick(Kabar kabar);
    }

    public KabarUserAdapter(List<Kabar> kabarList, OnKabarClickListener listener) {
        this.kabarList = kabarList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public KabarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_kabar_user, parent, false);
        return new KabarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KabarViewHolder holder, int position) {
        Kabar kabar = kabarList.get(position);
        holder.bind(kabar, listener);
    }

    @Override
    public int getItemCount() {
        return kabarList.size();
    }

    static class KabarViewHolder extends RecyclerView.ViewHolder {
        TextView tvJudul, tvTanggal, tvPreview;

        public KabarViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJudul = itemView.findViewById(R.id.tvJudulKabar);
            tvTanggal = itemView.findViewById(R.id.tvTanggalKabar);
            tvPreview = itemView.findViewById(R.id.tvPreviewKabar);
        }

        public void bind(Kabar kabar, OnKabarClickListener listener) {
            tvJudul.setText(kabar.getJudul());

            if (kabar.getTanggal() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                tvTanggal.setText(sdf.format(kabar.getTanggal().toDate()));
            }

            tvPreview.setText(kabar.getIsi());

            itemView.setOnClickListener(v -> listener.onKabarClick(kabar));
        }
    }
}