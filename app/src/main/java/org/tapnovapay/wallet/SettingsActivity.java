package org.tapnovapay.wallet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SettingsActivity extends AppCompatActivity {
    private TextView addressDisplay;
    private TextView privateKeyDisplay;
    private TextView seedDisplay;
    private Button btnTheme;
    private Button btnShowKeys;
    private Button btnBackup;
    private Button btnPin;
    private Button btnFingerprint;
    private SharedPreferences prefs;
    private boolean keysVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences("wallet", MODE_PRIVATE);

        addressDisplay = findViewById(R.id.address_display);
        privateKeyDisplay = findViewById(R.id.private_key_display);
        seedDisplay = findViewById(R.id.seed_display);
        btnTheme = findViewById(R.id.btn_theme);
        btnShowKeys = findViewById(R.id.btn_show_keys);
        btnBackup = findViewById(R.id.btn_backup);
        btnPin = findViewById(R.id.btn_pin);
        btnFingerprint = findViewById(R.id.btn_fingerprint);

        // Показуємо адресу
        String address = prefs.getString("address", "Не створено");
        addressDisplay.setText(address);

        // Приховуємо ключі за замовчуванням
        privateKeyDisplay.setVisibility(View.GONE);
        seedDisplay.setVisibility(View.GONE);

        // Оновлюємо статус PIN
        updatePinStatus();

        // Налаштовуємо кнопки
        btnTheme.setOnClickListener(v -> toggleTheme());
        btnShowKeys.setOnClickListener(v -> toggleKeys());
        btnBackup.setOnClickListener(v -> backupWallet());
        btnPin.setOnClickListener(v -> setupPin());
        btnFingerprint.setOnClickListener(v -> setupFingerprint());

        // Перевіряємо чи підтримується відбиток
        checkFingerprintSupport();
    }

    private void updatePinStatus() {
        String savedPin = prefs.getString("pin", "");
        if (!savedPin.isEmpty()) {
            btnPin.setText("🔒 Змінити PIN-код");
            btnPin.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.holo_green_dark));
        } else {
            btnPin.setText("🔒 Встановити PIN-код");
            btnPin.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.holo_orange_dark));
        }
    }

    private void toggleTheme() {
        int currentMode = AppCompatDelegate.getDefaultNightMode();
        if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            btnTheme.setText("🌞 Світла тема");
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            btnTheme.setText("🌙 Темна тема");
        }
        // Перезапускаємо активність для застосування теми
        recreate();
    }

    private void toggleKeys() {
        keysVisible = !keysVisible;

        if (keysVisible) {
            String privateKey = prefs.getString("private_key", "Не знайдено");
            String seed = prefs.getString("seed", "Не знайдено");
            
            privateKeyDisplay.setText("🔑 " + privateKey);
            seedDisplay.setText("🌱 " + seed);
            privateKeyDisplay.setVisibility(View.VISIBLE);
            seedDisplay.setVisibility(View.VISIBLE);
            btnShowKeys.setText("🙈 Приховати ключі");
        } else {
            privateKeyDisplay.setVisibility(View.GONE);
            seedDisplay.setVisibility(View.GONE);
            btnShowKeys.setText("👁️ Показати ключі");
        }
    }

    private void backupWallet() {
        String seed = prefs.getString("seed", "");
        String address = prefs.getString("address", "");
        
        if (seed.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Спочатку створіть гаманець!", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("💾 Бекап гаманця");
        
        String message = 
            "📌 Адреса:\n" + address + "\n\n" +
            "🌱 SEED-ФРАЗА (12 слів):\n" + seed + "\n\n" +
            "⚠️ НІКОМУ НЕ ПОКАЗУЙТЕ ЦІ ДАНІ!\n" +
            "❗ Втрата seed-фрази = втрата монет!";
        
        builder.setMessage(message);
        builder.setPositiveButton("📋 Копіювати seed", (dialog, which) -> {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Seed", seed);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "📋 Seed скопійовано!", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Закрити", null);
        builder.show();
    }

    private void setupPin() {
        // Переходимо до налаштування PIN
        Intent pinIntent = new Intent(this, PinActivity.class);
        startActivityForResult(pinIntent, 100);
    }

    private void checkFingerprintSupport() {
        BiometricManager biometricManager = BiometricManager.from(this);
        int canAuthenticate = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG);
        
        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            btnFingerprint.setEnabled(true);
            btnFingerprint.setText("🖐️ Встановити відбиток");
            btnFingerprint.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.holo_green_dark));
        } else {
            btnFingerprint.setEnabled(false);
            btnFingerprint.setText("🖐️ Відбиток не підтримується");
            btnFingerprint.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.darker_gray));
        }
    }

    private void setupFingerprint() {
        // Перевіряємо чи є PIN
        String savedPin = prefs.getString("pin", "");
        if (savedPin.isEmpty()) {
            Toast.makeText(this, "Спочатку встановіть PIN-код!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Перевіряємо підтримку відбитка
        BiometricManager biometricManager = BiometricManager.from(this);
        int canAuthenticate = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG);
        
        if (canAuthenticate != BiometricManager.BIOMETRIC_SUCCESS) {
            Toast.makeText(this, "❌ Відбиток не доступний на цьому пристрої", Toast.LENGTH_SHORT).show();
            return;
        }

        // Показуємо діалог налаштування відбитка
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("🖐️ Налаштування відбитка");
        builder.setMessage(
            "Для використання відбитка пальця:\n\n" +
            "1. Перейдіть в налаштування телефону\n" +
            "2. Додайте відбиток пальця\n" +
            "3. Поверніться в додаток\n\n" +
            "Після додавання відбитка, ви зможете входити за відбитком."
        );
        builder.setPositiveButton("Перейти в налаштування", (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
            startActivity(intent);
        });
        builder.setNegativeButton("OK", null);
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            // Оновлюємо статус PIN після повернення
            updatePinStatus();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Оновлюємо статус PIN
        updatePinStatus();
        // Оновлюємо тему
        updateThemeButton();
    }

    private void updateThemeButton() {
        int currentMode = AppCompatDelegate.getDefaultNightMode();
        if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
            btnTheme.setText("🌙 Темна тема");
        } else {
            btnTheme.setText("🌞 Світла тема");
        }
    }
}
