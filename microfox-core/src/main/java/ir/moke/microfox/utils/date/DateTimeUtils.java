package ir.moke.microfox.utils.date;

import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.ULocale;

import java.time.*;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateTimeUtils {
    @SuppressWarnings("unchecked")
    public static <T extends Temporal> T fromString(String strDate, TimeZone timeZone, Locale locale, CalendarType type, DatePattern pattern, Class<T> clazz) {
        if (strDate == null || locale == null || type == null || pattern == null || clazz == null) {
            throw new IllegalArgumentException("Input parameters date|locale|type|pattern|clazz should not be null");
        }

        TimeZone tz = timeZone == null ? TimeZone.getTimeZone(ZoneId.systemDefault().getId()) : timeZone;

        try {
            ULocale uLocale = new ULocale("%s@calendar=%s".formatted(locale.toString(), type.getValue()));
            Calendar calendar = Calendar.getInstance(uLocale);
            SimpleDateFormat sdf = new SimpleDateFormat(pattern.toString(), uLocale);
            sdf.setCalendar(calendar);

            if (strDate.endsWith("Z")) {
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            } else {
                Matcher m = Pattern.compile("([+-]\\d{2}:?\\d{2})$").matcher(strDate);
                if (m.find()) {
                    String off = m.group(1);
                    if (!off.contains(":")) off = off.substring(0, 3) + ":" + off.substring(3);
                    sdf.setTimeZone(TimeZone.getTimeZone(ZoneOffset.of(off).getId()));
                } else {
                    sdf.setTimeZone(tz);
                }
            }

            ZoneId zid = ZoneId.of(tz.getID());

            // Parse to java.util.Date, then convert to Instant
            Date parsedDate = sdf.parse(strDate);
            Instant instant = parsedDate.toInstant();

            // Map Instant to requested Temporal type
            if (clazz == Instant.class) {
                return (T) instant;
            } else if (clazz == ZonedDateTime.class) {
                return (T) instant.atZone(zid);
            } else if (clazz == OffsetDateTime.class) {
                return (T) instant.atZone(zid).toOffsetDateTime();
            } else if (clazz == LocalDateTime.class) {
                return (T) LocalDateTime.ofInstant(instant, zid);
            } else if (clazz == LocalDate.class) {
                return (T) LocalDate.ofInstant(instant, zid);
            } else if (clazz == LocalTime.class) {
                return (T) LocalTime.ofInstant(instant, zid);
            } else {
                throw new UnsupportedOperationException("Unsupported Temporal type: " + clazz.getName());
            }

        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse date string: " + strDate, e);
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
