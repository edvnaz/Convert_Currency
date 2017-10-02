package com.example.ed.valiutoskonvertavimaspaysera_edvinaspeciauskas;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.example.ed.valiutoskonvertavimaspaysera_edvinaspeciauskas.Constants.*;
import static com.example.ed.valiutoskonvertavimaspaysera_edvinaspeciauskas.CurrenciesDBContract.Currencies.*;

public class MainActivity extends AppCompatActivity {

    TextView textViewNrEur, textViewNrUsd, textViewNrJpy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewNrEur = (TextView) findViewById(R.id.numberViewEur);
        textViewNrUsd = (TextView) findViewById(R.id.numberViewUsd);
        textViewNrJpy = (TextView) findViewById(R.id.numberViewJpy);
    }

    @Override
    protected void onResume() {
        super.onResume();

        readCurrenciesDB();
    }

    private void readCurrenciesDB() {
        SQLiteDatabase database = new DBHelper(this).getReadableDatabase();

        String[] projection = {
                CurrenciesDBContract.Currencies._ID,
                DB_COLUMN_CURRENCY,
                CurrenciesDBContract.Currencies.DB_COLUMN_AMOUNT
        };

//        String selection = DB_COLUMN_CURRENCY + " = ? ";
//        String[] selectionArgs = {EUR};

        Cursor cursor = database.query(
                DB_TABLE_CURRENCIES,             // The table to query
                projection,                      // The columns to return
                null,                            // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                            // don't group the rows
                null,                            // don't filter by row groups
                null                             // don't sort
        );

        while (cursor.moveToNext()) {
            DecimalFormat format = new DecimalFormat(MAIN_ACTIVITY_FORMAT_SPINNER);

            if (cursor.getString(cursor.getColumnIndex(DB_COLUMN_CURRENCY)).equals(EUR)) {
                String formatted = format.format(cursor.getDouble(cursor.getColumnIndex(DB_COLUMN_AMOUNT)));
                textViewNrEur.setText(formatted);

            } else if (cursor.getString(cursor.getColumnIndex(DB_COLUMN_CURRENCY)).equals(USD)) {
                String formatted = format.format(cursor.getDouble(cursor.getColumnIndex(DB_COLUMN_AMOUNT)));
                textViewNrUsd.setText(formatted);

            } else if (cursor.getString(cursor.getColumnIndex(DB_COLUMN_CURRENCY)).equals(JPY)) {
                String formatted = format.format(cursor.getDouble(cursor.getColumnIndex(DB_COLUMN_AMOUNT)));
                textViewNrJpy.setText(formatted);
            }
        }
        cursor.close();

    }

    public void callHistoryActivity(View view) {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }

    public void callConversionActivity(View view) {
        Intent intent = new Intent(this, ConversionActivity.class);
        startActivity(intent);
    }


}
