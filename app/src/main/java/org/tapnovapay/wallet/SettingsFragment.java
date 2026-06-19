package org.tapnovapay.wallet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        TextView settingsText = view.findViewById(R.id.settings_text);
        
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        double balance = dbHelper.getBalance();
        
        settingsText.setText(
            "⚙️ НАЛАШТУВАННЯ\n\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
            "👤 ПРОФІЛЬ\n" +
            "   Ім'\''я: Юрій\n" +
            "   Email: yurij@email.com\n\n" +
            "💳 БАЛАНС\n" +
            "   " + String.format("₴ %.2f", balance) + "\n\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
            "🔒 БЕЗПЕКА\n" +
            "   PIN-код: Встановлено\n" +
            "   Відбиток: Вимкнено\n" +
            "   2FA: Вимкнено\n\n" +
            "🎨 ТЕМА\n" +
            "   Світла тема\n\n" +
            "ℹ️ ПРО ДОДАТОК\n" +
            "   Версія: 1.0.0\n" +
            "   Розробник: TapNovaPay\n" +
            "   Ліцензія: MIT"
        );
        return view;
    }
}
