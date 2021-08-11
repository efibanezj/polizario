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
}
