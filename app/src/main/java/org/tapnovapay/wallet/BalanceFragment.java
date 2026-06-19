package org.tapnovapay.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class BalanceFragment extends Fragment {
    private TextView balanceText;
    private TextView balanceUsd;
    private TextView addressText;
    private TextView privateKeyText;
    private TextView seedPhraseText;
    private Button btnSend;
    private Button btnReceive;
    private Button btnRefresh;
    private Button btnSwap;
    private Button createWalletButton;
    private Button showKeysButton;
    private ImageView settingsButton;  // ЗМІНЕНО: ImageView замість Button
    private TapNovaPayAPI api;
    private String currentAddress = "";
    private String currentPrivateKey = "";
    private String currentSeed = "";
    private boolean keysVisible = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_balance, container, false);

        balanceText = view.findViewById(R.id.balance_text);
        balanceUsd = view.findViewById(R.id.balance_usd);
        addressText = view.findViewById(R.id.address_text);
        privateKeyText = view.findViewById(R.id.private_key_text);
        seedPhraseText = view.findViewById(R.id.seed_phrase_text);
        btnSend = view.findViewById(R.id.btn_send);
        btnReceive = view.findViewById(R.id.btn_receive);
        btnRefresh = view.findViewById(R.id.btn_refresh);
        btnSwap = view.findViewById(R.id.btn_swap);
        createWalletButton = view.findViewById(R.id.create_wallet_button);
        showKeysButton = view.findViewById(R.id.show_keys_button);
        settingsButton = view.findViewById(R.id.settings_button);  // ImageView

        api = new TapNovaPayAPI();

        checkExistingWallet();

        btnSend.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SendFragment())
                .commit();
        });

        btnReceive.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new ReceiveFragment())
                .commit();
        });

        btnRefresh.setOnClickListener(v -> loadBalance());
        btnSwap.setOnClickListener(v -> Toast.makeText(getContext(), "🔄 Swap буде доступно скоро!", Toast.LENGTH_SHORT).show());
        createWalletButton.setOnClickListener(v -> showCreateWalletDialog());
        
        showKeysButton.setOnClickListener(v -> showKeysDialog());

        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void checkExistingWallet() {
        android.content.SharedPreferences prefs = getActivity().getSharedPreferences("wallet", android.content.Context.MODE_PRIVATE);
        String savedAddress = prefs.getString("address", "");
        currentPrivateKey = prefs.getString("private_key", "");
        currentSeed = prefs.getString("seed", "");

        if (!savedAddress.isEmpty()) {
            currentAddress = savedAddress;
            addressText.setText("📌 " + savedAddress.substring(0, Math.min(20, savedAddress.length())) + "...");
            loadBalance();
        } else {
            balanceText.setText("ℹ️ Створіть гаманець");
            balanceUsd.setText("");
            addressText.setText("Натисніть 'Створити новий гаманець'");
        }
    }

    private void showCreateWalletDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("🆕 Створення гаманця");
        builder.setMessage(
            "Ви впевнені, що хочете створити новий гаманець?\n\n" +
            "⚠️ ВАЖЛИВО:\n" +
            "• Seed-фраза зберігається локально\n" +
            "• Нікому не показуйте приватний ключ\n" +
            "• Втрата ключів = втрата монет"
        );

        builder.setPositiveButton("Створити", (dialog, which) -> createNewWallet());
        builder.setNegativeButton("Скасувати", null);
        builder.show();
    }

    private void createNewWallet() {
        getActivity().runOnUiThread(() -> {
            Toast.makeText(getContext(), "⏳ Створення гаманця...", Toast.LENGTH_SHORT).show();
        });

        WalletKeyGenerator keyGen = new WalletKeyGenerator();
        String newAddress = keyGen.getAddress();
        String newPrivateKey = keyGen.getPrivateKey();
        String newSeed = keyGen.getSeedPhrase();

        android.content.SharedPreferences prefs = getActivity().getSharedPreferences("wallet", android.content.Context.MODE_PRIVATE);
        prefs.edit().putString("address", newAddress).apply();
        prefs.edit().putString("private_key", newPrivateKey).apply();
        prefs.edit().putString("seed", newSeed).apply();
        prefs.edit().putBoolean("wallet_created", true).apply();

        currentAddress = newAddress;
        currentPrivateKey = newPrivateKey;
        currentSeed = newSeed;

        addressText.setText("📌 " + newAddress.substring(0, Math.min(20, newAddress.length())) + "...");
        
        Toast.makeText(getContext(), "✅ Гаманець створено! Баланс: 0 TNP", Toast.LENGTH_LONG).show();
        loadBalance();
        showWalletInfoDialog(newAddress);
    }

    private void showWalletInfoDialog(String address) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("🔐 ВАША АДРЕСА");
        builder.setMessage(
            "📌 Адреса гаманця:\n" + address + "\n\n" +
            "💡 Використовуйте цю адресу для отримання TNP\n\n" +
            "❗ Збережіть цю адресу!"
        );
        builder.setPositiveButton("✅ Зрозуміло", null);
        builder.show();
    }

    private void showKeysDialog() {
        if (currentPrivateKey.isEmpty() || currentSeed.isEmpty()) {
            Toast.makeText(getContext(), "Спочатку створіть гаманець!", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("🔐 КЛЮЧІ ГАМАНЦЯ");
        
        String message = 
            "🔑 ПРИВАТНИЙ КЛЮЧ:\n" + currentPrivateKey + "\n\n" +
            "🌱 SEED-ФРАЗА (12 слів):\n" + currentSeed + "\n\n" +
            "⚠️ НІКОМУ НЕ ПОКАЗУЙТЕ ЦІ ДАНІ!";

        builder.setMessage(message);
        builder.setPositiveButton("📋 Копіювати все", (dialog, which) -> {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getContext().getSystemService(android.content.Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Wallet Keys", 
                "Private Key: " + currentPrivateKey + "\nSeed: " + currentSeed);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), "📋 Дані скопійовано!", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Закрити", null);
        builder.show();
    }

    private void loadBalance() {
        if (currentAddress.isEmpty()) {
            balanceText.setText("ℹ️ Створіть гаманець");
            return;
        }

        getActivity().runOnUiThread(() -> {
            balanceText.setText("🔄 Завантаження...");
            balanceUsd.setText("");
        });

        api.getBalance(currentAddress, new TapNovaPayAPI.ApiCallback() {
            @Override
            public void onSuccess(String result) {
                getActivity().runOnUiThread(() -> {
                    try {
                        JsonObject json = JsonParser.parseString(result).getAsJsonObject();
                        String balance = "0";
                        if (json.has("balance")) {
                            String balanceStr = json.get("balance").getAsString();
                            String[] parts = balanceStr.split(" ");
                            for (String part : parts) {
                                if (part.matches("\\d+")) {
                                    balance = part;
                                    break;
                                }
                            }
                        }
                        balanceText.setText(balance + " TNP");
                        double usdRate = 0.012;
                        double usdValue = Double.parseDouble(balance) * usdRate;
                        balanceUsd.setText("$" + String.format("%.2f", usdValue) + " USD");
                    } catch (Exception e) {
                        balanceText.setText("❌ Помилка");
                    }
                });
            }

            @Override
            public void onError(String error) {
                getActivity().runOnUiThread(() -> {
                    balanceText.setText("❌ " + error);
                });
            }
        });
    }
}
