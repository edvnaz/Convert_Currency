package com.example.ed.valiutoskonvertavimaspaysera_edvinaspeciauskas;

import java.text.DecimalFormat;

public final class Constants {
    /**
     * Warnings
     */
    public static final String TOAST_COMMISSION_ERROR = "Negalima atlikti konvertavimo, patikrinkite sumą ir valiutą";
    public static final String TOAST_COMMISSION_PAYMENT_ERROR = "Nepakankama suma komisiniam mokesčiui";
    public static final String INTERNET_PROBLEM = "Interneto problema";
    public static final String BUTTON_UNDERSTOOD = "Supratau";
    public static final String TOAST_BAD_TRANSFER_PRICE = "Bloga konvertuojama suma";

    /**
     * Alert Dialogs
     */
    public static final String ALERT_DIALOG_CONTINUE = "Tęsti?";
    public static final String ALERT_DIALOG_BUTTON_YES = "Taip";
    public static final String ALERT_DIALOG_BUTTON_NO = "Ne";


    /**
     * Double values formats
     */
    public static final String CONVERT_FORMAT_SPINNER = "0.00";
    public static final String MAIN_ACTIVITY_FORMAT_SPINNER = "#,##0.00";


    /**
     * Currencies
     */
    public static final String EUR = "EUR";
    public static final String USD = "USD";
    public static final String JPY = "JPY";
    public static final String COMMISSION = "commission";
    public static final String AMOUNT = "amount";


    /**
     * Commissions
     */
    public static final Double COMMISSION_PRICE = 0.7;

    /**
     * Number of free conversions before commission
     */
    public static final int FREE_TRANSFERS = 2;

    /**
     * Form currency conversion information text
     *
     * @param conversionAmount   - amount with possible commission
     * @param currency1          - first spinner currency
     * @param currency2          - second spinner currency
     * @param conversionToAmount - from conversion amount
     * @param commission         - to conversion amount
     * @return - currency conversion information text
     */
    public static String createTransferDetailsString(Double conversionAmount, String currency1,
                                                     String currency2, String conversionToAmount,
                                                     Double commission) {
        DecimalFormat df = new DecimalFormat(CONVERT_FORMAT_SPINNER);

        return "Jūs konvertavote " + df.format(conversionAmount) + " " + currency1 +
                " į " + conversionToAmount + " " + currency2 + ". Komisinis mokestis - " +
                (commission > 0.00 ? df.format(commission) + " " + currency1 + ". (0.7% komisinis mokestis)" : "0.00 " + currency1 + ". (nemokamai)");
    }
}
