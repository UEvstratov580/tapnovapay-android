package org.tapnovapay.wallet;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    
    private TextView balanceText;
    private Button refreshButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        balanceText = findViewById(R.id.balance_text);
        refreshButton = findViewById(R.id.refresh_button);
        
        refreshButton.setOnClickListener(v -> updateBalance());
        
        updateBalance();
    }
    
    private void updateBalance() {
        // TODO: Реальна логіка для балансу
        balanceText.setText("1000.00 TNP");
    }
}
