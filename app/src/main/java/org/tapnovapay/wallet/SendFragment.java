package org.tapnovapay.wallet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SendFragment extends Fragment {
    private EditText recipientInput;
    private EditText amountInput;
    private EditText descriptionInput;
    private TextView balanceInfo;
    private Button sendButton;
    private TapNovaPayAPI api;
    private String currentAddress = "";
    private double currentBalance = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send, container, false);

        recipientInput = view.findViewById(R.id.recipient_input);
        amountInput = view.findViewById(R.id.amount_input);
        descriptionInput = view.findViewById(R.id.description_input);
        balanceInfo = view.findViewById(R.id.balance_info);
        sendButton = view.findViewById(R.id.send_button);

        api = new TapNovaPayAPI();

        // Отримуємо адресу та баланс
        loadWalletInfo();

        sendButton.setOnClickListener(v -> performSend());

        return view;
    }

    private void loadWalletInfo() {
        android.content.SharedPreferences prefs = getActivity().getSharedPreferences("wallet", android.content.Context.MODE_PRIVATE);
        currentAddress = prefs.getString("address", "");

        if (currentAddress.isEmpty()) {
            balanceInfo.setText("Спочатку створіть гаманець");
            return;
        }

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
                        currentBalance = Double.parseDouble(balance);
                        balanceInfo.setText("Баланс: " + balance + " TNP");
                    } catch (Exception e) {
                        balanceInfo.setText("Баланс: 0 TNP");
                    }
                });
            }

            @Override
            public void onError(String error) {
                getActivity().runOnUiThread(() -> {
                    balanceInfo.setText("❌ " + error);
                });
            }
        });
    }

    private void performSend() {
        String recipient = recipientInput.getText().toString().trim();
        String amountStr = amountInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();

        if (currentAddress.isEmpty()) {
            Toast.makeText(getContext(), "Спочатку створіть гаманець!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (recipient.isEmpty()) {
            Toast.makeText(getContext(), "Введіть адресу отримувача", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amountStr.isEmpty()) {
            Toast.makeText(getContext(), "Введіть суму", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                Toast.makeText(getContext(), "Сума має бути більше 0", Toast.LENGTH_SHORT).show();
                return;
            }

            if (amount > currentBalance) {
                Toast.makeText(getContext(), "Недостатньо коштів! Баланс: " + currentBalance + " TNP", Toast.LENGTH_SHORT).show();
                return;
            }

            showConfirmDialog(recipient, amount, description);

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Введіть коректну суму", Toast.LENGTH_SHORT).show();
        }
    }

    private void showConfirmDialog(String recipient, double amount, String description) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("📤 Підтвердження переказу");
        builder.setMessage(String.format(
            "Ви підтверджуєте переказ %.2f TNP\n" +
            "Отримувач: %s\n" +
            "Призначення: %s\n\n" +
            "💰 Після підтвердження транзакцію буде підписано та відправлено в блокчейн",
            amount, recipient, description.isEmpty() ? "—" : description
        ));

        builder.setPositiveButton("Підтвердити", (dialog, which) -> {
            performSendTransaction(recipient, amount, description);
        });

        builder.setNegativeButton("Скасувати", null);
        builder.show();
    }

    private void performSendTransaction(String recipient, double amount, String description) {
        Toast.makeText(getContext(), "⏳ Відправка транзакції...", Toast.LENGTH_SHORT).show();

        // Використовуємо новий метод з address
        api.sendTransaction(currentAddress, recipient, String.valueOf(amount), new TapNovaPayAPI.ApiCallback() {
            @Override
            public void onSuccess(String result) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "✅ Переказано " + amount + " TNP для " + recipient, Toast.LENGTH_LONG).show();
                    recipientInput.setText("");
                    amountInput.setText("");
                    descriptionInput.setText("");
                    loadWalletInfo();
                });
            }

            @Override
            public void onError(String error) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "❌ Помилка: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
