package com.example.ed.valiutoskonvertavimaspaysera_edvinaspeciauskas;


import android.provider.BaseColumns;

public final class TransfersDBContract {

    private TransfersDBContract() {
    }

    public static class Transfers implements BaseColumns {

        public static final String DB_TABLE_TRANSFERS = "transfers";
        public static final String DB_COLUMN_COMMISSION = "commission";
        public static final String DB_COLUMN_TRANSFER = "transfer";
        public static final String DB_COLUMN_DATE = "date";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                DB_TABLE_TRANSFERS + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DB_COLUMN_COMMISSION + " REAL, " +
                DB_COLUMN_TRANSFER + " TEXT )";
    }
}
