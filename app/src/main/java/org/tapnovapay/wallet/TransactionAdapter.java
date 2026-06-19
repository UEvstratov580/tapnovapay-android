package org.tapnovapay.wallet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private List<TransactionHistoryActivity.TransactionItem> items;

    public TransactionAdapter(List<TransactionHistoryActivity.TransactionItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TransactionHistoryActivity.TransactionItem item = items.get(position);
        
        String type = item.type != null ? item.type : "";
        String amount = item.amount != null ? item.amount : "0";
        String details = "";
        
        if (type.equals("IN")) {
            holder.icon.setText("📥");
            holder.type.setText("Отримано");
            holder.amount.setText("+ " + amount);
            holder.amount.setTextColor(holder.itemView.getContext().getColor(android.R.color.holo_green_dark));
            details = "Від: " + (item.from != null ? item.from.substring(0, Math.min(12, item.from.length())) + "..." : "network");
        } else if (type.equals("OUT")) {
            holder.icon.setText("📤");
            holder.type.setText("Відправлено");
            holder.amount.setText("- " + amount);
            holder.amount.setTextColor(holder.itemView.getContext().getColor(android.R.color.holo_red_dark));
            details = "Кому: " + (item.to != null ? item.to.substring(0, Math.min(12, item.to.length())) + "..." : "unknown");
        } else {
            holder.icon.setText("⛏️");
            holder.type.setText("Майнінг");
            holder.amount.setText("+ " + amount);
            holder.amount.setTextColor(holder.itemView.getContext().getColor(android.R.color.holo_orange_dark));
            details = "Блок: " + item.height;
        }
        
        holder.details.setText(details);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView icon, type, amount, details;

        ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            type = itemView.findViewById(R.id.type);
            amount = itemView.findViewById(R.id.amount);
            details = itemView.findViewById(R.id.details);
        }
    }
}
