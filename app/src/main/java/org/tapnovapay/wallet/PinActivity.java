package org.tapnovapay.wallet;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PinActivity extends AppCompatActivity {
    private TextView pinDisplay;
    private TextView pinTitle;
    private StringBuilder pinCode = new StringBuilder();
    private String savedPin = "";
    private boolean isSettingPin = false;
    private String tempPin = "";
    private int attempts = 0;
    private static final int MAX_ATTEMPTS = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        pinDisplay = findViewById(R.id.pin_display);
        pinTitle = findViewById(R.id.pin_title);
        
        SharedPreferences prefs = getSharedPreferences("wallet", MODE_PRIVATE);
        savedPin = prefs.getString("pin", "");
        isSettingPin = savedPin.isEmpty();

        if (isSettingPin) {
            pinTitle.setText("🔒 Встановлення PIN-коду");
            pinDisplay.setText("Введіть 4 цифри");
        } else {
            pinTitle.setText("🔒 Введіть PIN-код");
            pinDisplay.setText("Введіть 4 цифри");
        }

        setupNumberButtons();
        setupActionButtons();
    }

    private void setupNumberButtons() {
        int[] ids = {
            R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4,
            R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9
        };

        for (int id : ids) {
            Button btn = findViewById(id);
            btn.setOnClickListener(v -> {
                if (pinCode.length() < 4) {
                    pinCode.append(btn.getText().toString());
                    updateDisplay();
                    if (pinCode.length() == 4) {
                        handlePinComplete();
                    }
                }
            });
        }
    }

    private void setupActionButtons() {
        findViewById(R.id.btn_delete).setOnClickListener(v -> {
            if (pinCode.length() > 0) {
                pinCode.deleteCharAt(pinCode.length() - 1);
                updateDisplay();
            }
        });

        findViewById(R.id.btn_clear).setOnClickListener(v -> {
            pinCode.setLength(0);
            updateDisplay();
        });
    }

    private void updateDisplay() {
        String display = "";
        for (int i = 0; i < pinCode.length(); i++) {
            display += "● ";
        }
        for (int i = pinCode.length(); i < 4; i++) {
            display += "○ ";
        }
        pinDisplay.setText(display.trim());
    }

    private void handlePinComplete() {
        String enteredPin = pinCode.toString();
        
        if (isSettingPin) {
            // Встановлення PIN
            if (tempPin.isEmpty()) {
                tempPin = enteredPin;
                pinCode.setLength(0);
                updateDisplay();
                pinDisplay.setText("Повторіть PIN-код");
                pinTitle.setText("🔒 Підтвердження");
            } else {
                if (enteredPin.equals(tempPin)) {
                    SharedPreferences prefs = getSharedPreferences("wallet", MODE_PRIVATE);
                    prefs.edit().putString("pin", enteredPin).apply();
                    Toast.makeText(this, "✅ PIN-код встановлено!", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(this, "❌ PIN-коди не співпадають", Toast.LENGTH_SHORT).show();
                    tempPin = "";
                    pinCode.setLength(0);
                    updateDisplay();
                    pinDisplay.setText("Введіть 4 цифри");
                    pinTitle.setText("🔒 Встановлення PIN-коду");
                }
            }
        } else {
            // Перевірка PIN
            if (enteredPin.equals(savedPin)) {
                Toast.makeText(this, "✅ PIN-код правильний", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                attempts++;
                if (attempts >= MAX_ATTEMPTS) {
                    Toast.makeText(this, "❌ Забагато спроб! Додаток закривається", Toast.LENGTH_LONG).show();
                    setResult(RESULT_CANCELED);
                    finish();
                } else {
                    Toast.makeText(this, "❌ Неправильний PIN. Спроб: " + attempts + "/" + MAX_ATTEMPTS, Toast.LENGTH_SHORT).show();
                    pinCode.setLength(0);
                    updateDisplay();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (isSettingPin && tempPin.isEmpty()) {
            // Дозволяємо вихід під час встановлення PIN
            setResult(RESULT_CANCELED);
            super.onBackPressed();
        } else if (!isSettingPin) {
            // Забороняємо вихід під час вводу PIN
            Toast.makeText(this, "Введіть PIN-код для входу", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }
}
