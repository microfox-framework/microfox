package ir.moke.microfox.utils;

import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.ULocale;

import java.time.*;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtils {
    @SuppressWarnings("unchecked")
    public static <T extends Temporal> T fromString(String input, ZoneId id, Locale locale, CalendarType type, DatePattern pattern, Class<? extends Temporal> clazz) {
        if (input == null || id == null || locale == null || type == null || pattern == null || clazz == null) {
            throw new IllegalArgumentException("Input parameters must not be null");
        }

        try {
            ULocale uLocale = new ULocale("%s@calendar=%s".formatted(locale.toString(), type.getValue()));
            Calendar calendar = Calendar.getInstance(uLocale);
            SimpleDateFormat sdf = new SimpleDateFormat(pattern.toString(), uLocale);
            sdf.setCalendar(calendar);

            // Parse to java.util.Date, then convert to Instant
            Date parsedDate = sdf.parse(input);
            Instant instant = parsedDate.toInstant();

            // Map Instant to requested Temporal type
            if (clazz == Instant.class) {
                return (T) instant;
            } else if (clazz == ZonedDateTime.class) {
                return (T) instant.atZone(id);
            } else if (clazz == OffsetDateTime.class) {
                return (T) instant.atZone(id).toOffsetDateTime();
            } else if (clazz == LocalDateTime.class) {
                return (T) LocalDateTime.ofInstant(instant, id);
            } else if (clazz == LocalDate.class) {
                return (T) LocalDate.ofInstant(instant, id);
            } else if (clazz == LocalTime.class) {
                return (T) LocalTime.ofInstant(instant, id);
            } else {
                throw new UnsupportedOperationException("Unsupported Temporal type: " + clazz.getName());
            }

        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse date string: " + input, e);
        }
    }

    public static String toString(ZonedDateTime zonedDateTime, Locale locale, CalendarType type, DatePattern pattern) {
        ULocale uLocale = new ULocale("%s@calendar=%s".formatted(locale.toString(), type.getValue()));
        Calendar calendar = Calendar.getInstance(uLocale);

        Instant instant = zonedDateTime.toInstant();
        Date date = Date.from(instant);
        calendar.setTimeZone(TimeZone.getTimeZone(zonedDateTime.getZone().getId()));
        calendar.setTime(date);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern.toString(), uLocale);
        simpleDateFormat.setTimeZone(calendar.getTimeZone());
        return simpleDateFormat.format(calendar.getTime());
    }
}
