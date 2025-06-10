package com.example.aksaraaa.features.beranda.user.history;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aksaraaa.R;
import com.example.aksaraaa.models.DonasiHistory;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DonationHistoryAdapter extends RecyclerView.Adapter<DonationHistoryAdapter.ViewHolder> {

    private static final String TAG = "DonationHistoryAdapter";
    private List<DonasiHistory> historyList;
    private Context context;

    public DonationHistoryAdapter(List<DonasiHistory> historyList) {
        this.historyList = historyList;
        Log.d(TAG, "Adapter created with " + (historyList != null ? historyList.size() : 0) + " items");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            context = parent.getContext();
            View view = LayoutInflater.from(context).inflate(R.layout.item_history_donasi, parent, false);
            Log.d(TAG, "ViewHolder created successfully");
            return new ViewHolder(view);
        } catch (Exception e) {
            Log.e(TAG, "Error creating ViewHolder", e);
            throw e;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            if (historyList == null || position >= historyList.size()) {
                Log.w(TAG, "Invalid position or null list at position: " + position);
                return;
            }

            DonasiHistory history = historyList.get(position);
            if (history == null) {
                Log.w(TAG, "Null history item at position: " + position);
                return;
            }

            holder.bind(history);
            Log.d(TAG, "Bound item at position: " + position);
        } catch (Exception e) {
            Log.e(TAG, "Error binding ViewHolder at position: " + position, e);
        }
    }

    @Override
    public int getItemCount() {
        int count = historyList != null ? historyList.size() : 0;
        Log.d(TAG, "Item count: " + count);
        return count;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvStatus, tvTanggal, tvJudulKampanye, tvJumlahDonasi, tvMetodePembayaran, tvTransactionId;
        private ImageView ivPaymentIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            try {
                tvStatus = itemView.findViewById(R.id.tvStatus);
                tvTanggal = itemView.findViewById(R.id.tvTanggal);
                tvJudulKampanye = itemView.findViewById(R.id.tvJudulKampanye);
                tvJumlahDonasi = itemView.findViewById(R.id.tvJumlahDonasi);
                tvMetodePembayaran = itemView.findViewById(R.id.tvMetodePembayaran);
                tvTransactionId = itemView.findViewById(R.id.tvTransactionId);
                ivPaymentIcon = itemView.findViewById(R.id.ivPaymentIcon);

                Log.d(TAG, "ViewHolder views initialized");
            } catch (Exception e) {
                Log.e(TAG, "Error initializing ViewHolder views", e);
            }
        }

        public void bind(DonasiHistory history) {
            try {
                if (history == null) {
                    Log.w(TAG, "Cannot bind null history");
                    return;
                }

                // Set status
                setStatus(history.getStatus());

                // Set tanggal
                setTanggal(history.getTimestamp());

                // Set judul kampanye
                setJudulKampanye(history.getCampaignTitle());

                // Set jumlah donasi
                setJumlahDonasi(history.getAmount());

                // Set metode pembayaran
                setMetodePembayaran(history.getPaymentMethod());

                // Set transaction ID
                setTransactionId(history.getId());

                Log.d(TAG, "Successfully bound history item: " + history.getId());
            } catch (Exception e) {
                Log.e(TAG, "Error binding history data", e);
            }
        }

        private void setStatus(String status) {
            try {
                if (tvStatus == null) {
                    Log.w(TAG, "tvStatus is null");
                    return;
                }

                if (status == null) {
                    status = "unknown";
                }

                switch (status.toLowerCase()) {
                    case "success":
                        tvStatus.setText("BERHASIL");
                        tvStatus.setBackgroundResource(R.drawable.bg_status_success);
                        break;
                    case "failed":
                        tvStatus.setText("GAGAL");
                        tvStatus.setBackgroundResource(R.drawable.bg_status_pending);
                        break;
                    case "pending":
                        tvStatus.setText("PENDING");
                        tvStatus.setBackgroundResource(R.drawable.bg_status_pending);
                        break;
                    default:
                        tvStatus.setText("UNKNOWN");
                        tvStatus.setBackgroundResource(R.drawable.bg_status_pending);
                        break;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error setting status", e);
                if (tvStatus != null) {
                    tvStatus.setText("ERROR");
                }
            }
        }

        private void setTanggal(com.google.firebase.Timestamp timestamp) {
            try {
                if (tvTanggal == null) {
                    Log.w(TAG, "tvTanggal is null");
                    return;
                }

                if (timestamp != null) {
                    Date date = timestamp.toDate();
                    SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy", new Locale("id", "ID"));
                    tvTanggal.setText(sdf.format(date));
                } else {
                    tvTanggal.setText("-");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error setting tanggal", e);
                if (tvTanggal != null) {
                    tvTanggal.setText("-");
                }
            }
        }

        private void setJudulKampanye(String title) {
            try {
                if (tvJudulKampanye == null) {
                    Log.w(TAG, "tvJudulKampanye is null");
                    return;
                }

                tvJudulKampanye.setText(title != null ? title : "Judul tidak tersedia");
            } catch (Exception e) {
                Log.e(TAG, "Error setting judul kampanye", e);
                if (tvJudulKampanye != null) {
                    tvJudulKampanye.setText("Error loading title");
                }
            }
        }

        private void setJumlahDonasi(Long amount) {
            try {
                if (tvJumlahDonasi == null) {
                    Log.w(TAG, "tvJumlahDonasi is null");
                    return;
                }

                if (amount != null && amount > 0) {
                    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                    String formattedAmount = currencyFormat.format(amount);
                    tvJumlahDonasi.setText(formattedAmount);
                } else {
                    tvJumlahDonasi.setText("Rp 0");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error setting jumlah donasi", e);
                if (tvJumlahDonasi != null) {
                    tvJumlahDonasi.setText("Rp 0");
                }
            }
        }

        private void setMetodePembayaran(String paymentMethod) {
            try {
                if (tvMetodePembayaran == null) {
                    Log.w(TAG, "tvMetodePembayaran is null");
                    return;
                }

                if (paymentMethod == null) {
                    paymentMethod = "unknown";
                }

                // Set icon dan text berdasarkan payment method
                String method = paymentMethod.toLowerCase();

                if (ivPaymentIcon != null) {
                    try {
                        if (method.contains("bni")) {
                            ivPaymentIcon.setImageResource(R.drawable.bni);
                            tvMetodePembayaran.setText("BNI");
                        } else if (method.contains("bri")) {
                            ivPaymentIcon.setImageResource(R.drawable.bri);
                            tvMetodePembayaran.setText("BRI");
                        } else if (method.contains("bca")) {
                            ivPaymentIcon.setImageResource(R.drawable.bca);
                            tvMetodePembayaran.setText("BCA");
                        } else if (method.contains("dana")) {
                            ivPaymentIcon.setImageResource(R.drawable.dana);
                            tvMetodePembayaran.setText("DANA");
                        } else if (method.contains("shopee")) {
                            ivPaymentIcon.setImageResource(R.drawable.shoppeepay);
                            tvMetodePembayaran.setText("ShopeePay");
                        } else if (method.contains("gopay")) {
                            ivPaymentIcon.setImageResource(R.drawable.gopay);
                            tvMetodePembayaran.setText("GoPay");
                        } else {
                            // Default bank icon
                            ivPaymentIcon.setImageResource(R.drawable.bni);
                            tvMetodePembayaran.setText(paymentMethod);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error setting payment icon", e);
                        ivPaymentIcon.setImageResource(R.drawable.bni);
                        tvMetodePembayaran.setText(paymentMethod);
                    }
                } else {
                    tvMetodePembayaran.setText(paymentMethod);
                }

            } catch (Exception e) {
                Log.e(TAG, "Error setting metode pembayaran", e);
                if (tvMetodePembayaran != null) {
                    tvMetodePembayaran.setText("Unknown");
                }
            }
        }

        private void setTransactionId(String id) {
            try {
                if (tvTransactionId == null) {
                    Log.w(TAG, "tvTransactionId is null");
                    return;
                }

                if (id != null && !id.isEmpty()) {
                    tvTransactionId.setText("ID: " + id);
                } else {
                    tvTransactionId.setText("ID: -");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error setting transaction ID", e);
                if (tvTransactionId != null) {
                    tvTransactionId.setText("ID: Error");
                }
            }
        }
    }
}