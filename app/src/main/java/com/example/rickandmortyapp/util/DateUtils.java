package com.example.rickandmortyapp.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class DateUtils {
    private static final String ISO_LOCAL_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String ISO_UTC_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String DISPLAY_PATTERN = "dd/MM/yyyy HH:mm";

    private DateUtils() {
    }

    public static String nowIso() {
        return new SimpleDateFormat(ISO_LOCAL_PATTERN, Locale.getDefault()).format(new Date());
    }

    public static String formatDisplayDate(String rawValue) {
        if (rawValue == null || rawValue.trim().isEmpty()) {
            return "";
        }

        Date parsedDate = parseDate(rawValue.trim(), ISO_UTC_PATTERN, TimeZone.getTimeZone("UTC"));
        if (parsedDate == null) {
            parsedDate = parseDate(rawValue.trim(), ISO_LOCAL_PATTERN, null);
        }
        if (parsedDate == null) {
            return rawValue;
        }
        return new SimpleDateFormat(DISPLAY_PATTERN, Locale.getDefault()).format(parsedDate);
    }

    private static Date parseDate(String value, String pattern, TimeZone timeZone) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
            if (timeZone != null) {
                dateFormat.setTimeZone(timeZone);
            }
            return dateFormat.parse(value);
        } catch (ParseException exception) {
            return null;
        }
    }
}
