package org.tapnovapay.wallet;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;

public class ReceiveDialog extends Dialog {
    
    private String address;
    private TextView addressTextView;
    
    public ReceiveDialog(@NonNull Context context, String address) {
        super(context);
        this.address = address;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_receive);
        
        addressTextView = findViewById(R.id.address_text_view);
        addressTextView.setText(address);
        
        // Копіювання адреси
        addressTextView.setOnLongClickListener(v -> {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) 
                getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("address", address);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), "Адресу скопійовано", Toast.LENGTH_SHORT).show();
            return true;
        });
    }
}
