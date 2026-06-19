package org.tapnovapay.wallet;

import android.util.Log;
import java.security.SecureRandom;

public class WalletKeyGenerator {
    private static final String TAG = "WalletKeyGen";
    private String privateKey;
    private String address;
    private String seedPhrase;

    public WalletKeyGenerator() {
        generateKeys();
    }

    private void generateKeys() {
        try {
            // Генеруємо seed-фразу
            seedPhrase = generateSeedPhrase();
            
            // Генеруємо приватний ключ (простий спосіб)
            SecureRandom random = new SecureRandom();
            byte[] bytes = new byte[32];
            random.nextBytes(bytes);
            privateKey = bytesToHex(bytes);
            
            // Генеруємо адресу
            address = "tnp" + privateKey.substring(0, 40);
            
            Log.d(TAG, "Address: " + address);
            Log.d(TAG, "Seed: " + seedPhrase);
            
        } catch (Exception e) {
            Log.e(TAG, "Помилка: " + e.getMessage());
            generateFallback();
        }
    }

    private void generateFallback() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        privateKey = bytesToHex(bytes);
        address = "tnp" + privateKey.substring(0, 40);
        seedPhrase = generateSeedPhrase();
    }

    private String generateSeedPhrase() {
        String[] words = {
            "abandon", "ability", "able", "about", "above", "absent",
            "absorb", "abstract", "absurd", "abuse", "access", "accident",
            "account", "accuse", "achieve", "acid", "acoustic", "acquire",
            "across", "act", "action", "actor", "actress", "actual",
            "adapt", "add", "addict", "address", "adjust", "admit",
            "adult", "advance", "advice", "aerobic", "affair", "afford",
            "afraid", "again", "age", "agent", "agree", "ahead",
            "aim", "air", "airport", "aisle", "alarm", "album",
            "alcohol", "alert", "alien", "all", "alley", "allow"
        };

        StringBuilder seed = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 12; i++) {
            int index = random.nextInt(words.length);
            seed.append(words[index]);
            if (i < 11) seed.append(" ");
        }
        return seed.toString();
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }

    public String getPrivateKey() { return privateKey; }
    public String getAddress() { return address; }
    public String getSeedPhrase() { return seedPhrase; }
}
