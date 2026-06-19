package org.tapnovapay.wallet;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class TestActivity extends AppCompatActivity {
    private TextView resultText;
    private Button btnTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        resultText = findViewById(R.id.result_text);
        btnTest = findViewById(R.id.btn_test);

        btnTest.setOnClickListener(v -> testAPI());
    }

    private void testAPI() {
        resultText.setText("⏳ Перевірка API...");
        
        TapNovaPayAPI api = new TapNovaPayAPI();
        api.getHealth(new TapNovaPayAPI.ApiCallback() {
            @Override
            public void onSuccess(String result) {
                runOnUiThread(() -> {
                    resultText.setText("✅ API працює!\n" + result);
                    Toast.makeText(TestActivity.this, "✅ Успішно!", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    resultText.setText("❌ Помилка: " + error);
                    Toast.makeText(TestActivity.this, "❌ Помилка: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}
