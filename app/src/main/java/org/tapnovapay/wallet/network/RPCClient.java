package org.tapnovapay.wallet.network;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import org.tapnovapay.wallet.model.Transaction;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RPCClient {
    
    private static final String RPC_URL = "https://api.tapnovapay.com/rpc"; // Замініть на реальний URL
    private Context context;
    
    public RPCClient(Context context) {
        this.context = context;
    }
    
    public String getBalance() throws Exception {
        JSONObject request = new JSONObject();
        request.put("method", "getbalance");
        request.put("params", new JSONArray());
        request.put("id", 1);
        
        JSONObject response = sendRequest(request);
        return response.getString("result");
    }
    
    public String getAddress() throws Exception {
        SharedPreferences prefs = context.getSharedPreferences("wallet", Context.MODE_PRIVATE);
        String address = prefs.getString("address", null);
        if (address == null) {
            // Генеруємо нову адресу
            JSONObject request = new JSONObject();
            request.put("method", "getnewaddress");
            request.put("params", new JSONArray());
            request.put("id", 2);
            
            JSONObject response = sendRequest(request);
            address = response.getString("result");
            prefs.edit().putString("address", address).apply();
        }
        return address;
    }
    
    public List<Transaction> getTransactions() throws Exception {
        JSONObject request = new JSONObject();
        request.put("method", "listtransactions");
        request.put("params", new JSONArray().put("*").put(10));
        request.put("id", 3);
        
        JSONObject response = sendRequest(request);
        JSONArray txArray = response.getJSONArray("result");
        
        List<Transaction> transactions = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        
        for (int i = 0; i < txArray.length(); i++) {
            JSONObject tx = txArray.getJSONObject(i);
            Transaction transaction = new Transaction();
            transaction.setTxid(tx.getString("txid"));
            transaction.setAmount(tx.getDouble("amount"));
            transaction.setAddress(tx.getString("address"));
            transaction.setCategory(tx.getString("category"));
            transaction.setConfirmations(tx.getInt("confirmations"));
            
            long time = tx.getLong("time") * 1000;
            transaction.setTime(new Date(time));
            
            transactions.add(transaction);
        }
        
        return transactions;
    }
    
    public String sendToAddress(String address, double amount) throws Exception {
        JSONObject request = new JSONObject();
        request.put("method", "sendtoaddress");
        JSONArray params = new JSONArray();
        params.put(address);
        params.put(amount);
        request.put("params", params);
        request.put("id", 4);
        
        JSONObject response = sendRequest(request);
        return response.getString("result");
    }
    
    private JSONObject sendRequest(JSONObject request) throws Exception {
        URL url = new URL(RPC_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        
        // Додаємо автентифікацію
        String auth = "Basic " + android.util.Base64.encodeToString("user:password".getBytes(), android.util.Base64.NO_WRAP);
        conn.setRequestProperty("Authorization", auth);
        
        try (OutputStream os = conn.getOutputStream()) {
            os.write(request.toString().getBytes());
            os.flush();
        }
        
        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new Exception("HTTP error: " + responseCode);
        }
        
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line);
        }
        br.close();
        
        JSONObject jsonResponse = new JSONObject(response.toString());
        if (jsonResponse.has("error") && !jsonResponse.isNull("error")) {
            JSONObject error = jsonResponse.getJSONObject("error");
            throw new Exception(error.getString("message"));
        }
        
        return jsonResponse;
    }
}
