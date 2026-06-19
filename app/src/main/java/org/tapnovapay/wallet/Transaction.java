package org.tapnovapay.wallet;

public class Transaction {
    private String type;
    private double amount;
    private String description;
    private String date;
    private String status;
    
    public Transaction(String type, double amount, String description, String date, String status) {
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.status = status;
    }
    
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public String getDate() { return date; }
    public String getStatus() { return status; }
}
