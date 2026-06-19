package org.tapnovapay.wallet;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;

public class TransactionHistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyText;
    private TransactionAdapter adapter;
    private List<TransactionItem> transactions = new ArrayList<>();
    private TapNovaPayAPI api;
    private String currentAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        emptyText = findViewById(R.id.empty_text);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransactionAdapter(transactions);
        recyclerView.setAdapter(adapter);

        api = new TapNovaPayAPI();

        android.content.SharedPreferences prefs = getSharedPreferences("wallet", MODE_PRIVATE);
        currentAddress = prefs.getString("address", "");

        loadHistory();
    }

    private void loadHistory() {
        progressBar.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.GONE);

        api.getHistory(currentAddress, new TapNovaPayAPI.ApiCallback() {
            @Override
            public void onSuccess(String result) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    parseHistory(result);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    emptyText.setText("❌ Помилка: " + error);
                    emptyText.setVisibility(View.VISIBLE);
                });
            }
        });
    }

    private void parseHistory(String response) {
        try {
            JsonObject json = JsonParser.parseString(response).getAsJsonObject();
            if (json.has("history")) {
                String historyText = json.get("history").getAsString();
                String[] lines = historyText.split("\n");
                
                transactions.clear();
                
                for (String line : lines) {
                    if (line.contains("Height:")) {
                        TransactionItem item = new TransactionItem();
                        item.height = line.replace("Height:", "").trim();
                        transactions.add(item);
                    } else if (line.contains("Type:")) {
                        if (!transactions.isEmpty()) {
                            TransactionItem last = transactions.get(transactions.size() - 1);
                            last.type = line.replace("Type:", "").trim();
                        }
                    } else if (line.contains("From:")) {
                        if (!transactions.isEmpty()) {
                            TransactionItem last = transactions.get(transactions.size() - 1);
                            last.from = line.replace("From:", "").trim();
                        }
                    } else if (line.contains("To:")) {
                        if (!transactions.isEmpty()) {
                            TransactionItem last = transactions.get(transactions.size() - 1);
                            last.to = line.replace("To:", "").trim();
                        }
                    } else if (line.contains("Amount:")) {
                        if (!transactions.isEmpty()) {
                            TransactionItem last = transactions.get(transactions.size() - 1);
                            last.amount = line.replace("Amount:", "").trim();
                        }
                    }
                }
                
                if (transactions.isEmpty()) {
                    emptyText.setText("📭 Немає транзакцій");
                    emptyText.setVisibility(View.VISIBLE);
                } else {
                    adapter.notifyDataSetChanged();
                }
            }
        } catch (Exception e) {
            emptyText.setText("❌ Помилка парсингу");
            emptyText.setVisibility(View.VISIBLE);
        }
    }

    // Внутрішній клас для елемента транзакції
    public static class TransactionItem {
        String height;
        String type;
        String from;
        String to;
        String amount;
    }
}
