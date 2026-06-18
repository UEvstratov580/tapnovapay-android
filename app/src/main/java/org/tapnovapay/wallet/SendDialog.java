package org.tapnovapay.wallet;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import org.tapnovapay.wallet.network.RPCClient;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SendDialog extends Dialog {
    
    private EditText addressInput;
    private EditText amountInput;
    private Button sendButton;
    private Button cancelButton;
    private RPCClient rpcClient;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    
    public SendDialog(@NonNull Context context, RPCClient rpcClient) {
        super(context);
        this.rpcClient = rpcClient;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_send);
        
        addressInput = findViewById(R.id.address_input);
        amountInput = findViewById(R.id.amount_input);
        sendButton = findViewById(R.id.send_button);
        cancelButton = findViewById(R.id.cancel_button);
        
        sendButton.setOnClickListener(v -> sendTransaction());
        cancelButton.setOnClickListener(v -> dismiss());
    }
    
    private void sendTransaction() {
        String address = addressInput.getText().toString().trim();
        String amountStr = amountInput.getText().toString().trim();
        
        if (TextUtils.isEmpty(address)) {
            Toast.makeText(getContext(), "Введіть адресу", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (TextUtils.isEmpty(amountStr)) {
            Toast.makeText(getContext(), "Введіть суму", Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                Toast.makeText(getContext(), "Сума має бути більше 0", Toast.LENGTH_SHORT).show();
                return;
            }
            
            sendButton.setEnabled(false);
            sendButton.setText("Відправка...");
            
            executor.execute(() -> {
                try {
                    String txid = rpcClient.sendToAddress(address, amount);
                    runOnUiThread(() -> {
                        Toast.makeText(getContext(), "✅ Відправлено! TXID: " + txid, Toast.LENGTH_LONG).show();
                        dismiss();
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(getContext(), "❌ Помилка: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        sendButton.setEnabled(true);
                        sendButton.setText("Відправити");
                    });
                }
            });
            
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Невірний формат суми", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void runOnUiThread(Runnable runnable) {
        if (getContext() instanceof android.app.Activity) {
            ((android.app.Activity) getContext()).runOnUiThread(runnable);
        }
    }
}
