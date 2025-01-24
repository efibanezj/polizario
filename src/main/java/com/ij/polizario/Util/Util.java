package com.ij.polizario.Util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class Util {
    public static double mapDoubleNumber(String value) {
        return Double.parseDouble(value.replace("-", "").replace(",", ""));
    }

    public static String doubleToString(Double value) {
        BigDecimal bd = new BigDecimal(value);

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(bd.doubleValue());
    }

    public static String transformDate(String date) {
        String day, month, year;

        if (date.contains("-")) {
            String[] dateParts = date.split("-");
            year = dateParts[0];
            month = dateParts[1];
            day = dateParts[2];
        } else {
            day = date.substring(6,8);
            month = date.substring(4, 6);
            year = date.substring(0, 4);
        }
        return day + "-" + month + "-" + year;
    }
}
