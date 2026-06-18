package org.tapnovapay.wallet.model;

import java.util.Date;

public class Transaction {
    private String txid;
    private double amount;
    private String address;
    private String category;
    private int confirmations;
    private Date time;
    
    public String getTxid() { return txid; }
    public void setTxid(String txid) { this.txid = txid; }
    
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public int getConfirmations() { return confirmations; }
    public void setConfirmations(int confirmations) { this.confirmations = confirmations; }
    
    public Date getTime() { return time; }
    public void setTime(Date time) { this.time = time; }
}
