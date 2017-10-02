package com.example.ed.valiutoskonvertavimaspaysera_edvinaspeciauskas;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;

import static com.example.ed.valiutoskonvertavimaspaysera_edvinaspeciauskas.TransfersDBContract.Transfers.*;
import static com.example.ed.valiutoskonvertavimaspaysera_edvinaspeciauskas.CurrenciesDBContract.Currencies.*;
import static com.example.ed.valiutoskonvertavimaspaysera_edvinaspeciauskas.Constants.*;


public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "currencies.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CurrenciesDBContract.Currencies.CREATE_TABLE);
        sqLiteDatabase.execSQL(TransfersDBContract.Transfers.CREATE_TABLE);

        //create default database first time launching app
        if (getCountCurrencies(sqLiteDatabase) == 0) {
            addCurrency(sqLiteDatabase, EUR, 1000);
            addCurrency(sqLiteDatabase, USD, 0);
            addCurrency(sqLiteDatabase, JPY, 0);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_CURRENCIES);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_TRANSFERS);
        onCreate(sqLiteDatabase);
    }

    public long getCountCurrencies(SQLiteDatabase sqLiteDatabase) {
        return DatabaseUtils.queryNumEntries(sqLiteDatabase, DB_TABLE_CURRENCIES);
    }


    /**
     * For adding currencies first time creating app database
     *
     * @param sqLiteDatabase
     * @param currencyText - currency type
     * @param amount
     */
    public void addCurrency(SQLiteDatabase sqLiteDatabase, String currencyText, int amount) {
        ContentValues values = new ContentValues();
        values.put(DB_COLUMN_CURRENCY, currencyText);
        values.put(DB_COLUMN_AMOUNT, amount);
        sqLiteDatabase.insert(DB_TABLE_CURRENCIES, null, values);
    }


    /**
     * @return - number of finished successful conversions
     */
    public long getCompletedComversionsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, DB_TABLE_TRANSFERS);
    }

    /**
     * Add conversion information to database
     *
     * @param commission - commission price
     * @param conversionText - completed conversion text
     */
    public void addCommission(Double commission, String conversionText) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();

        values.put(DB_COLUMN_COMMISSION, commission);
        values.put(DB_COLUMN_TRANSFER, conversionText);

        db.insert(DB_TABLE_TRANSFERS, null, values);
    }

    /**
     * @return - return cursor of all completed currency conversions
     */
    public Cursor getAllConversionsCursor() {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.query( DB_TABLE_TRANSFERS, // The table to query
                new String[]{"_id", DB_COLUMN_COMMISSION, DB_COLUMN_TRANSFER}, // The columns to return
                null,                                     // The columns for the WHERE clause
                null,                                     // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // don't sort
        );
    }
}