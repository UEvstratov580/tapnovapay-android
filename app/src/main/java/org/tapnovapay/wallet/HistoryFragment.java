package org.tapnovapay.wallet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import java.util.List;

public class HistoryFragment extends Fragment {
    private TextView historyText;
    private DatabaseHelper dbHelper;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        historyText = view.findViewById(R.id.history_text);
        dbHelper = new DatabaseHelper(getContext());
        
        loadHistory();
        return view;
    }
    
    private void loadHistory() {
        List<Transaction> transactions = dbHelper.getAllTransactions();
        StringBuilder sb = new StringBuilder();
        sb.append("📋 ІСТОРІЯ ТРАНЗАКЦІЙ\n");
        sb.append("═".repeat(30) + "\n\n");
        
        if (transactions.isEmpty()) {
            sb.append("ℹ️ Немає транзакцій\n");
        } else {
            for (Transaction t : transactions) {
                String icon = t.getType().equals("Поповнення") ? "✅" : "❌";
                String sign = t.getType().equals("Поповнення") ? "+" : "-";
                sb.append(String.format("%s %s₴ %.2f\n", icon, sign, t.getAmount()));
                sb.append("   📝 " + t.getDescription() + "\n");
                sb.append("   📅 " + t.getDate() + "\n");
                sb.append("   " + t.getStatus() + "\n");
                sb.append("─".repeat(25) + "\n");
            }
        }
        
        historyText.setText(sb.toString());
    }
}
