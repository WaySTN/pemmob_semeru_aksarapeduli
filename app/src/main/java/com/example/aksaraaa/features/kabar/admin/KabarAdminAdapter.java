package com.example.aksaraaa.features.kabar.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.aksaraaa.R;
import com.example.aksaraaa.models.Kabar;
import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class KabarAdminAdapter extends RecyclerView.Adapter<KabarAdminAdapter.KabarViewHolder> {
    private List<Kabar> kabarList;
    private OnKabarClickListener listener;

    public interface OnKabarClickListener {
        void onEditClick(Kabar kabar);
        void onDeleteClick(Kabar kabar);
    }

    public KabarAdminAdapter(List<Kabar> kabarList, OnKabarClickListener listener) {
        this.kabarList = kabarList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public KabarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_kabar_admin, parent, false);
        return new KabarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KabarViewHolder holder, int position) {
        Kabar kabar = kabarList.get(position);
        holder.bind(kabar, listener);
    }

    @Override
    public int getItemCount() { return kabarList.size(); }

    public void updateData(List<Kabar> newList) {
        kabarList.clear();
        kabarList.addAll(newList);
        notifyDataSetChanged();
    }

    static class KabarViewHolder extends RecyclerView.ViewHolder {
        TextView tvJudul, tvTanggal, tvPreview;
        ImageButton btnEdit, btnDelete;

        public KabarViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJudul = itemView.findViewById(R.id.tvJudulKabar);
            tvTanggal = itemView.findViewById(R.id.tvTanggalKabar);
            tvPreview = itemView.findViewById(R.id.tvPreviewKabar);
            btnEdit = itemView.findViewById(R.id.btnEditKabar);
            btnDelete = itemView.findViewById(R.id.btnHapusKabar);
        }

        public void bind(Kabar kabar, OnKabarClickListener listener) {
            tvJudul.setText(kabar.getJudul());

            if (kabar.getTanggal() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                tvTanggal.setText(sdf.format(kabar.getTanggal().toDate()));
            }

            String preview = kabar.getIsi().length() > 100
                    ? kabar.getIsi().substring(0, 100) + "..."
                    : kabar.getIsi();
            tvPreview.setText(preview);

            btnEdit.setOnClickListener(v -> listener.onEditClick(kabar));
            btnDelete.setOnClickListener(v -> listener.onDeleteClick(kabar));
        }
    }
}