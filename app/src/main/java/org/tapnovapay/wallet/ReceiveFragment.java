package org.tapnovapay.wallet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

public class ReceiveFragment extends Fragment {
    private static final int REQUEST_CODE_SCAN = 1001;
    private TextView addressText;
    private ImageView qrImage;
    private Button btnCopy, btnShare, btnScan;
    private String currentAddress = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_receive, container, false);

        addressText = view.findViewById(R.id.receive_address_text);
        qrImage = view.findViewById(R.id.qr_image);
        btnCopy = view.findViewById(R.id.btn_copy_address);
        btnShare = view.findViewById(R.id.btn_share_address);
        btnScan = view.findViewById(R.id.btn_scan_qr);

        loadAddress();

        btnCopy.setOnClickListener(v -> copyAddress());
        btnShare.setOnClickListener(v -> shareAddress());
        btnScan.setOnClickListener(v -> scanQR());

        return view;
    }

    private void loadAddress() {
        android.content.SharedPreferences prefs = getActivity().getSharedPreferences("wallet", android.content.Context.MODE_PRIVATE);
        String savedAddress = prefs.getString("address", "");

        if (!savedAddress.isEmpty()) {
            currentAddress = savedAddress;
            addressText.setText(currentAddress);
            generateQRCode(currentAddress);
        } else {
            addressText.setText("Створіть гаманець");
            qrImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    private void generateQRCode(String text) {
        if (text.isEmpty()) return;
        
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 400, 400);
            
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            
            qrImage.setImageBitmap(bitmap);
            
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "❌ Помилка генерації QR-коду", Toast.LENGTH_SHORT).show();
        }
    }

    private void copyAddress() {
        if (currentAddress.isEmpty()) {
            Toast.makeText(getContext(), "Спочатку створіть гаманець!", Toast.LENGTH_SHORT).show();
            return;
        }

        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getContext().getSystemService(android.content.Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Address", currentAddress);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getContext(), "✅ Адресу скопійовано!", Toast.LENGTH_SHORT).show();
    }

    private void shareAddress() {
        if (currentAddress.isEmpty()) {
            Toast.makeText(getContext(), "Спочатку створіть гаманець!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Моя адреса TNP: " + currentAddress);
        startActivity(Intent.createChooser(shareIntent, "Поділитися адресою"));
    }

    private void scanQR() {
        Intent intent = new Intent(getActivity(), CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SCAN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_CODE_SCAN && resultCode == getActivity().RESULT_OK && data != null) {
            String scannedAddress = data.getStringExtra("SCAN_RESULT");
            if (scannedAddress != null && !scannedAddress.isEmpty()) {
                // Переходимо на екран відправки з заповненою адресою
                SendFragment sendFragment = new SendFragment();
                Bundle bundle = new Bundle();
                bundle.putString("recipient_address", scannedAddress);
                sendFragment.setArguments(bundle);
                
                getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, sendFragment)
                    .commit();
            }
        }
    }
}
