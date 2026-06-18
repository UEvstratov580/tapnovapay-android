package org.tapnovapay.wallet;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.tapnovapay.wallet.model.Transaction;
import org.tapnovapay.wallet.network.RPCClient;
import org.tapnovapay.wallet.ui.TransactionAdapter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    
    private TextView balanceText;
    private TextView addressText;
    private TextView networkStatusText;
    private RecyclerView transactionsRecyclerView;
    private Button sendButton;
    private Button receiveButton;
    private FloatingActionButton refreshButton;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactions = new ArrayList<>();
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private RPCClient rpcClient;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Ініціалізація
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        rpcClient = new RPCClient(this);
        
        // UI компоненти
        balanceText = findViewById(R.id.balance_text);
        addressText = findViewById(R.id.address_text);
        networkStatusText = findViewById(R.id.network_status);
        transactionsRecyclerView = findViewById(R.id.transactions_recycler_view);
        sendButton = findViewById(R.id.send_button);
        receiveButton = findViewById(R.id.receive_button);
        refreshButton = findViewById(R.id.refresh_button);
        
        // Налаштування RecyclerView
        transactionAdapter = new TransactionAdapter(transactions);
        transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactionsRecyclerView.setAdapter(transactionAdapter);
        
        // Обробники кліків
        sendButton.setOnClickListener(v -> openSendDialog());
        receiveButton.setOnClickListener(v -> showReceiveDialog());
        refreshButton.setOnClickListener(v -> refreshData());
        
        // Завантаження даних
        loadWalletData();
    }
    
    private void loadWalletData() {
        executor.execute(() -> {
            try {
                // Отримання балансу
                String balance = rpcClient.getBalance();
                String address = rpcClient.getAddress();
                List<Transaction> txList = rpcClient.getTransactions();
                
                runOnUiThread(() -> {
                    balanceText.setText(balance + " TNP");
                    addressText.setText(address);
                    transactions.clear();
                    transactions.addAll(txList);
                    transactionAdapter.notifyDataSetChanged();
                    networkStatusText.setText("🟢 Підключено");
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    networkStatusText.setText("🔴 Помилка");
                    Toast.makeText(this, "Помилка завантаження: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
                e.printStackTrace();
            }
        });
    }
    
    private void refreshData() {
        networkStatusText.setText("🔄 Оновлення...");
        loadWalletData();
    }
    
    private void openSendDialog() {
        // Відкриваємо діалог відправки
        SendDialog dialog = new SendDialog(this, rpcClient);
        dialog.show();
        dialog.setOnDismissListener(d -> refreshData());
    }
    
    private void showReceiveDialog() {
        // Показуємо QR код та адресу
        String address = addressText.getText().toString();
        ReceiveDialog dialog = new ReceiveDialog(this, address);
        dialog.show();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Налаштування", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_logout) {
            Toast.makeText(this, "Вихід", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
