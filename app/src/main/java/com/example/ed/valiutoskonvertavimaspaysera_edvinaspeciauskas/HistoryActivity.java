package com.example.ed.valiutoskonvertavimaspaysera_edvinaspeciauskas;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import static com.example.ed.valiutoskonvertavimaspaysera_edvinaspeciauskas.TransfersDBContract.Transfers.*;

public class HistoryActivity extends AppCompatActivity {
    ListView listView = null;
    DBHelper dbHelper;
    Cursor cursor;
    CursorAdapter myCustomAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        dbHelper = new DBHelper(this);
        cursor = dbHelper.getAllConversionsCursor();
        myCustomAdapter = new CursorAdapter(this, cursor);

        listView = (ListView) findViewById(R.id.historyList);
        listView.setAdapter(myCustomAdapter);

    }

    public class CursorAdapter extends android.widget.CursorAdapter {
        public CursorAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
        }

        // The newView method is used to inflate a new view and return it,
        // you don't bind any data to the view at this point.
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.history_item, parent, false);
        }

        // The bindView method is used to bind all data to a given view
        // such as setting the text on a TextView.
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // Find fields to populate in inflated template
            TextView tvKomisinis = (TextView) view.findViewById(R.id.commission);
            TextView tvTransferDetail = (TextView) view.findViewById(R.id.transferDetail);
            // Extract properties from cursor
            Double body = cursor.getDouble(cursor.getColumnIndexOrThrow(DB_COLUMN_COMMISSION));
            String priority = cursor.getString(cursor.getColumnIndexOrThrow(DB_COLUMN_TRANSFER));
            // Populate fields with extracted properties
            tvKomisinis.setText(String.valueOf(body));
            tvTransferDetail.setText(priority);
        }
    }
}