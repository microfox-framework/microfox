package ir.moke.test;

import com.ibm.icu.util.TimeZone;
import ir.moke.utils.date.CalendarType;
import ir.moke.utils.date.DatePattern;
import ir.moke.utils.date.DateTimeUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;

public class DateTimeTest {

    @Test
    public void checkConvertZonedDateTime() {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(2025, 7, 16, 12, 30, 0, 0, ZoneId.of("Asia/Tehran"));
        String enOutput = DateTimeUtils.toString(zonedDateTime, Locale.ENGLISH, CalendarType.PERSIAN, DatePattern.ISO_8601);
        String faOutput = DateTimeUtils.toString(zonedDateTime, Locale.of("fa_IR"), CalendarType.PERSIAN, DatePattern.ISO_8601);
        String faOutputIslamic = DateTimeUtils.toString(zonedDateTime, Locale.of("fa_IR"), CalendarType.ISLAMIC, DatePattern.ISO_8601);
        String arOutputIslamic = DateTimeUtils.toString(zonedDateTime, Locale.of("ar_SA"), CalendarType.ISLAMIC, DatePattern.ISO_8601);
        System.out.println(enOutput);
        System.out.println(faOutput);
        System.out.println(faOutputIslamic);
        System.out.println(arOutputIslamic);
    }

    @Test
    public void checkStringToInstant() {
        // TimeZone : Asia/Tehran
        String en = "1404-05-12T12:30:00.000Z";
        String fa = "۱۴۰۴-۰۴-۲۵T۱۲:۳۰:۰۰.۰۰۰Z";
        String faIslamic = "۱۴۴۷-۰۱-۲۱T۱۲:۳۰:۰۰.۰۰۰Z";
        String arIslamic = "١٤٤٧-٠١-٢١T١٢:٣٠:٠٠.٠٠٠Z";

        ZonedDateTime enOutput = DateTimeUtils.fromString(en, TimeZone.getTimeZone("Asia/Tehran"), Locale.ENGLISH, CalendarType.PERSIAN, DatePattern.ISO_8601, ZonedDateTime.class);
        ZonedDateTime faOutput = DateTimeUtils.fromString(fa, TimeZone.getTimeZone("Asia/Tehran"), Locale.of("fa_IR"), CalendarType.PERSIAN, DatePattern.ISO_8601, ZonedDateTime.class);
        ZonedDateTime faIslamicOutput = DateTimeUtils.fromString(faIslamic, TimeZone.getTimeZone("Asia/Tehran"), Locale.of("fa_IR"), CalendarType.ISLAMIC, DatePattern.ISO_8601, ZonedDateTime.class);
        ZonedDateTime arIslamicOutput = DateTimeUtils.fromString(arIslamic, TimeZone.getTimeZone("Asia/Tehran"), Locale.of("ar_SA"), CalendarType.ISLAMIC, DatePattern.ISO_8601, ZonedDateTime.class);
        System.out.println(enOutput);
        System.out.println(faOutput);
        System.out.println(faIslamicOutput);
        System.out.println(arIslamicOutput);

        // Migrate TimeZone : UTC
        ZonedDateTime utc = enOutput.withZoneSameInstant(ZoneId.of("UTC"));
        System.out.println(utc);
    }

    @Test
    public void checkStringToLocalTime() {
        // TimeZone : Asia/Tehran
        String fullDateTimeZoneString = "1404-04-25T12:30:00.000+0330";
        LocalTime output1 = DateTimeUtils.fromString(fullDateTimeZoneString, TimeZone.getTimeZone("UTC"), Locale.ENGLISH, CalendarType.PERSIAN, DatePattern.ISO_8601, LocalTime.class);
        System.out.println(output1);

        System.out.println("-------------------");

        String timeZoneString = "12:30:00.000+0330";
        LocalTime output2 = DateTimeUtils.fromString(timeZoneString, TimeZone.getTimeZone("America/Bahia"), Locale.ENGLISH, CalendarType.PERSIAN, DatePattern.FULL_TIME_WITH_OFFSET_PATTERN, LocalTime.class);
        System.out.println(output2);

        LocalTime output3 = DateTimeUtils.fromString(timeZoneString, TimeZone.getTimeZone("UTC"), Locale.ENGLISH, CalendarType.PERSIAN, DatePattern.FULL_TIME_WITH_OFFSET_PATTERN, LocalTime.class);
        System.out.println(output3);

        LocalTime output4 = DateTimeUtils.fromString(timeZoneString, TimeZone.getTimeZone("Asia/Tokyo"), Locale.ENGLISH, CalendarType.PERSIAN, DatePattern.FULL_TIME_WITH_OFFSET_PATTERN, LocalTime.class);
        System.out.println(output4);

        String timeString = "12:30:00";
        LocalTime output5 = DateTimeUtils.fromString(timeString, TimeZone.getTimeZone("Asia/Tokyo"), Locale.ENGLISH, CalendarType.PERSIAN, DatePattern.FULL_TIME_PATTERN, LocalTime.class);
        System.out.println(output5);
    }

    @Test
    public void checkTimeZone() {
        String fullDateTimeZoneString = "1404-04-25T12:30:00.000+0330";
        ZonedDateTime zonedDateTime = DateTimeUtils.fromString(fullDateTimeZoneString, null, Locale.ENGLISH, CalendarType.PERSIAN, DatePattern.ISO_8601, ZonedDateTime.class);
        System.out.println(zonedDateTime);
    }
}
