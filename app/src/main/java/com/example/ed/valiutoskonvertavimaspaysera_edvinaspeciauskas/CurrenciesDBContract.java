package com.example.ed.valiutoskonvertavimaspaysera_edvinaspeciauskas;

import android.provider.BaseColumns;

public final class CurrenciesDBContract {

    private CurrenciesDBContract() {}

    /* Inner class that defines the table contents */
    public static class Currencies implements BaseColumns {
        public static final String DB_TABLE_CURRENCIES = "currencies";
        public static final String DB_COLUMN_CURRENCY = "currency";
        public static final String DB_COLUMN_AMOUNT = "amount";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                DB_TABLE_CURRENCIES + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DB_COLUMN_CURRENCY + " TEXT, " +
                DB_COLUMN_AMOUNT + " REAL )";
    }
}