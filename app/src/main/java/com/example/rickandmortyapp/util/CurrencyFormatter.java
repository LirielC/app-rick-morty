package com.example.rickandmortyapp.util;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public final class CurrencyFormatter {
    private static final Locale PT_BR = new Locale("pt", "BR");

    private CurrencyFormatter() {
    }

    public static String formatCurrency(double value) {
        return NumberFormat.getCurrencyInstance(PT_BR).format(value);
    }

    public static String formatInput(double value) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(PT_BR);
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);
        return numberFormat.format(value);
    }

    public static Double parseFlexible(String rawValue) {
        if (rawValue == null) {
            return null;
        }

        String sanitized = rawValue
                .replace("R$", "")
                .replace("\u00A0", " ")
                .trim();

        if (sanitized.isEmpty()) {
            return null;
        }

        try {
            return NumberFormat.getNumberInstance(PT_BR).parse(sanitized).doubleValue();
        } catch (ParseException ignored) {
        }

        try {
            return Double.parseDouble(sanitized.replace(".", "").replace(",", "."));
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}
