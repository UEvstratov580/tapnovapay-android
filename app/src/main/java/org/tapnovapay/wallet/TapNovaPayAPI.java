package org.tapnovapay.wallet;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class TapNovaPayAPI {
    private static final String TAG = "TapNovaPayAPI";
    
    // Для емулятора: 185.235.218.214
    // Для реального пристрою: 185.235.218.214
    private static final String API_URL = "http://185.235.218.214:5000/api";
    
    private final OkHttpClient client;
    private final Gson gson;
    
    public TapNovaPayAPI() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }
    
    public interface ApiCallback {
        void onSuccess(String result);
        void onError(String error);
    }
    
    // ============ БАЗОВІ МЕТОДИ ============
    
    public void getHealth(ApiCallback callback) {
        get("/health", callback);
    }
    
    public void createWallet(ApiCallback callback) {
        get("/createwallet", callback);
    }
    
    public void getBalance(String address, ApiCallback callback) {
        get("/getbalance?address=" + address, callback);
    }
    
    public void getAddress(ApiCallback callback) {
        get("/getaddress", callback);
    }
    
    public void getHeight(ApiCallback callback) {
        get("/getheight", callback);
    }
    
    public void getHistory(String address, ApiCallback callback) {
        get("/history?address=" + address, callback);
    }
    
    // ============ МЕТОДИ ДЛЯ ТРАНЗАКЦІЙ ============
    
    public void sendTransaction(String address, String to, String amount, ApiCallback callback) {
        JsonObject json = new JsonObject();
        json.addProperty("address", address);
        json.addProperty("to", to);
        json.addProperty("amount", amount);
        post("/send", json.toString(), callback);
    }
    
    public void sendRawTransaction(String signedTx, String address, ApiCallback callback) {
        JsonObject json = new JsonObject();
        json.addProperty("signed_tx", signedTx);
        json.addProperty("address", address);
        post("/sendrawtransaction", json.toString(), callback);
    }
    
    public void walletInfo(String address, ApiCallback callback) {
        get("/walletinfo?address=" + address, callback);
    }
    
    // ============ ВНУТРІШНІ МЕТОДИ ============
    
    private void get(String endpoint, ApiCallback callback) {
        Request request = new Request.Builder()
                .url(API_URL + endpoint)
                .get()
                .build();
        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Помилка: " + e.getMessage());
                callback.onError(e.getMessage());
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body().string());
                } else {
                    callback.onError("HTTP Error: " + response.code());
                }
            }
        });
    }
    
    private void post(String endpoint, String json, ApiCallback callback) {
        RequestBody body = RequestBody.create(
                json, MediaType.parse("application/json"));
        
        Request request = new Request.Builder()
                .url(API_URL + endpoint)
                .post(body)
                .build();
        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Помилка POST: " + e.getMessage());
                callback.onError(e.getMessage());
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body().string());
                } else {
                    callback.onError("HTTP Error: " + response.code());
                }
            }
        });
    }
}

