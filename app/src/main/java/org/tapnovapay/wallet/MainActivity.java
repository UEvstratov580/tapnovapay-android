package org.tapnovapay.wallet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigation;
    private boolean isPinVerified = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Перевіряємо PIN-код
        SharedPreferences prefs = getSharedPreferences("wallet", MODE_PRIVATE);
        String savedPin = prefs.getString("pin", "");
        boolean walletCreated = prefs.getBoolean("wallet_created", false);

        if (!walletCreated) {
            startActivity(new Intent(this, CreateWalletActivity.class));
            finish();
            return;
        }

        if (!savedPin.isEmpty()) {
            // Якщо PIN встановлено - запитуємо
            Intent pinIntent = new Intent(this, PinActivity.class);
            startActivityForResult(pinIntent, 100);
        }

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_balance) {
                    getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new BalanceFragment())
                        .commit();
                    return true;
                } else if (id == R.id.nav_send) {
                    getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new SendFragment())
                        .commit();
                    return true;
                } else if (id == R.id.nav_receive) {
                    getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ReceiveFragment())
                        .commit();
                    return true;
                } else if (id == R.id.nav_history) {
                    startActivity(new Intent(MainActivity.this, TransactionHistoryActivity.class));
                    return true;
                } else if (id == R.id.nav_settings) {
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                    return true;
                }
                return false;
            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new BalanceFragment())
                .commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode != RESULT_OK) {
            finish();
        }
    }
}
