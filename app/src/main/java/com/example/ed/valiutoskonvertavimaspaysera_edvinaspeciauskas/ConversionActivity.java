package com.example.ed.valiutoskonvertavimaspaysera_edvinaspeciauskas;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.ed.valiutoskonvertavimaspaysera_edvinaspeciauskas.Constants.*;
import static com.example.ed.valiutoskonvertavimaspaysera_edvinaspeciauskas.CurrenciesDBContract.Currencies.*;
import static com.example.ed.valiutoskonvertavimaspaysera_edvinaspeciauskas.TransfersDBContract.Transfers.*;

public class ConversionActivity extends AppCompatActivity implements ConvertAlertDialog.ConvertAlertListener {

    Spinner currencySpinner1, currencySpinner2;
    EditText editTextFrom, editTextTo;
    Double amountFrom;
    String currencyFrom, currencyTo, conversionToAmount;
    Double amountFromDB, amountToDB;
    Button buttonConvert;
    SimpleCursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion);

        currencySpinner1 = (Spinner) findViewById(R.id.currenciesSpinner1);
        currencySpinner2 = (Spinner) findViewById(R.id.currenciesSpinner2);

        editTextFrom = (EditText) findViewById(R.id.editTextFrom);
        editTextTo = (EditText) findViewById(R.id.editTextTo);

        buttonConvert = (Button) findViewById(R.id.buttonConvert);
    }

    /**
     * Fills all currency Spinners
     */
    public void loadSpinnerList() {
        SQLiteDatabase database = new DBHelper(this).getReadableDatabase();
        Cursor cursor = database.query(
                DB_TABLE_CURRENCIES,                      // The table to query
                new String[]{"_id", DB_COLUMN_CURRENCY, DB_COLUMN_AMOUNT},  // The columns to return
                null,                                     // The columns for the WHERE clause
                null,                                     // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // don't sort
        );

        String[] adapterCols = new String[]{DB_COLUMN_CURRENCY};
        int[] adapterRowViews = new int[]{android.R.id.text1};

        cursorAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_spinner_item,
                cursor,
                adapterCols,
                adapterRowViews,
                0);
        cursorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        currencySpinner1.setAdapter(cursorAdapter);
        currencySpinner2.setAdapter(cursorAdapter);

    }

    /**
     * Create request url for conversion result preview
     */
    public void convertPreview() {
        if (currencyFrom != null && currencyTo != null && amountFrom != null) {
            String url = "http://api.evp.lt/currency/commercial/exchange/" +
                    amountFrom.toString() + "-" +
                    currencyFrom.toUpperCase() + "/" +
                    currencyTo.toUpperCase() + "/latest";

            if (amountFrom > 0) {
                try {
                    networkRequest(url);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, INTERNET_PROBLEM, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    /**
     * Network request for conversion using OKHttp library
     */
    void networkRequest(String url) throws IOException {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                final String myResponse = response.body().string();

                ConversionActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject obj = new JSONObject(myResponse);
                            conversionToAmount = obj.getString(DB_COLUMN_AMOUNT);
                            editTextTo.setText(conversionToAmount);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
    }

    /**
     * @return - number of left free conversions
     */
    public int leftFreeTransfers() {
        long completedTransfers = new DBHelper(this).getCompletedComversionsCount();
        return FREE_TRANSFERS - (int) (completedTransfers);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Spinner "Converting From"
        currencySpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                Cursor cursor = (Cursor) currencySpinner1.getSelectedItem();
                amountFromDB = cursor.getDouble(cursor.getColumnIndex(DB_COLUMN_AMOUNT));
                currencyFrom = cursor.getString(cursor.getColumnIndex(DB_COLUMN_CURRENCY));

                DecimalFormat format = new DecimalFormat(CONVERT_FORMAT_SPINNER);
                String formatted = format.format(amountFromDB);

                editTextFrom.setText(formatted);

                amountFrom = Double.valueOf(formatted);

                convertPreview();
                try {
                    if (currencyFrom.equals(currencyTo)) {
                        buttonConvert.setEnabled(false);
                    } else {
                        buttonConvert.setEnabled(true);
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        //Spinner "Converting To"
        currencySpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Cursor cursor = (Cursor) currencySpinner2.getSelectedItem();

                amountToDB = cursor.getDouble(cursor.getColumnIndex(DB_COLUMN_AMOUNT));
                currencyTo = cursor.getString(cursor.getColumnIndex(DB_COLUMN_CURRENCY));

                convertPreview();

                if (currencyFrom != null && currencyTo != null) {
                    if (currencyFrom.equals(currencyTo)) {
                        buttonConvert.setEnabled(false);
                    } else {
                        buttonConvert.setEnabled(true);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        buttonConvert.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Double checkAmount;

                //If there is no more free transfers add commission price to left amount
                if (leftFreeTransfers() < 1) {
                    checkAmount = amountFrom + ((amountFrom * COMMISSION_PRICE) / 100);
                } else {
                    checkAmount = amountFrom;
                }

                if (amountFromDB >= checkAmount && !currencyFrom.equals(currencyTo)) {
                    showNoticeDialog();
                } else {
                    if (amountFromDB >= amountFrom && amountFromDB < checkAmount) {
                        loadSimpleCommissionErrorAlertDialog(TOAST_COMMISSION_ERROR);
                    } else {
                        loadSimpleCommissionErrorAlertDialog(TOAST_COMMISSION_PAYMENT_ERROR);
                    }
                }
            }
        });

        editTextFrom.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    private Timer timer = new Timer();
                    private final long DELAY = 1000; // milliseconds

                    @Override
                    public void afterTextChanged(final Editable s) {
                        try {
                            amountFrom = Double.valueOf(s.toString().trim());
                        } catch (NumberFormatException e) {
                            amountFrom = 0.0; // your default value
                        }

                        if (TextUtils.isEmpty(s.toString().trim())) {
                            buttonConvert.setEnabled(false);
                        } else {
                            buttonConvert.setEnabled(true);
                        }

                        timer.cancel();
                        timer = new Timer();
                        timer.schedule(
                                new TimerTask() {
                                    @Override
                                    public void run() {
                                        // TODO: do what you need here (refresh list)
                                        // you will probably need to use runOnUiThread(Runnable action) for some specific actions
                                        if (!TextUtils.isEmpty(s.toString().trim())) {
                                            if (currencyTo != null && currencyFrom != null) {
                                                convertPreview();
                                            }
                                        }
                                    }
                                },
                                DELAY
                        );
                    }
                }
        );


        loadSpinnerList();

    }

    /*
     * AlertDialog
     */
    public void showNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new ConvertAlertDialog();
        dialog.show(getFragmentManager(), "ConvertAlertDialog");
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        try {
            ContentValues cvFromAmount = new ContentValues();
            ContentValues cvToAmount = new ContentValues();
            ContentValues cvCommission = new ContentValues();

            Double conversionAmount = amountFrom;
            Double afterConversionFrom = amountFromDB - conversionAmount;
            Double afterConversionTo = amountToDB + Double.parseDouble(conversionToAmount);

            Double commission = 0.00;

            //take count and take away commission from transferring amount
            if (leftFreeTransfers() < 1) {
                commission = (conversionAmount * COMMISSION_PRICE) / 100.00;
                afterConversionFrom = afterConversionFrom - commission;
            }

            String transferDetails = createTransferDetailsString(conversionAmount, currencyFrom,
                    currencyTo, conversionToAmount, commission);

            new DBHelper(this).addCommission(commission, transferDetails);

            cvFromAmount.put(AMOUNT, afterConversionFrom);
            cvToAmount.put(AMOUNT, afterConversionTo);
            cvCommission.put(COMMISSION, commission);

            SQLiteDatabase database = new DBHelper(this).getReadableDatabase();
            database.update(DB_TABLE_CURRENCIES, cvFromAmount, "currency = '" + currencyFrom + "'", null);
            database.update(DB_TABLE_CURRENCIES, cvToAmount, "currency = '" + currencyTo + "'", null);
            database.update(DB_TABLE_TRANSFERS, cvCommission, "commission = '" + commission + "'", null);

            loadSimpleCompletedAlertDialog(transferDetails);

            loadSpinnerList();

        } catch (NumberFormatException e) {
            Toast.makeText(this, TOAST_BAD_TRANSFER_PRICE, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
    }

    /**
     * Create successful currency conversion AlertDialog
     *
     * @param transferDetails - wrong currency error text
     */
    public void loadSimpleCompletedAlertDialog(String transferDetails) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ConversionActivity.this);

        builder.setMessage(transferDetails);
        builder.setNeutralButton(BUTTON_UNDERSTOOD, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Create wrong currency conversion AlertDialog
     *
     * @param problemText - currency problem text
     */
    public void loadSimpleCommissionErrorAlertDialog(String problemText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ConversionActivity.this);

        builder.setMessage(problemText);
        builder.setNeutralButton(BUTTON_UNDERSTOOD, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
