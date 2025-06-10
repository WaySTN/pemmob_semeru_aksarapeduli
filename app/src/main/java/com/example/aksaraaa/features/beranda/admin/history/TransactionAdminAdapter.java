package com.example.aksaraaa.features.beranda.admin.history;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aksaraaa.R;
import com.example.aksaraaa.models.Transaction;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TransactionAdminAdapter extends RecyclerView.Adapter<TransactionAdminAdapter.TransactionViewHolder> {

    private static final String TAG = "TransactionAdminAdapter";

    private List<Transaction> transactionList;
    private Context context;
    private NumberFormat currencyFormat;
    private SimpleDateFormat dateFormat;

    public TransactionAdminAdapter(List<Transaction> transactionList, Context context) {
        this.transactionList = transactionList;
        this.context = context;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        this.dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", new Locale("id", "ID"));
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_transaction_admin, parent, false);
            return new TransactionViewHolder(view);
        } catch (Exception e) {
            Log.e(TAG, "Error creating view holder: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        try {
            if (transactionList != null && position < transactionList.size()) {
                Transaction transaction = transactionList.get(position);
                holder.bind(transaction);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error binding view holder at position " + position + ": " + e.getMessage(), e);
        }
    }

    @Override
    public int getItemCount() {
        return transactionList != null ? transactionList.size() : 0;
    }

    public class TransactionViewHolder extends RecyclerView.ViewHolder {

        private TextView tvDonorName;
        private TextView tvTransactionId;
        private TextView tvAmount;
        private TextView tvCampaignTitle;
        private TextView tvPaymentMethod;
        private TextView tvTimestamp;
        private TextView tvStatus;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            initializeViews(itemView);
        }

        private void initializeViews(View itemView) {
            try {
                tvDonorName = itemView.findViewById(R.id.tvDonorName);
                tvTransactionId = itemView.findViewById(R.id.tvTransactionId);
                tvAmount = itemView.findViewById(R.id.tvAmount);
                tvCampaignTitle = itemView.findViewById(R.id.tvCampaignTitle);
                tvPaymentMethod = itemView.findViewById(R.id.tvPaymentMethod);
                tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
                tvStatus = itemView.findViewById(R.id.tvStatus);

                Log.d(TAG, "Views initialized successfully");
            } catch (Exception e) {
                Log.e(TAG, "Error initializing view holder views: " + e.getMessage(), e);
            }
        }

        public void bind(Transaction transaction) {
            if (transaction == null) {
                Log.w(TAG, "Transaction is null, skipping bind");
                return;
            }

            try {
                bindDonorName(transaction);
                bindTransactionId(transaction);
                bindAmount(transaction);
                bindCampaignTitle(transaction);
                bindPaymentMethod(transaction);
                bindTimestamp(transaction);
                bindStatus(transaction);
            } catch (Exception e) {
                Log.e(TAG, "Error in bind method: " + e.getMessage(), e);
            }
        }

        private void bindDonorName(Transaction transaction) {
            if (tvDonorName == null) return;

            try {
                // Menggunakan getNama() karena field di Firestore adalah "nama"
                String donorName = transaction.getNama();
                if (donorName != null && !donorName.trim().isEmpty()) {
                    tvDonorName.setText(donorName.trim());
                } else {
                    tvDonorName.setText("Anonim");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error binding donor name: " + e.getMessage());
                tvDonorName.setText("Anonim");
            }
        }

        private void bindTransactionId(Transaction transaction) {
            if (tvTransactionId == null) return;

            try {
                String transactionId = transaction.getId();
                if (transactionId != null && !transactionId.isEmpty()) {
                    // Tampilkan 12 karakter pertama dari ID
                    String shortId = transactionId.length() > 12
                            ? transactionId.substring(0, 12) + "..."
                            : transactionId;
                    tvTransactionId.setText("ID: " + shortId);
                } else {
                    tvTransactionId.setText("ID: -");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error binding transaction ID: " + e.getMessage());
                tvTransactionId.setText("ID: -");
            }
        }

        private void bindAmount(Transaction transaction) {
            if (tvAmount == null) return;

            try {
                double amount = transaction.getAmount();
                String formattedAmount = currencyFormat.format(amount);
                tvAmount.setText(formattedAmount);
            } catch (Exception e) {
                Log.e(TAG, "Error formatting amount: " + e.getMessage());
                tvAmount.setText("Rp 0");
            }
        }

        private void bindCampaignTitle(Transaction transaction) {
            if (tvCampaignTitle == null) return;

            try {
                String campaignTitle = transaction.getCampaignTitle();
                if (campaignTitle != null && !campaignTitle.trim().isEmpty()) {
                    tvCampaignTitle.setText(campaignTitle.trim());
                } else {
                    tvCampaignTitle.setText("Campaign tidak diketahui");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error binding campaign title: " + e.getMessage());
                tvCampaignTitle.setText("Campaign tidak diketahui");
            }
        }

        private void bindPaymentMethod(Transaction transaction) {
            if (tvPaymentMethod == null) return;

            try {
                String paymentMethod = transaction.getPaymentMethod();
                if (paymentMethod != null && !paymentMethod.trim().isEmpty()) {
                    tvPaymentMethod.setText(paymentMethod.trim().toUpperCase());
                } else {
                    tvPaymentMethod.setText("N/A");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error binding payment method: " + e.getMessage());
                tvPaymentMethod.setText("N/A");
            }
        }

        private void bindTimestamp(Transaction transaction) {
            if (tvTimestamp == null) return;

            try {
                if (transaction.getTimestamp() != null) {
                    String formattedDate = dateFormat.format(transaction.getTimestamp().toDate());
                    tvTimestamp.setText(formattedDate);
                } else {
                    tvTimestamp.setText("Tanggal tidak tersedia");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error formatting timestamp: " + e.getMessage());
                tvTimestamp.setText("Tanggal tidak tersedia");
            }
        }

        private void bindStatus(Transaction transaction) {
            if (tvStatus == null) return;

            try {
                String status = transaction.getStatus();
                setStatusStyle(status);
            } catch (Exception e) {
                Log.e(TAG, "Error binding status: " + e.getMessage());
                setDefaultStatus();
            }
        }

        private void setStatusStyle(String status) {
            if (tvStatus == null) return;

            try {
                // Normalize status
                String normalizedStatus = normalizeStatus(status);

                // Set text
                tvStatus.setText(getStatusDisplayText(normalizedStatus));

                // Set background and text color
                setStatusBackground(normalizedStatus);

            } catch (Exception e) {
                Log.e(TAG, "Error setting status style: " + e.getMessage());
                setDefaultStatus();
            }
        }

        private String normalizeStatus(String status) {
            if (status == null) return "unknown";

            String lowerStatus = status.toLowerCase().trim();

            switch (lowerStatus) {
                case "success":
                case "berhasil":
                case "completed":
                case "settlement":
                    return "success";

                case "pending":
                case "processing":
                case "pending_payment":
                    return "pending";

                case "failed":
                case "gagal":
                case "cancelled":
                case "canceled":
                case "failure":
                case "expire":
                case "deny":
                    return "failed";

                default:
                    return "unknown";
            }
        }

        private String getStatusDisplayText(String normalizedStatus) {
            switch (normalizedStatus) {
                case "success":
                    return "Berhasil";
                case "pending":
                    return "Pending";
                case "failed":
                    return "Gagal";
                default:
                    return "Unknown";
            }
        }

        private void setStatusBackground(String normalizedStatus) {
            try {
                switch (normalizedStatus) {
                    case "success":
                        setBackgroundWithFallback(R.drawable.bg_status_success, R.color.green_light);
                        setTextColor(R.color.green_dark);
                        break;

                    case "pending":
                        setBackgroundWithFallback(R.drawable.bg_status_pending, R.color.yellow_light);
                        setTextColor(R.color.yellow_dark);
                        break;

                    case "failed":
                        setBackgroundWithFallback(R.drawable.bg_status_failed, R.color.red_light);
                        setTextColor(R.color.red_dark);
                        break;

                    default:
                        setBackgroundWithFallback(0, R.color.gray_light);
                        setTextColor(R.color.gray_dark);
                        break;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error setting status background: " + e.getMessage());
                setDefaultStatusBackground();
            }
        }

        private void setBackgroundWithFallback(int drawableRes, int colorRes) {
            try {
                if (drawableRes != 0) {
                    // Try to set drawable background first
                    tvStatus.setBackgroundResource(drawableRes);
                } else {
                    // Use color background
                    tvStatus.setBackgroundColor(ContextCompat.getColor(context, colorRes));
                }
            } catch (Exception e) {
                try {
                    // Fallback to color
                    tvStatus.setBackgroundColor(ContextCompat.getColor(context, colorRes));
                } catch (Exception e2) {
                    // Ultimate fallback
                    setDefaultStatusBackground();
                }
            }
        }

        private void setTextColor(int colorRes) {
            try {
                tvStatus.setTextColor(ContextCompat.getColor(context, colorRes));
            } catch (Exception e) {
                // Use default text color if resource not found
                tvStatus.setTextColor(ContextCompat.getColor(context, android.R.color.black));
            }
        }

        private void setDefaultStatus() {
            if (tvStatus != null) {
                tvStatus.setText("Unknown");
                setDefaultStatusBackground();
            }
        }

        private void setDefaultStatusBackground() {
            try {
                tvStatus.setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray));
                tvStatus.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            } catch (Exception e) {
                Log.e(TAG, "Error setting default status background: " + e.getMessage());
            }
        }
    }

    // Public methods for adapter management
    public void updateData(List<Transaction> newTransactionList) {
        try {
            if (newTransactionList != null) {
                this.transactionList = newTransactionList;
                notifyDataSetChanged();
                Log.d(TAG, "Data updated successfully. New size: " + newTransactionList.size());
            } else {
                Log.w(TAG, "Attempted to update with null transaction list");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating data: " + e.getMessage(), e);
        }
    }

    public void addTransaction(Transaction transaction) {
        try {
            if (transaction != null && transactionList != null) {
                transactionList.add(0, transaction); // Add to top
                notifyItemInserted(0);
                Log.d(TAG, "Transaction added successfully");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error adding transaction: " + e.getMessage(), e);
        }
    }

    public void removeTransaction(int position) {
        try {
            if (transactionList != null && position >= 0 && position < transactionList.size()) {
                transactionList.remove(position);
                notifyItemRemoved(position);
                Log.d(TAG, "Transaction removed from position: " + position);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error removing transaction: " + e.getMessage(), e);
        }
    }

    public Transaction getTransaction(int position) {
        try {
            if (transactionList != null && position >= 0 && position < transactionList.size()) {
                return transactionList.get(position);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting transaction at position " + position + ": " + e.getMessage());
        }
        return null;
    }

    public void clearData() {
        try {
            if (transactionList != null) {
                int size = transactionList.size();
                transactionList.clear();
                notifyItemRangeRemoved(0, size);
                Log.d(TAG, "All transactions cleared");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error clearing data: " + e.getMessage(), e);
        }
    }

    // Getter for transaction list (useful for debugging)
    public List<Transaction> getTransactionList() {
        return transactionList;
    }
}