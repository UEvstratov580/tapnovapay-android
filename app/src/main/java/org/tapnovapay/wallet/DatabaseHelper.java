package org.tapnovapay.wallet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "wallet.db";
    private static final int DATABASE_VERSION = 1;
    
    // Таблиця транзакцій
    private static final String TABLE_TRANSACTIONS = "transactions";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_STATUS = "status";
    
    // Таблиця балансу
    private static final String TABLE_BALANCE = "balance";
    private static final String COLUMN_BALANCE = "balance";
    private static final String COLUMN_CURRENCY = "currency";
    
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Створюємо таблицю транзакцій
        String createTransactionsTable = "CREATE TABLE " + TABLE_TRANSACTIONS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TYPE + " TEXT,"
                + COLUMN_AMOUNT + " REAL,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_DATE + " TEXT,"
                + COLUMN_STATUS + " TEXT" + ")";
        db.execSQL(createTransactionsTable);
        
        // Створюємо таблицю балансу
        String createBalanceTable = "CREATE TABLE " + TABLE_BALANCE + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_BALANCE + " REAL,"
                + COLUMN_CURRENCY + " TEXT" + ")";
        db.execSQL(createBalanceTable);
        
        // Додаємо початковий баланс
        ContentValues values = new ContentValues();
        values.put(COLUMN_BALANCE, 1500.00);
        values.put(COLUMN_CURRENCY, "UAH");
        db.insert(TABLE_BALANCE, null, values);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BALANCE);
        onCreate(db);
    }
    
    // Отримати поточний баланс
    public double getBalance() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BALANCE, new String[]{COLUMN_BALANCE}, 
                null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            double balance = cursor.getDouble(0);
            cursor.close();
            return balance;
        }
        return 0.0;
    }
    
    // Оновити баланс
    public void updateBalance(double newBalance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BALANCE, newBalance);
        db.update(TABLE_BALANCE, values, null, null);
    }
    
    // Додати транзакцію
    public void addTransaction(String type, double amount, String description, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TYPE, type);
        values.put(COLUMN_AMOUNT, amount);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_DATE, java.text.DateFormat.getDateTimeInstance().format(new java.util.Date()));
        values.put(COLUMN_STATUS, status);
        db.insert(TABLE_TRANSACTIONS, null, values);
    }
    
    // Отримати всі транзакції
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TRANSACTIONS, null, null, null, null, null, COLUMN_ID + " DESC");
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Transaction transaction = new Transaction(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS))
                );
                transactions.add(transaction);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return transactions;
    }
}
