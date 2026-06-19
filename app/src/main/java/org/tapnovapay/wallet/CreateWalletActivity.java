package org.tapnovapay.wallet;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class CreateWalletActivity extends AppCompatActivity {
    private static final String TAG = "CreateWallet";
    
    private LinearLayout choiceLayout;
    private LinearLayout restoreChoiceLayout;
    private LinearLayout restoreSeedLayout;
    private LinearLayout restoreKeyLayout;
    private LinearLayout seedDisplayLayout;
    private LinearLayout seedVerifyLayout;
    
    private EditText seedInput;
    private EditText privateKeyInput;
    private EditText verifyInput;
    private TextView seedDisplay;
    private TextView verifyHint;
    private Button btnCreateNew;
    private Button btnRestoreChoice;
    private Button btnRestoreSeed;
    private Button btnRestoreKey;
    private Button btnRestoreSeedConfirm;
    private Button btnRestoreKeyConfirm;
    private Button btnCopySeed;
    private Button btnSeedVerified;
    private Button btnVerifySeed;
    private Button btnBackToChoice;
    private Button btnBackToRestoreChoice;
    private Button btnBackToRestoreChoiceKey;
    
    private String generatedAddress = "";
    private String generatedPrivateKey = "";
    private String generatedSeed = "";
    private int verifyWordIndex = 0;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wallet);
        
        Log.d(TAG, "onCreate");

        // Ініціалізація UI
        choiceLayout = findViewById(R.id.choice_layout);
        restoreChoiceLayout = findViewById(R.id.restore_choice_layout);
        restoreSeedLayout = findViewById(R.id.restore_seed_layout);
        restoreKeyLayout = findViewById(R.id.restore_key_layout);
        seedDisplayLayout = findViewById(R.id.seed_display_layout);
        seedVerifyLayout = findViewById(R.id.seed_verify_layout);
        
        seedInput = findViewById(R.id.seed_input);
        privateKeyInput = findViewById(R.id.private_key_input);
        verifyInput = findViewById(R.id.verify_input);
        seedDisplay = findViewById(R.id.seed_display);
        verifyHint = findViewById(R.id.verify_hint);
        
        btnCreateNew = findViewById(R.id.btn_create_new);
        btnRestoreChoice = findViewById(R.id.btn_restore_choice);
        btnRestoreSeed = findViewById(R.id.btn_restore_seed);
        btnRestoreKey = findViewById(R.id.btn_restore_key);
        btnRestoreSeedConfirm = findViewById(R.id.btn_restore_seed_confirm);
        btnRestoreKeyConfirm = findViewById(R.id.btn_restore_key_confirm);
        btnCopySeed = findViewById(R.id.btn_copy_seed);
        btnSeedVerified = findViewById(R.id.btn_seed_verified);
        btnVerifySeed = findViewById(R.id.btn_verify_seed);
        btnBackToChoice = findViewById(R.id.btn_back_to_choice);
        btnBackToRestoreChoice = findViewById(R.id.btn_back_to_restore_choice);
        btnBackToRestoreChoiceKey = findViewById(R.id.btn_back_to_restore_choice_key);

        btnCreateNew.setOnClickListener(v -> {
            Log.d(TAG, "Натиснуто створення");
            btnCreateNew.setEnabled(false);
            mainHandler.postDelayed(() -> createNewWallet(), 100);
        });
        
        btnRestoreChoice.setOnClickListener(v -> showRestoreChoice());
        btnRestoreSeed.setOnClickListener(v -> showRestoreSeed());
        btnRestoreKey.setOnClickListener(v -> showRestoreKey());
        btnRestoreSeedConfirm.setOnClickListener(v -> restoreBySeed());
        btnRestoreKeyConfirm.setOnClickListener(v -> restoreByPrivateKey());
        btnCopySeed.setOnClickListener(v -> copySeed());
        btnSeedVerified.setOnClickListener(v -> startSeedVerification());
        btnVerifySeed.setOnClickListener(v -> verifySeed());
        btnBackToChoice.setOnClickListener(v -> showChoice());
        btnBackToRestoreChoice.setOnClickListener(v -> showRestoreChoice());
        btnBackToRestoreChoiceKey.setOnClickListener(v -> showRestoreChoice());

        showChoice();
        Log.d(TAG, "Готово");
    }

    private void showChoice() {
        choiceLayout.setVisibility(View.VISIBLE);
        restoreChoiceLayout.setVisibility(View.GONE);
        restoreSeedLayout.setVisibility(View.GONE);
        restoreKeyLayout.setVisibility(View.GONE);
        seedDisplayLayout.setVisibility(View.GONE);
        seedVerifyLayout.setVisibility(View.GONE);
    }

    private void showRestoreChoice() {
        choiceLayout.setVisibility(View.GONE);
        restoreChoiceLayout.setVisibility(View.VISIBLE);
        restoreSeedLayout.setVisibility(View.GONE);
        restoreKeyLayout.setVisibility(View.GONE);
        seedDisplayLayout.setVisibility(View.GONE);
    }

    private void showRestoreSeed() {
        restoreChoiceLayout.setVisibility(View.GONE);
        restoreSeedLayout.setVisibility(View.VISIBLE);
        restoreKeyLayout.setVisibility(View.GONE);
    }

    private void showRestoreKey() {
        restoreChoiceLayout.setVisibility(View.GONE);
        restoreSeedLayout.setVisibility(View.GONE);
        restoreKeyLayout.setVisibility(View.VISIBLE);
    }

    private void createNewWallet() {
        Log.d(TAG, "Створення гаманця");
        
        try {
            // Генеруємо ключі
            WalletKeyGenerator keyGen = new WalletKeyGenerator();
            generatedAddress = keyGen.getAddress();
            generatedPrivateKey = keyGen.getPrivateKey();
            generatedSeed = keyGen.getSeedPhrase();

            Log.d(TAG, "Адреса: " + generatedAddress);

            // Зберігаємо
            android.content.SharedPreferences prefs = getSharedPreferences("wallet", MODE_PRIVATE);
            prefs.edit().putString("address", generatedAddress).apply();
            prefs.edit().putString("private_key", generatedPrivateKey).apply();
            prefs.edit().putString("seed", generatedSeed).apply();
            prefs.edit().putBoolean("wallet_created", true).apply();

            // Показуємо seed
            seedDisplay.setText(generatedSeed);
            seedDisplayLayout.setVisibility(View.VISIBLE);
            choiceLayout.setVisibility(View.GONE);
            btnSeedVerified.setVisibility(View.GONE);
            seedVerifyLayout.setVisibility(View.GONE);
            
            Toast.makeText(this, "✅ Гаманець створено!", Toast.LENGTH_LONG).show();
            btnCreateNew.setEnabled(true);

        } catch (Exception e) {
            Log.e(TAG, "Помилка: " + e.getMessage());
            Toast.makeText(this, "❌ Помилка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            btnCreateNew.setEnabled(true);
        }
    }

    private void copySeed() {
        if (generatedSeed.isEmpty()) {
            Toast.makeText(this, "Немає seed-фрази", Toast.LENGTH_SHORT).show();
            return;
        }
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Seed", generatedSeed);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "📋 Seed-фразу скопійовано!", Toast.LENGTH_SHORT).show();
        btnSeedVerified.setVisibility(View.VISIBLE);
    }

    private void startSeedVerification() {
        if (generatedSeed.isEmpty()) {
            Toast.makeText(this, "Немає seed-фрази", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String[] words = generatedSeed.split(" ");
        verifyWordIndex = (int) (Math.random() * words.length);
        verifyHint.setText("Введіть слово #" + (verifyWordIndex + 1) + " з 12");
        seedVerifyLayout.setVisibility(View.VISIBLE);
        verifyInput.setText("");
        btnSeedVerified.setVisibility(View.GONE);
        btnVerifySeed.setEnabled(true);
    }

    private void verifySeed() {
        String input = verifyInput.getText().toString().trim();
        String[] words = generatedSeed.split(" ");
        
        if (input.isEmpty()) {
            Toast.makeText(this, "Введіть слово", Toast.LENGTH_SHORT).show();
            return;
        }

        if (input.equals(words[verifyWordIndex])) {
            Toast.makeText(this, "✅ Правильно!", Toast.LENGTH_SHORT).show();
            
            android.content.SharedPreferences prefs = getSharedPreferences("wallet", MODE_PRIVATE);
            prefs.edit().putBoolean("wallet_verified", true).apply();
            
            startActivity(new android.content.Intent(CreateWalletActivity.this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "❌ Неправильно! Спробуйте ще раз", Toast.LENGTH_SHORT).show();
            verifyInput.setText("");
        }
    }

    private void restoreBySeed() {
        String seed = seedInput.getText().toString().trim();
        if (seed.isEmpty()) {
            Toast.makeText(this, "Введіть seed-фразу", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "⏳ Відновлення...", Toast.LENGTH_SHORT).show();
        
        android.content.SharedPreferences prefs = getSharedPreferences("wallet", MODE_PRIVATE);
        prefs.edit().putString("seed", seed).apply();
        prefs.edit().putBoolean("wallet_created", true).apply();
        
        Toast.makeText(this, "✅ Гаманець відновлено!", Toast.LENGTH_LONG).show();
        
        startActivity(new android.content.Intent(CreateWalletActivity.this, MainActivity.class));
        finish();
    }

    private void restoreByPrivateKey() {
        String privateKey = privateKeyInput.getText().toString().trim();
        if (privateKey.isEmpty()) {
            Toast.makeText(this, "Введіть закритий ключ", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "⏳ Відновлення...", Toast.LENGTH_SHORT).show();
        
        android.content.SharedPreferences prefs = getSharedPreferences("wallet", MODE_PRIVATE);
        prefs.edit().putString("private_key", privateKey).apply();
        prefs.edit().putBoolean("wallet_created", true).apply();
        
        Toast.makeText(this, "✅ Гаманець відновлено!", Toast.LENGTH_LONG).show();
        
        startActivity(new android.content.Intent(CreateWalletActivity.this, MainActivity.class));
        finish();
    }
}
