package org.tapnovapay.wallet.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.tapnovapay.wallet.R;
import org.tapnovapay.wallet.model.Transaction;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    
    private List<Transaction> transactions;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
    
    public TransactionAdapter(List<Transaction> transactions) {
        this.transactions = transactions;
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
        Transaction tx = transactions.get(position);
        
        String type = tx.getCategory().equals("receive") ? "📥 Отримано" : "📤 Відправлено";
        String sign = tx.getCategory().equals("receive") ? "+" : "-";
        String color = tx.getCategory().equals("receive") ? "#4CAF50" : "#F44336";
        
        holder.typeText.setText(type);
        holder.amountText.setText(sign + String.format("%.8f", tx.getAmount()) + " TNP");
        holder.addressText.setText(tx.getAddress());
        holder.timeText.setText(sdf.format(tx.getTime()));
        holder.confirmationsText.setText(tx.getConfirmations() + " підтверджень");
        
        holder.amountText.setTextColor(android.graphics.Color.parseColor(color));
    }
    
    @Override
    public int getItemCount() {
        return transactions.size();
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView typeText, amountText, addressText, timeText, confirmationsText;
        
        ViewHolder(View itemView) {
            super(itemView);
            typeText = itemView.findViewById(R.id.transaction_type);
            amountText = itemView.findViewById(R.id.transaction_amount);
            addressText = itemView.findViewById(R.id.transaction_address);
            timeText = itemView.findViewById(R.id.transaction_time);
            confirmationsText = itemView.findViewById(R.id.transaction_confirmations);
        }
    }
}
